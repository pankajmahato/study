## Definition

MongoDB is a **document-oriented NoSQL database** designed for high performance, scalability, and ease of development.

### Key Features:

1. **Document-Based Storage**:
    - Stores data as **BSON** (Binary JSON) documents.
    - Each document is like a JSON object: flexible, hierarchical, and schema-less.
	```
	{
	  "name": "Alice",
	  "age": 30,
	  "skills": ["Java", "MongoDB"]
	}
	```
2. **Collections**:
    - Documents are grouped into **collections**, similar to tables in SQL.
        
3. **No Fixed Schema**:
    - Documents in the same collection can have different fields and structures.
        
4. **Rich Query Language**:
    - Powerful queries with filtering, projections, aggregations, and indexing.
        
5. **Horizontal Scalability (Sharding)**:
    - Supports automatic **sharding** to scale across multiple machines.
        
6. **Replication for High Availability**:
    - Uses **replica sets** to ensure redundancy and failover.
        
7. **Indexing**:
    - Supports various indexes (single field, compound, text, geo, etc.) for fast lookups.

## Collections in MongoDB
### Collections:

In **MongoDB**, a **collection** is a group of documents, similar to a **table** in relational databases.

- A **collection** holds multiple **documents** (BSON objects).
- All documents in a collection are typically related (e.g., all `users`, all `orders`, etc.).
- Unlike relational tables, collections:
    - **Do not enforce a schema** — documents can have different fields.
    - **Can grow dynamically** as documents are added.

#### Types of Collections in MongoDB

MongoDB primarily supports **two types of collections**:

##### 1. **Regular Collections** (default)
- Most common type.
- Supports full CRUD operations, indexes, and aggregations.
- Schema-flexible.
```
db.users.insertOne({ name: "Alice", age: 30 });
```
##### 2. **Capped Collections**
- Fixed-size collections that **maintain insertion order**.
- Once the size limit is reached, old documents are **overwritten** in insertion order.
- Ideal for **logs**, **real-time analytics**, or **queues**.
```
db.createCollection("logs", { capped: true, size: 10485760 }); // 10MB
```


## Document Structure

