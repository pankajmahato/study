## Overview
### ✅ **What is CDC?**

**Change Data Capture (CDC)** is a technique used to **track and capture changes** (like inserts, updates, and deletes) made to data in a database and **deliver those changes in real-time or near real-time** to downstream systems — without having to repeatedly query the database for the entire dataset.

There are various ways to capture the changes in a database, including polling for last updated columns and having database triggers, but we will focus on detection of transaction log changes. All changes to a transactional database will be stored in transaction logs; any update to these logs (i.e., any changes to the database) will be detected and captured by the CDC system, which will then propagate those changes to the relevant downstream systems. With this method, data changes are captured in real time and there is little to no performance impact as there is no additional overhead on the database.

### 🧠 **Why Use CDC?**

- **Avoids polling** the whole table repeatedly
- Keeps downstream systems like **search indexes, data lakes, caches, or analytics** in sync
- Supports **event-driven architecture** and **real-time streaming**
- Helps with **audit trails**, **replication**, and **ETL pipelines**

### 🔁 **What Changes Are Captured?**

- **Insertions**
- **Updates** (what changed, old and new value)
- **Deletions**

### 🔧 **How CDC Is Implemented**

| Approach                    | Description                                             | Examples                                                                  |
| --------------------------- | ------------------------------------------------------- | ------------------------------------------------------------------------- |
| **Database-native CDC**     | Built-in support in the database engine                 | PostgreSQL (`logical decoding`), SQL Server, MySQL binlog, Cassandra 4.0+ |
| **Transaction log reading** | Read DB transaction logs (e.g., WAL, binlogs)           | Debezium, Maxwell's Daemon                                                |
| **Trigger-based CDC**       | Use SQL triggers to write changes to a log/change table | Custom, but adds overhead                                                 |
| **Timestamp-based polling** | Periodically query rows with a `last_updated` timestamp | Simple, but not truly real-time                                           |

### 📦 **Popular Tools That Use CDC**

- **Debezium**: Open-source platform for streaming changes from databases like PostgreSQL, MySQL, MongoDB
- **Kafka Connect**: With Debezium connector for CDC
- **Apache Flink / Spark Streaming**: For processing CDC events in real-time
- **AWS DMS**: Supports CDC for replicating data across systems

### 🧭 **Common Use Cases**

- **Replicating data** from one DB to another
- **Feeding real-time analytics pipelines**
- **Maintaining cache systems (e.g., Redis, Elasticsearch)**
- **Event sourcing and microservices communication**



## Kafka Connect

### 🧩 What is Kafka Connect?

**Kafka Connect** is a **framework** provided by Apache Kafka for **integrating Kafka with external systems** like databases, key-value stores, search indexes, cloud storage, etc.
It lets you **stream data into and out of Kafka** with minimal effort — using reusable **connectors** — without writing custom code.
**Kafka Connect** is a **standalone service** (a separate Java process) that runs **outside of the main Kafka brokers**.

Kafka Connect is **bundled with Apache Kafka** (since version 0.9.0). When you download and extract Apache Kafka, you'll find the `connect` related components in the Kafka distribution.

Directory Structure (in a Kafka download):
```
kafka/
├── bin/
│   ├── connect-distributed.sh   <-- for distributed mode
│   ├── connect-standalone.sh    <-- for standalone mode
├── config/
│   ├── connect-standalone.properties
│   ├── connect-distributed.properties
├── libs/
│   └── (Kafka Connect JARs and dependencies live here)
```
So, Kafka Connect is **already part of the Kafka distribution**, but:
- It’s **launched separately** using `connect-standalone.sh` or `connect-distributed.sh`.
- It runs its own JVM process.
- You configure it using `.properties` files.

### 🛠️ Why Use Kafka Connect?

Traditionally, if you wanted to move data between Kafka and, say, a PostgreSQL database or Elasticsearch, you had to write a custom Kafka producer or consumer.
Kafka Connect removes that need by providing a **pluggable and scalable system** where you:
- **Configure** a connector
- **Deploy** it to the Connect cluster
- And it automatically handles the data movement

### 🔁 What Can Kafka Connect Do?

