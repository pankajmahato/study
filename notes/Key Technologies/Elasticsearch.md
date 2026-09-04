## Overview

**Elasticsearch** is a **fast, open-source search engine** built on top of **Apache Lucene**. It uses a smart data structure called an **inverted index** (like the index at the end of a book) to **quickly find documents** based on search terms.
Unlike traditional databases, it is:
- **Distributed** – can run across many servers
- **Scalable** – handles large volumes of data
- **Efficient for full-text search and analytics** (e.g., log data, security data)

It stores both the data and the index to enable **fast lookups** and **real-time insights**.

### Apache Lucene

**Apache Lucene** is a **Java-based open-source search library** used to build powerful full-text search capabilities into applications. It was first released in 1999 under the Apache License.
- Lucene provides the **core search engine functionality**, including indexing and querying text.
- **But it is limited to a single machine**—it does **not handle distributed search or scaling** out-of-the-box.

👉 This is where **Elasticsearch** comes in—it **builds on Lucene** and adds:
- **Distributed architecture**
- **Scalability**
- **User-friendly APIs**
- **Operational features** like clustering, monitoring, and fault tolerance

📌 **Example:** Wikipedia originally used Lucene directly, but managing and scaling it became complex—Elasticsearch was designed to simplify this.

### Overview (Elasticsearch)

**Elasticsearch** is an open-source search and analytics engine built on top of **Apache Lucene**, a Java-based search library. While Lucene provides fast indexing and querying capabilities on a single machine, Elasticsearch enhances it by adding distributed capabilities, scalability, and ease of use.

Elasticsearch does this by **breaking down data into units called "shards"**, which are distributed across nodes in a cluster. This approach enables horizontal scaling and fault tolerance.

Key concepts include:
- **Shards**: Each Elasticsearch index is divided into shards. A shard is a self-contained index that stores a subset of data.
    - **Primary shards** handle writes and indexing.
    - **Replica shards** are copies used for redundancy and to serve read requests.

- **Cluster Management**:
    - A central allocator distributes shards across nodes to ensure balance and resilience.
    - When new nodes join, shards are automatically redistributed.
    - Replica shards are placed on different nodes from their corresponding primaries for fault tolerance.

- **Data Representation**:
    - Data in Elasticsearch is stored as **JSON documents**.
    - Elasticsearch offers **RESTful APIs** for ingesting, updating, and querying these documents.

- **Resilience and Performance**:
    - Replica shards not only provide fault tolerance but also help scale read throughput.
    - Elasticsearch handles **leader election**, **node discovery**, and **cluster state coordination** internally.

In summary, Elasticsearch scales Lucene by making it distributed, resilient, and more user-friendly, enabling real-time search and analytics over large volumes of data. It’s widely used for log analytics, full-text search, and many other big data use cases.


![[Pasted image 20250625231308.png]]
The diagram above shows a single **node** in an Elasticsearch cluster. It is responsible for multiple shards of data, each of which has a Lucene index behind it.

### Top Elasticsearch Use Cases

- Elasticsearch for Text Search
- Elasticsearch for Analytics
- Elasticsearch for Application Performance Monitoring (APM)
- Elasticsearch for Security Information and Event Management (SIEM)
- The ELK Stack

### What is required out of a good search?

What is required out of a **good** search:
- The search should be **relevant and fast**
	- The search should return **relevant** search results, return statistics, and should do all that **quickly**
	- Good keyword searching is often not enough; you need some statistics on the results so you can narrow them down to what the user is interested in.
		- relevancy score
	- It should also allow searching **beyond exact matches**
- This is where Elasticsearch comes into picture

## Design and Architecture

### Core concepts

- **Index (noun)**:  
    A logical namespace for storing documents, similar to a _table_ in RDBMS. Internally, each Elasticsearch index is split into **shards**—each shard being a Lucene index.
- **Document**:  
    A JSON object containing fields and values, representing a single record—much like a _row_ in RDBMS.
- **Field**:  
    A key-value pair inside a document. Each field has a specific type (e.g., text, keyword, number, geo point) which determines how it is indexed and searched. Think of this as a _column_ in RDBMS.
- **Index (verb)**:  
    The operation of storing a document into an index, similar to an _INSERT_ in SQL.
- **Mapping**:  
    Defines the schema of documents within an index—specifying fields, their types, and indexing rules. This schema influences how documents are stored and queried. Mappings support advanced types like nested fields, arrays, geospatial data, and custom analyzers.
	The mapping is crucial because it tells Elasticsearch how to interpret the data you're storing.



![[Pasted image 20250625181945.png]]

### 📘 Lucene Field Types & Indexing

In Lucene, all fields are instances of the base class `Field`. These fields determine **how data is stored and indexed** in a Lucene document. Regardless of their original data type, **field values are internally stored as binary (`BytesRef`) streams**.
Each field has:
- `name` – the identifier of the field.
- `fieldsData` – the actual value (stored as binary).
- `type` – an instance of `FieldType` that defines how the field behaves during indexing and querying.

#### 🧩 Common Lucene Field Types

Some of the typical field classes include:
- `TextField`: stores full-text, analyzed (tokenized) data.
- `StringField`: stores keyword-like data, **not tokenized**, useful for exact matches.
- `LongPoint`, `IntPoint`, etc.: numeric types, optimized for range queries.
- `NumericDocValuesField`: for sortable/stored numeric values (used in sorting/aggregations).


![[Pasted image 20250627211044.png]]


#### 🛠️ `FieldType` – Controls Indexing Behavior

The `FieldType` class defines **how a field is indexed, stored, and queried**. Key attributes include:
- **`stored`**:  
    Whether the original value is stored (retrievable in search results). If `false`, only index/search is possible.
- **`tokenized`**:  
    Whether the value is broken into tokens. Needed for full-text search (e.g., `TextField`).
- **`termVector`**:  
    Stores term positions, offsets, and frequencies for highlighting/snippets.
- **`indexOptions`**:  
    Controls what details are stored in the inverted index:
    - `NONE` – Not indexed.
    - `DOCS` – Store document IDs only.
    - `DOCS_AND_FREQS` – Include term frequencies.
    - `DOCS_AND_FREQS_AND_POSITIONS` – Also store word positions.
    - `DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS` – Adds character offsets.
- **`docValuesType`**:  
    Enables forward indexing (doc-to-value mapping) for fast **sorting, faceting**, and **aggregations**.  
    Types: `NUMERIC`, `BINARY`, `SORTED`, `SORTED_NUMERIC`, `SORTED_SET`.
- **`dimension`**:  
    Supports multi-dimensional indexing (like latitude/longitude or spatial data). Lucene applies special tree-based indexing for efficient querying in such use cases.

### 🔄 Inverted Index in Elasticsearch

Elasticsearch relies on a **data structure called an _inverted index_** to make full-text searches fast and efficient. This structure is fundamental to how search engines retrieve results quickly, especially when dealing with large volumes of text.

You can think of an inverted index like the **index at the back of a book**—instead of listing pages and what’s on them, it lists each word and tells you which pages (or documents) contain that word.

#### How It Works

During the **indexing phase**, Elasticsearch processes each document by **tokenizing** the content of its text fields. Tokenization breaks down sentences into individual terms or words. Then, for every unique term, Elasticsearch stores:

- A list of document IDs where the term appears.
- Metadata like the frequency and position of the term within each document.

This structure lets Elasticsearch **quickly reverse-lookup** all documents that contain a given term—hence the term _inverted_ index (from term → document, instead of document → term).

#### Exact vs. Fuzzy Search
While inverted indices are perfect for **exact match** searches (e.g., “show me all documents containing the word _choice_”), they need help when the search is **fuzzy**, like dealing with typos, different word forms, or similar-sounding words.

