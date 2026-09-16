## Overview
### 📘 What is Cassandra?

**Apache Cassandra** is a highly scalable, **wide-column (column family)**, distributed NoSQL **database** designed to **handle large volumes of structured data** across many commodity servers, with **no single point of failure**. It was originally developed at Facebook and later became an open-source Apache project.

### 📘 Column Family vs Column Store databases?
#### Column Family
* Column family database are based on google's Big Table structure
	* Data is stored and accessed as rows (Row store)
	* Optimized for answering questions about a row
	* Examples: Bigtable, HBase, Cassandra
#### Column Store
* Column Store databases are relational and primarily used for Data warehouse applications
	* Data is stored and accessed as columns (Column store)
	* Optimized for answering questions about a single Attribute
	* Examples: Sybase IQC-Store, Vertica, MonetDB

### ⚙️ Core Features

| Feature                        | Description                                                                     |
| ------------------------------ | ------------------------------------------------------------------------------- |
| **Distributed**                | Data is distributed across multiple nodes, often across multiple datacenters.   |
| **High Availability**          | No single point of failure; supports continuous uptime even when nodes go down. |
| **Horizontal Scalability**     | Easily add new nodes without downtime.                                          |
| **Tunable Consistency**        | You can configure the level of consistency for reads/writes.                    |
| **Write Optimized**            | Extremely fast writes; good for write-heavy workloads.                          |
| **Decentralized Architecture** | All nodes are equal — no master-slave.                                          |
### 🧱 Data Model

- **Keyspace**: Similar to a database in RDBMS.
- **Table**: Contains rows of data (like in SQL), but with a flexible schema.
- **Row**: Identified by a **Primary Key**.
- **Column Family**: Columns can vary between rows (semi-structured).
- **Primary Key** = Partition Key + Optional Clustering Columns.

### 🗃️ Storage Model

- Uses **Log-Structured Merge Trees (LSM Trees)**.
- **Writes** go to in-memory structure (Memtable) and **commit log** for durability.
- Periodically flushed to disk into **SSTables**.

### 🔄 Replication and Consistency

- Data is **replicated** across nodes.
- You define a **replication factor** (e.g., 3 copies).
- **Consistency level** can be:
    - `ONE`, `QUORUM`, `ALL`, etc.
    - Different for **reads and writes**.

### 🛠️ Use Cases

- Real-time analytics
- IoT and time-series data
- Messaging apps
- Recommendation systems
- User activity tracking
- Any **write-heavy** workload

### ✅ Pros
- Fault-tolerant and highly available
- Scales linearly
- Fast write performance
- Multi-datacenter support
### ⚠️ Cons
- Steeper learning curve than traditional RDBMS
- Limited ad-hoc querying (no JOINs, subqueries)
- Needs careful **data modeling** for efficient access

## Architecture & Features

### Cluster Topology and Design

**Apache Cassandra** is a **distributed, horizontally scalable database** that operates on a **peer-to-peer architecture** with no master node, ensuring high availability and no single point of failure. Each database instance is called a **node**, and multiple nodes form a **cluster**.

- **Scalability**: Adding more nodes improves performance linearly if configured well.
- **Racks and Data Centers**: Nodes are logically grouped into _racks_ and _data centers_ for fault tolerance and replication. With the `NetworkTopologyStrategy`, Cassandra can survive rack failures.
- **Multi-Data Center Use Cases**:
    - Serve users in different geographies (e.g., US, EU, APAC).
    - Separate workloads like OLTP and analytics.
    - Enable active disaster recovery.

**Key Components**
- **Seeds**: Help new nodes join the cluster; typically 2–3 per data center
- **Gossip Protocol**: Enables nodes to exchange state information every second for cluster coordination.

Proper configuration of racks, data centers, and snitches ensures optimal replication and fault tolerance.

![[Pasted image 20250518233315.png]]

![[Pasted image 20250518233347.png]]

![[Pasted image 20250518233358.png]]

### Database structures

Cassandra stores data in tables where each table is organized in rows and columns the same as any other database. Cassandra table was formerly referred to as **column family**. Tables are grouped in keyspaces. A keyspace could be used to group tables serving a similar purpose from a business perspective like all transactional tables, metadata tables, use information tables etc. Data replication is configured per keyspace in terms of replication factor per data center and the replication strategy. 

Each table has a defined primary key. The primary key is divided into partition key and clustering columns. The clustering columns are optional. There is no uniqueness constraint for any of the keys.

The partition key is used by Cassandra to index the data. All rows which share a common partition key make a single **data partition** which is the basic unit of data partitioning, storage, and retrieval in Cassandra.

### Partitioning

