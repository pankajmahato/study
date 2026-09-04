## Links

https://www.youtube.com/watch?v=2X2SO3Y-af8

https://medium.com/@joudwawad/dynamodb-dax-3c2a7aa39fe1


## Motivation & Overview

**Database as a service** is an example of a managed database where any hardware provisioning, scaling, or shrinking of resources with dynamic load, Sharding, replication, and many database administrative tasks is performed by the system. The end users of such a managed database use queries to interact with it. AWS’s DynamoDB is an example of such a database

AWS DynamoDB is a **fully managed NoSQL database service** that offers **reliable performance**, **scalability**, and **security** for modern applications. It is widely used by companies like **Lyft**, **Airbnb**, and **Nordstrom** to power their core applications.

### **Key Capabilities of DynamoDB**

1. **Key-Value and Document Data Model**:
    
    - **Tables and Items**: DynamoDB stores data in **tables**, which contain collections of **items**. Each item is composed of **attributes**, similar to columns in a relational database.
    - **Flexible Data Types**: Attributes can be **scalar types** (e.g., strings, numbers, binary values) or **document types** (e.g., JSON-like nested structures).
    - **Schemaless Design**: Tables do not enforce a schema, allowing each item to have different attributes. This enables **rapid application development** and iteration.
    - **Primary Key**: Each item is uniquely identified by a **primary key**, which consists of a **partition key** (used for hashing) and an optional **sort key** (for logical ordering).
    
2. **Push-Button Scaling and High Availability**:
    
    - **Automatic Scaling**: DynamoDB handles infrastructure provisioning and scaling behind the scenes. You only need to specify the **read and write capacity units** for a table.
    - **Seamless Scaling**: As application demands grow, DynamoDB automatically partitions data and workload across more servers, ensuring **high performance** at any scale.
    - **High Availability**: Data is replicated across multiple **AWS Availability Zones (AZs)** within a region, providing **built-in fault tolerance** and **data durability**.
    - **Multi-Region Replication**: You can enable **global tables** to keep DynamoDB tables synchronized across AWS regions, protecting against region-specific outages.

3. **Advanced Security Features**:
    
    - **Encryption at Rest**: DynamoDB supports **AES-256 encryption** using AWS Key Management Service (KMS) to secure data.
    - **Granular Access Control**: Use **IAM policies** to precisely control which users or resources can access specific DynamoDB operations.
    - **Item-Level Permissions**: Temporary credentials can restrict access to specific item attributes, enhancing security.
    - **Audit Logs**: DynamoDB provides **CloudTrail integration** for visibility into access and modification of tables.

4. **Automatic Indexing and Query Flexibility**:
    
    - **Primary Key Indexing**: DynamoDB automatically creates and maintains indexes on primary key attributes for **efficient data access**.
    - **Query APIs**: Flexible query APIs allow you to retrieve data using the **partition key** or a combination of **partition and sort keys**.
    - **Secondary Indexes**: You can create **global secondary indexes (GSIs)** and **local secondary indexes (LSIs)** to enable queries on non-key attributes.
    - **Automatic Index Maintenance**: DynamoDB manages index updates automatically as data changes.

5. **In-Memory Caching for Internet-Scale Applications**:
    
    - **DynamoDB Accelerator (DAX)**: DAX is a fully managed **in-memory cache** that reduces read request times from **milliseconds to microseconds**.
    - **Performance at Scale**: DAX brings computing closer to the storage layer, minimizing network round trips and delivering **internet-scale performance**.
    - **Managed Features**: DAX handles replication, encryption, and in-memory request routing, allowing developers to focus on business logic.

6. **Integration with AWS Services**:
    
    - **DynamoDB Streams**: Capture data modifications and trigger **AWS Lambda** functions for real-time processing.
    - **AWS Step Functions**: Build scalable workflows and data pipelines using DynamoDB and Step Functions.
    - **Data Analysis**: Analyze DynamoDB data changes using **Amazon EMR** and **Amazon Redshift**.
    - **Machine Learning**: Use **Amazon SageMaker** to build advanced machine learning models on DynamoDB data.
    - **Backup and Restore**: Automate backups and restores using **AWS Backup**.

#### **Conclusion**

AWS DynamoDB is a powerful, fully managed NoSQL database service that combines **performance**, **scalability**, and **security** to meet the demands of modern applications. Its **flexible data model**, **automatic scaling**, and **advanced features** make it an excellent choice for businesses of all sizes. Whether you’re building a small application or a global-scale system, DynamoDB provides the tools and reliability you need to succeed.

## Architecture, Design and Use
### High Level Design