This is where **text analysis** comes in. Elasticsearch can preprocess text using techniques like stemming, lowercasing, synonym expansion, etc., to normalize terms before indexing and improve the effectiveness of fuzzy and full-text searches.

### Text Analysis

Elasticsearch prepares data to make it efficiently stored and searchable. Elasticsearch cleans the text fields, breaks the text data into individual tokens, and enriches the tokens before storing them in the inverted indices. 
_The analyzer module_ manages the text analysis process.
![[Pasted image 20250625191844.png]]

**Character Filters**: It is applied on the character level, where every character of the text goes through these filters. They work by adding, removing, or changing characters in the input text. For instance, the built-in _HTML strip character filter_ purges HTML tags like `<h1>`, `<href>`, and `<src>` from the input text. Multiple character filters can be specified and they will be applied in order.

**Tokenizer**: It converts the input stream of characters into tokens based on certain criteria such as whitespace, punctuation, or some form of word boundaries.

**Token Filters:** They post-process the tokens from the tokenizer. For example, the token can change the case, create synonyms, or provide the root word (stemming), and so on.

**Normalizer:** It is similar to an analyzer except that it guarantees that the analysis chain produces a single token. They work with character filters and token filters that work on a character basis.

Example of the **standard (default) analyzer** in action:
![[Pasted image 20250625192120.png]]


![[Pasted image 20250627195920.png]]

### Relevance Scoring

Elasticsearch not only returns results that exact matches based on the query but also analyzes and returns _the most relevant results._ Returned results for full-text queries are sorted, usually, by a score, it calls a `_relevancy score_`. Relevancy is a positive floating-point number that determines the ranking of the search results. Elasticsearch uses the **BM25 (Best Match)** relevancy algorithm by default for scoring the return results so the client can expect relevant results.

![[Pasted image 20250625192241.png]]


There are three main factors involved in associating a relevancy score with the results:

1. **Term frequency (TF)**: How frequently the given term appears in the field. The higher the number of times the term appears in the document, the more likely the document is to be relevant.
2. **Inverse document frequency (IDF):** How frequently the term appears across all documents in the index. If it appears more commonly, then the term is less relevant. Common words such as “_the_” could appear many times in an index and a match against them is less important.
3. **Field length norm:** The same term appearing twice in a field with a length of 100 is more important than the term appearing twice in a field with a length of 1000.

### Architecture
![[Pasted image 20250627195740.png]]


![[Pasted image 20250625203041.png]]

### 📁 Segments in Elasticsearch

When Elasticsearch writes documents to a **shard**, it doesn’t immediately save them as individual files on disk. Instead, it creates **immutable file segments**, and each of these segments is essentially a self-contained **inverted index**. That means each segment holds a slice of your data that can be searched independently.

#### Why Immutability Matters

Because segments never change once written:

- They are **safe to use in multi-threaded environments**, as no thread can modify the data after it’s created. This reduces the risk of bugs caused by race conditions or shared state.
- They are **cache-friendly**. Since the files never change, the operating system can cache them in memory efficiently. Frequently accessed segments can stay in memory, making searches much faster.

This immutability is a deliberate design choice in **Lucene** (the library Elasticsearch is built upon) to balance performance, reliability, and search speed.

#### Segment Creation Process

New documents aren't immediately written to disk. Instead:
1. **They are first kept in memory** for performance.
2. When enough documents accumulate, a **refresh** operation occurs (by default, every second).
3. The in-memory data is then converted into a new **segment**, which becomes immediately **searchable**.
4. However, at this point, the data is not yet **durable**—it’s searchable, but not guaranteed to persist after a crash.

To guard against data loss, Elasticsearch maintains a **transaction log** (also called a translog), which keeps track of changes that haven’t been written to a segment yet. If there’s a crash, Elasticsearch can **replay** the transaction log to recover uncommitted data.
To ensure the data is permanently saved to disk, Elasticsearch performs a **flush**. Flushing commits all data (including the transaction log) into disk-based segments, making it fully durable.

In summary, segments are the fundamental, read-optimized building blocks of Elasticsearch's indexing strategy. Their immutability makes them safe, fast, and efficient—especially when combined with a smart refresh and transaction logging mechanism that balances write performance and data safety.

![[Pasted image 20250625203052.png]]
**on refresh:**
![[Pasted image 20250625203122.png]]
**on flushing:**
![[Pasted image 20250625205410.png]]

The shards are distributed across the cluster for availability and failover. Having multiple shards helps in increasing the search speed as the operation can be distributed across multiple nodes.

![[Pasted image 20250625210336.png]]

To **prevent data loss** and ensure **high availability**, Elasticsearch supports configuring **replica shards** at the index level. A **replica** is simply a **duplicate copy of a primary shard**. You can adjust the number of replicas dynamically at any time.
Replica shards serve two main purposes:
1. **Redundancy** – If a node hosting a primary shard fails, its replica can take over, preventing data loss.
2. **Scalability** – Since replicas can handle **read requests**, they help distribute the load during high-traffic periods, improving query performance.

Importantly, **replica shards are never stored on the same node** as their corresponding primary shard. Doing so would defeat their purpose—if that single node goes down, you'd lose both the primary and its backup. Elasticsearch ensures replicas are placed on different nodes to maximize fault tolerance and system resilience.


![[Pasted image 20250625210530.png]]

### Nodes And Cluster

In Elasticsearch, a **node** is a single running instance of the Elasticsearch server. When you start Elasticsearch, you're launching a node.
A group of these nodes working together forms a **cluster**. Even a single node by itself is technically a one-node cluster.

Nodes in a cluster can take on **specific roles** to divide responsibilities and improve performance:
- A node can perform **all roles**, but doing so in large-scale setups is not ideal.
- Instead, it's common to **separate nodes by function**, such as:
    - **Master nodes** – manage cluster state and coordinate tasks.
    - **Data nodes** – store and handle search/indexing data.
    - **Ingest nodes** – pre-process data before indexing.
    - **Coordinating nodes** – route requests, merge results.
	- **Alerting Node** - Runs all alerting jobs

Separating node roles like this helps maintain **cluster stability, scalability, and performance**, especially in production environments with large volumes of data and queries.


### Routing a Document to a Shard

When Elasticsearch indexes a document, it must decide **which primary shard** will store that document. This is done using a **routing algorithm**, which ensures that each document is stored on exactly **one** primary shard and can later be retrieved from the **same shard** using the same logic.

The routing process uses the following formula: `shard = hash(routing) % number_of_primary_shards`
- By default, the **routing value** is the document’s **ID**.
- Elasticsearch applies a hash function to this routing value.
- Then it takes the result modulo (`%`) the total number of primary shards to determine the target shard.

#### Why This Matters:
- Ensures **uniform distribution** of documents across shards.
- Makes **document retrieval efficient**, since Elasticsearch knows exactly which shard to query.
- **Primary shard count is fixed** when an index is created—changing it would break routing logic and make old documents unreachable.

### How Primary and Replica Shards Interact


![[Pasted image 20250625211745.png]]

![[Pasted image 20250625211806.png]]


🔄 Write Flow: Step-by-Step
- **Client Sends Request**  
    A client sends a write request (like adding or deleting a document) to a **coordinator node** (say, Node 1).
- **Routing to the Right Shard**  
    The coordinator node uses a **routing algorithm** to determine which **primary shard** the document should go to — e.g., Shard 0 on **Node 3**.
- **Primary Shard Executes**  
    Node 3 executes the write on its **primary shard** (Shard 0). If the operation succeeds, it **forwards the request to all replica shards** (e.g., on Node 1 and Node 2).