A partition key is converted to a **token** by a **partitioner.** There are various partitioner options available in Cassandra out of which **Murmur3Partitioner** is used by default. The tokens are signed integer values between -2^63 to +2^63-1, and this range is referred to as **token range**. Each Cassandra node owns a portion of this range and it primarily owns data corresponding to the range. A token is used to precisely locate the data among the nodes and on the data storage of the corresponding node.

It is evident that when there is only one node in a cluster, it owns the complete token range. As more nodes are added, the token range ownership is split between the nodes, and each node is aware of the range of all the other nodes. 

Here is a simplified example to illustrate the token range assignment. If we consider there are only 100 tokens used for a Cassandra cluster with three nodes. Each node is assigned approximately 33 tokens like: 

 **node1:** 0-33 **node2:** 34-66 **node3:** 67-99. 

 If there are nodes added or removed, the token range distribution should be shuffled to suit the new topology. This process takes a lot of calculation and configuration change for each cluster operation.

### Virtual nodes/Vnodes

To simplify the token calculation complexity and other token assignment difficulties, Cassandra uses the concept of virtual nodes referred to as Vnodes. A cluster is divided into a large number of virtual nodes for token assignment. Each physical node is assigned an equal number of virtual nodes. In our previous example, if each node is assigned three Vnodes and each Vnode 11 tokens: 

 **v1:**0-9, **v2:**10-19, **v3:**20-29 so on 

 Each physical node is assigned these vnodes as:

 **node1:** v1, v4, v7 **node2:** v2, v5, v8 **node3:** v3, v6, v9

![[Pasted image 20250518233841.png]]

The default number of Vnodes owned by a node in Cassandra is 256, which is set by  num_tokens property. When a node is added into a cluster, the token allocation algorithm allocates tokens to the node. The algorithm selects random token values to ensure uniform distribution. But, the num_tokens property can be changed to achieve uniform data distribution. The number of 256 Vnodes per physical node is calculated to achieve uniform data distribution for clusters of any size and with any replication factor.


### Replication

In Cassandra, data in a **keyspace** is duplicated using a **replication factor** (commonly 3). One replica is stored on the node owning the data’s token; others are placed based on a **replica placement strategy**.

#### Key Concepts:
- **Snitch** (node-level):
    - Determines the **rack** and **data center** of a node.
    - Helps Cassandra understand network topology for efficient replica placement.
    - Common choice: **GossipingPropertyFileSnitch**, configured via `cassandra-rackdc.properties`.
    - Cloud-specific snitches exist (e.g., for AWS, GCP).

- **Replication Strategy** (keyspace-level):
    - **SimpleStrategy**: Sequential placement, not rack or DC aware — for testing only.
    - **NetworkTopologyStrategy**: Rack & data center aware — recommended for production.
    - Example:
        `CREATE KEYSPACE ks WITH replication = {   'class': 'NetworkTopologyStrategy',    'dc_1': 3,    'dc_2': 1 };`

All replicas are treated equally during reads and writes, except in rare mutation cases.

### Consistency and Availability

Cassandra follows the **CAP theorem**, prioritizing **Partition Tolerance**, and offering a trade-off between **Consistency** and **Availability** via **tunable consistency**—you can choose how many replicas must respond for an operation to be considered successful.

#### Key Concepts:
- **Tunable Consistency**: You can configure the required number of replica acknowledgments per query (read/write) to balance consistency vs. latency/availability.
- **Read Consistency**: Latest timestamp among replicas is used to resolve conflicts.
- **Write Consistency**: The specified number of replicas must acknowledge the write.

#### Common Consistency Levels:
- **ONE, TWO, THREE**: Number of replicas to acknowledge.
- **QUORUM**: Majority of replicas (e.g., 2 out of 3 for RF=3).
- **LOCAL_***: Limits consistency checks to the local data center.
- **EACH_***: Ensures consistency level is met in _each_ data center independently.

#### Best Practices:
- Use **odd replication factors** (typically 3) for balance.
- Have node and rack counts in multiples of the replication factor.
- Low consistency reads/writes are still replicated via **eventual consistency**, where remaining replicas are synchronized in the background.
- Cassandra uses **anti-entropy mechanisms** to fix inconsistencies between replicas over time.

#### Here's how it works:
##### ✅ During a write operation:
- Cassandra **immediately writes** to a **commit log** and **memtable** on the **coordinator node**.
- It **sends the write to all replicas** (based on the replication factor).
- **Only the replicas required by the chosen consistency level need to acknowledge** for the write to be considered successful.
- **The remaining replicas** receive the write **asynchronously in the background** (this is where **eventual consistency** comes in).