There are numerous components in the system, but we will focus on some salient ones.
![]()![High-level architecture of the system](https://www.educative.io/api/collection/10370001/6389837936197632/page/4985166665351168/image/5587228665053184?page_type=collection_lesson&get_optimised=true&collection_token=JKnWkmZVIqQdVjFDzmTXsN "High-level architecture of the system")

High-level architecture of the system

#### **1. Request Router**

1. **Role**:
    - The **Request Router** is responsible for directing incoming read/write requests to the appropriate **partition** and **node** in the DynamoDB cluster.
2. **Functionality**:
    - Uses the **partition key** to determine the correct partition for a request.
    - Routes requests to the **leader node** of the replication group for writes or to any node for reads.
3. **Importance**:
    - Ensures **efficient request handling** and **load balancing** across the cluster.

---

#### **2. Global Admission Control (GAC)**

1. **Role**:
    - **Global Admission Control (GAC)** manages **throughput allocation** and **resource utilization** across the DynamoDB cluster.
2. **Functionality**:
    - Tracks **token buckets** for each partition to enforce **workload isolation** and **fair resource allocation**.
    - Allows partitions to **burst** temporarily using unused throughput from other partitions.
3. **Importance**:
    - Prevents **throttling** and ensures **consistent performance** during traffic spikes.

---

#### **3. Authentication System**

1. **Role**:
    - The **Authentication System** ensures that only authorized users and applications can access DynamoDB resources.
2. **Functionality**:
    - Uses **AWS Identity and Access Management (IAM)** to define granular permissions for DynamoDB operations.
    - Supports **temporary credentials** for short-term access.
3. **Importance**:
    - Protects sensitive data and ensures **secure access** to DynamoDB tables.

---

#### **4. Partition Metadata System**

1. **Role**:
    - The **Partition Metadata System** maintains information about the **location**, **state**, and **configuration** of partitions in the DynamoDB cluster.
2. **Functionality**:
    - Tracks partition **key ranges**, **replica locations**, and **leader nodes**.
    - Updates metadata during **partition splitting**, **merging**, or **replication**.
3. **Importance**:
    - Ensures **efficient routing** of requests and **scalability** of the DynamoDB cluster.

### Schema

DynamoDB is a **schemaless NoSQL database**, which means it does not enforce a rigid schema like traditional relational databases (e.g., MySQL or PostgreSQL). Instead, DynamoDB offers a **flexible data model** that allows you to store and manage data in a way that best suits your application’s needs. Here’s a detailed look at how schema works in DynamoDB:


![No fixed schema required](https://www.educative.io/api/collection/10370001/6389837936197632/page/4985166665351168/image/5000301989593088?page_type=collection_lesson&get_optimised=true&collection_token=JKnWkmZVIqQdVjFDzmTXsN "No fixed schema required")


We have chosen a NoSQL database because of its flexibility with highly functional APIs, easy scalability, performance at scale, and high availability.

#### **Key Concepts of DynamoDB Schema**

1. **Tables**:
    
    - DynamoDB organizes data into **tables**, which are collections of items.
    - Each table is independent, and you can create as many tables as needed for your application.
2. **Items**:
    
    - An **item** is a single data record in a table, similar to a row in a relational database.
    - Each item is composed of **attributes**, which are key-value pairs.
3. **Attributes**:
    
    - An **attribute** is a fundamental data element in DynamoDB, similar to a column in a relational database.
    - Attributes can store **scalar values** (e.g., strings, numbers, binary data) or **complex types** (e.g., lists, maps, sets).
4. **Primary Key**:
    
    - Every table must have a **primary key**, which uniquely identifies each item in the table.
    - There are two types of primary keys:
        - **Simple Primary Key**: Consists of a single attribute (the **partition key**).
        - **Composite Primary Key**: Consists of two attributes (the **partition key** and the **sort key**).


![[dynamodb-partition-key-1.gif]]
#### **Schemaless Nature of DynamoDB**

1. **Flexible Data Model**:
    - DynamoDB does not enforce a fixed schema for items in a table.
    - Each item can have a **different set of attributes**, allowing you to store heterogeneous data in the same table.
2. **Dynamic Attributes**:
    - You can add or remove attributes from items at any time without modifying the table structure.
    - This flexibility enables **rapid iteration** and **adaptability** as your application evolves.
3. **No Data Type Enforcement**:
    - DynamoDB does not enforce data types for attributes across items.
    - For example, one item can have an attribute `age` as a number, while another item can have `age` as a string.


#### **Schema Design Considerations**

1. **Primary Key Design**:
    - The primary key is the most critical aspect of DynamoDB schema design.
    - Choose a partition key that ensures **even data distribution** across partitions.
    - Use a sort key to **logically group related items** (e.g., timestamps for time-series data).
2. **Access Patterns**:
    - Design your schema based on your application’s **query patterns**.
    - Use **secondary indexes** (Global Secondary Indexes or Local Secondary Indexes) to support queries on non-key attributes.
3. **Data Modeling**:
    - DynamoDB encourages **denormalized data models** to minimize the need for joins.
    - Store related data together in a single item or table to optimize for **single-query access**.
4. **Attribute Naming**:
    - Use consistent and meaningful attribute names to improve readability and maintainability.
    - Avoid overly long attribute names to reduce storage costs.

#### **Example Schema in DynamoDB**

Consider a table for storing user data:
#### Table: `Users`

|Partition Key (UserID)|Sort Key (Email)|Attributes|
|---|---|---|
|U123|[john@example.com](mailto:john@example.com)|`Name: "John Doe"`, `Age: 30`, `Address: {City: "New York", Zip: "10001"}`|
|U456|[jane@example.com](mailto:jane@example.com)|`Name: "Jane Smith"`, `Age: 25`, `Phone: "123-456-7890"`|
|U789|[bob@example.com](mailto:bob@example.com)|`Name: "Bob Johnson"`, `Address: {City: "San Francisco", Zip: "94105"}`|

- **Partition Key**: `UserID` (unique identifier for each user).
- **Sort Key**: `Email` (additional identifier for logical grouping).
- **Attributes**: Each item has different attributes, demonstrating DynamoDB’s schemaless nature.

#### **Advantages of Schemaless Design**

1. **Flexibility**:
    - DynamoDB’s schemaless design allows you to store **diverse data types** and **evolve your data model** over time.
2. **Rapid Development**:
    - You can start storing data immediately without defining a schema, speeding up **application development**.
3. **Scalability**:
    - The schemaless nature supports **horizontal scaling**, as data can be distributed across partitions without schema constraints.
4. **Cost Efficiency**:
    - You only store the attributes you need, reducing storage costs compared to rigid schemas with unused columns.

#### **Challenges of Schemaless Design**

1. **Data Consistency**:
    - Without a schema, ensuring **data consistency** and **integrity** becomes the responsibility of the application.
2. **Query Complexity**:
    - Supporting complex queries may require careful schema design and the use of **secondary indexes**.
3. **Documentation**:
    - Since the schema is not enforced, maintaining **documentation** of the data model is crucial for long-term maintainability.

#### **Best Practices for Schema Design**

1. **Plan for Access Patterns**:
    - Design your schema based on how your application will query the data, not just how the data is structured.
2. **Use Secondary Indexes**:
    - Create **Global Secondary Indexes (GSIs)** or **Local Secondary Indexes (LSIs)** to support queries on non-key attributes.
3. **Normalize When Necessary**:
    - While DynamoDB encourages denormalization, **normalize data** when it reduces redundancy and improves consistency.
4. **Monitor and Optimize**:
    - Use DynamoDB’s monitoring tools (e.g., CloudWatch) to track performance and optimize your schema as needed.

### Indexes
DynamoDB provides **indexes** to enable efficient querying of data beyond the primary key. Indexes allow you to access data using **non-key attributes** and support **flexible query patterns**. DynamoDB supports two types of indexes: **Primary Indexes** and **Secondary Indexes**. Here’s a detailed explanation of all indexes in DynamoDB:

#### **1. Primary Index**

1. **What is a Primary Index?**
    - Every DynamoDB table has a **primary index**, which is automatically created when the table is created.
    - The primary index is used to uniquely identify each item in the table.
2. **Types of Primary Keys**:
    - **Simple Primary Key**: Consists of a single attribute (the **partition key**).
        - Example: `UserID` (partition key).
    - **Composite Primary Key**: Consists of two attributes (the **partition key** and the **sort key**).
        - Example: `UserID` (partition key) and `OrderID` (sort key).
3. **Querying with Primary Index**:
    - You can query items using the **partition key** (for simple primary keys) or a combination of **partition key** and **sort key** (for composite primary keys).
    - Example Query: Retrieve all orders for a specific user (`UserID = 123`).

#### **2. Secondary Indexes**
Secondary indexes allow you to query data using **non-key attributes**. DynamoDB supports two types of secondary indexes:

##### **a. Global Secondary Index (GSI)**

1. **What is a GSI?**    
    - A **Global Secondary Index** (GSI) is an index with a **partition key** and an optional **sort key** that can be different from the primary key of the table.
    - GSIs are **sparse**, meaning they only include items that have the specified index attributes.
2. **Key Features**:
    - **Global Scope**: GSIs span all partitions of the table, allowing queries across the entire dataset.
    - **Flexible Schema**: You can define a GSI on any attributes, regardless of the primary key.
    - **Projection**: You can choose which attributes to include in the GSI (e.g., **ALL**, **KEYS_ONLY**, or **INCLUDE** specific attributes).
3. **Use Cases**:
    - Querying data using **non-key attributes** (e.g., find all users by `Email`).
    - Supporting **alternative query patterns** (e.g., find all orders by `Status`).
4. **Example**:
    - Table: `Orders`
        - Primary Key: `UserID` (partition key), `OrderID` (sort key).
    - GSI: `StatusIndex`
        - Partition Key: `Status`
        - Sort Key: `OrderDate`
    - Query: Retrieve all orders with `Status = "Shipped"`.

#### **b. Local Secondary Index (LSI)**

1. **What is an LSI?**    
    - A **Local Secondary Index** (LSI) is an index that has the same **partition key** as the table but a different **sort key**.
    - LSIs are **local** to the partition, meaning they only include items within the same partition as the primary key.
2. **Key Features**:
    - **Local Scope**: LSIs are tied to the partition key of the table, so they only index items within the same partition.
    - **Same Partition Key**: The partition key of the LSI must match the partition key of the table.
    - **Projection**: Similar to GSIs, you can choose which attributes to include in the LSI.
3. **Use Cases**:
    - Querying data within a partition using a **different sort key** (e.g., find all orders for a user by `OrderDate`).
    - Supporting **alternative sort orders** within a partition.
4. **Example**:
    - Table: `Orders`
        - Primary Key: `UserID` (partition key), `OrderID` (sort key).
    - LSI: `OrderDateIndex`
        - Partition Key: `UserID`
        - Sort Key: `OrderDate`
    - Query: Retrieve all orders for `UserID = 123` sorted by `OrderDate`.

#### **3. Comparison of GSI and LSI**

| Feature           | Global Secondary Index (GSI)           | Local Secondary Index (LSI)                |
| ----------------- | -------------------------------------- | ------------------------------------------ |
| **Scope**         | Global (spans all partitions)          | Local (within the same partition)          |
| **Partition Key** | Can be different from the table        | Must match the table’s partition key       |
| **Sort Key**      | Optional and can be different          | Must be different from the table           |
| **Creation Time** | Can be created or deleted anytime      | Must be created at table creation          |
| **Throughput**    | Consumes separate provisioned capacity | Shares provisioned capacity with the table |
| **Use Case**      | Alternative query patterns             | Alternative sort orders within a partition |

#### **4. Projection**

1. **What is Projection?**
    - **Projection** determines which attributes are copied (or projected) from the table into the index.
    - You can choose to include **ALL**, **KEYS_ONLY**, or **INCLUDE** specific attributes.
2. **Projection Types**:
    - **ALL**: All attributes from the table are projected into the index.
    - **KEYS_ONLY**: Only the primary key attributes are projected.
    - **INCLUDE**: Specific attributes are projected in addition to the primary key.
3. **Example**:
    - GSI: `StatusIndex`
        - Projection: `INCLUDE` (`OrderDate`, `TotalAmount`).

#### **Best Practices for Indexes**

1. **Choose the Right Index**:
    - Use **GSIs** for global queries and **LSIs** for partition-specific queries.
    - Avoid creating unnecessary indexes to minimize costs and complexity.
2. **Optimize Projection**:
    - Use **KEYS_ONLY** or **INCLUDE** projections to reduce storage and costs.
    - Only project attributes that are needed for queries.
3. **Monitor Performance**:
    - Use **CloudWatch metrics** to monitor index performance and adjust provisioned capacity as needed.
4. **Plan for Access Patterns**:
    - Design indexes based on your application’s **query patterns** to ensure efficient data access.

### Partitioning and Replication of Tables Across Nodes

In a **multi-tenant architecture**, efficient **partitioning** and **replication** of tables across nodes are critical for achieving **horizontal scalability**, **high availability**, and **consistent performance**. Here’s a detailed explanation of how this can be achieved:

#### **Horizontal Partitioning**

1. **What is Horizontal Partitioning?**
    - Horizontal partitioning involves splitting a table by **rows** rather than columns.
    - Each partition contains a subset of the table’s rows, and all partitions share the same schema.
2. **Why Use Horizontal Partitioning?**
    - **Schemaless Data**: Since our data does not have a fixed schema, horizontal partitioning is more suitable than vertical partitioning.
    - **Throughput Distribution**: It allows us to distribute the **read/write throughput** across multiple nodes, ensuring scalability.
    - **Storage Flexibility**: Different partitions can have varying storage and throughput requirements based on their data.
3. **How Partitioning Works**:
    - Each table is divided into **partitions**, with each partition hosting a **disjoint part of the table’s key range**.
    - The number of partitions increases in the following scenarios:
        - When the user increases the **provisioned throughput** beyond what the existing partitions can handle.
        - When an existing partition **fills up** or requires more resources.

![[p1.png]]

![[p2.png]]

The partition key in this example is "ClothesType," and the sort key is "Size." Only the partition key is used to determine the partition the item belongs to; the sort key is not used yet. The sort key is used to determine the position of the item among other items with the same partition key.
#### **Accessing Partitions**

1. **Primary Key Design**:
    - The primary key for a table can have one of two schemas:
        - **Partition Key Only**: A single attribute used to determine the partition.
        - **Composite Key**: A combination of a **partition key** and a **sort key**.
    - The partition key is used as input to an **internal hash function** to determine the partition where the item is stored.
2. **Determining the Partition**:
    - For both schemas, the partition key is hashed to identify the partition.
    - Items within a partition are stored in **ascending or descending order** based on the sort key (if applicable).
3. **Example Scenarios**:
    - **Partition Key Only**: The partition key (e.g., `ClothesType`) is hashed to locate the partition.
    - **Composite Key**: The partition key (e.g., `ClothesType`) is hashed to locate the partition, and the sort key (e.g., `Size`) is used to order items within the partition.

#### **Replication and Consistency**

1. **Replication Groups**:
    - Each partition is replicated across multiple nodes to ensure **high availability** and **data durability**.
    - The replicas of a single partition form a **replication group**.
2. **Global Consistency**:
    - Replication groups maintain **consistent replicas** across the global network using protocols like **Multi-Paxos**.
    - This ensures that all replicas of a partition are synchronized, even in the presence of failures or network partitions.
3. **Benefits of Replication**:
    - **Fault Tolerance**: If a node fails, the system can continue operating using replicas on other nodes.
    - **Low Latency**: Replicas can be placed closer to users to reduce access latency.
    - **Data Durability**: Multiple copies of data ensure that it is not lost even in the event of hardware failures.

#### **Additional Considerations**

1. **Partition Management**:
    - Automate partition management to handle **scaling up/down** and **rebalancing** as data grows or shrinks.
    - Monitor partition health and performance to ensure optimal resource utilization.
2. **Replication Strategy**:
    - Choose a replication strategy (e.g., **synchronous** or **asynchronous**) based on the application’s consistency and latency requirements.
    - Use **quorum-based writes** to ensure data consistency across replicas.
3. **Conflict Resolution**:
    - Implement mechanisms to resolve conflicts that may arise during replication (e.g., **last-write-wins** or **application-specific reconciliation**).
4. **Global Distribution**:
    - For globally distributed applications, replicate partitions across **multiple regions** to ensure low latency and high availability.
    - Use **multi-region replication** to protect against region-specific outages.


### Automated Adaptation to Traffic Patterns

In a distributed database system, **automated adaptation to traffic patterns** is crucial for maintaining **performance**, **scalability**, and **availability**. This involves dynamically adjusting **partitioning**, **throughput allocation**, and **resource utilization** based on real-time traffic demands. Here’s a detailed explanation of how this can be achieved:

#### **Provisioned Throughput and Partitioning**

1. **Provisioned Throughput**:
    - Users can set **provisioned throughput** for their tables, which defines the **read capacity units (RCUs)** and **write capacity units (WCUs)** the table can handle.
    - **RCU**: The ability to complete one read request of an item of arbitrary size (e.g., 4 KB).
    - **WCU**: The ability to complete one write request of an item of the same size.
2. **Initial Partitioning**:
    - The initial partitioning of a table divides its provisioned throughput **equally across all partitions**.
    - This works well if the **key access patterns** are uniform, but real-world applications often have **skewed access patterns**.
3. **Challenges with Uniform Partitioning**:
    - **Hot Partitions**: Some partitions may experience higher traffic due to frequently accessed keys, leading to **throttling** (rejected requests).
    - **Underutilized Partitions**: Other partitions may remain underutilized, wasting resources.

#### **Adaptive Partitioning**

1. **Dynamic Partition Splitting**:
    - When a partition becomes **overloaded** (e.g., due to high traffic or insufficient throughput), it is **split into multiple partitions**.
    - The key range of the original partition is divided, and its throughput is **equally distributed** among the new partitions.
    - Example: If a partition handling the key range `A-Z` is split, it might be divided into `A-M` and `N-Z`.
2. **Throughput Dilation**:
    - Splitting a partition increases the **total throughput** available to the table, as each new partition inherits a portion of the original partition’s throughput.
3. **Handling Hot Keys**:
    - If a specific key (or key range) is frequently accessed, the partition containing that key can be split to **distribute the load**.

![](https://www.educative.io/api/collection/10370001/6389837936197632/page/4985166665351168/image/6416176177217536?page_type=collection_lesson&get_optimised=true&collection_token=JKnWkmZVIqQdVjFDzmTXsN "A table partition experiencing high load (left figure) is split and distributed to different nodes (right figure) to meet throughput demands")

A table partition experiencing high load (left figure) is split and distributed to different nodes (right figure) to meet throughput demands

![](https://www.educative.io/api/collection/10370001/6389837936197632/page/4985166665351168/image/5325336323293184?page_type=collection_lesson&get_optimised=true&collection_token=JKnWkmZVIqQdVjFDzmTXsN "In the illustration above, we've added another partition to a table. We did this because there was a request to insert an item with the key "ClothesType" and the value "Shorts." This key is in the key range covered by the partition (old partition), and the partition was full. Now, the table has two partitions, and its throughput is equally divided among its partitions. Half of the old partition's key range is covered by new partition 1 and the other half by new partition 2.")

In the illustration above, we've added another partition to a table. We did this because there was a request to insert an item with the key "ClothesType" and the value "Shorts." This key is in the key range covered by the partition (old partition), and the partition was full. Now, the table has two partitions, and its throughput is equally divided among its partitions. Half of the old partition's key range is covered by new partition 1 and the other half by new partition 2.
#### **Bursting**

1. **What is Bursting?**:
    - Bursting allows a partition to **temporarily use unused throughput** from other partitions on the same node.
    - This helps absorb **short-lived traffic spikes** without permanently reallocating resources.
2. **Token Bucket System**:
    - Each partition has two token buckets:
        - **Allocation Bucket**: Tracks the partition’s **provisioned throughput**.
        - **Burst Bucket**: Tracks **available burst capacity**.
    - Requests are served if tokens are available in either bucket.
3. **Workload Isolation**:
    - Bursting is implemented in a way that maintains **workload isolation**, ensuring that one partition’s burst does not negatively impact others.
4. **Limitations of Bursting**:
    - Bursting is effective for **short-term spikes** but cannot handle **long-term throttling** or sustained high traffic.

![](https://www.educative.io/api/collection/10370001/6389837936197632/page/4985166665351168/image/5227334095077376?page_type=collection_lesson&get_optimised=true&collection_token=JKnWkmZVIqQdVjFDzmTXsN "Without bursting, when the number of incoming requests per second (black line) exceeds the number of requests a partition can handle with its provisioned throughput (red line), the difference of requests is rejected because the partition can’t handle such requests. With bursting, we can acquire extra throughput for short periods of increased traffic.")

Without bursting, when the number of incoming requests per second (black line) exceeds the number of requests a partition can handle with its provisioned throughput (red line), the difference of requests is rejected because the partition can’t handle such requests. With bursting, we can acquire extra throughput for short periods of increased traffic.

![](https://www.educative.io/api/collection/10370001/6389837936197632/page/4985166665351168/image/5767955285278720?page_type=collection_lesson&get_optimised=true&collection_token=JKnWkmZVIqQdVjFDzmTXsN "Tokens in the allocation bucket indicate that a partition has provisioned throughput available. The allocation bucket is empty when a bucket's provisioned throughput is being used. Tokens in the burst bucket of a partition indicate if the partition can burst while providing workload isolation. If it is empty, then the partition's node does not have burst capacity: no available throughput without sacrificing workload isolation.")

Tokens in the allocation bucket indicate that a partition has provisioned throughput available. The allocation bucket is empty when a bucket's provisioned throughput is being used. Tokens in the burst bucket of a partition indicate if the partition can burst while providing workload isolation. If it is empty, then the partition's node does not have burst capacity: no available throughput without sacrificing workload isolation.
#### **Adaptive Capacity**

1. **Relocating Partitions**:
    - If a partition experiences **long-term throttling**, it can be **relocated** to a node with **spare throughput capacity**.
2. **Increasing Allocated Throughput**:
    - The allocated throughput of a partition can be **increased** to meet sustained high traffic demands.
    - This is done by **reallocating resources** or **splitting the partition**.

#### **Global Admission Control (GAC)**

1. **What is GAC?**:
    - GAC is a **global token bucket system** that tracks and allocates tokens across all partitions in the system.
    - It ensures that workloads sending requests to specific partitions can execute at **maximum throughput** without starving other partitions.
2. **Request Routers**:
    - GAC replenishes tokens in **request router buckets**, which allow requests to pass through to partitions if tokens are available.
    - This ensures **fair resource allocation** and **workload isolation**.
3. **Advantages of GAC**:
    - Prevents a single application from consuming a significant share of the system’s throughput.
    - Enables **efficient resource utilization** and **scalability**.

![](https://www.educative.io/api/collection/10370001/6389837936197632/page/4985166665351168/image/5928517577211904?page_type=collection_lesson&get_optimised=true&collection_token=JKnWkmZVIqQdVjFDzmTXsN "GAC replenishes tokens in request router buckets. Request routers allow requests to pass through to partitions if they have tokens available.")

GAC replenishes tokens in request router buckets. Request routers allow requests to pass through to partitions if they have tokens available.

#### **Splitting for Consumption**

1. **Permanent Scaling Out**:
    - If a partition experiences **sustained high traffic**, it can be **permanently split** based on key distribution.
    - The split point is chosen to **equally distribute the throughput requirement** between the resulting child partitions.
2. **Continuous Key Ranges**:
    - Splitting ensures that **key ranges remain continuous**, maintaining the logical structure of the data.

#### **On-Demand Provisioning**

1. **Dynamic Throughput Regulation**:
    - For applications with **highly irregular loads**, throughput is regulated based on **recent traffic patterns**.
    - Resources are **automatically increased** or partitions are **split** as soon as a traffic peak is detected.
2. **Benefits**:
    - Eliminates the need for users to manually adjust provisioned throughput.
    - Ensures that the system can handle **unpredictable traffic spikes** without downtime.


### Durability and correctness

Ensuring **durability** and **correctness** in a large-scale database service is critical to prevent **data loss**, **corruption**, and **inconsistencies**. This involves implementing mechanisms to handle **hardware failures**, **silent data errors**, and maintaining **high availability** for both **writes** and **reads**. Here’s a detailed explanation of these concepts and strategies:

#### **Avoiding Data Loss and Inconsistencies**

1. **Hardware Failures and Backups**:
    - **Write-Ahead Log (WAL)**:
        - A **write-ahead log** is a **log-based data structure** that stores all incoming write requests sequentially in storage.
        - An **in-memory index** is maintained to allow **constant-time access** to the data in the log.
        - If the in-memory index is lost (e.g., due to memory failure), the write-ahead log can be used to **recreate the index**.
    - **Regular Backups**:
        - The write-ahead log is **regularly archived** to minimize data loss in case of storage failures.
        - Backups ensure that the system can **recover data** even if a node’s storage fails.

![[p3.png]]

2. **Silent Data Errors**:
    - **Checksums**:
        - **Checksums** are added to detect and prevent **faulty data writes** caused by hardware failures.
        - The frequency of checksum verification can be adjusted based on the **historical performance** of the hardware.
    - **Data Integrity**:
        - Regularly verify data integrity using checksums to ensure that **silent data errors** (e.g., bit flips) are detected and corrected.

#### **High Availability**

1. **Write Availability**:
    - **Replication Groups**:
        - A set of replicas for the same partition is called a **replication group**.
        - Replication groups use a **leader election mechanism** (e.g., **Multi-Paxos**) to elect a **leader replica**.
    - **Leader Responsibilities**:
        - The leader replica is responsible for handling **write requests** and sharing its **write-ahead log** and **tree index** with other replicas in the group.
    - **Fault Tolerance**:
        - If a replica becomes **unresponsive** or **faulty**, the leader can **add a new member** to the replication group to maintain the **minimum number of replicas** required for a **quorum**.

2. **Consistent Read Availability**:
    - **Eventual Consistency**:
        - The replication system provides **eventual read consistency**, meaning that all replicas will eventually reflect the latest write.
    - **Instant Consistent Reads**:
        - Only the **leader replica** provides **instant consistent reads**, ensuring that read requests return the **latest written value**.
    - **Read Quorum**:
        - To ensure consistent reads, a **quorum of replicas** must acknowledge the read request, preventing stale data from being returned.

#### **Additional Considerations**

1. **Leader Election and Failover**:
    - Implement **robust leader election** mechanisms (e.g., **Raft** or **Multi-Paxos**) to ensure that a new leader is elected quickly in case of leader failure.
    - Automate **failover processes** to minimize downtime and maintain **write availability**.
2. **Data Recovery**:
    - In case of **data corruption** or **loss**, use the **write-ahead log** and **backups** to **recover data** and restore the system to a consistent state.
    - Regularly test **recovery processes** to ensure they work as expected.
3. **Monitoring and Alerts**:
    - Implement **monitoring tools** to track **hardware performance**, **data integrity**, and **replication health**.
    - Set up **alerts** to notify administrators of potential issues (e.g., hardware failures, silent data errors, or replication delays).
4. **Quorum-Based Writes**:
    - Use **quorum-based writes** to ensure that a majority of replicas acknowledge a write request before it is considered successful.
    - This ensures **data durability** and **consistency** even in the presence of failures.
5. **Global Consistency**:
    - For globally distributed systems, implement **multi-region replication** to ensure that data is consistent across regions.
    - Use **conflict resolution mechanisms** (e.g., **last-write-wins** or **application-specific reconciliation**) to handle divergent versions.


### DAX

Amazon DynamoDB is designed for scale and performance. In most cases, the DynamoDB response times can be measured in single-digit milliseconds. However, there are certain use cases that require response times in **microseconds**. For these use cases, DynamoDB Accelerator (DAX) delivers fast response times for accessing eventually consistent data.

![](https://miro.medium.com/v2/resize:fit:875/1*sGphRy-7ymu-OqsnitMZKQ.png)

DynamoDB Dax fast in-memory Cache For DynamoDB

DAX is a DynamoDB-compatible caching service that enables you to benefit from fast **_in-memory performance_** for demanding applications. DAX addresses three core scenarios:

1. As an in-memory cache, DAX reduces the response times of “**_eventually consistent read”_** workloads by an order of magnitude from single-digit milliseconds to microseconds.
2. DAX reduces operational and application complexity by providing a managed service that is API-compatible with DynamoDB. Therefore, it requires only minimal functional changes to use with an existing application.
3. For **_read-heavy or bursty workloads_**, DAX provides increased throughput and potential operational cost savings by reducing the need to overprovision read capacity units. This is especially beneficial for applications that require repeated reads for individual keys.

DAX supports server-side encryption. With encryption at rest, the data persisted by DAX on disk will be encrypted. DAX writes data to disk as part of propagating changes from the primary node to read replicas.


#### DAX: How it works

Amazon DynamoDB Accelerator (DAX) is designed to run within an Amazon Virtual Private Cloud (Amazon VPC) environment. The Amazon VPC service defines a virtual network that closely resembles a traditional data center. With a VPC, you have control over its IP address range, subnets, routing tables, network gateways, and security settings. You can launch a DAX cluster in your virtual network and control access to the cluster by using Amazon VPC security groups.

The following diagram shows a high-level overview of DAX.

![](https://miro.medium.com/v2/resize:fit:875/1*133xL-GS9o1tqWhMp7jPQg.png)

How DAX work with your application

At runtime, the “**_DAX client”_:**

1. Directs all of your application’s DynamoDB API requests to the DAX cluster.
2. If DAX can process one of these API requests directly, it does so.
3. Otherwise, it passes the request through to DynamoDB.
4. Finally, the DAX cluster returns the results to your application.

![](https://miro.medium.com/v2/resize:fit:875/1*mYH7kF_e_g-RUNPPTJgGVA.png)

How DynamoDB DAX Client


#### DAX Components and Working

1. **Components**:
    - **Cluster**: A DAX cluster consists of one or more **nodes** that store cached data.
    - **Node Types**:
        - **Primary Node**: Handles write-through caching and coordinates with DynamoDB.
        - **Replica Nodes**: Serve read requests and replicate data from the primary node.
    - **Endpoint**: Applications connect to the DAX cluster via a **cluster endpoint**.
2. **How DAX Works**:
    - **Read Requests**:
        - When a read request is sent to DAX, it first checks the cache.
        - If the data is found (cache hit), it returns the data directly from the cache.
        - If the data is not found (cache miss), it fetches the data from DynamoDB, caches it, and returns it to the application.
    - **Write Requests**:
        - Write requests are forwarded to DynamoDB and the cache is updated synchronously (write-through caching).
        - This ensures that the cache remains consistent with the data in DynamoDB.

![](https://miro.medium.com/v2/resize:fit:1250/1*wjcXclDN8gv-LWT7pFfa6A.png)


> If there are any read replicas in the cluster, DAX automatically keeps the replicas in sync with the primary node.


##### How DAX processes requests

A DAX cluster consists of **one or more nodes**. Each node runs its own instance of the DAX caching software. One of the nodes serves as the primary node for the cluster. Additional nodes (if present) serve as read replicas.

![](https://miro.medium.com/v2/resize:fit:875/1*Eg0dBqzMgMlsDE9Q2_OxMw.png)

How DAX processes requests

Your application can access DAX by specifying the **endpoint** for the “**_DAX cluster_**”. The DAX client software works with the cluster endpoint to perform intelligent load balancing and routing.

##### Item cache

DAX stores results from `GetItem` and `BatchGetItem` in an item cache, using primary keys. When an application requests data, DAX first checks the cache. If the item is found (cache hit), it returns it immediately. If not (cache miss), DAX fetches the data from DynamoDB, stores it in the cache, and then returns it.

![](https://miro.medium.com/v2/resize:fit:875/1*oVXPyl3U1qNZf2rYSb3Gdw.png)

Item cache handle in DAX

Cached items expire after a default TTL of 5 minutes. If an item is requested after expiration, DAX treats it as a cache miss and fetches fresh data from DynamoDB.

DAX also uses an LRU (Least Recently Used) strategy to remove older items when the cache is full. If TTL is set to zero, items are only refreshed through LRU eviction or a write-through operation.


##### Query cache

DAX stores results from `Query` and `Scan` operations in a query cache, using “**_parameter values”_**.

```
const params = {  
TableName: "Users",  
KeyConditionExpression: "userId = :userId",  
ExpressionAttributeValues: {  
":userId": "12345"  
}  
};  
// DAX will cache this query result using the parameter values (userId = "12345")
```

When an application requests data, DAX first checks the cache. If a matching result set is found (cache hit), it returns it immediately. If not (cache miss), DAX fetches the data from DynamoDB, stores it in the cache, and then returns it.

![](https://miro.medium.com/v2/resize:fit:1250/1*CQgii_NSUBtZ7B2tM4Uheg.png)

Query cache handle in DAX

DAX uses an LRU (Least Recently Used) strategy to remove older result sets when the cache is full. If the TTL is set to zero, query responses are not cached at all.


#### Use Cases for DAX

1. **Read-Heavy Workloads**:
    - Applications with high read traffic (e.g., gaming leaderboards, social media feeds) benefit from DAX’s microsecond latency.
2. **Low-Latency Requirements**:
    - Applications that require **real-time responses** (e.g., ad tech, financial trading) can use DAX to reduce latency.
3. **Cost Optimization**:
    - By reducing the number of read requests to DynamoDB, DAX can lower **provisioned read capacity costs**.
4. **Caching Frequently Accessed Data**:
    - DAX is ideal for caching **hot data** (e.g., user profiles, product catalogs) that is accessed frequently.

#### Best Practices for DAX

1. **Cache Hot Data**:
    - Use DAX to cache frequently accessed data, but avoid caching **cold data** that is rarely accessed.
2. **Set Appropriate TTL**:
    - Configure TTL based on the **freshness requirements** of your data.
    - For example, set a shorter TTL for rapidly changing data and a longer TTL for static data.
3. **Monitor Performance**:
    - Use **CloudWatch metrics** to monitor DAX performance, including **cache hits**, **cache misses**, and **latency**.
    - Adjust the cluster size and TTL settings based on performance metrics.
4. **Optimize Queries**:
    - Use **efficient queries** to minimize cache misses and maximize cache hits.
    - Avoid scanning large datasets, as this can lead to poor cache performance.
5. **Test and Tune**:
    - Test DAX with your application’s workload to identify optimal configurations (e.g., cluster size, TTL).
    - Continuously tune DAX settings based on changing traffic patterns.

#### Limitations of DAX

1. **Write Latency**:
    - DAX does not reduce **write latency**, as writes are forwarded to DynamoDB and the cache is updated synchronously.
2. **Cache Size**:
    - The cache size is limited by the memory of the DAX nodes. Ensure that the cluster size is sufficient to cache your hot data.
3. **Complex Queries**:
    - DAX is optimized for **simple queries** (e.g., `GetItem`, `Query`, `Scan`). Complex queries may not benefit as much from caching.
4. **Cost**:
    - While DAX can reduce read costs, it adds additional costs for the cache itself. Evaluate the cost-benefit trade-off before using DAX.

### Sample Queries for DynamoDB

DynamoDB supports a variety of query types to retrieve data efficiently. Below are examples of **different types of queries** you can perform on a DynamoDB table, along with explanations of their use cases:

---

#### **1. GetItem (Retrieve a Single Item)**

1. **Description**:
    
    - Retrieves a single item from a table using its **primary key**.
    - This is the most efficient way to fetch a specific item.
2. **Example**:
    ```
    response = table.get_item(
        Key={
            'UserID': '123',
            'OrderID': '456'
        }
    )
    item = response['Item']
    ```
    
3. **Use Case**:
    
    - Fetching a specific user’s order by `UserID` and `OrderID`.

---

#### **2. Query (Retrieve Multiple Items with a Partition Key)**

1. **Description**:
    
    - Retrieves multiple items that share the same **partition key**.
    - You can optionally specify a **sort key condition** to filter results.
2. **Example**:
    
    ```
    response = table.query(
        KeyConditionExpression=Key('UserID').eq('123')
    )
    items = response['Items']
    ```
    
3. **Use Case**:
    
    - Fetching all orders for a specific user (`UserID = 123`).

---

#### **3. Query with Sort Key Condition**

1. **Description**:
    
    - Retrieves items that match both the **partition key** and a **sort key condition** (e.g., begins with, between, greater than).
2. **Example**:
    
    ```
    response = table.query(
        KeyConditionExpression=Key('UserID').eq('123') & Key('OrderDate').begins_with('2023-10')
    )
    items = response['Items']
    ```
    
3. **Use Case**:
    
    - Fetching all orders for a specific user in October 2023.

---

#### **4. Scan (Retrieve All Items in a Table)**

1. **Description**:
    
    - Scans the entire table to retrieve all items.
    - This is less efficient than `Query` and should be used sparingly.
2. **Example**:
    
    ```
    response = table.scan()
    items = response['Items']
    ```
    
3. **Use Case**:
    
    - Fetching all items in a small table (e.g., for reporting or analytics).

---

#### **5. Scan with Filter (Retrieve Items Based on Non-Key Attributes)**

1. **Description**:
    
    - Scans the entire table and applies a **filter expression** to return only items that match the filter.
2. **Example**:
    
    ```
    response = table.scan(
        FilterExpression=Attr('Status').eq('Shipped')
    )
    items = response['Items']
    ```
    
3. **Use Case**:
    
    - Fetching all orders with a specific status (e.g., `Status = "Shipped"`).

---

#### **6. Query with GSI (Global Secondary Index)**

1. **Description**:
    
    - Queries a **Global Secondary Index (GSI)** to retrieve items based on non-key attributes.
2. **Example**:
    
    ```
    response = table.query(
        IndexName='StatusIndex',
        KeyConditionExpression=Key('Status').eq('Shipped')
    )
    items = response['Items']
    ```
    
3. **Use Case**:
    
    - Fetching all orders with a specific status using a GSI.

---

#### **7. Query with LSI (Local Secondary Index)**

1. **Description**:
    
    - Queries a **Local Secondary Index (LSI)** to retrieve items within a partition using a different sort key.
2. **Example**:
    
    ```
    response = table.query(
        IndexName='OrderDateIndex',
        KeyConditionExpression=Key('UserID').eq('123') & Key('OrderDate').between('2023-10-01', '2023-10-31')
    )
    items = response['Items']
    ```
    
3. **Use Case**:
    
    - Fetching all orders for a specific user within a date range using an LSI.

---

#### **8. BatchGetItem (Retrieve Multiple Items in Bulk)**

1. **Description**:
    
    - Retrieves multiple items from one or more tables in a single request.
2. **Example**:
    
    ```
    response = dynamodb.batch_get_item(
        RequestItems={
            'Orders': {
                'Keys': [
                    {'UserID': '123', 'OrderID': '456'},
                    {'UserID': '123', 'OrderID': '789'}
                ]
            }
        }
    )
    items = response['Responses']['Orders']
    ```
    
3. **Use Case**:
    
    - Fetching multiple orders in a single request to reduce latency.

---

#### **9. BatchWriteItem (Write Multiple Items in Bulk)**

1. **Description**:
    
    - Writes or deletes multiple items in one or more tables in a single request.
2. **Example**:

    ```
    response = dynamodb.batch_write_item(
        RequestItems={
            'Orders': [
                {
                    'PutRequest': {
                        'Item': {
                            'UserID': '123',
                            'OrderID': '456',
                            'Status': 'Shipped'
                        }
                    }
                },
                {
                    'PutRequest': {
                        'Item': {
                            'UserID': '123',
                            'OrderID': '789',
                            'Status': 'Pending'
                        }
                    }
                }
            ]
        }
    )
    ```
    
3. **Use Case**:
    
    - Inserting or updating multiple orders in a single request.

---

#### **10. UpdateItem (Modify an Existing Item)**

1. **Description**:
    
    - Updates an existing item’s attributes or adds new attributes.
2. **Example**:
    
    ```
    response = table.update_item(
        Key={
            'UserID': '123',
            'OrderID': '456'
        },
        UpdateExpression='SET Status = :new_status',
        ExpressionAttributeValues={
            ':new_status': 'Delivered'
        }
    )
    ```
    
3. **Use Case**:
    
    - Updating the status of an order from `Shipped` to `Delivered`.

---

#### **11. DeleteItem (Remove an Item)**

1. **Description**:
    
    - Deletes an item from the table using its primary key.
2. **Example**:
    
    ```
    response = table.delete_item(
        Key={
            'UserID': '123',
            'OrderID': '456'
        }
    )
    ```
    
3. **Use Case**:
    
    - Deleting a specific order from the table.

---

#### **Conclusion**

DynamoDB provides a variety of query types to efficiently retrieve and manipulate data. From simple `GetItem` operations to complex `Query` and `Scan` requests, DynamoDB supports diverse access patterns. By leveraging **primary indexes**, **secondary indexes (GSIs and LSIs)**, and **bulk operations**, you can optimize your queries for performance and scalability. These sample queries demonstrate how to interact with DynamoDB to meet the needs of different use cases.