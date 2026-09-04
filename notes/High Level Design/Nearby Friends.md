	## Overview

**“Nearby Friends” real-time system** at scale involves real-time location updates, querying nearby users, and sending updates to clients efficiently.

Here’s a **scalable system design** using:
- **WebSockets** for persistent communication
- **Redis** (Geo + Pub/Sub) for location storage and cross-node messaging
- A **database** (like PostgreSQL or Cassandra) for user profile/history and location data
- **Load balancer** and **WebSocket servers** for connection handling

## ✅ High-Level Goals

- Accept **300–400K location updates/sec**
- Track each user’s **latest location**
- Notify users about **friends nearby or movements**
- Handle **millions of concurrent connections**

## 🧱 COMPONENTS & ROLES

| Component                    | Technology                                 | Purpose                                                                |
| ---------------------------- | ------------------------------------------ | ---------------------------------------------------------------------- |
| **Client (Mobile/Web)**      | WebSocket-enabled app                      | Sends location, receives nearby updates                                |
| **Load Balancer**            | AWS ALB / NGINX / HAProxy                  | Routes WebSocket upgrade requests to servers                           |
| **WebSocket Gateway Server** | Java (Netty), Node.js (`ws`), Go (Gorilla) | Manages WebSocket connections, handles messages                        |
| **Redis (Geo + Pub/Sub)**    | Redis                                      | Stores real-time location data, queries nearby users, notifies servers |
| **Database**                 | PostgreSQL or Cassandra                    | Stores persistent user profiles, location history, analytics           |
| **Location Service**         | Internal service                           | Interfaces with Redis Geo commands for write/query                     |

## 🔄 FLOW DIAGRAM

               +------------+
               |   Clients  |
               | (Web/Mobile|
               |  WebSocket)|
               +-----+------+
                     |
                     v
            +----------------+
            |   API Gateway  |
            | (Routing/Auth) |
            +--+--------+----+
               |        |
      +--------+        +---------------------+
      |                                 |