##### ✅ During a read operation:
- Cassandra **fetches data from one or more replicas** based on the consistency level.
- If there's a mismatch in data versions (timestamps), it **uses the latest version** and **repairs stale replicas in the background** (this is called **read repair**).

##### Background processes ensuring consistency:
- **Read Repair**: During reads, stale replicas can be updated automatically if inconsistency is detected.
- **Hinted Handoff**: If a replica is down during a write, Cassandra stores a “hint” to replay the write later when the node is back online.
- **Anti-Entropy Repair (Full Repair)**: Periodically compares data between replicas using Merkle trees to fix inconsistencies.

### Query Interface

**Cassandra Query Language (CQL)** is the primary interface for interacting with Cassandra, replacing the older **Thrift** protocol. CQL resembles SQL to simplify learning and provides standard DDL and CRUD operations.

#### Key Features:
- **DDL**: Create keyspaces and tables.
- **CRUD**:
    - `SELECT`: Read operation.
    - `INSERT`, `UPDATE`, `DELETE`: Write operations.

#### Table Design:
- Tables are defined with **columns**, **primary key** (partition key + optional clustering keys), and other settings.
- The **partition key** determines data distribution across nodes.
- **Clustering columns** define sort order within a partition.

#### Query Limitations:
- No **joins** or **nested queries**.
- Queries must usually include a **full partition key**.
- Limited support for **aggregations**.
- `ORDER BY` is only allowed on clustering columns and must follow their defined order.

#### Data Modeling:
- Cassandra favors **denormalized, query-driven** design.
- Schema must be modeled around expected **query patterns** for efficiency and performance.

#### Query Execution:
- Queries are handled by a **coordinator node**, chosen by the client driver.
- Cassandra drivers manage **connections, pooling, and routing** automatically.

### Data Storage

#### Data Organization
- **Cassandra is _not_ a column-oriented database** (like Apache Parquet or ClickHouse).
- Cassandra is actually **row-based**: it stores data by **rows**, not by columns across the whole table.
- However, **each row can have a flexible set of columns**, unlike traditional relational databases where every row has the same columns. That’s why it’s called a **wide-column** or **column-family** database.

#### 🏗️ How Cassandra differs:

- **Row-Oriented DBs (e.g., MySQL, PostgreSQL):**
    - Data is stored and read **row by row**.
    - Good for transactions involving full rows.

- **Column-Oriented DBs (e.g., Redshift, HBase, Parquet):**
    - Data is stored **column by column**.
    - Ideal for analytics and queries that scan many rows but only a few columns.

- **Cassandra (Wide-Column DB):**
    - Stores data **row by row**, like row-oriented databases.
    - But each row can have **different columns**, which are internally organized for fast access.
    - Offers **high write throughput** and **scalability**.
    - Data is stored in **partitions** based on the **partition key**, with rows grouped together.

#### 📌 Why "Wide-Column"?
When we say **Cassandra is a wide-column database**, we’re referring to how data is **logically and physically organized**—it’s more like a **key-value store with rows as keys and flexible sets of columns as values**.
![[wcd-pic1.png]]


- Each **row can contain many columns**, and they can be different for each row.
- Internally organized for **efficient lookups of specific rows and columns**.
- Combines the scalability of **key-value stores** with the structured layout of **column families**.

Cassandra uses a commit log for each incoming write request on a node. **Commit log** is a write-ahead log, and it can be replayed in case of failure. The on-disk data structure is called **SSTable**. SSTables are created per table in the database. 

Cassandra uses immutable data storage to ensure optimal performance, meaning that SSTables (Sorted String Tables) are never modified after being written. Instead of updating or deleting existing data in place, Cassandra handles these operations by writing new versions of the data. This approach leads to multiple versions of a data item coexisting at any given time. Cassandra is designed to be write-optimized, allowing write operations to be fast and non-blocking. However, read operations take on the responsibility of merging these versions to return the most recent value. Each data cell is stored with a write timestamp, which is used during reads to determine the latest and correct version of the data.

Cassandra ensures data consistency and optimal performance using two key mechanisms: **compaction** and **anti-entropy operations**.

#### 🔄 Compaction

Compaction merges multiple **SSTables** into a new one, consolidating versions of data and removing obsolete data (tombstones) when eligible. The result is a cleaner, more efficient SSTable.

**Common Compaction Strategies:**
- **SizeTieredCompactionStrategy (STCS)**:
    - Default strategy.
    - Triggers compaction based on the size of SSTables on disk.
- **LevelledCompactionStrategy (LCS)**:
    - Designed to improve **read performance**.
    - Organizes SSTables into levels with controlled overlap for efficient access.