| Direction             | Example                                               |
| --------------------- | ----------------------------------------------------- |
| **Source Connectors** | Pull data **into Kafka** from a DB, file, cloud, etc. |
| **Sink Connectors**   | Push data **from Kafka** to a target system           |
### 🧠 Kafka Connect Is NOT:
- It's **not a message broker** (that's Kafka itself)
- It's **not a data processing engine** (like Kafka Streams or Flink)
- It’s **not specific to Debezium**, but Debezium **uses it**

### 🔧 Kafka Connect Key Features
- **Distributed & Scalable**: Can run across multiple nodes (just like Kafka brokers)
- **Fault-tolerant**: Supports exactly-once or at-least-once semantics
- **Offset Tracking**: Keeps track of how much data has been read/written
- **REST API**: You configure connectors via a simple HTTP API
- **Schema-aware**: Can integrate with Kafka Schema Registry (Avro/JSON schema evolution)

## Debezium

**Debezium** is an open-source platform for **capturing changes from databases in real-time** and streaming those changes into downstream systems like **Apache Kafka**, **Kafka Connect**, and other event-driven platforms.
- Built on top of **Kafka Connect**
- Works by **reading the database’s transaction logs** (e.g., binlogs for MySQL, WAL for PostgreSQL)
- Outputs change events as **structured JSON records**

### 🧩 **Debezium**: A Set of Kafka Connect Connectors

- **Debezium is built on top of Kafka Connect**.
- It provides **ready-to-use Kafka Connect _source_ connectors** specifically for **Change Data Capture (CDC)**.
- These connectors **listen to database changes** (via logs like binlog/WAL) and **emit change events into Kafka topics**.

### 🧠 Analogy
Imagine Kafka Connect as a **platform or plugin system**, and Debezium as a **plugin** (or many plugins — one per DB) that you install and run **within** Kafka Connect.

### 🔧 How They Work Together

|Component|Role|
|---|---|
|**Kafka Connect**|Runs the **connector lifecycle**, handles tasks, error handling, and integration with Kafka|
|**Debezium**|Provides the **actual logic** to capture DB changes and emit events|
|**Kafka**|Receives the change events and makes them available to consumers|