- **Replicas Acknowledge**  
    Each **replica shard** performs the same operation (index, update, or delete) and replies with success or failure to the **primary shard**.
- **Coordinator Responds to Client**  
    Once **all replicas respond**, the **primary shard informs the coordinator node**, which then **sends a success acknowledgment back to the client**.

#### 🔁 Important Notes:
- The **coordinator node waits** until all stages (primary and replicas) are complete before confirming to the client.
- All these steps happen **sequentially** and **synchronously** to maintain **data consistency**.
- This model ensures that **data is safely written to both the primary and its replicas**, increasing **fault tolerance and availability**.

### Distributed Search Execution

![[Pasted image 20250625213558.png]]


#### 🔍 How Search Works in a Cluster

Unlike CRUD operations that involve a single, known document (and hence a single shard), **search operations are distributed** because Elasticsearch doesn’t know in advance which documents will match a query. Matching documents can reside **anywhere** in the cluster.

#### 🧠 Step-by-Step: Distributed Search Execution

- **Coordinator Node Selected**  
    A node (e.g., Node A) receives the search request from a client and acts as the **coordinator node**. This role is not fixed—any node can be the coordinator.
- **Targeting Shards**  
    The coordinator identifies all **primary or replica shards** (collectively called the **replication group**) for the index/indices involved in the search.
- **Query Phase (Fan-out)**  
    The coordinator sends the query to **one copy of each shard** (either primary or replica) to **locally execute the search**.  
    Each shard searches its segment and returns **top N matching documents** (based on scoring or sorting).
- **Merge Phase (Fan-in)**  
    The coordinator receives these shard results, **merges and sorts** them to form the final list of matches.
- **Respond to Client**  
    The coordinator returns the **final aggregated results** back to the client.

#### 🧱 Cluster and Node Behavior

- When you start Elasticsearch, you’re starting a **node**.
- A group of nodes working together forms a **cluster**.
- Every node knows about every other node and can **route requests**.
- Each node can, by default, serve multiple roles:
    - **Master-eligible** (cluster coordination)
    - **Data** (holds shards)
    - **Ingest** (preprocess data)
    - **ML/Transform** (if enabled)

> As your cluster scales, it's best to **dedicate certain nodes** for specific roles like master, data, or ML jobs to optimize performance and stability.

#### 📌 Summary
- Search must **query all relevant shards** since matching documents could be anywhere.
- The **coordinator node handles query distribution and result aggregation**.
- This architecture allows Elasticsearch to handle **large-scale, distributed, and fast search queries efficiently**.

### 🌐 Types of Nodes in Elasticsearch

Elasticsearch nodes play different roles in a cluster. While a single node can perform all roles by default, in larger deployments, it's recommended to separate responsibilities for better performance and stability.

#### 1. Master-Eligible Node

These nodes are responsible for **cluster-wide metadata operations** such as
- Creating or deleting indices
- Tracking which nodes are part of the cluster
- Allocating shards to nodes

Only **one master node** is active at any time, elected from the pool of master-eligible nodes.
##### Types:
- **Dedicated Master Node**: Only has the `master` role. It doesn't store data or handle search/index requests. This isolation ensures cluster stability.
- **Voting-Only Node**: Participates in master elections but **never becomes the master**. Useful as a tiebreaker in even-sized clusters.

#### 2. Data Node

Data nodes **store and manage data**. They handle all heavy-lifting operations such as
- CRUD operations
- Search queries
- Aggregations
These tasks are **CPU-, memory-, and I/O-intensive**, so it’s important to monitor resource usage and scale data nodes accordingly.
> Separating data nodes from master nodes is a best practice for large clusters.

#### 3. Ingest Node

Ingest nodes are responsible for **pre-processing documents** before they are indexed.
They can run **pipelines** made of multiple **processors** (e.g., for geo-parsing, date conversion, or enrichment). If pipelines are resource-heavy, you can run dedicated ingest nodes.

#### 4. Coordinating-Only Node

This is a node that doesn't:
- Store data
- Act as a master
- Preprocess documents
It **only routes client requests** to appropriate data nodes and collects results. Think of it as a **smart load balancer** that helps offload work from other nodes in a busy cluster.
> Sometimes referred to as **client nodes**.

#### 5. Machine Learning Node _(if enabled)_

ML nodes **run anomaly detection jobs** and handle machine learning APIs. These are used in advanced observability and security use cases.
You can choose to allow a node to handle only ML API requests (without running jobs) by setting `node.ml: false`.

#### 6. Transform Node

These nodes run **data transforms**, which are used to pivot, group, and summarize large datasets into entity-centric indices. Useful for analytics and aggregations.

#### 🧠 Summary

|Node Type|Primary Responsibility|
|---|---|
|Master-eligible|Cluster coordination & metadata operations|
|Data node|Store data and handle indexing/search|
|Ingest node|Preprocess data before indexing|
|Coordinating-only|Route/search coordination (no data/storage role)|
|ML node|Run anomaly detection & ML jobs|
|Transform node|Execute transforms (entity summarization)|

## ELK stack

The **ELK Stack** stands for **Elasticsearch**, **Logstash**, and **Kibana**—a powerful trio of open-source tools commonly used together for centralized logging, monitoring, and analytics.

This stack allows you to collect logs from multiple sources, process and transform them, store them efficiently, and finally, search and visualize the data—all in near real time. It’s widely used for **application monitoring**, **infrastructure visibility**, **security analytics**, and **troubleshooting**.

### 💡 How It Works
The ELK stack follows a straightforward pipeline:
1. **Logstash** acts as the **data collector**. It gathers logs or metrics from various sources—servers, databases, files, etc.—processes them using filters, and forwards them to Elasticsearch.
2. **Elasticsearch** is the **search and analytics engine**. It stores the data in a structured way (using an inverted index) and makes it searchable and analyzable almost instantly.
3. **Kibana** serves as the **dashboard and visualization tool**. It lets users interact with the data in Elasticsearch using intuitive charts, graphs, tables, and maps.

This combination transforms raw, unstructured log data into rich, actionable insights.

### 🔧 The ELK Components

#### ✅ Elasticsearch (E)
The heart of the stack. Built on Apache Lucene, Elasticsearch is a highly scalable and distributed engine that stores data as JSON documents. It supports full-text search, structured queries, and fast aggregations—ideal for analyzing large volumes of log data in real time.

#### ✅ Logstash (L)
Logstash is the **data pipeline** component. It can pull in data from various sources like syslog, AWS, Kafka, or files, process it with custom filters (e.g., parsing, enriching, masking), and push the structured data into Elasticsearch. With 200+ plugins available, Logstash adapts well to different data formats and systems.

#### ✅ Kibana (K)
Kibana is the **front-end** of the stack. It allows users to explore data stored in Elasticsearch through visual dashboards. Whether you're tracking application performance, system health, or detecting anomalies, Kibana makes it easy to interpret your data through histograms, pie charts, time-series graphs, maps, and more.

### 🚀 Why Use ELK?
- Centralized logging from multiple applications and services
- Fast, real-time search and analytics
- Custom visualizations and dashboards
- Extensibility with plugins and community tools
- Scalable to large datasets and distributed environments

## Components and workflow
![[Pasted image 20250625232326.png]]
### Internal Components of Elasticsearch

1. **Cluster**:
- A cluster is a collection of one or more nodes (servers) that together hold the entire data and provide indexing and search capabilities across all nodes.
- Each cluster has a unique identifier, known as the cluster name.