- **TimeWindowCompactionStrategy (TWCS)**:
    - Tailored for **time-series data**.
    - Groups and compacts SSTables based on defined time windows.

#### 🔧 Anti-Entropy Mechanisms

These operations help maintain **eventual consistency** across replicas in a distributed setup.
- **Hinted Handoff**:
    - When a replica node is temporarily down, the coordinator stores a "hint" of the write on another node.
    - Once the failed node recovers, the hint is replayed to sync missed writes.
    - Hints are only stored for a limited time and are not a primary consistency mechanism.
- **Read Repair**:
    - During reads, Cassandra compares replicas using timestamps.
    - If inconsistencies are found, it updates outdated replicas in the background.
    - Only triggered in a subset of reads to avoid performance issues.
- **Repair**:
    - The **primary anti-entropy** operation.
    - Uses **Merkle trees** (hash trees) to compare data between replicas and detect mismatches.
    - Inconsistent data is then streamed and corrected.
    - Must be **manually scheduled**, as it is resource-intensive.

### Write Path

Cassandra writes path is the process followed by a Cassandra node to store data in response to a write operation. A coordinator node initiates a write path and is responsible for the request completion. 

#### 📥 Write Path in Cassandra

1. **Client sends a write request**  
    The client sends data to a **coordinator node** (chosen by the driver).

2. **Coordinator determines replica nodes**  
    The coordinator uses the **partition key** and **partitioner** to identify which nodes are responsible for storing the data (based on the replication factor).

3. **Write to Commit Log (Durability)**  
    Each replica node **first writes the data to a commit log** on disk to ensure durability in case of a crash.

4. **Write to Memtable (In-memory buffer)**  
    The data is then written to an in-memory data structure called the **memtable**. This is where data is stored temporarily for quick access.

5. **Acknowledgment sent to Coordinator**  
    Once the write is recorded in both the **commit log and memtable**, the replica node sends an **acknowledgment** back to the coordinator.

6. **Coordinator responds to client**  
    The coordinator waits for acknowledgments based on the **consistency level** specified by the client (e.g., `ONE`, `QUORUM`, `ALL`) and then **responds back to the client**.

7. **Memtable flushes to SSTable (Eventually)**  
    Over time, as the memtable fills up, it is **flushed to disk** as an **immutable SSTable**. This process happens **asynchronously** in the background.

**Common error scenarios**:
1. If the sufficient number of nodes required to fulfil the request are not available or do not return the requested acknowledgement, the coordinator throws an exception.  
2. Even after satisfying the request with the required number of replica acknowledgements, if an additional node that stores a replica for the data is not available,  the data could be saved as a hint on another node. 

In a multi-datacenter cluster, the coordinator forwards write requests to all applicable local nodes. For the remote data centers, the write request is forwarded to a single node per data center. The node replicates data to the data center with the required number of nodes to satisfy the consistency level. 

![[Pasted image 20250518235212.png]]

### Read Path

#### 📚 Cassandra Read Path

The **read path** in Cassandra describes the sequence of steps a node follows to fetch data in response to a client’s read request. Unlike the write path, the read path is more complex due to the need for consistency checks and potential background repair.

#### 🧭 High-Level Steps in a Read Operation

1. **Coordinator Selection**  
    The client sends a read query to a randomly chosen **coordinator node**.

2. **Replica Selection**  
    The coordinator hashes the **partition key** to determine which nodes (replicas) hold the relevant data, based on the **partitioner** and **replication strategy**.

3. **Consistency Check**  
    The coordinator ensures enough replicas are available to meet the **requested consistency level** (e.g., `ONE`, `QUORUM`). If not, the read fails with an exception.

4. **Fast Replica Read (Speculative Read)**  
    Using **dynamic snitching**, which tracks node latencies, the coordinator sends the full data read request to the **fastest replica** (could be itself).

5. **Digest Requests to Other Replicas**  
    The coordinator sends **digest read requests** (lightweight hash comparisons) to other replicas to check data consistency.

6. **Digest Comparison**  
    If all digests match, the coordinator assumes all replicas have the same version and returns the data from the fastest replica to the client.

7. **Mismatch Handling**  
    If digests differ:
    - Full data is fetched from **all replicas**.
    - The **latest version** (based on write timestamps) is returned to the client.
    - **Read repair** is triggered in the background to update stale replicas.

#### 🧩 Node-Level Components in the Read Path

Each Cassandra node uses several layers of in-memory and on-disk structures to optimize reads:

- **Row Cache (optional)**
    - Stores complete rows for fast return.
    - Best used when a small set of "hot" rows is frequently accessed.
        