![](https://miro.medium.com/v2/resize:fit:875/1*ZRE_HgFcLsfBnj8d9dktLA.png)
### 📦 Kafka Connect + Debezium Recap
- Kafka Connect is the **platform**
- Debezium is a **plugin** (connector) that runs on it
- Together, they let you stream **real-time database changes** into Kafka

### ✅ How Debezium Runs (High-Level)

1. You **run Kafka Connect** (which is a Java process — can run standalone or distributed).
2. Kafka Connect loads any **installed connectors** (like Debezium) as **plugins**.
3. You submit a **Debezium connector configuration** (JSON) via Kafka Connect's REST API.
4. Kafka Connect **assigns tasks** to workers (background threads) that run the connector logic.
5. Debezium listens to the **database’s transaction log**, and for every change:
    - It **converts it into a structured change event**
    - It **writes that event into a Kafka topic**
6. **Consumers** (e.g., microservices, analytics tools) can read those Kafka topics and process them in real-time.

> Debezium behaves like a **long-running background job** — hosted inside the Kafka Connect worker process.

### 🛠️ Supported Databases

| Database       | Mechanism Debezium Uses                    |
| -------------- | ------------------------------------------ |
| **MySQL**      | Binary Log (binlog)                        |
| **PostgreSQL** | Write-Ahead Log (WAL) via logical decoding |
| **MongoDB**    | Oplog (operations log)                     |
| **SQL Server** | Transaction log                            |
| **Oracle**     | XStream (or LogMiner for newer versions)   |

> Debezium supports **incremental snapshots** as well (capturing the current state + future changes).

🧪 Example Event (JSON)
```
{
  "before": {
    "id": 42,
    "email": "old@example.com"
  },
  "after": {
    "id": 42,
    "email": "new@example.com"
  },
  "op": "u",        // u = update, c = create, d = delete
  "ts_ms": 1719498120000,
  "source": {
    "db": "users_db",
    "table": "users"
  }
}
```

### 🧩 Components in a Typical Setup

```
[Database] → [Debezium Source Connector] → [Kafka Topic] → [Your Consumers]
```
You can also add:
- **Kafka Connect** UI (e.g., Confluent Control Center)
- **Kafka Streams**, **Flink**, or **Spark** to process CDC streams
- **Sink connectors** (e.g., to push CDC to Elasticsearch, Snowflake)

### 🔧 Example Config (MySQL source connector)

```
{
  "name": "mysql-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "database.hostname": "localhost",
    "database.port": "3306",
    "database.user": "debezium",
    "database.password": "dbz",
    "database.server.id": "184054",
    "database.server.name": "dbserver1",
    "database.include.list": "inventory",
    "table.include.list": "inventory.customers",
    "database.history.kafka.bootstrap.servers": "kafka:9092",
    "database.history.kafka.topic": "schema-changes.inventory"
  }
}
```

### 🔁 Deployment Options

| Option          | How Debezium Runs                                            |
| --------------- | ------------------------------------------------------------ |
| **Local (dev)** | Kafka Connect + Debezium on single machine                   |
| **Docker**      | With `debezium/connect` container                            |
| **Kubernetes**  | With Helm charts or operators                                |
| **Cloud**       | Via managed Kafka Connect (e.g., Confluent Cloud + Debezium) |

### 📦 Example Directory Structure

```
/kafka-connect/
├── connect-distributed.properties
├── plugins/
│   └── debezium-connector-postgres/
│       └── (JARs and dependencies)
```

### 📨 How You Start It (Summary)

```
# Start Kafka Connect (loads Debezium plugins)
connect-distributed.sh connect-distributed.properties

# Register Debezium connector via REST
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" -d '{
  "name": "my-postgres-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    ...
  }
}'
```

### 🧠 Execution Summary

- Debezium **runs inside Kafka Connect** as a plugin
- It **runs as a background task**, continuously streaming DB changes
- It’s implemented using **Kafka Connect APIs** (`SourceConnector`, `SourceTask`)
- You install it by dropping the JARs in a plugins folder
- It monitors the **DB’s transaction log**, so it's efficient and non-invasive


## Q&A
### Is Debezium only for Kafka Connect?

✅ **Yes, primarily.**

Debezium was **originally designed specifically as a set of Kafka Connect source connectors**. It relies on Kafka Connect’s architecture to:
- Manage connector lifecycle
- Assign tasks and parallelism
- Track offsets
- Integrate with Kafka brokers

So **Debezium’s core offering** is tightly coupled with **Kafka Connect**.

#### 🚧 But… Can it be used **without Kafka Connect**?
👇 Yes, but with some caveats.
Debezium also provides:
##### 1. **Debezium Embedded Engine**
- This allows you to use Debezium **as a library** inside your own Java application, **without Kafka Connect**.
- You integrate it directly with your code, and it emits change events via a callback.
- Use case: when you don’t want the overhead of running Kafka Connect, or need more custom control.

Example:
```
Configuration config = Configuration.create()
    .with("name", "my-connector")
    .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
    .with("database.hostname", "localhost")
    ...
    .build();

EmbeddedEngine engine = EmbeddedEngine.create()
    .using(config)
    .notifying(record -> {
        // Handle change event here
    })
    .build();

Executors.newSingleThreadExecutor().execute(engine);
```
This **still needs a database log (e.g. MySQL binlog)** but doesn't require Kafka Connect or Kafka.

##### 2. **Debezium Server**
- A more recent addition.
- **Lightweight standalone service** that reads DB changes and sends them to:
    - Kafka
    - Kinesis
    - Pulsar
    - Google Pub/Sub
    - Redis
    - MQTT
- Great for **non-Kafka users**.

You configure it via YAML and run it like a simple containerized app or jar.

Example Target with Redis: `sink.type: redis`

##### 🔚 Summary

| Mode                   | Kafka Connect Required | Notes                      |
| ---------------------- | ---------------------- | -------------------------- |
| **Debezium Connector** | ✅ Yes                  | Main usage, runs as plugin |
| **Embedded Engine**    | ❌ No                   | Java lib, app-controlled   |
| **Debezium Server**    | ❌ No                   | Sends CDC to many systems  |

So:

- If you're using **Kafka**, Kafka Connect + Debezium is the best combo.
- If you're using **something else** (or want lightweight deployment), you can use Embedded or Debezium Server.