2. **Node**:
- A node is a single server that is part of the cluster. It stores data and participates in the cluster's indexing and search capabilities.
- Each node has a unique identifier, and nodes are distinguished by their roles (master, data, ingest, etc.).

3. **Index**:
- An index is a collection of documents that have somewhat similar characteristics.
- For instance, you could have an index for customer data, another for product data, and another for order data.

4. **Document**:
- A document is a basic unit of information that can be indexed. It is expressed in JSON (JavaScript Object Notation) format.
- Each document resides in an index and has a unique identifier.

5. **Shard**:
- An index can be divided into multiple pieces called shards. This allows Elasticsearch to distribute and parallelize operations across a cluster.
- Each shard is a fully functional and independent "index" that can be hosted on any node in the cluster.

6. **Replica**:
- A replica is a copy of a shard. Replicas provide redundancy and high availability. If a node fails, the data can still be served from its replica.


### Data Flow in Elasticsearch

1. **Indexing**:
    - Adding a document assigns it to an index.
    - It gets routed to a shard using a hash.
    - The shard analyzes and saves the document, creating a searchable index and storing the original data.

2. **Searching**:
    - Elasticsearch identifies which indices and shards to search in response to a query.
    - The query is sent to all relevant shards.
    - Each shard searches locally and returns results.
    - Elasticsearch combines these results and sends them back to the client.


#### Data Structures used internally by Elasticsearch

Elasticsearch uses several key data structures internally to provide its fast and efficient search capabilities. The most important ones are:

##### 1. Inverted Index

The inverted index is the core data structure behind Elasticsearch's fast full-text search capabilities. An inverted index maps terms (words or tokens) to the documents that contain them, allowing quick lookups of documents based on the terms they include.

**Example**: For documents containing the following texts:

- Document 1: "Elasticsearch is powerful"
- Document 2: "Elasticsearch is scalable"

The inverted index might look like:

- `"Elasticsearch": [Document 1, Document 2]`
- `"is": [Document 1, Document 2]`
- `"powerful": [Document 1]`
- `"scalable": [Document 2]`

##### 2. Document Store

Documents in Elasticsearch are stored in a document-oriented database. Each document is a JSON object, and Elasticsearch stores and retrieves these documents efficiently.

##### 3. BKD Tree

Elasticsearch uses a specialized tree structure called a BKD (block k-d) tree for efficient indexing and searching of **numeric and geo-point data**. This tree structure helps in handling high-dimensional numeric data and provides efficient range queries and geo-distance searches.

##### 4. Doc Values

Doc values are a columnar storage format used by Elasticsearch to handle sorting, aggregations, and access to field values in documents efficiently. They allow Elasticsearch to access field values quickly without having to load entire documents.

##### 5. Finite State Transducers (FSTs)

FSTs are used to efficiently store and query prefix and exact match queries. They are particularly useful for handling autocomplete and suggestive search features.

##### 6. Priority Queues and Heaps

Elasticsearch uses priority queues and heaps internally for managing tasks like merging search results and handling top-k queries (e.g., fetching the top N search results).

##### 7. Segment Files

Elasticsearch indexes are divided into segments, and each segment is an inverted index. Segment files contain the actual data and metadata for the documents within that segment. When documents are added, deleted, or updated, Elasticsearch creates new segments and periodically merges them to optimize performance.


#### Sequence Diagram

![[Pasted image 20250626224745.png]]
Elasticsearch has a nice, clean REST API that makes it easy to perform these operations although there are plenty of GUIs and clients available.

## Usage
### Create an Index

A simple PUT request will create an index with a dynamic mapping, 1 shard, and 1 replica. These are parameters you can update after the index is created.

```
// PUT /books
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 1
  }
}
```

### Set a Mapping

If dynamic mapping isn't appropriate (maybe most of the fields in my data aren't searchable), I can set a mapping for the index up front. This lets Elasticsearch know that certain fields should be treated as searchable and what types to expect in those fields.

```
// PUT /books/_mapping
{
  "properties": {
    "title": { "type": "text" },
    "author": { "type": "keyword" },
    "description": { "type": "text" },
    "price": { "type": "float" },
    "publish_date": { "type": "date" },
    "categories": { "type": "keyword" },
    "reviews": {
      "type": "nested",
      "properties": {
        "user": { "type": "keyword" },
        "rating": { "type": "integer" },
        "comment": { "type": "text" }
      }
    }
  }
}
```
Here, we have pre-registered the fields I want to be searchable for my bookstore. When I add documents, Elasticsearch will extract values for these fields and index them so that they're ready to be searched.

### Add Documents

So I got an index and a mapping, great. Next, I need to add documents to the index! This is a simple HTTP POST to the `/_doc` endpoint.

```
// POST /books/_doc
{
  "title": "The Great Gatsby",
  "author": "F. Scott Fitzgerald",
  "description": "A novel about the American Dream in the Jazz Age",
  "price": 9.99,
  "publish_date": "1925-04-10",
  "categories": ["Classic", "Fiction"],
  "reviews": [
    {
      "user": "reader1",
      "rating": 5,
      "comment": "A masterpiece!"
    },
    {
      "user": "reader2",
      "rating": 4,
      "comment": "Beautifully written, but a bit sad."
    }
  ]
}
```

The request will return a document ID along with data about how the document was persisted across the cluster:

```
{
  "_index": "books",
  "_id": "kLEHMYkBq7V9x4qGJOnh",
  "_version": 1, // NOTE!
  "result": "created",
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  },
  "_seq_no": 0,
  "_primary_term": 1
}
```

Take special note of that `_version` field. This is a special field that Elasticsearch uses to ensure the document can be updated atomically.

### Updating Documents

Updating a document is similar to creating a document, but you need to specify the document ID in the URL. We _can_ raise our price by specifying the entire document:

```
// PUT /books/_doc/kLEHMYkBq7V9x4qGJOnh
{
  "title": "To Kill a Mockingbird",
  "author": "Harper Lee",
  "description": "A novel about racial injustice in the American South",
  "price": 13.99,
  "publish_date": "1960-07-11",
  "categories": ["Classic", "Fiction"],
  "reviews": [
    {
      "user": "reader3",
      "rating": 5,
      "comment": "Powerful and moving."
    }
  ]
}
```

And this might be appropriate in some instances, but it can be risky! If another process is updating the same document concurrently, you could overwrite their changes.

If we want to guard against this we can use the `_version` field from above to specify that we only want to update the document if the version matches. The following request will only update the document if the version is 1. Otherwise it will throw an error.

`// PUT /books/_doc/kLEHMYkBq7V9x4qGJOnh?version=1`

It uses **optimistic concurrency control**.

Finally, the `_update` endpoint (note POST) allows you to update some fields of a document without having to fetch the entire document.
```
// POST /books/_update/kLEHMYkBq7V9x4qGJOnh
{
  "doc": {
    "price": 14.99
  }
}
```


### Search

Ok, so we've got an index with documents, how do we actually search for them? Elasticsearch makes this straightforward! The Elasticsearch query syntax is very similar to that of SQL, and it's also JSON based which makes it very easy to work with.

A simple query might be to search for books with "Great" in the title:
```
// GET /books/_search
{
  "query": {
    "match": {
      "title": "Great"
    }
  }
}
```

We can also search for books with "Great" in the title that are priced less than 15 dollars:
```
// GET /books/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "title": "Great" } },
        { "range": { "price": { "lte": 15 } } }
      ]
    }
  }
}
```

Finally, we can search within our nested "reviews" field for books with an "excellent" review:
```
// GET /books/_search
{
  "query": {
    "nested": {
      "path": "reviews",
      "query": {
        "bool": {
          "must": [
            { "match": { "reviews.comment": "excellent" } },
            { "range": { "reviews.rating": { "gte": 4 } } }
          ]
        }
      }
    }
  }
}
```