- **Partition Key Cache**
    - Caches partition index locations (not data).
    - Enables faster SSTable lookups for frequently accessed partitions.
        
- **Bloom Filter**
    - A probabilistic structure that checks if a partition **might exist** in an SSTable.
    - Prevents unnecessary disk reads (false positives possible, but no false negatives).

- **Partition Index & Summary**
    - Index holds actual offsets of partitions within an SSTable.
    - Summary is a sampled version of the index, used to quickly narrow down where to look.

- **Memtable**
    - In-memory structure holding recently written data not yet flushed to disk.
    - Queried before SSTables.

- **Compression Offset Map**
    - Maps logical partition locations to physical disk positions when SSTables are compressed.

- **SSTables**
    - Immutable, on-disk data files that store flushed data.
    - Scanned only when necessary, after checking the above caches and indexes.

#### 🧠 Node-Side Flow of a Read Request

1. **Check Row Cache**: If the row exists in cache, return it immediately.
2. **Check Bloom Filters**: Identify if a partition might exist in an SSTable.
3. **Check Partition Key Cache**: If cached, get direct offset in SSTable.
4. **Use Index/Summary**: If not cached, traverse partition summary and index to locate offset.
5. **Search Memtable**: Check for fresh data before hitting disk.
6. **Read from SSTable**: Use the gathered offset to seek and retrieve the data.
7. **Update caches**: Cache recent lookups to optimize future queries.

#### 🔁 Optimizations and Repairs

- **Dynamic Snitching** ensures reads are directed to the most responsive nodes.
- **Read Repair** helps maintain consistency during reads without overloading the system.
- Cassandra balances **performance and consistency** using this read path, especially with tunable consistency levels.

![[Pasted image 20250518235234.png]]

### Lightweight Transactions (LWTs)

**Lightweight Transactions (LWTs)** provide **linearizable consistency** by using a **Paxos-based consensus protocol**. They ensure that a condition is **checked and mutated atomically**, which prevents write conflicts—especially useful in **multi-region active-active** setups.

**When to Use LWTs:**
- Enforcing **uniqueness** (e.g., unique usernames or emails)
- Preventing **overwrites** of concurrent updates
- **Idempotent upserts** where you want to “write if not already set”


**Example 1: Insert if Not Exists**
```
INSERT INTO users (id, email, name)
VALUES (123, 'alice@example.com', 'Alice')
IF NOT EXISTS;
```
This ensures that **no duplicate user** is inserted. If someone else tries to insert the same ID concurrently in another region, only one will succeed.


**Example 2: Conditional Update**
```
UPDATE users
SET city = 'London'
WHERE id = 123
IF name = 'Alice';
```
This guarantees the update **only happens** if the row is in the expected state. If another update has changed the name, the update fails.


### Data Modelling

#### Query First Approach
The **Query-First approach** in Cassandra is a design philosophy that flips traditional relational database design on its head:
>  In Cassandra, you design your schema based on your queries, not your data model.

**Why Query-First?**
Cassandra prioritizes **high availability, scalability, and fast reads**, but **does not support ad-hoc queries or joins** efficiently like relational databases. So, to ensure efficient access, you:
- Identify the **specific queries** your application will run.
- Then design **tables optimized for those queries**, even if it means **duplicating data**.

**Example: Traditional vs Query-First**

- In Traditional approach we model our data and get the desired results by joining
- In Query-First approach we model our tables according to what we have to query

##### In SQL:
**Employee:**

| ID  | FirstName | SurName | CompanyCarId | Salary |
| --- | --------- | ------- | ------------ | ------ |
| 1   | Elvis     | Presley | 1            | $30000 |
| 2   | David     | Bowie   | 2            | $40000 |
| 3   | Kylie     | Jenner  | 3            | $60000 |
| 4   | Elton     | John    | 1            | $20000 |
| 5   | Mariah    | Carey   | 2            | $30000 |
| 6   | Justin    | Bieber  | 4            | $30000 |
| 7   | Selena    | Gomez   | 5            | $50000 |

**Company Car:**

| ID  | Make     | Model    | Cost   | Engine |
| --- | -------- | -------- | ------ | ------ |
| 1   | BMW      | 5 Series | $50000 | 1.8    |
| 2   | Audi     | A6       | $55000 | 1.6    |
| 3   | Mercedes | C-Class  | $60000 | 1.6    |
| 4   | Mercedes | A-Class  | $30000 | 1.4    |
| 5   | BMW      | 3 Series | $35000 | 1.6    |


##### In Cassandra:

**Employee by Car Make:**