+-----v------+                  +--------v---------+
|  Auth Svc  |                  |  User Profile Svc|
| (Login,    |                  | (Get/Update user |
|  JWT, OAuth|                  |  info/settings)  |
+------------+                  +------------------+
               |
               v
     +-----------------------------+
     | Load Balancer (ALB / NLB)   |
     +-------------+---------------+
                   |
    +--------------+--------------+
    |     WebSocket Servers       |
    | (Connection + Msg Handler)  |
    +------+------------+---------+
           |            |       |
           |            |       +-------------------------+
           |            |                                            |
           |        +---v--------+                        |
           |        | Redis GEO  |                         |
           |        +------------+                        |
           |                                                        |
    +------v---------+                         +---------+--------+
    | Redis Pub/Sub  |<------------|  Other WebSocket |
    | (User Channel) |                         |     Servers              |
    +----------------+                         +------------------+
           |
           +------------------------------------------------+
           |                                                |
    +------v------------------+                      +------v--------+
    | Redis Stream / Kafka    |                      | Subscribed    |
    | (Durable Queue)           |                      | Location Svc  |
    +-------------------------+                      +---------------+
                                                          |
                                                          v
                                            +--------------------------+
                                            | PostgreSQL / Cassandra   |
                                            | (User location history,  |
                                            |  analytics, audit, etc.) |
                                            +--------------------------+


---

## 🔁 FLOW OF A LOCATION UPDATE

### 1. **User sends location** via WebSocket

```
{ "userId": "u123", "lat": 12.96, "lon": 77.59 }
```

### 2. **API Gateway**:

- Handles **authentication**, **authorization**, and **request validation**
- Routes the request to WebSocket servers via **Load Balancer**
### 3. **WebSocket Server**:

- Performs three parallel actions:
    - ✅ Updates Redis Geo for proximity lookup
    - ✅ Publishes nearby user notifications (direct or via Pub/Sub)
    - ✅ Publishes to Redis Stream (or Kafka) for durable persistence

### 4. **Real-Time Location Update in Redis Geo**:

```
GEOADD user_locations 77.59 12.96 u123
```

- Then query nearby users:
```
GEORADIUS user_locations 77.59 12.96 500 m WITHDIST
```

---

### 5. **Cross-User Communication (Real-Time Delivery):**

For each user nearby (say `u456`):
- **If u456 is connected to the same WebSocket server**:
    - Push message directly via WebSocket connection
- **If u456 is connected to a different server**:
    - Publish update to Redis Pub/Sub:
    ```
    PUBLISH user:u456 "{ "type": "nearby", "from": "u123", "lat": 12.96, "lon": 77.59 }"
	```
    - The receiving WebSocket server (subscribed to `user:u456`) delivers the message to the user via WebSocket
---

### 6. **Asynchronous Persistence for Durability**:

- The WebSocket server publishes the update to a **durable queue**, e.g., Redis Stream:
```
XADD location_stream * userId u123 lat 12.96 lon 77.59 ts 1729000123
```
- Or Kafka topic (if used instead)

---

### 7. **Location Service (Worker/Consumer)**:

- Subscribes to the stream
- Writes the location update to persistent storage:
```
INSERT INTO location_history(user_id, lat, lon, timestamp)
```
- Also may perform:
    - Geo-enrichment (e.g., city/zone lookup)
    - Analytics, alerts, or further processing
---

## ✅ COMPONENT JUSTIFICATION

#### 🔐 **API Gateway**

- Entry point for all external traffic including WebSocket upgrade requests.
- Handles:
    - **Authentication** (e.g., via JWT, OAuth)
    - **Authorization checks**
    - **Rate limiting**, **IP whitelisting**, and **logging**

- Validates tokens and forwards only **authenticated** requests to downstream services.
- Routes WebSocket requests to the Load Balancer for proper server allocation.
- Decouples **authentication logic** from WebSocket servers, keeping them lean and stateless.

---
#### 🔄 **WebSocket Server**

- Maintains **persistent WebSocket connections** with clients.
- Assumes **incoming traffic is already authenticated** by the gateway.
- Tracks connected users and manages in-memory connection maps:  
    `Map<userId, WebSocket>`
- Handles incoming location updates and performs:
    - ✅ Real-time **proximity lookup** via Redis Geo
    - ✅ Push to **nearby users** via direct WebSocket or Redis Pub/Sub
    - ✅ Asynchronous publish to **Redis Stream** (or Kafka) for durable processing
- Designed to be **stateless beyond active socket connections**, enabling **horizontal scaling** across instances.
- Typical no. of connections a WebSocket can handle concurrently
	|2–4 vCPUs, 4–8 GB RAM|**10K – 50K** concurrent connections|
	|8–16 vCPUs, 16–64 GB RAM|**100K – 500K+** connections (with tuning)|

---

### 🌐 **Redis GEO**

- Stores latest user locations with spatial indexing
- Supports **low-latency `GEOADD` and `GEORADIUS`** queries
- Scales to 100K+ operations/second
- Geohash-based proximity queries allow fast real-time detection of nearby friends
- Data can be set with an expiry (`EXPIRE`) to auto-clean inactive users

---

### 📡 **Redis Pub/Sub**

- Handles **real-time, cross-server messaging**
- Each WebSocket server subscribes to channels for the users it manages
- Enables **server-to-server message delivery** for nearby user updates (e.g., `PUBLISH user:u456`)
- Ensures low-latency fan-out without involving external brokers
- Not responsible for durability — just real-time delivery

---
### 🔄 **Redis Stream / Kafka**

- Provides a **durable, append-only log** of location updates
- Used for **decoupling real-time updates from persistence**
- Ensures updates are never lost even if downstream consumers are temporarily unavailable
- Scalable and fault-tolerant (especially Kafka)
- Enables **event-driven processing**: persistence, analytics, geofencing, notifications, etc.

---
### 🛢️ **PostgreSQL / Cassandra**

| DB             | Why Use It                                                                                                               |
| -------------- | ------------------------------------------------------------------------------------------------------------------------ |
| **PostgreSQL** | Relational, strongly consistent, great for user profiles, moderate write throughput, and historical location queries     |
| **Cassandra**  | Write-optimized, highly available, ideal for **time-series location history** at very large scale (e.g., fleet tracking) |

## ⚙️ OPTIMIZATIONS

| Area                           | Strategy                                                                                               |
| ------------------------------ | ------------------------------------------------------------------------------------------------------ |
| **Redis Load**                 | Partition user location data by region or use Redis Cluster to distribute GEO operations               |
| **Redis GEO**                  | Use `EXPIRE` or `GEOREMOVE` with timestamps to evict stale locations beyond 3–5 mins                   |
| **Pub/Sub**                    | Use **sharded Pub/Sub channels** per user or region; switch to **Kafka** for higher scale + durability |
| **Nearby Queries**             | Use `GEORADIUS` with bounding box filters or optimize with **Geohash-based clustering**                |
| **WebSocket Scaling**          | Use **sticky sessions** at the load balancer or **consistent hashing** for uniform load                |
| **Redis Stream / Kafka**       | Use batching and consumer groups to scale async writes and reduce DB IOPS                              |
| **Persistence Buffer**         | Apply a short queue TTL + retry logic to Redis Stream/Kafka to handle backpressure                     |
| **Data Cleanup**               | Periodically archive or compact historical data in DB; Redis GEO entries expire automatically          |
| **Location Frequency Control** | Rate-limit updates from clients to avoid flooding the system with noisy or redundant updates           |

## Questions

### Does Each user has a Redis channel, and friends subscribe to it ?

You're thinking of **user-centric Pub/Sub**, like:
```
PUBLISH user:u123 { location update }
```

…and all **friends of u123 are subscribed** to `user:u123` channel.

✅ This would work **in a single-node or simple system** where clients can directly subscribe to Redis channels.
❌ But in **a large-scale distributed system**, **clients don't connect directly to Redis** — instead, they’re connected to **WebSocket servers**, which act as intermediaries.

✅ Real Use in Our Design: **Server-to-Server Routing via Pub/Sub**

#### 🎯 Goal:

Ensure a location update from user `A` (on WebSocket Server 1) reaches friend `B`, who might be connected to Server 2.

#### 🔁 Flow:

1. **User A** sends a location update.
2. WebSocket Server 1:
    - Updates Redis Geo
    - Finds B is nearby
    - But B is not connected to **this** server
3. WebSocket Server 1 publishes to Redis:
```
PUBLISH user:B_channel { "from": "A", "type": "nearby", "lat": ..., "lon": ... }
```
4. WebSocket Server 2 is subscribed to:
```
SUBSCRIBE user:B_channel
```
5. Server 2 receives the message and pushes it to B over the WebSocket connection.

#### 🔄 So Who Subscribes to Redis?

✅ Not the **clients**
✅ But the **WebSocket servers**, each server:
- **Subscribes to channels for the users it currently has connected**
- **Publishes to Redis when it needs to notify a user connected elsewhere**

#### ❓Why Don't Clients Subscribe to Redis Themselves?
- Redis Pub/Sub doesn’t scale to **millions of clients**.
- Redis doesn't persist messages; if you're offline, you miss them.
- You don't want every mobile client to connect directly to Redis—it’s **insecure**, **inefficient**, and **not scalable**.
- Redis is best used as a **backplane** for **server-to-server** or **process-to-process** communication.
- Think of Redis Pub/Sub as a **messenger between WebSocket servers**, not between users directly.
	- Redis doesn’t know who’s online—it just **delivers to whoever is subscribed**
	- WebSocket servers **know which users are connected to them**, so they handle:
	    - Redis subscription
	    - Message routing
	    - Cleanup and delivery

### Is there any application running on the WebSocket server?

Yes. The WebSocket server **runs an application** — it’s not just a dumb socket handler.

 This application does things like:

| Responsibility                      | Example                                             |
| ----------------------------------- | --------------------------------------------------- |
| **Manage connections**              | Accept new WebSocket handshakes, track open sockets |
| **Parse and route messages**        | Handle messages like location updates, chat, status |
| **Interface with backend services** | Communicate with location service, database, etc.   |
| **Maintain in-memory state**        | `Map<userId, WebSocketConnection>`                  |
| **Subscribe to Redis Pub/Sub**      | To receive cross-server messages                    |
| **Send messages to clients**        | Push events using the right WebSocket object        |

🛠️ It's often implemented in a language with async/event-driven support:

- Java (Netty, Spring WebFlux)
- Node.js (ws, socket.io)
- Go (Gorilla)
- Python (FastAPI + websockets)
- Rust, C++, etc. for low-latency cases

### How Does the Load Balancer Discover WebSocket Servers?

The gateway (like an **AWS ALB**, NGINX, or custom TCP proxy) needs to know **where to route WebSocket traffic**.

#### There are 2 options:

##### ✅ Option 1: **Static Discovery**
- You configure a **list of WebSocket server IPs/ports**
- Used in small or non-dynamic clusters
- Works with traditional LBs like HAProxy, NGINX

##### ✅ Option 2: **Service Discovery (Dynamic)**
In cloud-native setups (Kubernetes, ECS, etc.):
- WebSocket servers **register themselves** in a **service registry** (e.g., Consul, etcd, Kubernetes DNS)
- Gateway or internal service uses this to find servers

🔄 In both cases:
- **Sticky sessions** or **consistent hashing** is used to route users to the same server consistently.

### Does the WebSocket Server Talk to the Location Service, or Is It Built-In?

#### Location Service is a Separate Microservice

**WebSocket Server** ➝ **HTTP/gRPC** ➝ **Location Service**
- **Recommended for large-scale systems**
- Separation of concerns: easier to scale, test, deploy independently
- Location Service handles Redis GEO/queries, TTL, cleanup, etc.
```
POST /location/update
{ userId: 'u123', lat: 12.96, lon: 77.59 }
```

### Should we short-circuit and push directly to a nearby user without routing through the Location Service.

Context:
- The **Location Service** might be responsible for **logging location updates to a database** (e.g., for analytics, history, etc.)
- So if we bypass it for performance reasons, **we might lose durability or long-term tracking**

##### 🧠 Key Design Principle
> **Split the responsibilities** of **real-time propagation** and **data persistence**.

This is a classic **Command + Side Effect** pattern.

#### Recommended Approach: **Fan-out Fast, Persist Async**

#### 🔁 Step-by-Step

1. **Client sends location update**
2. **WebSocket server:**
    - Writes to `Redis GEO` (fast, low-latency)
    - Queries `GEORADIUS` for nearby users
    - For each user:
        - **If local**: push via socket
        - **If remote**: `PUBLISH` to Redis channel
    - **Also publishes** update to a **Redis Stream / Kafka / queue** for persistence
3. **Location Service (consumer)**:
    - Subscribed to Redis Stream or Kafka
    - Saves to PostgreSQL/Cassandra/etc.
    - Adds enrichments if needed (e.g., IP, accuracy, timestamp, etc.)

### How are users distributed across WebSocket servers?

✅ **Use Sticky sessions**:

> Once a user connects to a WebSocket server, future reconnections go to the **same server**, so their in-memory session (e.g., `Map<userId, WebSocket>`) stays valid.

🎯 How to achieve it?

| Approach                         | Description                                                                               | Example                   |
| -------------------------------- | ----------------------------------------------------------------------------------------- | ------------------------- |
| **Cookie-based sticky sessions** | Load balancer injects a `Set-Cookie` (e.g., `SRV_ID=ws-3`) and uses it in future requests | ✅ AWS ALB supports this   |
| **IP hash-based routing**        | Hash user’s IP → consistent WebSocket server                                              | Works with NGINX, HAProxy |
| **Custom hashing (user ID)**     | Hash `userId` → `serverId` (like consistent hashing ring)                                 |                           |
📌 Best Practice:

Use **consistent hashing on userId** for deterministic server assignment (especially if you have an auth gateway before WebSocket server).

Why?
- Works across reconnects
- Avoids IP hash inconsistencies (especially on mobile)
- Helps you replicate user-server mapping logic during recovery


### What happens when a WebSocket server goes down?

Problem:
If a server goes down:
- All its connected users lose their WebSocket connections.
- Any in-memory state is lost (`Map<userId, WebSocket>`)
- Redis Pub/Sub listeners for those users are gone.

---

✅ Recovery Flow

| Step                                 | What Happens                                                                                         |
| ------------------------------------ | ---------------------------------------------------------------------------------------------------- |
| **1. Clients detect disconnect**     | WebSocket client tries to reconnect                                                                  |
| **2. Load balancer redistributes**   | Based on sticky cookie or IP hash, but server is now **offline** → LB routes to a **healthy** server |
| **3. New server handles connection** | Authenticates user, reinitializes session, and **re-subscribes to Pub/Sub** for `user:u123`          |
| **4. System self-heals**             | The new server takes over user's messages and presence                                               |
🧠 Optional: Use a Presence Service

To make **user-server mapping discoverable** across your cluster:
```
SET user:u123 serverId ws-3 EX 60
```

This way:
- You know where each user is (useful for routing)
- When a server fails, others can identify orphaned users
- You can eventually move to **serverless or stateless WebSocket layers**

### How is the routing of message happening for the actual message to the correct server and then to the correct user who should receive the message ?

For **each user connected to a specific WebSocket server**, that server subscribes to **that user's dedicated Redis channel**.

🔁 Who subscribes?
	**❌ Not the user/client directly**  
	**✅ The WebSocket server** subscribes **on behalf of** each connected user.

🧩 So to clarify the relationship:

| Concept             | Who owns it?                            | Example                                  |
| ------------------- | --------------------------------------- | ---------------------------------------- |
| Redis channel       | Logical pub/sub channel                 | `user:u123`                              |
| Subscription action | WebSocket server process                | `SUBSCRIBE user:u123`                    |
| Redis message       | Sent to all subscribers of that channel | Only to `user:u123` handlers             |
| WebSocket routing   | Done inside WebSocket server            | Look up socket for `u123`, call `send()` |

##### 🔄 “Now that a Redis message has arrived at the correct WebSocket server, how does it know which WebSocket connection to send it to?”

When a user connects via WebSocket:

1. User `u123` sends a WebSocket handshake (often with a token).
2. WebSocket server:
    - Authenticates the token to get `userId = u123`
    - Stores the socket:
		```
		userSocketMap.put("u123", socketConnection);
		```
3. Subscribes to Redis:
```
SUBSCRIBE user:u123
```

🔁 When a Redis Pub/Sub Message Arrives

Let’s say the server receives:
```
{
  "type": "location",
  "from": "u456",
  "lat": 12.96,
  "lon": 77.59
}
```
On channel: `user:u123`

Then:
```
redisSubscriber.on('message', (channel, message) => {
    const userId = channel.split(':')[1]; // Extract 'u123' from 'user:u123'
    const socket = userSocketMap.get(userId);

    if (socket && socket.readyState === WebSocket.OPEN) {
        socket.send(message); // Deliver to the actual client
    }
});
```


### We are using a load balancer and not a Gateway, also what are the responsibilities of Load Balancer here?

This AWS Load Balancer is **not** a full API Gateway — but it's acting as a **lightweight routing layer**, mainly for **WebSocket traffic**.
The **Load Balancer (LB)** only acts as a **reverse proxy** during the initial connection setup. After routing, the **WebSocket connection is persisted between the client and one specific WebSocket server**.

🧰 Responsibilities of the **AWS Load Balancer** (ALB/NLB)

| Responsibility             | ALB/NLB Support? | Notes                                           |
| -------------------------- | ---------------- | ----------------------------------------------- |
| TCP/WebSocket routing      | ✅ Yes            | NLB = layer 4 (TCP), ALB = layer 7 (HTTP/WS)    |
| Sticky sessions            | ✅ (ALB only)     | Needed for WebSocket affinity                   |
| TLS termination            | ✅ Yes            | Offloads HTTPS to LB                            |
| Health checks              | ✅ Yes            | Ensures traffic goes only to healthy WS servers |
| WebSocket support          | ✅ Yes            | ALB and NLB both support long-lived WS          |
| Header manipulation        | ❌ No             | Limited; not programmable like a gateway        |
| Authentication             | ❌ No             | Must be handled downstream (e.g., WS server)    |
| Rate limiting / throttling | ❌ No             | Requires API Gateway or custom logic            |
✅ **You don’t _need_ an API Gateway** _just for WebSocket traffic_, if:

- You’re only using WebSockets for all client-server communication
- You handle authentication, rate-limiting, and metrics inside your WebSocket servers
- Your AWS Load Balancer (ALB) handles routing, sticky sessions, TLS

✅ Suggested Hybrid Design

In production, most real-time systems **separate WebSocket and REST paths** like this:
```
+-------------------+       +----------------------+
|   Client (Mobile) |  -->  |   API Gateway (REST) | ---> [ Auth Svc, Config Svc, etc. ]
|                   |       +----------------------+
|                   |
|    WebSocket conn |  -->  |  ALB / NLB (WebSocket LB) | ---> WebSocket Servers
+-------------------+       +---------------------------+
```
This gives you:

- API Gateway for REST traffic (login, user profile, etc.)
- WebSocket-specific LB for real-time messaging