The response might look like this:
```
{
  "took": 7,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 2,
      "relation": "eq"
    },
    "max_score": 2.1806526,
    "hits": [
      {
        "_index": "books",
        "_type": "_doc",
        "_id": "1",
        "_score": 2.1806526,
        "_source": {
          "title": "The Great Gatsby",
          "author": "F. Scott Fitzgerald",
          "price": 12.99
        }
      },
      {
        "_index": "books",
        "_type": "_doc",
        "_id": "2",
        "_score": 1.9876543,
        "_source": {
          "title": "Great Expectations",
          "author": "Charles Dickens",
          "price": 10.50
        }
      }
    ]
  }
}
```

### Sort

Once we've narrowed down the results to a set of books that we think are interesting, how do we sort them so that our users get the best results at the top of the page?

Sorting is a crucial feature in Elasticsearch that allows you to order your search results based on specific fields.

#### Basic Sorting

To sort results, you can use the sort parameter in your search query. Here's a basic example that sorts books by price in ascending order:
```
// GET /books/_search
{
  "sort": [
    { "price": "asc" }
  ],
  "query": {
    "match_all": {}
  }
}
```

You can also sort by multiple fields. For instance, to sort by price ascending and then by publish date descending:
```
// GET /books/_search
{
  "sort": [
    { "price": "asc" },
    { "publish_date": "desc" }
  ],
  "query": {
    "match_all": {}
  }
}
```

#### Sorting by Script

Elasticsearch also allows sorting based on custom scripts (using the "Painless" scripting language). This is useful when you need to sort by a computed value. Here's an example that sorts books by a discounted price (10% off) - which you would never do because the sort order is identical:
```
// GET /books/_search
{
  "sort": [
    {
      "_script": {
        "type": "number",
        "script": {
          "source": "doc['price'].value * 0.9"
        },
        "order": "asc"
      }
    }
  ],
  "query": {
    "match_all": {}
  }
}
```

#### Sorting on Nested Fields

When dealing with nested fields, you need to use a nested sort. This ensures that the sort values come from the same nested object. Here's how you might sort books by their highest review rating:
```
// GET /books/_search
{
  "sort": [
    {
      "reviews.rating": {
        "order": "desc",
        "mode": "max",
        "nested": {
          "path": "reviews"
        }
      }
    }
  ],
  "query": {
    "match_all": {}
  }
}
```


#### Relevance-Based Sorting

If we don't specify a sort order, Elasticsearch sorts results by relevance score (_score). This is configurable, but the default scoring algorithm is related closely to [TF-IDF](https://en.wikipedia.org/wiki/Tf%E2%80%93idf) (Term Frequency-Inverse Document Frequency).

### Pagination and Cursors

Our last concern after specifying how we filter and sort our results is how to get them back to the user, basically how we can paginate them. Pagination in Elasticsearch allows you to retrieve a subset of search results, typically used to display results across multiple pages. While we need to determine how we're going to specify the results on each page (either by number or by filtering criteria), we also need to consider whether we want to maintain state or re-run our search query on every page/request.

#### From/Size Pagination

This is the simplest form of pagination, where you specify:
- from: The starting index of the results
- size: The number of results to return

Example query:
```
// GET /my_index/_search
{
  "from": 0,
  "size": 10,
  "query": {
    "match": {
      "title": "elasticsearch"
    }
  }
}
```

However, this method becomes inefficient for deep pagination (e.g., beyond 10,000 results) due to the overhead of sorting and fetching all preceding documents. The cluster needs to retrieve and sort all these documents on each request, which can be prohibitively expensive.

#### Search After

This method is more efficient for deep pagination. It uses the sort values of the last result as the starting point for the next page. With these values we can restrict each page to only fetch the documents that come after the last document of the previous page, progressively restricting the search set.

Example:
```
// GET /my_index/_search
{
  "size": 10,
  "query": {
    "match": {
      "title": "elasticsearch"
    }
  },
  "sort": [
    {"date": "desc"},
    {"_id": "desc"}
  ],
  "search_after": [1463538857, "654323"]
}
```

The search_after parameter uses the sort values from the last result of the previous page. Here's how it works:
1. In your initial query, you don't include the search_after parameter.
2. From the results of your initial query, you take the sort values of the last document.
3. These sort values become the search_after parameter for your next query.