|          |          |                      |                     |                   |
| -------- | -------- | -------------------- | ------------------- | ----------------- |
| BMW      | {ID : 1} | {FirstName : Elvis}  | {SurName : Presley} | {Salary : $30000} |
| BMW      | {ID : 4} | {FirstName : Elton}  | {SurName : John}    | {Salary : $20000} |
| BMW      | {ID : 7} | {FirstName : Selena} | {SurName : Gomez}   | {Salary : $50000} |
| Audi     | {ID : 2} | {FirstName : David}  | {SurName : Bowie}   | {Salary : $40000} |
| Audi     | {ID : 5} | {FirstName : Mariah} | {SurName : Carey}   | {Salary : $30000} |
| Mercedes | {ID : 3} | {FirstName : Kylie}  | {SurName : Jenner}  | {Salary : $60000} |
| Mercedes | {ID : 6} | {FirstName : Justin} | {SurName : Bieber}  | {Salary : $30000} |


**Company Car by ID:**

| 1   | {Make : BMW}      | {Model : 5 Series} | {Engine : $50000} | { Engine : 1.8} |
| --- | ----------------- | ------------------ | ----------------- | --------------- |
| 2   | {Make : Audi}     | {Model : A6}       | {Engine : $55000} | {Engine : 1.6}  |
| 3   | {Make : Mercedes} | {Model : C-Class}  | {Engine : $60000} | {Engine : 1.6}  |
| 4   | {Make : Mercedes} | {Model : A-Class}  | {Engine : $30000} | {Engine : 1.4}  |
| 5   | {Make : BMW}      | {Model : 3 Series} | {Engine : $35000} | {Engine : 1.6}  |

Querying using columns other than those specified in the partitioning key is not allowed.
However, you can use "**ALLOW FILTERING**" to force the case, but the query will be inefficient and will query all the nodes for the results.

**Benefits**
- Super fast queries at scale.
- Schema optimized for **exact application access patterns**.
- Enables Cassandra to scale linearly with predictable performance.

**Downsides**
- Data duplication increases storage cost.
- Updates must be done in multiple places if data is duplicated.
- Inflexible for new/unplanned query patterns.


### Secondary Indexes and Materialized Views
#### 🔍 1. Secondary Indexes in Cassandra

##### ✅ What are they?
Secondary indexes let you **query a column that is not part of the primary key**. For example, if your table is partitioned by `user_id`, but you want to query by `email`, a secondary index allows that.

##### 🛠 How they work:
- Cassandra creates a hidden, internal table that maps the indexed column to the primary key(s).
- When a query uses a secondary index, Cassandra uses this index table to **look up the matching primary keys**, and then fetches the actual data.

##### ⚠️ Limitations:
- Not efficient at scale; only works well when:
    - The **indexed column has high cardinality** (many unique values).
    - The **result set is small**.
- Can perform poorly if:
    - The data is **unevenly distributed**.
    - You query large datasets.
- **Not distributed across nodes efficiently**—queries may hit **all nodes**, making performance unpredictable.

##### ✅ Best Use Cases:
- Small tables.
- Low-traffic or internal lookups.
- Columns with **high uniqueness** (like user emails or IDs, not boolean flags).

#### 🧱 2. Materialized Views (MVs)

##### ✅ What are they?

Materialized Views automatically maintain **precomputed, query-optimized tables** based on your base table. You define the view with a **different primary key** to allow efficient querying on alternate dimensions.

Example:
```
CREATE MATERIALIZED VIEW users_by_email AS
SELECT * FROM users
WHERE email IS NOT NULL
PRIMARY KEY (email);
```
Now, you can do `SELECT * FROM users_by_email WHERE email='abc@example.com';` efficiently.

##### 🛠 How they work:
- Cassandra **automatically keeps the view in sync** with the base table.
- Any **inserts, updates, or deletes** in the base table are **asynchronously propagated** to the view.
- The MV has its **own storage**, replication, and compaction.

##### ⚠️ Limitations & Caveats:
- Views **can lag** behind the base table (eventual consistency).
- **No full support for all data types and updates** (especially counters or complex schema changes).
- May lead to **data inconsistencies** or partial updates in some edge cases.
- Heavy write amplification, as changes to the base table result in **extra writes**.

##### ✅ Best Use Cases:
- When you know the query patterns ahead of time.
- When you want to support **multiple query paths** without manually duplicating logic.
- Better for **read-heavy workloads**.

##### 🆚 Secondary Index vs Materialized View

|Feature|Secondary Index|Materialized View|
|---|---|---|
|Query Flexibility|Moderate (any column, limited scale)|High (query by alternate primary keys)|
|Performance at Scale|Poor|Good (with predictable access patterns)|
|Write Overhead|Low|High (asynchronous propagation)|
|Data Consistency|Stronger (uses base table)|Eventually consistent|
|Use Case|Small tables, ad hoc lookups|Alternate views for known query patterns|