MongoDB documents are composed of field-and-value pairs and have the following structure:
```
{
   field1: value1,
   field2: value2,
   field3: value3,
   ...
   fieldN: valueN
}
```
The value of a field can be any of the BSON [data types](https://www.mongodb.com/docs/manual/reference/bson-types/#std-label-bson-types), including other documents, arrays, and arrays of documents.
- `_id` holds an [ObjectId.](https://www.mongodb.com/docs/manual/reference/bson-types/#std-label-objectid)

#### ObjectId
ObjectIds are small, likely unique, fast to generate, and ordered. ObjectId values are 12 bytes in length, consisting of:
- A 4-byte timestamp, representing the ObjectId's creation, measured in seconds since the Unix epoch.
- A 5-byte random value generated once per client-side process. This random value is unique to the machine and process. If the process restarts or the primary node of the process changes, this value is re-generated.
- A 3-byte incrementing counter per client-side process, initialized to a random value. The counter resets when a process restarts.

For timestamp and counter values, the most significant bytes appear first in the byte sequence (big-endian).

In MongoDB, each document stored in a standard collection requires a unique `_id` field that acts as a **primary key**. If an inserted document omits the `_id` field, the MongoDB driver automatically generates an [ObjectId](https://www.mongodb.com/docs/manual/reference/bson-types/#std-label-objectid) for the `_id` field.

## Data Modeling

It’s the process of **structuring your documents and collections** to support your application’s **queries**, **update patterns**, and **scalability** efficiently.
Unlike relational databases (which normalize data into separate tables), MongoDB promotes **embedding related data** together when it makes sense.

### 🏗️ Core Data Modeling Strategies

#### 1. **Embedding (Denormalization)**

✅ Store related data **in the same document**.
Best when:
- Related data is accessed together.
- There’s a **1-to-few** relationship.
- Size remains below Mongo’s **16MB document limit**.
```
// Blog post with embedded comments
{
  "_id": 1,
  "title": "MongoDB Modeling",
  "comments": [
    { "user": "bob", "text": "Great post!" },
    { "user": "jane", "text": "Thanks for sharing" }
  ]
}
```

#### 2. **Referencing (Normalization)**

✅ Store related data in **separate documents** and link using IDs.

Best when:
- There’s a **1-to-many or many-to-many** relationship.
- You need to frequently **update** related data independently.
- Data is **shared** across many documents.
```
// Users collection
{ "_id": 101, "name": "Alice" }

// Orders collection
{ "_id": 201, "userId": 101, "item": "Laptop", "price": 1000 }
```

#### 3. **Bucket Pattern** (Time-series)

Group time-series data points into **buckets** to reduce document count.

```
{
  "sensorId": "abc123",
  "readings": [
    { "ts": "2023-06-16T01:00", "value": 40 },
    { "ts": "2023-06-16T01:05", "value": 41 }
  ]
}
```

#### 🧠 Modeling Guidelines

1. **Design for your read patterns**, not just for write normalization.
2. Use **indexing** to speed up queries (`compound`, `text`, `TTL`, `geospatial`, etc.)
3. Watch for **document size** limits (16MB max).
4. Use **schema validation** to enforce some structure.
5. Favor **embedding** for performance-critical reads and **referencing** for flexibility.


## CRUD Operations

### Create Operations

Create or insert operations add new [documents](https://www.mongodb.com/docs/manual/core/document/#std-label-bson-document-format) to a [collection](https://www.mongodb.com/docs/manual/core/databases-and-collections/#std-label-collections). If the collection does not currently exist, insert operations will create the collection.
In MongoDB, insert operations target a single [collection](https://www.mongodb.com/docs/manual/reference/glossary/#std-term-collection). All write operations in MongoDB are [atomic](https://www.mongodb.com/docs/manual/core/write-operations-atomicity/) on the level of a single [document.](https://www.mongodb.com/docs/manual/core/document/#std-label-bson-document-format)

### Read Operations

Read operations retrieve [documents](https://www.mongodb.com/docs/manual/core/document/#std-label-bson-document-format) from a [collection](https://www.mongodb.com/docs/manual/core/databases-and-collections/#std-label-collections); i.e. query a collection for documents.

### Update Operations

Update operations modify existing [documents](https://www.mongodb.com/docs/manual/core/document/#std-label-bson-document-format) in a [collection](https://www.mongodb.com/docs/manual/core/databases-and-collections/#std-label-collections). In MongoDB, update operations target a single collection. All write operations in MongoDB are [atomic](https://www.mongodb.com/docs/manual/core/write-operations-atomicity/) on the level of a single document.

### Delete Operations

Delete operations remove documents from a collection. In MongoDB, delete operations target a single [collection](https://www.mongodb.com/docs/manual/reference/glossary/#std-term-collection). All write operations in MongoDB are [atomic](https://www.mongodb.com/docs/manual/core/write-operations-atomicity/) on the level of a single document.
### Read Concern

Read concern in databases (not specific to any one) ==defines the consistency and isolation guarantees during read operations==. It controls how up-to-date or consistent the data returned by a read operation is. Different levels of read concern exist, trading off between data consistency and availability.
In distributed databases like MongoDB (especially with replication), data may not be instantly available on all nodes. Read concern helps define _what level of guarantee_ you're asking for when reading.

✅ Why is Read Concern Important?
- Controls **data freshness** and **replica consistency**
- Helps ensure **correctness** in critical reads (e.g., financial transactions)
- Allows for trade-offs between **latency** and **consistency**

🧩 Read Concern Levels in MongoDB

|Read Concern|Description|Use Case|
|---|---|---|
|`local` (default)|Reads from the node's memory, no guarantee it has been replicated.|Fast reads, low consistency needed|
|`available`|Similar to `local`, but doesn't block if no data is available.|Useful in sharded/disconnected setups|
|`majority`|Only returns data **acknowledged by majority of replica set** members.|Durable reads; stronger consistency|
|`linearizable`|Guarantees read reflects **most recent write** (requires `w: "majority"` + primary read).|Strongest consistency; rare use|
|`snapshot`|For **multi-document transactions**, ensures all reads use the same snapshot view.|Used in ACID transactions|

📌 Real-World Use Cases
- **`local`**: Non-critical reads (e.g., logging, analytics dashboards)
- **`majority`**: Reading data you just wrote (e.g., confirm payment success)
- **`snapshot`**: Required in multi-document transactions
- **`linearizable`**: Voting systems or mission-critical single reads

⚠️ Notes & Best Practices
- Read concern **applies per operation or transaction**.
- Using higher levels like `majority` or `linearizable` can **increase latency**.
- For transactions or real-time analytics, prefer `snapshot` or `majority`.
- Default behavior is usually `local`.

### Write Concern

Write concern ==defines the level of acknowledgment a database system provides when a write operation is performed==. It essentially dictates how many nodes in a replicated environment need to confirm the write before the operation is considered successful, balancing data durability with performance.

**Write Concern** defines the **level of acknowledgment** MongoDB requires from the database before considering a write operation successful.
It controls:
- How many replica set members must confirm the write
- Whether the write must be **persisted to disk**
- Whether the write must wait for **journaling**
It’s critical for ensuring **data durability**, especially in distributed systems.

✅ Why is Write Concern Important?

Because MongoDB is a distributed system.
- A write may reach the primary but not yet replicate to secondaries
- Acknowledging the write too early can result in **data loss**
- Higher write concern means more **durability**, but potentially more **latency**

🧩 MongoDB Write Concern Levels

| Write Concern         | Description                                                              |
| --------------------- | ------------------------------------------------------------------------ |
| `w: 0`                | Fire-and-forget. No acknowledgment of success or failure.                |
| `w: 1`                | Acknowledged by the **primary only**. Default.                           |
| `w: "majority"`       | Acknowledged by **majority of replica set members**. Ensures durability. |
| `w: n` (e.g., `w: 2`) | Wait for acknowledgment from `n` members.                                |
| `j: true`             | Wait until the write is committed to the **journal** on disk (safe).     |
| `wtimeout`            | Time in ms to wait for write acknowledgment before giving up.            |

⚠️ Trade-offs

| Write Concern | Durability | Latency  | Risk of Data Loss |
| ------------- | ---------- | -------- | ----------------- |
| `w: 0`        | ❌ None     | ✅ Fast   | ✅ High            |
| `w: 1`        | ⚠️ Low     | ✅ Fast   | ⚠️ Medium         |
| `"majority"`  | ✅ Strong   | ❌ Slower | ❌ Low             |
✅ Best Practices
- Use `w: "majority"` for **critical writes** (e.g., transactions, financial data)
- Use `w: 1` for **faster writes** where occasional loss is acceptable
- Avoid `w: 0` unless absolutely necessary (e.g., logging, performance tests)
- Pair `j: true` with `w` for **maximum safety**


### Atomicity and Transactions

In MongoDB, a write operation is [atomic](https://www.mongodb.com/docs/manual/reference/glossary/#std-term-atomic-operation) on the level of a single document, even if the operation modifies multiple values. When multiple update commands happen in parallel, each individual command ensures that the query condition still matches.
#### Multi-Document Transactions

When a single write operation (e.g. [`db.collection.updateMany()`](https://www.mongodb.com/docs/manual/reference/method/db.collection.updateMany/#mongodb-method-db.collection.updateMany)) modifies multiple documents, the modification of each document is atomic, but the operation as a whole is not atomic.
When performing multi-document write operations, whether through a single write operation or multiple write operations, other operations may interleave.
For situations that require atomicity of reads and writes to multiple documents (in a single or multiple collections), MongoDB supports distributed transactions, including transactions on replica sets and sharded clusters.

## Replication

### 🧱 Replica Set Overview

A **replica set** in MongoDB is a group of `mongod` instances that work together to ensure **data redundancy** and **high availability**. The replica set includes a **primary** node, one or more **secondary** nodes, and optionally **arbiters**. The minimum recommended setup includes **three members** (typically one primary and two secondaries) to ensure fault tolerance and proper election handling. The set can have up to **50 members**, but only **7 voting members** are allowed.

#### 🟢 Primary Node

The **primary** is the only member that can accept **write operations**. It also handles reads by default unless the read preference is changed. Every write made to the primary is also recorded in its **oplog**, which secondaries then replicate. MongoDB ensures there is **only one primary at a time**. If the current primary fails or becomes unreachable, the replica set automatically triggers an **election** to select a new primary.

#### 🔵 Secondary Nodes

**Secondaries** replicate the data from the primary's **oplog** asynchronously to maintain identical datasets. While secondaries do not accept writes, they can serve **read operations** depending on the client's read preference. Secondaries are also **eligible to become primary** during an election.

#### ⚖️ Arbiters

An **arbiter** is a special replica set member that **does not store data** and **cannot become primary**, but **does participate in elections** with one vote. Arbiters are useful in cost-sensitive setups where adding a full data-bearing node isn’t feasible. However, arbiters should not be placed on the same server as a primary or secondary to avoid single points of failure.

#### 🙈 Hidden Replica Set Members

**Hidden members** are part of a MongoDB replica set and maintain a full copy of the primary's data set, but they are **invisible to client applications**. They do not appear in the results of commands like `db.hello()` and are **not eligible to become primary** because they must be configured with **priority 0**. Despite being hidden, they are still **eligible to vote** during replica set elections. Hidden members are useful for isolating workloads with different performance characteristics, such as **dedicated reporting, analytics, or backups**, since they **do not receive regular client read traffic**. To run queries on a hidden node, you must **explicitly connect to it directly**—it won’t be automatically queried as part of a replica set connection string.

#### ⏳ Delayed Replica Set Members

**Delayed members** also maintain a complete copy of the data set, but their state is **intentionally behind** the rest of the replica set by a configured time delay. For instance, if a member has a delay of 1 hour, its data reflects the state of the replica set as it was an hour ago. This delay is implemented by **applying operations from the oplog with a lag**, providing a kind of **rolling backup or historical snapshot**. Delayed members are extremely useful for **recovering from human errors**—such as accidental data deletion or failed application deployments—since they preserve an older view of the data. They offer a practical recovery mechanism without needing to restore from archived backups.

##### 🔄 The data flow in write:
> ✅ **Write → Memory (WiredTiger cache) → Journal → Oplog (if replica set) → Disk (eventually flushed)**

In MongoDB, **the write operation and the creation of the oplog entry happen together, atomically, only if the operation succeeds and modifies data.**

1. **Write → Memory (WiredTiger cache)**:
    - When a write operation is performed, it is first applied to the in-memory data structures within the WiredTiger cache. This is the fastest way to handle writes, as memory access is much quicker than disk access.
2. **Journal**:
    - The journal is a write-ahead log (WAL) that records all write operations. This ensures that even if the server crashes before the data is written to disk, the operations can be replayed from the journal to recover the data. The journal is written to disk periodically (by default, every 100 milliseconds or when the journal file reaches a certain size).
3. **Oplog (if replica set)**:
    - In a replica set, the oplog (operations log) is a special capped collection that records all write operations that modify the data. This allows secondary nodes to replicate the changes from the primary node. The oplog is crucial for maintaining consistency across the replica set.
4. **Disk (eventually flushed)**:
    - Eventually, the data in the WiredTiger cache is flushed to disk. This is done periodically or when certain thresholds are reached. The data is written to the actual data files on disk, ensuring long-term persistence.

❓ Is **Oplog** the Same as **Journal** ?

🚫 **No**, they are **not the same**. They serve **different purposes** in MongoDB.

|Feature|**Oplog**|**Journal**|
|---|---|---|
|Purpose|For **replication** between nodes|For **durability** on the same node|
|Stored in|`local.oplog.rs` (capped collection)|Storage engine journal files|
|Accessed by|**Secondary** nodes|Internal recovery mechanism|
|Content|High-level ops (insert, update...)|Low-level changes to data files|
|Retention|Limited history (e.g., hours–days)|Cleared once data is flushed to disk|
|Format|BSON operation docs|Binary logs specific to storage engine|
In cases where adding a full secondary is too costly, you can add a **MongoDB arbiter** to a replica set. An **arbiter** participates in **elections** to help choose a primary but **does not store any data**.

### Replication Lag and Flow Control

**Replication lag** in MongoDB is the delay between a write on the **primary** and its application on **secondaries** via the **oplog**. Minor lag is acceptable, but large delays can cause issues like **cache pressure** on the primary.

To control lag, **flow control** is enabled by default. It slows down the primary’s write rate as lag nears a threshold (`flowControlTargetLagSeconds`). It does this by requiring writes to acquire **tickets**, thus limiting throughput and helping keep replication lag within acceptable bounds.

### Automatic Failover

When a primary does not communicate with the other members of the set for more than the configured [`electionTimeoutMillis`](https://www.mongodb.com/docs/manual/reference/replica-configuration/#mongodb-rsconf-rsconf.settings.electionTimeoutMillis) period (10 seconds by default), an eligible secondary calls for an election to nominate itself as the new primary. The cluster attempts to complete the election of a new primary and resume normal operations.

The replica set cannot process write operations until the election completes successfully. The replica set can continue to serve read queries if such queries are configured to [run on secondaries](https://www.mongodb.com/docs/manual/core/read-preference/#std-label-replica-set-read-preference) while the primary is offline.

### 📖 Read Preferences and Staleness

By default, read operations go to the **primary**, but clients can specify **read preferences** to route reads to secondaries. While this helps distribute load, it comes at the cost of **potentially stale data**, since secondaries may not have caught up to the primary due to replication lag.

### 🔒 Transactions and Consistency

In **multi-document transactions**, all reads and writes must be routed to the same node (typically the primary). Data changes inside a transaction remain **invisible to other clients until commit**. Once committed, the changes are atomically visible. However, in multi-shard setups, depending on the read concern, it's possible for parts of the transaction to be visible on some shards before others, due to asynchronous propagation.


### 🔥 Mirrored Reads for Cache Warm-up

To improve performance after **failovers**, MongoDB supports **mirrored reads**. These are read operations that the primary node "mirrors" to **electable secondaries** to **pre-warm their caches**. This helps newly promoted primaries handle queries more efficiently. Mirrored reads are "fire-and-forget" and do not affect the client-facing behavior of the primary. The sampling rate of mirrored reads is configurable using the `mirrorReads` parameter.

### ✅ Conclusion

MongoDB's replication model offers scalability and high availability, but introduces trade-offs around **data consistency and freshness**. These are managed using **read preferences**, **read concerns**, and **transactions**. Additionally, **mirrored reads** help optimize secondary readiness, especially during failover transitions.


### 🔁 Replica Set Data Synchronization in MongoDB

MongoDB ensures that all nodes in a replica set remain synchronized using two core methods: **initial sync**, which is used to onboard new nodes with the full dataset, and **replication**, which applies ongoing changes from the primary to secondaries using the oplog.

#### 1. Initial Sync
Initial sync is the process of copying all data from a source replica set member (usually the primary or another secondary) to a new or resyncing node. There are two types of initial sync processes: **logical** and **file copy based** (the latter is available only in MongoDB Enterprise).
#### 2. Ongoing Replication
Once initial sync is complete, the member begins ongoing replication. This process involves continuously fetching and applying **oplog** entries from the source member to stay in sync with changes happening in the replica set. This ensures data consistency across the cluster and allows secondaries to be promoted to primary if needed.

#### ⚠️ Fault Tolerance
MongoDB includes fault tolerance mechanisms to handle issues during initial sync. If a **persistent network error** occurs, the initial sync will **restart completely** from the beginning. For **temporary disruptions** like brief network drops or a collection rename, MongoDB can attempt to **resume** the initial sync without starting over. This resilience ensures smoother recovery and minimizes wasted compute cycles in case of minor interruptions.

## Sharding

**Sharding** is a technique used by MongoDB for **horizontal scaling**—the process of distributing large datasets across multiple machines to ensure performance and storage efficiency. It allows MongoDB to support deployments with **very large data volumes** and **high throughput requirements** by breaking up data into manageable pieces. Sharding in MongoDB is at the _collection level_, not at the database or document level.

#### 🗃️ Sharded Cluster Components

A **MongoDB sharded cluster** is composed of three main types of components:
- **Shards**: These are the actual data-bearing nodes in the cluster. Each shard contains a subset of the total data and is itself deployed as a **replica set** to ensure high availability and fault tolerance.
- **`mongos` Routers**: These act as **query routers**. They receive client requests and route them to the appropriate shards based on the shard key and cluster metadata.
- **Config Servers**: These hold metadata and configuration details for the cluster, including shard key mappings and chunk distribution. Config servers are also deployed as a **replica set** (called **Config Server Replica Set or CSRS**) to maintain consistency and availability.

#### 🧭 Shard Keys

To distribute documents among shards, MongoDB uses a **shard key**, which is a specific field (or fields) in each document. The value of this shard key determines where the document resides in the cluster. If a document lacks the shard key field, MongoDB treats its value as `null` for distribution purposes (but not for query routing). The shard key plays a **central role in balancing, routing, and indexing**.

MongoDB requires a **shard key index**—that is, an index beginning with the shard key field—before it allows a collection to be sharded. If the collection is empty, MongoDB can auto-create this index if it's missing.

#### ⚖️ Balancing and Migration

MongoDB includes an automated background process known as the **balancer**. Its job is to ensure **even data distribution** by monitoring the size of each shard's data. When it detects imbalance—where one shard holds significantly more data than another—it **migrates chunks** from one shard to another. This process respects **zones**, if defined (i.e., geographic or regulatory data placement rules), and adheres to **migration thresholds** to decide when to take action. The balancing activity is **transparent** to client applications, although it might temporarily affect performance.

#### Advantages of Sharding

##### ⚡Read and Write Scalability

MongoDB improves **read and write performance** through **horizontal scaling** by distributing operations across multiple shards in a sharded cluster. Each shard handles a portion of the workload, allowing the system to scale out efficiently. When a query includes the **shard key** (or the **prefix of a compound shard key**), the query router (`mongos`) can **target specific shards**, making these operations fast and efficient. In contrast, if the shard key is not included in the query, `mongos` must **broadcast the query to all shards**, resulting in **scatter/gather operations** that are typically **slower and more resource-intensive**.

##### 🗃️ Storage Scalability

Sharding distributes documents across shards based on the shard key, enabling each shard to store only a **subset of the total dataset**. As your application grows and data increases, you can **add more shards**, effectively **scaling out storage capacity** without rearchitecting the system.

##### 💡 High Availability

MongoDB enhances availability through the use of **replica sets** for both shards and config servers. This setup ensures that even if **one or more shard replica sets become unavailable**, the cluster can still **continue to serve partial read and write operations**. Although access to data on the unavailable shard is restricted, the cluster remains **operational** for all other available shards, thereby improving fault tolerance and uptime.


#### 🔀 Sharding Strategies

MongoDB provides **two primary sharding strategies** to distribute data across a sharded cluster: **Hashed Sharding** and **Ranged Sharding**. Each strategy uses a different mechanism to allocate data chunks across shards, impacting performance, data distribution, and query efficiency based on the shard key used.

##### 🧮 Hashed Sharding

Hashed sharding works by computing a **hash of the shard key field's value**, ensuring an even and uniform distribution of documents across all shards. MongoDB automatically handles the hash computation internally—client applications don’t need to compute it themselves. Because hashed values are randomized, **documents with close shard key values often get distributed to different chunks** and therefore across different shards. This makes hashed sharding particularly suitable for datasets with **monotonically increasing shard keys**, such as timestamps or sequential IDs, where range-based strategies could cause write hotspots. However, a key trade-off is that **range queries on the shard key become inefficient**, as the required data is likely to be scattered across multiple shards, triggering **broadcast operations**.

##### 📊 Ranged Sharding

Ranged sharding divides the data into **contiguous ranges based on the shard key**. The shard key can be a single indexed field or a compound index involving multiple fields. This approach ensures that **documents with similar shard key values are grouped within the same chunk**, which often resides on a single shard. Consequently, **range queries are more efficient**, since `mongos` can route them directly to the relevant shard(s), avoiding unnecessary scatter/gather queries. The efficiency of this strategy heavily depends on **choosing a good shard key**. Poorly chosen shard keys may lead to **data skew**, where one shard holds most of the data and receives the bulk of the traffic, defeating the purpose of load distribution.

##### ⚖️ Hashed vs. Ranged: When to Use What?

For collections with a **monotonically increasing shard key** like an auto-incrementing ID or a timestamp, ranged sharding can lead to **write concentration** on a single shard (e.g., the shard holding the chunk with the upper bound of `MaxKey`). This setup severely restricts distributed writes. In contrast, using a **hashed shard key in such scenarios leads to a more balanced write distribution**, as hash-based placement randomizes the shard selection. Therefore, hashed sharding is ideal for **write-heavy workloads with skewed or sequential shard key patterns**, while ranged sharding is preferred for **read-heavy workloads** that rely on **range-based queries** and have more uniform key distribution.

#### 🗺️ Zones Overview

**Zones** in MongoDB sharded clusters allow for greater control over **data locality**, especially for clusters that span **multiple data centers or geographic regions**. A zone is essentially a logical grouping that maps a **specific range of shard key values** to designated shards. This enables organizations to **co-locate data** closer to the users or applications that need it, improving latency and meeting data residency requirements.

##### 🧩 Zone-to-Shard Association

Each **zone** can be associated with **one or more shards**, and likewise, a single shard can belong to **multiple zones**. When the cluster is balanced, MongoDB ensures that **chunks falling within a zone's key range are only migrated to shards associated with that zone**. This means that MongoDB respects the zone mappings during internal chunk migrations, maintaining data consistency and locality according to the configured zone rules.

##### 🔑 Zone Ranges and Shard Keys

Zones operate based on **ranges of shard key values**, and each range is **inclusive of the lower bound and exclusive of the upper bound**. When defining a range for a zone, it is mandatory to use fields that are part of the **shard key**. For **compound shard keys**, the zone range must include at least the **prefix** of the compound key. This ensures that the range mapping aligns correctly with the way MongoDB partitions and manages data in chunks.

##### 📌 Purpose and Use Case

Zone sharding is especially useful in **multi-region deployments**, where keeping specific users' data within a regional shard improves performance and satisfies compliance requirements. It offers a flexible way to maintain **data locality**, optimize **query performance**, and control **data distribution policies** within a globally distributed sharded cluster.

#### Index and Shard Key

##### 📌 What is an Index in MongoDB?
An **index** in MongoDB is a **data structure** that improves the **speed of queries** on a collection.
- Just like a book index helps you find topics quickly, a MongoDB index helps the database find documents faster.
- Without an index, MongoDB must scan every document in a collection (called a **collection scan**).
- With indexes, MongoDB can quickly locate the matching documents by narrowing down the search space.

##### ✅ Common Types of Indexes:
- **Single-field index**: `{ name: 1 }` — for fast lookup by `name`
- **Compound index**: `{ age: 1, gender: -1 }` — uses both fields in a specific order
- **Multikey index**: for indexing arrays
- **Text index**: for full-text search
- **Geospatial index**: for location-based queries

##### 🔗 What is the Relationship Between Index and Shard Key?

A **shard key is _always indexed_**.
- The **shard key** determines how data is **distributed across shards** in a sharded MongoDB collection.
- When you shard a collection, **MongoDB automatically creates an index on the shard key** to:
    - Support fast lookups
    - Allow the **query router (mongos)** to route queries to the correct shard

###### 💡 Important Points:
- If you're sharding by `{ userId: 1 }`, MongoDB creates an index on `userId`.
- If you want to query efficiently by another field (e.g., `email`), you must **manually create an index** on it.
- In a sharded cluster, **queries that don't use the shard key** may result in **scatter-gather** queries — hitting all shards — which is slower.

##### 🧩 Shard Key Index Requirements
For sharded collections, MongoDB typically requires the presence of an index that supports the **shard key**. This index could be a simple index on the shard key itself or a **compound index** where the shard key appears as a **prefix**. If the collection is empty, the `sh.shardCollection()` command automatically creates an index on the shard key if none exists. However, if the collection already has data, the appropriate index must be manually created before sharding can proceed.

##### 🚫 Index Restrictions and Rules
Once the shard key index is in place, it cannot be **dropped** or **hidden** if it's the only non-hidden index that supports the shard key. This is essential to ensure the stability of the sharding infrastructure and consistent query routing.

## Storage Engine

The [storage engine](https://www.mongodb.com/docs/manual/core/storage-engines/#std-label-storage-engines) is the primary component of MongoDB responsible for managing data. MongoDB provides a variety of storage engines, allowing you to choose one most suited to your application.

### WiredTiger Storage Engine

The WiredTiger storage engine is the default storage engine.

#### 1. Concurrency and Locking

WiredTiger enables high concurrency with document-level locking, allowing multiple clients to write to different documents simultaneously. It uses intent locks for global, database, and collection levels, while also relying on optimistic concurrency control to detect and resolve write conflicts by transparently retrying them. Global or exclusive locks are still needed for certain operations like `renameCollection`

#### 2. Snapshots and Checkpoints

WiredTiger uses MVCC (Multi-Version Concurrency Control) to provide point-in-time snapshots for operations, ensuring a consistent in-memory view. Periodically, MongoDB writes a durable snapshot (checkpoint) to disk every 60 seconds. Even if the process crashes during a new checkpoint, the previous checkpoint remains valid and usable for recovery.

#### 3. Journaling and Recovery

To support durability, MongoDB also uses a journal (write-ahead log) in addition to checkpoints. It records all write operations, including index changes, in memory before flushing them. If MongoDB crashes between checkpoints, it uses the journal to replay changes since the last checkpoint.

#### 4. Compression

WiredTiger supports compression to reduce storage usage. By default, it uses `snappy` for collections and prefix compression for indexes. Additional options like `zlib` and `zstd` are also available for collections. This reduces disk usage but increases CPU consumption.


## Transactions

MongoDB offers robust **multi-document transaction support**, similar to traditional relational databases, allowing **ACID** (Atomicity, Consistency, Isolation, Durability) guarantees across one or more documents—even across multiple collections and shards.

### 🔹 1. Types of Transactions

#### a. Single-Document Transactions
- MongoDB operations on a **single document** (e.g., inserts, updates, deletes) are **always atomic**, even without using transactions.
- This behavior has existed since early MongoDB versions.

#### b. Multi-Document Transactions
- Starting from **MongoDB 4.0** (replica sets) and **4.2+** (sharded clusters), MongoDB supports **multi-document ACID transactions**, like those in SQL databases.
- These transactions can span:
    - Multiple documents
    - Multiple collections
    - Multiple databases
    - And, in sharded clusters, across multiple shards

MongoDB’s **Transactions API** provides a callback-based approach that handles transaction lifecycle automatically: it starts a transaction, performs operations, and commits or aborts on errors. Built-in retry logic handles transient errors like `TransientTransactionError` or `UnknownTransactionCommitResult`.

Transactions are tied to **sessions**, and all operations in a transaction must use the same session and explicitly pass it. Only **one transaction can be active per session**, and if the session ends while a transaction is open, the transaction is aborted.

Distributed transactions in MongoDB (across replica sets or shards) are **atomic**—all operations succeed or none do. Until commit, changes are **invisible** to outside operations. Once committed, data may be partially visible across shards depending on the read concern level.

All operations in a transaction must use:
- `readConcern` = `"snapshot"`
- `writeConcern` = typically `"majority"`
- `readPreference` = `"primary"`

MongoDB also supports **creating collections and indexes** within transactions, both explicitly and implicitly.

## CDC 

### 🔄 What is CDC in MongoDB?

**Change Data Capture (CDC)** is a mechanism to **capture changes** (inserts, updates, deletes) that happen in your MongoDB collections and make them **available for downstream processing**, such as syncing with other systems, triggering business logic, analytics, or feeding into message queues (like Kafka).
MongoDB provides CDC functionality primarily through the **Change Streams API**, introduced in version **3.6** and enhanced in later versions.

### 🔍 How Does CDC Work in MongoDB?

MongoDB’s CDC relies on the **oplog** (operation log), which is used for replication in replica sets. The **Change Streams API** taps into this oplog to surface real-time data changes without polling.
#### ✅ Prerequisites:
- You must be running a **replica set** (or a **sharded cluster**).
- The MongoDB version should be **3.6+** (preferably 4.0+ for full support).

### 📘 Key Concepts

#### 1. **Change Streams**
- Allow applications to subscribe to real-time changes in a **collection**, **database**, or the **entire deployment**.
- Supported operations: `insert`, `update`, `replace`, `delete`, and `invalidate`.
- Delivered changes are **idempotent** and **ordered**.
#### 2. **Resume Tokens**
- Each change event includes a **resume token**, which lets consumers **resume** from the last seen change if they get disconnected or crash.
- Stored in `_id` of the change event.
#### 3. **Full Document Lookup**
- For `update` events, only the changed fields are returned unless `fullDocument: 'updateLookup'` is specified, in which case the entire post-update document is returned.

### 🚀 Use Cases

- **Event-driven architectures** (e.g., microservices using Kafka)
- **Search index sync** (e.g., MongoDB to Elasticsearch)
- **Real-time analytics dashboards**
- **Audit logging**
- **Data replication** to other data stores (data lakes, warehouses, etc.)

### ⚠️ Considerations

- **Resource Usage**: Reading from the oplog can have an impact on memory and I/O if not managed well.
- **TTL**: The oplog has a **retention window**; resume tokens are only valid while changes remain in the oplog.
- **Sharded Clusters**: Starting in MongoDB 4.2, change streams support watching across **sharded clusters**.
- **Security**: Requires appropriate roles like `readChangeStream`.
## Q & A

### How do elections happen in MongoDB?

In **MongoDB**, elections occur in **replica sets** to determine which node should serve as the **primary**. Elections ensure high availability and fault tolerance.

Here’s how elections work in detail:

#### Why Elections Happen

MongoDB replica sets have **one primary** and one or more **secondaries**. The primary handles **all write operations**. If the primary becomes unavailable (due to crash, network failure, etc.), an **election is triggered** so a new primary can take over.

#### Election Process

MongoDB uses the **Raft-inspired consensus algorithm** starting from version 4.2 (previous versions used a custom algorithm).

**Steps:**

1. **Detection of Primary Failure:**    
    - Secondaries use heartbeat messages (every 2 seconds) to check if the primary is alive.
    - If a node doesn’t hear from the primary for **10 seconds**, it marks the primary as **down**.
2. **Start of Election:**
    - An eligible secondary nominates itself as a candidate.
    - It increments its **term** and requests votes from other members.
    - Each member can vote **once per term**.
3. **Voting:**
    - Members vote for the candidate if:
        - They haven’t already voted in this term.
        - The candidate’s **oplog is as up-to-date or more recent** than theirs.
4. **Election Win:**
    - The candidate becomes **primary** if it receives votes from a **majority** of the replica set.
    - It then starts accepting writes.

#### Election Rules & Constraints

- **Priority:** Nodes can have a priority setting. Higher-priority nodes are more likely to become primary.
- **Voting Rights:** Max 7 voting members in a replica set (though you can have more non-voting secondaries).
- **Arbiter:** A special node that only votes in elections (no data storage).
- **Rollback:** If the new primary’s data is ahead of other nodes, some operations may be rolled back to maintain consistency.

#### Election Scenarios

- **Step down**: An admin can force the primary to step down (rs.stepDown()), triggering an election.
- **Network partitions**: If a minority partition loses contact with the majority, they cannot elect a primary.

#### Commands & Logs

- Use rs.status() to view the replica set state.
- Election events are visible in the logs with terms and voting results.



### Explain Distributed Transactions in MongoDB.

Distributed transactions in **MongoDB** allow **multi-document, multi-collection, and even multi-shard ACID-compliant transactions** — similar to transactions in traditional RDBMS like PostgreSQL or MySQL.

#### 1. What Are Distributed Transactions?

In MongoDB, a **distributed transaction** is a transaction that spans:
- **Multiple documents**
- **Multiple collections**
- **Multiple databases**
- **Multiple shards** (in a sharded cluster)

These transactions ensure **ACID guarantees** (Atomicity, Consistency, Isolation, Durability) even in distributed environments.

#### 2. When Are They Needed?

MongoDB operations are atomic at the **document level** by default. But you need distributed transactions when:
- You update **multiple documents** that must be kept consistent together.
- You modify **multiple collections** or **shards** in a way that requires strict consistency.

#### 3. How It Works (High-Level)

MongoDB uses a **two-phase commit protocol** under the hood to coordinate distributed transactions.
##### Transaction Lifecycle:
1. **Start a session**:
    - Every transaction runs in the context of a logical session (startSession()).
2. **Begin the transaction**:
    - session.startTransaction()
3. **Perform operations**:
    - insert, update, delete, find — just like normal operations.
4. **Commit or Abort**:
    - session.commitTransaction() — all changes are committed atomically.
    - session.abortTransaction() — rollback if any part fails.

#### 4. Two-Phase Commit (Simplified)

1. **Phase 1: Prepare Phase**
    - Each shard participating in the transaction **writes changes to a local transaction log (WiredTiger)** but doesn’t apply them yet.
    - Shards respond with “ready to commit”.
2. **Phase 2: Commit Phase**
    - If all participants agree, the coordinator shard sends a **commit** message.
    - All shards **apply the transaction** and make changes durable.

If any shard fails before committing, the coordinator aborts and rolls back.

#### 5. Durability & Rollback

- Changes are journaled before commitTransaction returns.
- If there’s a crash or network partition, **MongoDB can recover** and **either complete or roll back the transaction** safely.

#### 6. Best Practices & Limitations

**✅ Best Practices:**
- Keep transactions **short-lived**.
- Use **retryable writes** and **retry logic** for commitTransaction().
- Monitor with db.currentOp() or transactions metrics.

**⚠️ Limitations:**
- **Performance**: Transactions are heavier than single-document operations.
- **Timeout**: Transactions auto-abort after 60 seconds by default.
- **Write conflicts**: Document-level locks may block concurrent operations.
- **Retry logic required**: Transient errors or failovers must be handled gracefully.

#### 7. Example

javascript:
```
const session = db.getMongo().startSession();
const txn = session.startTransaction();

try {
  const usersColl = session.getDatabase("app").users;
  const ordersColl = session.getDatabase("app").orders;

  usersColl.updateOne({ _id: 1 }, { $inc: { balance: -50 } });
  ordersColl.insertOne({ userId: 1, item: "Book", price: 50 });

  session.commitTransaction();
} catch (e) {
  session.abortTransaction();
  throw e;
}
```

#### 8. Sharded Cluster Considerations

In a sharded cluster:
- The **first shard** accessed becomes the **coordinator shard**.
- The config.transactions collection tracks state across shards.
- All participating shards must be MongoDB 4.2+ and run WiredTiger.


### How does MongoDB handles distributed transactions internally, especially focusing on oplog coordination, transaction retry patterns, and internal state management.

#### 1. Oplog Coordination in Transactions

MongoDB’s **oplog** (operations log) is a key component of replication and is also involved in transactions.

**How oplog works in transactions:**
- During a transaction, writes are **not immediately added to the oplog**.
- Instead, MongoDB **buffers changes in memory** until commitTransaction() is called.
- On commit, a **single oplog entry** of type applyOps is written, representing **all operations** in the transaction.

**Example oplog entry (simplified):**
json:
```
{
  "ts": Timestamp(1685900000, 1),
  "t": 2,
  "h": NumberLong("123456789"),
  "v": 2,
  "op": "c",
  "ns": "admin.$cmd",
  "o": {
    "applyOps": [
      { "op": "u", "ns": "app.users", "o": { "$set": { "balance": 50 } }, "o2": { "_id": 1 } },
      { "op": "i", "ns": "app.orders", "o": { "_id": 99, "userId": 1, "item": "Book" } }
    ],
    "partialTxn": false
  }
}
```

If a transaction spans multiple shards, each shard writes its own applyOps entry with the same transaction ID (logical session id + txn number).


#### 2. Transaction Retry Patterns

MongoDB distributed transactions may fail due to:
- Network partitions
- Transient errors
- Primary failovers
- Write conflicts

**Pattern to Handle:**

You should retry the entire transaction block on failure, **but only if the error is transient**.

**Retryable Error Handling (pseudocode):**
javascript:
```
while (true) {
  try {
    session.startTransaction();
    // Do writes...
    session.commitTransaction();
    break;
  } catch (e) {
    if (isTransientTransactionError(e)) {
      // retry the whole transaction
    } else {
      session.abortTransaction();
      throw e;
    }
  }
}
```
MongoDB drivers provide a helper like isTransientTransactionError(err) to simplify this.

#### 3. Internal Transaction State Management

MongoDB stores transaction state in:

**config.transactions**

**Collection:**
- Located in the config database.
- Tracks the **current state** of each transaction for a session:
    - sessionId
    - txnNumber
    - lastWriteOpTime
    - state: inProgress, committed, aborted
- This is crucial for **recovery, retries**, and **multi-shard coordination**.

**Per-shard transaction metadata:**
- Each shard maintains in-memory structures for active transactions.
- On commit, MongoDB ensures **write durability** (WiredTiger journal + oplog entry) before acknowledging success.

#### 4. Multi-shard Coordination

MongoDB uses a **coordinator shard** in sharded clusters:
- The **first shard** involved becomes the **coordinator**.
- It communicates with other shards using internal coordinateCommitTransaction messages.
- It drives the **two-phase commit protocol**.

This coordination is **resilient to failures**:
- If the coordinator crashes before commit → the transaction is aborted.
- If after commit → others will commit as part of recovery (via transaction metadata).

#### Advanced: What Happens During Failover?

If a primary fails during a transaction:
- The transaction state (config.transactions) ensures secondaries can **recover in-progress transactions**.
- If the transaction was **not committed**, it is rolled back.
- If it **was committed**, secondaries **apply the oplog entry** and finalize it.


### What does MongoDB use for storing data? Does it use B-Tree?

Yes, **MongoDB uses a variation of B-Tree**, but not directly. Here’s a clear breakdown of what MongoDB uses for **data storage and indexing**:

#### 1. Storage Engine: WiredTiger

As of MongoDB **3.2+**, the **default storage engine is WiredTiger**. This engine is responsible for:
- Managing how data is stored on disk
- Caching, journaling, compression
- Implementing **data and index structures**

#### 2. Index Structure: B+ Tree

MongoDB indexes (including the _id index and any user-defined ones) are implemented using a **B+ Tree** structure.

**Properties of B+ Tree (used in MongoDB):**
- All values are stored at the **leaf nodes**.
- Internal nodes store **keys** to guide traversal.
- Leaves are linked for efficient **range queries**.
- Provides **logarithmic time complexity** for search, insert, and delete.

js:
`db.users.createIndex({ age: 1 })`
This will create a **B+ Tree**-based index on the age field, sorted in ascending order.

#### 3. Data Storage Format

**MongoDB stores documents as:**
- **BSON** (Binary JSON) format
- Documents are grouped into **extents**, and then pages, within WiredTiger files

**Layout:**
- **Data files**: .wt files managed by WiredTiger
- **Indexes**: Also stored as .wt files, using B+ Tree internally

#### 4. Other Key Components

|**Component**|**Technology Used**|**Notes**|
|---|---|---|
|**Indexes**|B+ Tree|Default for single field and compound indexes|
|**Data compression**|Snappy / zlib / zstd|Configurable at collection or index level|
|**Concurrency**|Document-level locking|High concurrency compared to earlier MMAPv1 engine|
|**Journaling**|Write-ahead log|Ensures durability before commit|

#### Did MongoDB ever use B-Trees?

Earlier MongoDB storage engines (like **MMAPv1**) also used B-Trees for indexes, but they lacked fine-grained concurrency and modern compression. WiredTiger, while still using B+ Trees for indexes, offers far superior performance and flexibility.

#### Summary

|**Feature**|**Used in MongoDB?**|**Technology**|
|---|---|---|
|B-Tree|❌ (not directly)|Instead, B+ Tree is used|
|B+ Tree|✅|For indexes|
|LSM Trees|❌|Not used in MongoDB|
|Storage Engine|✅|WiredTiger (default since 3.2)|
|Data Format|✅|BSON|


### In case of MongoDB, where is the index of the sharded clusters stored? How does the mongos router know which shards to query in the cluster for the data?

In a **sharded MongoDB cluster**, index and routing metadata are **distributed and coordinated carefully** across several components. Here’s a detailed explanation of **where indexes are stored** and **how mongos determines which shards to query**:

#### 1. Where Are Indexes Stored in a Sharded Cluster?**

Each **shard** in MongoDB is a **regular replica set**, and:
- Indexes are **local to each shard**.
- When you create a collection or index in a sharded collection, **each shard builds and maintains its own copy** of that index **on its portion of the data**.

**Example:**

If you create:
`db.orders.createIndex({ customerId: 1 })`

MongoDB ensures that this index is created **on each shard** for the collection orders.

> 💡 The index is **not globally shared**, but **replicated independently on each shard** that holds part of the data.

#### 2. How Does mongos Know Which Shards to Query?

**Key Components:**
- **mongos**: The stateless query router.
- **config servers**: Store metadata about sharded collections, chunks, and shard key ranges.

**Key Concepts:**
- **Shard key**: A field (or compound fields) used to partition the data.
- **Chunks**: Contiguous ranges of shard key values assigned to specific shards.

**Process:**
**a. On startup, mongos loads metadata from the config servers:**
- What databases exist
- What collections are sharded
- What the **shard key** is
- Which chunks exist and which **shard owns which chunk**

**b. On query, mongos uses this metadata to:**
- Parse the query
- Check if it contains a shard key
- Use the config metadata to determine **which shards may contain matching documents**

**c. Based on this:**
- If the shard key is present → **targeted query** to one shard
- If no shard key → **scatter-gather** query to all shards

**Example**

**Given:**

`sh.shardCollection("shop.orders", { customerId: 1 })`

This partitions orders collection across shards using customerId.

**Config metadata (simplified):**

```
[
  { min: { customerId: 0 }, max: { customerId: 1000 }, shard: "shard1" },
  { min: { customerId: 1000 }, max: { customerId: 2000 }, shard: "shard2" }
]
```

**Query:**

`db.orders.find({ customerId: 1200 })`

→ mongos consults the config metadata and sends the query **only to shard2**.

**No shard key:**

`db.orders.find({ status: "pending" })`

→ mongos sends the query to **all shards** (scatter-gather), then merges the results.


**Summary Table**

|**Component**|**Role**|
|---|---|
|mongos|Parses query, routes to shards based on shard key and config metadata|
|Config servers|Hold metadata: shard keys, chunk ranges, shard mapping|
|Shards|Store data and indexes for their chunk ranges|
|Index location|Local to each shard|


### Explain Mongo multi-region multi-cluster deployment. Do they have their own separate primaries?

Yes — in a **MongoDB multi-region, multi-cluster deployment**, **each cluster typically has its own primary**. Let’s break it down clearly and explain how MongoDB handles **multi-region architecture**, including the role of **primaries, replication, failover, and data locality**.

#### What is a Multi-Region, Multi-Cluster MongoDB Deployment?

This architecture involves:
- **Multiple MongoDB replica sets or sharded clusters** deployed in **different geographic regions**.
- Each replica set or cluster is often **autonomous**, with **its own primary** node.
- Used for **global applications** to improve:
    - **Latency** (read/write close to users)
    - **High availability** (disaster recovery)
    - **Compliance** (data sovereignty)

#### Two Common Deployment Models

**1.** **Global Replica Set (Single Cluster, Multi-Region)**

- One **replica set** spans multiple regions.
- **One global primary** (e.g., in the US), with secondaries in other regions.
- **Writes go to the primary**, then are asynchronously replicated.
- **Reads can be local** (using read preference like nearest or secondary preferred).

**🔁 Primary is global:**
```
+--------------+     +----------------+     +--------------+
| Secondary IN | <-- |  Primary US     | --> | Secondary EU |
|    India     |     | (Write Region)  |     |   Germany    |
+--------------+     +----------------+     +--------------+
```

**Pros:**
- Simple setup.
- Strong consistency.

**Cons:**
- All writes go to one region → latency for global users.
- Failover across regions can take longer.

**2. Multiple Independent Replica Sets (Multi-Cluster)**

- Each region has a **separate replica set or cluster**.
- Each cluster has **its own primary** — i.e., local writes.
- Applications may sync data between clusters (e.g., using **MongoDB Atlas Global Clusters** or custom CDC logic).

**🧭 Example:**
```
Region A (India):   Replica Set A → Primary in Mumbai
Region B (US):      Replica Set B → Primary in Virginia
Region C (EU):      Replica Set C → Primary in Frankfurt
```
- Users in each region read/write to their **local cluster**.
- Data is **geo-partitioned or synchronized** across clusters.

**Pros:**
- Ultra-low latency for local reads/writes.
- Regional data sovereignty

**Cons:**
- **Data sync across clusters is complex** (eventual consistency or conflict resolution needed).
- More operational overhead.


**MongoDB Atlas Global Clusters (Managed Solution)**

MongoDB Atlas (the managed DBaaS) provides a built-in way to support **multi-region write-aware clusters**:

**Key Features:**
- You can assign **zones** (regions) to shards.
- Automatically routes reads/writes based on **user location** and **shard key**.
- Each **zone has its own primary for the subset of data it owns** (via shard partitioning).

**Example:**
```
User in India → Writes to zone Asia
User in US    → Writes to zone US
```
Each region **owns a chunk** of data (based on shard key ranges like country or user location).


#### Considerations

|**Topic**|**Global Replica Set**|**Multi-Cluster Setup**|
|---|---|---|
|Primaries|One (global)|Multiple (regional)|
|Write latency|High (remote users)|Low (local writes)|
|Data sync|Automatic via replication|Manual or Atlas-managed|
|Failure isolation|Partial (failover needed)|High (each region isolated)|
|Consistency model|Strong (with delay)|Eventual (if syncing data)|
|Use case|Strong consistency|Low-latency & availability|