In the example above:
- 1463538857 is a timestamp (the date field's value for the last document in the previous page).
- "654323" is the _id of the last document in the previous page.

By providing these values, Elasticsearch knows exactly where to start for the next page, making it very efficient even for deep pagination. This approach ensures that:
- You don't miss any documents added in subsequent pages (even if new documents are added between requests).
- You don't get duplicate results across pages.

However, it requires maintaining state on the client side (remembering the sort values of the last document), and it doesn't allow random access to pages - you can only move forward through the results. This style of pagination also risks missing documents in prior pages if the underlying data is updated or deleted.

#### Cursors

Cursors in Elasticsearch provide a stateful way to paginate through search results, solving the problem of the documents shifting underneath you. Cursors maintain consistency across paginated requests, and thus require a lot more overhead than the pagination methods we've already discussed.

Elasticsearch uses the point in time (PIT) API in conjunction with search_after for cursor-based pagination:

**Create a PIT**:
```
// POST /my_index/_pit?keep_alive=1m
```
This returns a PIT ID.

**Use the PIT in searches**:
```
// GET /_search
{
  "size": 10,
  "query": {
    "match": {
      "title": "elasticsearch"
    }
  },
  "pit": {
    "id": "46To...",
    "keep_alive": "1m"
  },
  "sort": [
    {"_score": "desc"},
    {"_id": "asc"}
  ]
}
```

**For subsequent pages, add search_after**:
```
// GET /_search
{
  "size": 10,
  "query": {
    "match": {
      "title": "elasticsearch"
    }
  },
  "pit": {
    "id": "46To...",
    "keep_alive": "1m"
  },
  "sort": [
    {"_score": "desc"},
    {"_id": "asc"}
  ],
  "search_after": [1.0, "1234"]
}
```

**Close the PIT when done**:
```
// DELETE /_pit
{
  "id" : "46To..."
}
```

Using PITs with search_after provides a consistent view of the data throughout the pagination process, even if the underlying index is being updated.

## Questions

### Before sending the document to a shard and then creating an inverted index, Elasticsearch indexes the document in a certain shard. Explain.

**High-Level Document Indexing Flow in Elasticsearch**

1. **Document Received by Elasticsearch**

- You submit a document to Elasticsearch via an HTTP API:
```
POST /my-index/_doc
{
  "title": "Elasticsearch is fast",
  "tags": ["search", "engine"]
}
```

2. **Routing: Deciding the Target Shard**

- Elasticsearch determines **which shard** will hold this document based on:
	`shard = hash(_routing or _id) % number_of_primary_shards`

- The document is routed to one of the **primary shards** for indexing.
- You can override routing via the _routing field if needed.

> This is important because each primary shard independently maintains its own **inverted index** and **DocValues**.

3. **Document Sent to the Primary Shard**

- The selected primary shard receives the document.
- The primary shard performs:
    - **Parsing and mapping** the fields (based on index mappings).
    - **Analyzing text fields** (e.g., tokenization using analyzers).
    - **Generating inverted indexes** for searchable fields.
    - **Generating DocValues** for sortable/aggregatable fields.

4. **Lucene Indexing on Primary**

- Internally, Elasticsearch delegates to **Apache Lucene** to:
    - Build **inverted indexes** for text and keyword fields.
    - Create **DocValues** for numeric/date/keyword fields (if needed).
    - Buffer data in memory and write to the **translog** for durability.
    - Periodically flush data into on-disk **Lucene segments**.

> **Note**: At this point, the document is **indexed** but **not immediately searchable** until a refresh occurs (by default, every 1 second).


5. **Replication to Replica Shards**

- The document (or the resulting translog entry) is sent to **replica shards**.
- Replica shards **replay the same indexing logic** or **copy Lucene segments**, depending on the stage.
- The goal is to ensure replicas are eventually consistent with the primary.

**What Exactly Happens When a Document Is Indexed?**

Let’s use the "title": "Elasticsearch is fast" field (type: text) as an example:

**Inverted Index Creation:**
- An analyzer (e.g., standard analyzer) processes "Elasticsearch is fast":
    - Tokenizes to: "elasticsearch", "is", "fast"
- The inverted index maps:
```
"elasticsearch" → [doc#]
"is"            → [doc#]
"fast"          → [doc#]
```

**DocValues (if applicable):
- For fields like "tags": `["search", "engine"]` (if mapped as keyword):
    - Stored as columnar DocValues per document:
	`DocID → ["search", "engine"]`

**Final Notes:**
- The **inverted index is local to each shard** — there is no global inverted index.
- That’s why querying across shards requires Elasticsearch to **fan out** the query.
- Elasticsearch handles **all of this transparently**, so you don’t need to manually route or replicate — but understanding the internals helps optimize design and performance.


### Where does this index come into picture in terms of a search request workflow?

In **Elasticsearch**, an **index** plays a central role in the **search request workflow** — it defines **where the documents live** and how the **search query is routed and executed**.

> An **index is a logical namespace** for storing **documents of the same type or schema**, much like a **table** in a relational database — but more flexible.
> 
> **What is an Index in Elasticsearch?**
> An **index** is a **collection of documents** stored together and partitioned across one or more **shards**.
> 
> Each index:
    - Has a **name** (used in queries, e.g., search on my-index).    
    - Has a **mapping**: defines the structure of the documents (like types, analyzers, etc.).        
    - Stores documents with **some shared purpose or context**.

Let’s walk through the **search workflow** and show **where the index fits in**.

#### Elasticsearch Search Request Workflow (with Index Context)

0. **Search Request Initiated**
A user or app sends a search request:
```
GET /my-index/_search
{
  "query": {
    "match": { "title": "Elasticsearch" }
  }
}
```
Here, `my-index` tells Elasticsearch which index to search in.

> You specify which **index or index pattern** to search `(/index/_search)`
> 
> **Index Patterns:**
> You can search **multiple indices** using patterns:
> 	`GET /logs-*/_search`
> This will search across all matching indices (logs-2025-06-26, logs-2025-06-27, etc.).

1. **Index Name → Index Metadata Lookup**

- Elasticsearch checks if the specified index(es) exist (e.g., my-index).
- Loads index **mappings**, settings, and **shard routing information**.
- Validates the query against the mapping (e.g., does the title field exist and support match?).

2. **Query Routed to Shards of the Index**

Each index is divided into **shards** (primary and replicas). Elasticsearch:
- Picks **one active copy of each shard** (primary or replica).
- **Distributes the query** to all relevant shards **in parallel**.

So if my-index has 5 primary shards and 1 replica:
- The search will fan out to **5 shards** (one copy of each primary shard).

> 💡 The index defines **how many shards** exist and where they’re allocated — crucial for query routing.

3. **Each Shard Executes the Query (Local Search)**

- On each shard, Elasticsearch:
    - Uses the shard’s **local inverted index** to search for terms.
    - Applies analyzers and scoring.
    - Retrieves matching **document IDs and scores**.

Each shard returns a **partial result** (top N hits) back to the coordinating node.

4. Results merged on the coordinating node

- The coordinating node:    
    - Merges the partial results from all shards.
    - Re-sorts by relevance or sort criteria.
    - Applies pagination (from, size).
    - (Optional) Fetches full documents if _source is requested.

5. Final Response Returned
- The final result (top hits, aggregations, etc.) is sent back to the client.

### Is it mandatory to have the index? If yes, who creates the index (e.g. people index, product index, etc.)?

Yes — in **Elasticsearch, having an index is absolutely mandatory**.
There is **no way to store or search a document without an index**.

**Why Is an Index Mandatory?**

- An **index is the fundamental unit** of storage, routing, and query execution in Elasticsearch.    
- Every document you store must **belong to an index**.
- Indexes define:
    - **Where documents live** (which shards, on which nodes).
    - **How documents are structured** (mapping: field types, analyzers).
    - **How queries are processed** (via field-specific logic and settings).
So even for the simplest use case, Elasticsearch **requires an index**.


**Who Creates the Index?**

There are **two ways** an index can be created:  
1. **Explicit Index Creation (Recommended)**
You (or your application) **create the index explicitly** before indexing documents:
```
PUT /people
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "name": { "type": "text" },
      "age": { "type": "integer" }
    }
  }
}
```

This is the **best practice** because you:
- Control the **mapping** (schema).
- Define shard/replica count.
- Avoid automatic mapping conflicts.

2. **Implicit Index Creation**

If you send a document to a **nonexistent index**, Elasticsearch will (by default) **auto-create the index**:
```
POST /product/_doc
{
  "name": "Elasticsearch Guide",
  "price": 49.99
}
```
If product doesn’t exist, Elasticsearch will:
- Auto-create the index with **default settings**.
- Infer the mapping **dynamically** based on the first document.

> ⚠️ This is convenient, but **not recommended for production**:
> - Can lead to **mapping explosion**.
> - Field types may be **inferred incorrectly**.
> - Makes indexing behavior less predictable.

You can disable this behavior by setting:
`action.auto_create_index: false`

**Example:**
A simple Elasticsearch document might look like this:
```
{
  "_index": "my-first-elasticsearch-index",
  "_id": "DyFpo5EBxE8fzbb95DOa",
  "_version": 1,
  "_seq_no": 0,
  "_primary_term": 1,
  "found": true,
  "_source": {
    "email": "john@smith.com",
    "first_name": "John",
    "last_name": "Smith",
    "info": {
      "bio": "Eco-warrior and defender of the weak",
      "age": 25,
      "interests": [
        "dolphins",
        "whales"
      ]
    },
    "join_date": "2024/05/01"
  }
}
```
- `_index`: The name of the index where the document is stored.
- `_id`: The document’s ID. IDs must be unique per index.

### What are mapping structures?

In **Elasticsearch**, **mappings** are like **schemas** in traditional databases — they define the structure of the documents and the **data types of fields** stored in an **index**.

Mappings tell Elasticsearch:
- How to **store**, **index**, and **analyze** each field in a document.
- How the field behaves in search, sorting, and aggregations.

**What Is a Mapping?**

A **mapping** defines:
- **Field names** in documents.
- **Field data types** (e.g., text, keyword, integer, date).
- **Indexing options** (e.g., analyzed vs not analyzed).
- **DocValues** behavior (for sorting/aggregation).
- **Nested** or **object** relationships.

Every index in Elasticsearch has a **mapping** associated with it.

**Example Mapping Structure**
Here’s a mapping for a products index:
```
PUT /products
{
  "mappings": {
    "properties": {
      "name":      { "type": "text" },
      "brand":     { "type": "keyword" },
      "price":     { "type": "float" },
      "available": { "type": "boolean" },
      "tags":      { "type": "keyword" },
      "release":   { "type": "date" }
    }
  }
}
```

| **Field** | **Type** | **Purpose**                    |
| --------- | -------- | ------------------------------ |
| name      | text     | Full-text search (tokenized)   |
| brand     | keyword  | Exact match / aggregation      |
| price     | float    | Numerical sorting, filtering   |
| available | boolean  | True/false filtering           |
| tags      | keyword  | Exact tag match or aggregation |
| release   | date     | Range queries, date histogram  |

**Key Concepts in Mappings**

1. **Data Types**

Common field types:
- text: Analyzed full-text (e.g., product descriptions).
- keyword: Not analyzed, used for exact matches (e.g., tags, IDs).
	- field with **keyword** type is treated as a whole value rather than as a string that can be tokenized
- integer, float, double: Numeric fields.
- date: ISO 8601 or custom formats.
- boolean: true / false.
- object, nested: For structured or nested JSON.

2. **Analyzers (for text fields)**

- Defines **how text is broken down** into searchable terms.
- Default is standard analyzer (lowercases, removes punctuation).
- Custom analyzers allow things like stemming, synonyms, language support.

3. **Dynamic Mapping**

- Elasticsearch can automatically infer field types when you don’t define a mapping.
- This is called **dynamic mapping** and is convenient for prototyping.
- But in production, you usually want **explicit mappings** to avoid type conflicts.

4. **Disabling Fields**

You can exclude certain fields from indexing/search to save space:
`"enabled": false`

**Real-World Mapping Example: people index**
```
PUT /people
{
  "mappings": {
    "properties": {
      "name":        { "type": "text" },
      "email":       { "type": "keyword" },
      "birthdate":   { "type": "date" },
      "address": {
        "type": "object",
        "properties": {
          "city":   { "type": "keyword" },
          "zip":    { "type": "integer" }
        }
      }
    }
  }
}
```

**What Happens Without Mappings?**

If you don’t define a mapping:
- Elasticsearch auto-creates one based on the first few documents.
- This can cause:
    - **Wrong field types** (e.g., storing dates as text).
    - **Mapping conflicts** across indices.
    - **Hard-to-debug issues** in queries.

If a document contains **fields not defined in the mapping**, Elasticsearch will (by default) **dynamically add those fields to the mapping** and **index them**.


**Behavior Depends on the Mapping Configuration**

1. **Default Behavior: dynamic: true (default)**

- Elasticsearch will:    
    - Detect new fields.
    - Guess the field type based on the first value.
    - Add the field to the mapping.
    - Index it accordingly.
Example:
```
PUT /people/_doc/1
{
  "name": "Peter",
  "age": 30,
  "new_field": "hello"
}
```
If `new_field` wasn’t in the mapping before, it will be dynamically added as:
`"new_field": { "type": "text" }`

**dynamic: false**
- New fields will **be accepted in the document**, but:    
    - ❌ Not added to the mapping.
    - ❌ Not indexed or searchable.
    - ✅ Stored in _source, so retrievable in _source-based responses.

Example:
```
"mappings": {
  "dynamic": false,
  "properties": {
    "name": { "type": "text" }
  }
}
```
If you send:
```
{ "name": "Peter", "age": 30 }
```
- `name` is indexed
- `age` is stored in `_source` but not searchable

**dynamic: strict**
- New fields are **rejected**, and the indexing request will fail with an error.
- Use this to strictly enforce schema.

### How to disable indexing certain fields?

To **disable indexing** of certain fields in Elasticsearch (i.e., store them in _source but **make them non-searchable**), you can set the field’s index property to false in the **mapping**.

**How to Disable Indexing for a Field**

Example Mapping:
```
PUT /products
{
  "mappings": {
    "properties": {
      "name": {
        "type": "text"
      },
      "internal_notes": {
        "type": "text",
        "index": false   ←🔹 disables indexing
      }
    }
  }
}
```

What this does:
- name: Will be indexed and searchable.
- internal_notes: Will be stored in `_source`, but **not searchable, sortable, or filterable**.

**Trying to Query on Non-Indexed Fields**

If you try to query or filter on internal_notes, you’ll get an error like:
`"field [internal_notes] was not indexed and cannot be searched"`

### How are DocValues different from Inverted indexes?

In **Elasticsearch**, both **DocValues** and **Inverted Indexes** are key data structures used for indexing and querying, but they serve **different purposes** and are optimized for **different types of operations**.

**Inverted Index**
- **Used for:** Full-text search (e.g., match, term, phrase queries).
- **Purpose:** Fast retrieval of documents that contain a specific term.

**How It Works:**

An **inverted index** is a mapping from **terms → list of document IDs** where the term appears.

**Example:**
If you have documents:
- Doc 1: “Elasticsearch is fast”
- Doc 2: “Elasticsearch is scalable”

The inverted index might look like:
```
"elasticsearch" → [1, 2]
"fast"          → [1]
"scalable"      → [2]
```
This makes search operations efficient.

**DocValues**

- **Used for:** Sorting, aggregations, and scripting.
- **Purpose:** Fast **lookup by document** for field values (the inverse of inverted indexes).

**How It Works:**

A **columnar store** where field values are stored **per document** on disk, optimized for retrieval during aggregation/sorting.

**Example:**

If a field `age` has values:
- Doc 1: 25
- Doc 2: 30

The DocValues for the `age` field would look like:
```
Doc 1 → 25
Doc 2 → 30
```
This is ideal for accessing field values **per document** efficiently, especially during aggregations like avg(age) or sorting like sort by age.

|**Feature**|**Inverted Index**|**DocValues**|
|---|---|---|
|Access pattern|Term → Documents|Document → Field value|
|Optimized for|Search (match, term)|Aggregation, sorting, scripting|
|Storage format|Inverted (term → docs)|Columnar (field → per-doc values)|
|Stored in|Memory (mostly)|Disk (usually mmap’d)|
|Required for|Full-text search|Efficient sorting, aggregation|
|Data types|Mostly text|Numeric, keyword, date, etc.|

**Practical Implication in Elasticsearch**

- For text fields, **inverted index** is used (and **DocValues are disabled by default**).
- For keyword, numeric, date, etc., **DocValues are enabled by default** to allow sorting/aggregation.
- You **cannot sort or aggregate** on fields without DocValues (unless the field is keyword, numeric, etc.).

**Summary**

- **Inverted Index**: Great for “which documents contain this term?”
- **DocValues**: Great for “what value does this document have for this field?”

Both are complementary — Elasticsearch uses them depending on the type of query you’re running.

### What are DocValues? Explain with examples. How is document created with DocValue and give an example usage of DocValue.

**What Are DocValues in Elasticsearch?**

**DocValues** are a **columnar data structure** used by Elasticsearch (via Lucene) to **support fast operations on field values** such as:

- **Sorting**
- **Aggregations**
- **Scripting**
- **Faceting**

While the **inverted index** is optimized for full-text **search**, **DocValues** are optimized for **per-document lookups** (the opposite direction).

**How They Work (Conceptually)**

Think of:
- **Inverted Index** as: term → docIDs
- **DocValues** as: docID → field value(s)

This structure enables fast access to the field values of documents, which is critical for non-search operations like sorting or computing averages.

**When Are DocValues Created?**

- **Automatically enabled** for most field types:
    - keyword, numeric types (integer, float, long, etc.), date, boolean.
- **Disabled by default** for text fields (since you don’t typically sort/aggregate text).
- You can **manually enable or disable** them using the `doc_values` property in mappings.

**Example Mapping with DocValues**
```
PUT /products
{
  "mappings": {
    "properties": {
      "name": {
        "type": "text"
      },
      "category": {
        "type": "keyword"
      },
      "price": {
        "type": "float"
      },
      "created_at": {
        "type": "date"
      }
    }
  }
}
```
Here:
- name: No DocValues (it’s a text field).
- category, price, created_at: DocValues enabled **by default**.

**Document Creation (Indexing)**
```
POST /products/_doc
{
  "name": "Elasticsearch Guide",
  "category": "books",
  "price": 49.99,
  "created_at": "2024-12-01"
}
```

Elasticsearch:
- Creates an **inverted index** for name (for full-text search).
- Creates **DocValues** for category, price, created_at (for sorting, aggregation).

**Example Usage of DocValues**

1. **Sorting (on a keyword, numeric, or date field)** 
```
GET /products/_search
{
  "query": {
    "match": { "category": "books" }
  },
  "sort": [
    { "price": "asc" }
  ]
}
```
**DocValues on price** are used to retrieve values for sorting efficiently.

2. **Aggregations**
```
GET /products/_search
{
  "size": 0,
  "aggs": {
    "avg_price": {
      "avg": {
        "field": "price"
      }
    }
  }
}
```
**DocValues on price** allow computing the average without loading full _source.

3. **Scripting Example**
```
GET /products/_search
{
  "query": {
    "match_all": {}
  },
  "script_fields": {
    "discounted_price": {
      "script": {
        "source": "doc['price'].value * 0.9"
      }
    }
  }
}
```
`doc['price'].value` uses **DocValues** for fast field access in scripts.


**Disabling DocValues (e.g., if you want to save disk)**

You can do this if you **don’t need to sort/aggregate** on a field:
```
PUT /products
{
  "mappings": {
    "properties": {
      "price": {
        "type": "float",
        "doc_values": false
      }
    }
  }
}
```
But now, you **cannot sort or aggregate** on price.

### Does Elasticsearch have Document-based replication or Segment-based replication?

Elasticsearch uses **segment-based replication**, **not document-based replication**.
##### Document-Based Replication (e.g., MongoDB, Couchbase)
- Every write (document insert/update/delete) is **sent to both the primary and replica nodes**.
- Each replica processes the write **independently**.
- This leads to **higher consistency** per write operation, but also **higher write amplification** and more network I/O.
##### Segment-Based Replication (used by Elasticsearch)
- Elasticsearch **only sends writes to the primary shard**.
- The primary shard applies the change, writes it to its local **translog**, and later **flushes it into a Lucene segment**.
- Once a flush/commit occurs, the **entire segment** (a low-level Lucene file) is **replicated to the replica shards**.
##### Key points
- **Translog (transaction log)** is also replicated for durability until flush.
- Replicas **replay the translog** or receive new **segments** created by the primary.
- This makes segment-based replication **more efficient** for bulk indexing and search.
##### How It Works (Simplified Flow)
1. Document is sent to primary shard.
2. Primary writes to translog and updates in-memory buffer.
3. Acknowledgment is returned once the translog is **synced on both primary and replicas** (if replication.type = async, only primary needs to sync).
4. Periodically, Lucene segments are created (flush).
5. These segments are **replicated to replicas**, which **do not re-index the document**.


### Is this segment based replication Near-real-time (NRT) segment replication?

Yes — Elasticsearch’s **segment-based replication** is a form of **Near Real-Time (NRT) segment replication**, but it’s important to clarify what “NRT” and “segment replication” each mean in Elasticsearch:

##### What is Near Real-Time (NRT) in Elasticsearch?
- Elasticsearch is built on **Lucene**, which is **not** a truly real-time engine.
- When a document is indexed, it’s:
    - Written to the **translog** (write-ahead log).
    - Stored in **in-memory buffers**.
    - Only becomes **searchable** after a **refresh** (by default every 1 second).
- This means there is a small delay (typically ≤ 1 sec) between indexing a document and it being visible in search.

This is what we mean by **Near Real-Time (NRT)**: search reflects recent writes **after a short delay**.

##### Segment-Based Replication in Elasticsearch
- Elasticsearch **does not re-index documents** on replicas.
- Instead:
    - The **primary shard** does the indexing and creates **Lucene segments**.
    - These segments (or translog entries) are **replicated** to replicas.
    - Replicas **copy the segments** and **make them searchable** after their own refresh.

##### So Is It “NRT Segment Replication”?
Yes, in the sense that:
- Replication is **based on segments**, not individual documents.
- Segments are **shared after a delay**, i.e., after flushing or translog replay.
- Search visibility on replicas is **also near-real-time**, just like on the primary.
- Therefore, both **replication and search** on replicas are **near-real-time**.


### In Elasticsearch, how are DocValues different from Inverted indexes?

In **Elasticsearch**, both **DocValues** and **Inverted Indexes** are key data structures used for indexing and querying, but they serve **different purposes** and are optimized for **different types of operations**.

##### Inverted Index
- **Used for:** Full-text search (e.g., match, term, phrase queries).
- **Purpose:** Fast retrieval of documents that contain a specific term.

###### How It Works

An **inverted index** is a mapping from **terms → list of document IDs** where the term appears.

**Example:**
If you have documents:
- Doc 1: “Elasticsearch is fast”
- Doc 2: “Elasticsearch is scalable”

The inverted index might look like:
```
"elasticsearch" → [1, 2]
"fast"          → [1]
"scalable"      → [2]
```
This makes search operations efficient.

##### DocValues
- **Used for:** Sorting, aggregations, and scripting.
- **Purpose:** Fast **lookup by document** for field values (the inverse of inverted indexes).
###### How It Works
A **columnar store** where field values are stored **per document** on disk, optimized for retrieval during aggregation/sorting.

**Example:**

If a field age has values:
- Doc 1: 25
- Doc 2: 30

The DocValues for the age field would look like:
```
Doc 1 → 25
Doc 2 → 30
```
This is ideal for accessing field values **per document** efficiently, especially during aggregations like avg(age) or sorting like sort by age.

##### Practical Implication in Elasticsearch
- For text fields, **inverted index** is used (and **DocValues are disabled by default**).
- For keyword, numeric, date, etc., **DocValues are enabled by default** to allow sorting/aggregation.
- You **cannot sort or aggregate** on fields without DocValues (unless the field is keyword, numeric, etc.).


## References

https://www.elastic.co/guide/en/elasticsearch/reference/8.x/index.html
https://www.elastic.co/docs/api/doc/elasticsearch/operation/operation-bulk-1
https://www.elastic.co/docs/api/doc/elasticsearch/
https://www.elastic.co/docs/api/doc/kibana/
https://www.elastic.co/docs/api/doc/logstash/

https://medium.com/@emredalc/elasticsearch-architecture-1-91a145a5108b
https://medium.com/@emredalc/elasticsearch-architecture-2-9c278efbe627

https://www.instaclustr.com/blog/opensearch-and-elasticsearch-architecture/

https://www.elastic.co/search-labs/blog/stateless-your-new-state-of-find-with-elasticsearch

Videos:
[Hello Interview - Elasticsearch Deep Dive](https://www.youtube.com/watch?v=PuZvF2EyfBM)
https://www.hellointerview.com/learn/system-design/deep-dives/elasticsearch

[Elasticsearch Course for Beginners - freeCodeCamp](https://www.youtube.com/watch?v=a4HBKEda_F8)

https://www.youtube.com/@OfficialElasticCommunity
https://www.youtube.com/watch?v=hO7HBVZJX_Q&list=PL_mJOmq4zsHbcdoeAwNWuhEWwDARMMBta

**BKD Trees:**
https://medium.com/@nickgerleman/the-bkd-tree-da19cf9493fb
https://medium.com/swlh/bkd-trees-used-in-elasticsearch-40e8afd2a1a4
https://opendsa-server.cs.vt.edu/ODSA/Books/CS3/html/KDtree.html