##### ✅ Final Thoughts:

- Use **secondary indexes** sparingly, for **low-volume, low-latency lookups** on non-primary key columns.
- Use **materialized views** when you need to **support multiple query patterns**, and you're okay with **eventual consistency and increased write cost**.
- For **full control**, consider **manually maintaining denormalized tables** instead of relying on MVs.

## Q&A

### In Cassandra, does a write query go to all the replicas or only the coordinator?

In **Apache Cassandra**, a write query is initially sent to a **coordinator node**, but it **does not stop there**—the coordinator is responsible for forwarding the write to **all the replicas** responsible for the partition being written.

### Does a read query also go to all the replicas?

In **Cassandra**, a **read query** does **not always go to all the replicas** — it depends on the **consistency level** and the **coordinator’s strategy** for ensuring correct results.

  
**Here’s how a read query works:**
1. **Client sends a read request to any node**, which acts as the **coordinator**.
2. The coordinator **determines the replicas** that store the requested partition (based on the partition key and replication strategy).
3. What happens next depends on the **consistency level** specified for the read

### In Cassandra, if one of the replica nodes goes down and is replaced by another node, how does the coordinator know which nodes to send the query to?

In Cassandra, **when a replica node goes down and is replaced**, the **coordinator node** knows **which nodes to send queries to** through a combination of:

**1. Gossip Protocol**
- Cassandra uses a **Gossip protocol** to continuously exchange state information (including node health and token ownership) between nodes.
- Each node knows about the state (UP/DOWN/LEFT/JOINING/etc.) of all other nodes.
- This allows the **coordinator node** to know **which nodes are currently live** and capable of serving reads or writes.

**2. Token Ring & Partitioner**
- Cassandra partitions data using a **consistent hashing mechanism** (the **token ring**) and a **partitioner** (e.g., Murmur3Partitioner).
- Each node is responsible for a **range of tokens**, and the coordinator uses the **partition key** in a query to determine **which tokens are responsible for that data**, and thus, which nodes own those tokens.

**3. Replication Strategy**
- Cassandra uses a **replication strategy** (e.g., SimpleStrategy or NetworkTopologyStrategy) to determine **which nodes should hold replicas** for a given token.
- When a node is replaced, the **replacement node is assigned the same token range** as the node it replaces, so it inherits the same data responsibility.

**4. Hinted Handoff / Repair / Bootstrap**
- If a node goes down:
    - Writes meant for that node are **hinted** and later replayed when it comes back.
- If a new node **replaces** a downed node:
    - It goes through **bootstrapping**, where it **streams data** from other replicas.
    - Once it finishes, it becomes an active replica.

**5. Query Routing by Coordinator**
- When a client sends a query, the **coordinator** (any node can act as one) does the following:
    1. **Hashes the partition key** to find the token.
    2. **Identifies the replicas** responsible for that token range.
    3. **Consults gossip** to check which of those replicas are alive.
    4. Sends the query to **the appropriate number of live replicas** to satisfy the consistency level (e.g., QUORUM, ONE, etc.).

**Example Scenario**
- Original replicas for key X: **Node1, Node2, Node3**
- Node2 goes down and is replaced by Node4 (which bootstraps and takes Node2’s token range)
- Coordinator receives query for key X:
    - Sees replicas are: **Node1, Node3, Node4** (using updated gossip and token map)
    - Sends read/write requests to a quorum of live nodes among these three.

**TL;DR**
The **coordinator node uses gossip and the token ring** to dynamically and accurately determine which nodes are replicas for a given key and which ones are alive, even if nodes have gone down and been replaced.

### How is the data stored in a memtable?

Let’s dive into **how data is stored in the memtable in Apache Cassandra**, and **whether it’s sorted**.

**Short Answer:**
Yes, **data in a memtable is kept sorted by clustering key** **within each partition**, because the memtable **uses a sorted in-memory data structure**, typically a **SkipList**, not an AVL or Red-Black tree.

**Detailed Explanation:**

**1. Memtable Overview**
- The **memtable** is an in-memory data structure that holds recent writes before they are flushed to disk into **SSTables**.
- There is one memtable per table (Cassandra “table”).

**2. Internal Data Structure**
- Cassandra **does not use AVL or Red-Black trees** in modern implementations.
- Instead, it uses a **ConcurrentSkipListMap** (Java’s concurrent sorted map) internally to store:
    - Partition key ➝ Partition data (rows)
    - Within each partition: rows are organized by **clustering keys**

	Why SkipList?
	- SkipList offers **sorted ordering**, **logarithmic time complexity**, and better concurrency characteristics than trees.
	- Perfect for Cassandra’s highly concurrent, write-heavy workload.

**3. Is Data Sorted?**
- Yes — **within each partition**, rows are **sorted by clustering key**.
- This helps when reading data directly from memtables (especially range queries within a partition).
- However, the data structure allows **fast inserts without strict rebalancing**, unlike AVL or Red-Black trees.

**4. Ordering at Memtable vs SSTable**
- The memtable maintains a **partial sorted order** (by clustering key within partition).
- During a **flush**, data is fully sorted and serialized into SSTables in strict clustering key order.
- SSTables are immutable and fully sorted on disk, enabling fast range reads.

### In Cassandra, how does a client know which node to send the request to? In other words, how is a coordinator node chosen? Is there a load balancer responsible for it?

In **Apache Cassandra**, the process of choosing a **coordinator node** is handled by the **Cassandra client driver**, not by a load balancer or the cluster itself.

**How a Coordinator Node Is Chosen**
1. **Client uses a Cassandra driver** (like DataStax, Java driver, Python driver, etc.).
2. The driver maintains a **connection pool** to **multiple nodes** in the cluster.
3. For each query, the driver chooses **one of these nodes as the coordinator** and sends the query to it.

So, **any node in the cluster can act as a coordinator**, but it’s the **driver that decides** which one to use for each query.


**How the Driver Picks a Node**  

Drivers use a **load balancing policy**, such as:
- **Round-robin policy**: Rotates through available nodes evenly.
- **Token-aware policy** (most efficient):
    - Calculates the **partition key token**.
    - Chooses a node that is **replica for that token** as the coordinator.
    - Reduces network hops by contacting a replica directly.
- **Data center aware policy**: Prioritizes nodes in the **same data center** as the client.

> 🔥 In practice, **token-aware + DC-aware policies** are the best combo for performance and efficiency.


**No Load Balancer Required**
- You **don’t need an external load balancer** (like HAProxy or NGINX).
- Cassandra clients **self-manage coordinator selection and load balancing**.
- Clients discover the topology and **adapt dynamically** if nodes go down or join the cluster.

### In Cassandra, for write operations using QUORUM, what happens if the quorum is not satisfied? Is the data that was written on some nodes rolled back?

In **Apache Cassandra**, if a **write operation using QUORUM** **does not satisfy the quorum**, the write is **considered a failure** and the client gets an **error**. However:

> ❌ **There is no rollback.**
> ✅ **Partial writes can still exist on some replicas.**

**What Happens Step-by-Step:**

1. The client sends a write to a **coordinator node** with **consistency level = QUORUM**.
2. The coordinator forwards the write to **all replicas** for that partition.
3. The coordinator waits for **acknowledgments from a quorum of replicas** (i.e., majority, like 2 out of 3 in RF=3).
4. **If the quorum is not achieved** (e.g., too many replicas are down or too slow):
    - The coordinator **returns a failure** to the client.
    - Some replicas **might have already accepted the write**.
5. **Those partial writes are not rolled back.**


**What Ensures Consistency After That?**

To prevent inconsistency due to partial writes:
- **Hinted Handoff**: The coordinator saves a “hint” to retry writing to down replicas later.
- **Read Repair**: When the data is read later at QUORUM or higher, inconsistencies are detected and repaired in the background.
- **Anti-Entropy Repair (manual or scheduled)**: Periodically synchronizes all replicas to fix any divergences.


**Durable Writes, but Eventual Consistency**

- **Writes are durable**: Once a replica writes to its commit log and memtable, the data is safe.
- **But Cassandra doesn’t support rollback**: It assumes **eventual consistency** will resolve discrepancies.


## Resources

Tutorial: https://www.youtube.com/playlist?list=PLalrWAGybpB-L1PGA-NfFu2uiWHEsdscD

Good Articles:
- https://www.instaclustr.com/education/apache-cassandra/apache-cassandra-database/#what-is-apache-cassandra-database
- https://www.instaclustr.com/blog/cassandra-data-partitioning/
- https://www.instaclustr.com/blog/cassandra-architecture/

Official Documentation:
- https://cassandra.apache.org/_/cassandra-basics.html
- https://cassandra.apache.org/doc/latest/cassandra/architecture/overview.html
- https://cassandra.apache.org/doc/latest/cassandra/developing/data-modeling/intro.html
- https://cassandra.apache.org/doc/latest/cassandra/managing/configuration/index.html
- https://cassandra.apache.org/doc/latest/cassandra/managing/operating/read_repair.html