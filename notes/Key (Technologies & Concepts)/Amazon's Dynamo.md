## Introduction

Amazon's e-commerce platform operates on a massive scale, serving millions of customers globally. This introduction outlines the challenges and solutions in managing such a system:

1. Scale and Requirements: 
	1. Tens of millions of customers at peak times 
	2. Tens of thousands of servers across global data centers
	3. Strict requirements for performance, reliability, efficiency, and scalability
    
2. Importance of Reliability: 
	1. Even minor outages have significant financial consequences 
	2. Impacts customer trust

Amazon uses a highly decentralized, loosely coupled, service-oriented architecture comprising hundreds of services. This environment necessitates always-available storage technologies.

3. Need for Always-Available Storage:
	1. Critical for features like shopping carts to function despite failures
	2. Data needs to be available across multiple data centers
    
4. Failure as a Norm:
	1. Constant component failures in the infrastructure 
	2. Systems designed to handle failures without impacting availability or performance
    
5. Dynamo's Purpose and Features: 
	1. Highly available and scalable distributed data store 
	2. Designed for services with high reliability requirements 
	3. Allows fine-tuning of trade-offs between availability, consistency, cost-effectiveness, and performance 
	4. Provides simple primary-key only interface
    
6. Dynamo's Technical Approach: 
	1. Consistent hashing for data partitioning and replication 
	2. Object versioning for consistency
	3. Quorum-like technique and decentralized replica synchronization 
	4. Gossip-based failure detection and membership protocol 
	5. Decentralized system with minimal manual administration
    
7. Real-World Performance: 
	1. Successfully used in core Amazon services 
	2. Handled extreme peak loads during holiday shopping seasons 
		1. Example: tens of millions of requests, 3 million checkouts in a single day
    
8. Research Contribution: 
	1. Demonstrates combination of techniques for high availability 
	2. Shows eventually-consistent storage can be used in demanding production environments

The paper is structured to cover background, related work, system design, implementation, production experiences, and conclusions. Some details are aggregated to protect Amazon's business interests.

## Background

Amazon's e-commerce platform is built on a complex architecture of hundreds of services working in concert. These services provide a wide range of functionalities, from recommendations to order fulfillment and fraud detection. Each service has a well-defined interface accessible over the network, and the entire infrastructure is supported by tens of thousands of servers distributed across global data centers.

The services within this ecosystem can be categorized into two types:

- Stateless: Services that aggregate responses from other services
- Stateful: Services that generate responses by executing business logic on their persistent stored state

Traditionally, production systems have relied on relational databases (RDBMS) for state storage. However, this approach has proven less than ideal for many common state persistence patterns in Amazon's service-oriented architecture. The limitations of relational databases in this context include:

- Excess functionality: Many services only require simple primary key-based storage and retrieval, not the complex querying capabilities of an RDBMS.
- Resource intensity: The advanced features of RDBMS systems demand expensive hardware and highly skilled personnel to operate.
- Replication challenges: Available replication technologies often prioritize consistency over availability and have limited flexibility.
- Scaling difficulties: Despite recent advancements, scaling out databases and implementing efficient partitioning schemes remains challenging.

To address these limitations, Amazon developed Dynamo, a highly available data storage technology tailored to the needs of their service classes. Dynamo offers several key features:

- Simple key/value interface
- High availability with a clearly defined consistency window
- Efficient resource usage
- Easy scale-out scheme to accommodate growth in data set size or request rates

Importantly, each service that utilizes Dynamo runs its own instances, allowing for customized configurations and performance optimizations based on specific service requirements.

This innovative approach to data storage and management has enabled Amazon to overcome many of the challenges posed by traditional database solutions, providing a more efficient and scalable foundation for their e-commerce platform.

### System Assumptions and Requirements

Dynamo's design is based on specific requirements and assumptions tailored to Amazon's service needs:

**Query Model:**

- Simple read and write operations to uniquely identified data items
- State stored as binary objects (blobs) with unique keys
- No multi-item operations or relational schema needed
- Targets applications storing relatively small objects (usually less than 1 MB)

**ACID Properties:** Dynamo prioritizes high availability over strict ACID guarantees:

- Weaker consistency is accepted if it results in higher availability
- No isolation guarantees provided
- Only single key updates permitted

This approach is based on Amazon's experience that ACID-compliant data stores often have poor availability, a observation acknowledged by both industry and academia.

**Efficiency:** The system is designed to operate on commodity hardware infrastructure while meeting stringent performance requirements:

- Latency requirements measured at the 99.9th percentile of distribution
- Must meet strict Service Level Agreements (SLAs)
- Services can configure Dynamo to consistently achieve latency and throughput needs
- Involves tradeoffs between performance, cost efficiency, availability, and durability guarantees

Other Assumptions:
- Used only by Amazon's internal services
- Non-hostile operation environment assumed
- No security-related requirements (e.g., authentication, authorization)
- Initial design targets scale of up to hundreds of storage hosts

Dynamo's design reflects a pragmatic approach to data storage, prioritizing availability and performance for Amazon's specific use cases over traditional database features. This allows for a more tailored and efficient solution for the company's e-commerce platform and related services.


### Service Level Agreements (SLA)

Service Level Agreements (SLAs) play a crucial role in Amazon's decentralized, service-oriented infrastructure. SLAs are formally negotiated contracts between clients and services, defining system characteristics like expected request rate distributions and service latency guarantees.

**In Amazon's architecture, a typical page request can involve over 150 services in the call chain.** To ensure the overall page delivery time remains bounded, each service within the chain must adhere to its individual performance contract.

Amazon's approach to SLAs differs from common industry practices:

- SLAs are not based on average or median metrics, as these do not address the needs of important customer segments (e.g., those requiring more personalization).
- **Instead, SLAs are expressed and measured at the 99.9th percentile of the response time distribution.**
- **This focus on the high-end of the distribution, rather than just the majority, provides a better overall customer experience.**

The storage system plays a critical role in a service's ability to meet its SLA, especially when the business logic is relatively lightweight. **Dynamo is designed to give services control over system properties like durability and consistency, allowing them to make tradeoffs between functionality, performance, and cost-effectiveness.**

Figure 1 provides an abstract view of Amazon's service-oriented architecture, showing how page rendering components query numerous dependent services, some of which act as stateless aggregators.

In summary, SLAs are a foundational element in Amazon's decentralized platform, driving the need for a highly available and performant storage system like Dynamo, which can be tuned to the specific requirements of individual services.

  
![](http://s3.amazonaws.com/wernervogels/public/sosp/sosp-figure1-small.png)  
										Figure 1: Service-oriented architecture of Amazon’s platform.


### Design Considerations

**Data Replication Algorithms:**

- Traditional commercial systems use synchronous replica coordination to provide strong data consistency, but this comes at the cost of reduced availability during failures.
- **Dynamo is designed as an eventually consistent data store, prioritizing availability over strong consistency.**
- Optimistic replication techniques are used, allowing concurrent, disconnected work and tolerating conflicting changes that must be resolved later.

**Conflict Resolution:**

- Dynamo pushes the complexity of conflict resolution to the read path, rather than rejecting writes, to ensure high write availability.
- The application is responsible for conflict resolution, as it has better knowledge of the data schema and can choose the most appropriate resolution method.
- While some applications may prefer to delegate conflict resolution to the data store, which would then use a simple "last write wins" policy.

**Other Key Design Principles:**

1. Incremental Scalability: Dynamo can scale out one node at a time with minimal impact.
2. Symmetry: All nodes have the same responsibilities, simplifying provisioning and maintenance.
3. Decentralization: Favoring peer-to-peer techniques over centralized control for better scalability and availability.
4. Heterogeneity: Ability to exploit differences in server capabilities for efficient work distribution.

These design choices reflect Dynamo's focus on availability, scalability, and flexibility, in contrast with traditional data stores that prioritize strong consistency over availability.

## Related Work & Comparison

### Peer-to-Peer Systems:

- First generation P2P systems (e.g., Freenet, Gnutella) were unstructured networks with arbitrary overlay links and flooding-based search.
- Second generation P2P systems (e.g., Pastry, Chord) used structured overlay networks with bounded-hop routing protocols for efficient data lookup.
- Some P2P systems (e.g., [14]) employed O(1) routing, where each node maintains enough local information to route requests in a constant number of hops.

**P2P Storage Systems:**

- Oceanstore: Provided a global, transactional, persistent storage service with support for conflict resolution to handle concurrent updates.
- PAST: Built on top of Pastry, providing a simple abstraction layer for persistent and immutable objects, with the application responsible for building higher-level storage semantics.

**Key Differences from Dynamo:**

- Dynamo is designed for an internal, non-hostile environment, unlike Oceanstore which targets an untrusted infrastructure.
- Dynamo pushes conflict resolution to the application layer, unlike Oceanstore which handles it centrally.
- Dynamo focuses on availability and scalability, rather than providing a global, transactional storage service like Oceanstore.

*The Dynamo design departs from the traditional P2P storage systems in its emphasis on high availability, application-driven conflict resolution, and a more lightweight, scalable architecture tailored to Amazon's specific service requirements.*

### Distributed File Systems and Databases

**Distributed File Systems:**

- Ficus, Coda, Farsite - Replicate files for high availability, but sacrifice consistency. Use specialized conflict resolution procedures.
- Google File System (GFS) - Simple design with a single metadata server and data stored across chunk servers.

**Distributed Databases:**

- Bayou - Allows disconnected operations and provides eventual consistency. Supports application-level conflict resolution.
- Similar to Dynamo in allowing reads/writes during partitions and using different conflict resolution mechanisms.

**Distributed Block Storage:**

- FAB - Splits large objects into smaller blocks and stores them in a highly available manner.
- Dynamo uses a key-value store, more suitable for smaller objects and easier to configure per-application.

**Other Systems:**

- Antiquity - Wide-area distributed storage system focused on data integrity and security, using Byzantine fault tolerance.
- Bigtable - Structured distributed storage system, compared to Dynamo's key-value focus on high availability.

**Traditional Replicated Databases:**

*Focus on strong consistency, limiting scalability and availability, and unable to handle network partitions.

**Key Differences of Dynamo:**
- Prioritizes availability over strong consistency, allowing reads/writes during partitions.
- Pushes conflict resolution to the application layer.
- Designed as a lightweight, scalable key-value store for Amazon's internal services.

### Differences

Here are the key differences between Dynamo and the other decentralized storage systems discussed:

**1. Target Requirements:**
    - Dynamo is focused on providing an "always writeable" data store, where no updates are rejected due to failures or concurrent writes.
    - This is a crucial requirement for many Amazon applications.
**2. Trust Model:**
    - Dynamo is built for a trusted infrastructure within a single administrative domain, unlike systems like Oceanstore that target untrusted environments.
**3. Data Model:**
    - Dynamo does not require support for hierarchical namespaces (file systems) or complex relational schemas (databases).
    - It is designed as a simple key-value store.
**4. Latency Requirements:**
    - Dynamo is built for latency-sensitive applications, requiring 99.9% of read/write operations to complete within a few hundred milliseconds.
    - To meet this, Dynamo avoids multi-hop routing used in distributed hash table (DHT) systems like Chord and Pastry, which can introduce higher latency variability.
    - Dynamo can be characterized as a "zero-hop DHT", where each node maintains enough routing information to directly access the appropriate node.

In summary, Dynamo's design is tailored to the specific needs of Amazon's internal services, prioritizing availability, low-latency, and a simple key-value data model, rather than the broader requirements addressed by other decentralized storage systems.


## System Architecture

The paper focuses on describing the core distributed systems techniques used in the Dynamo storage system, rather than going into the full architectural details. The key techniques covered are:

1. Partitioning
2. Replication
3. Versioning
4. Membership
5. Failure handling
6. Scaling

The authors provide a summary of these techniques and their respective advantages in Table 1. This allows the paper to concentrate on the novel distributed systems aspects of Dynamo, rather than getting bogged down in the full system implementation details.

The goal is to highlight the key design choices and mechanisms that enable Dynamo to meet its target requirements around high availability, low latency, and scalability - which differ from the priorities of some of the other distributed storage systems discussed in the related work section. This focused approach allows the paper to effectively communicate the core technical contributions of the Dynamo design.

_Table 1: Summary of techniques used in Dynamo and their advantages._

|                                    |                                                         |                                                                                                                   |
| ---------------------------------- | ------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------- |
| **Problem**                        | **Technique**                                           | **Advantage**                                                                                                     |
| Partitioning                       | Consistent Hashing                                      | Incremental Scalability                                                                                           |
| High Availability for writes       | Vector clocks with reconciliation during reads          | Version size is decoupled from update rates.                                                                      |
| Handling temporary failures        | Sloppy Quorum and hinted handoff                        | Provides high availability and durability guarantee when some of the replicas are not available.                  |
| Recovering from permanent failures | Anti-entropy using Merkle trees                         | Synchronizes divergent replicas in the background.                                                                |
| Membership and failure detection   | Gossip-based membership protocol and failure detection. | Preserves symmetry and avoids having a centralized registry for storing membership and node liveness information. |

### System Interface

Dynamo is a storage system that uses a simple interface with two main operations:

1. **get(key)** - This locates the object replicas associated with the given key and returns either a single object or a list of conflicting versions, along with a context.
2. **put(key, context, object)** - This determines where the replicas of the object should be placed based on the key, and writes the replicas to disk. The context contains metadata about the object, including version information, that is used to verify the validity of the put request.

Dynamo treats both the key and the object as opaque byte arrays. It applies an MD5 hash to the key to generate a 128-bit identifier, which is used to determine which storage nodes are responsible for that key.

The key points are the simple get and put operations, the use of context to manage object versions, and the MD5 hash of the key to determine storage node responsibilities.

### Partitioning Algorithm

The partitioning algorithm in Dynamo is designed to ensure scalable and efficient data distribution across multiple storage nodes. At its core, Dynamo employs **consistent hashing** to dynamically partition data. In this approach, the hash function's output range is treated as a circular "ring," where each node is assigned a random position. Data items are mapped to nodes by hashing their keys and locating the first node clockwise from the hashed position. This ensures that each node is responsible for a specific segment of the ring between itself and its predecessor.

However, the basic consistent hashing algorithm has limitations:

1. **Non-uniform data distribution**: Random node positions can lead to uneven load distribution.
2. **Ignoring node heterogeneity**: The algorithm does not account for differences in node performance or capacity.

To address these issues, Dynamo introduces **virtual nodes**. Instead of mapping a physical node to a single point on the ring, each node is assigned multiple positions (tokens) as virtual nodes. This approach offers several advantages:

1. **Load balancing during failures**: If a node becomes unavailable, its load is evenly distributed across remaining nodes.
2. **Efficient scaling**: When a node is added or reintroduced, it assumes a fair share of the load from existing nodes.
3. **Heterogeneity support**: The number of virtual nodes assigned to a physical node can be adjusted based on its capacity, ensuring optimal resource utilization.

By leveraging virtual nodes, Dynamo achieves a more balanced and adaptable partitioning scheme, enhancing system resilience and scalability. Further fine-tuning of this mechanism is discussed in Section 6 of the Dynamo paper.

### Replication

In Dynamo, **replication** is a critical mechanism to ensure **high availability** and **durability** of data. Each data item is replicated across **N hosts**, where **N** is a configurable parameter. The process of replication is managed by the **coordinator node**, which is responsible for a specific range of keys in the consistent hashing ring. Here’s how replication works:

1. **Replication Process**:
    - The coordinator node stores the key locally and replicates it at the **N-1 clockwise successor nodes** in the ring.
    - This ensures that each key is stored on **N distinct nodes**, with each node responsible for the region of the ring between itself and its **Nth predecessor**.
    - For example, in Figure 2, node B replicates key **k** at nodes C and D, in addition to storing it locally. Node D stores keys from ranges (A, B], (B, C], and (C, D].
  
![](http://s3.amazonaws.com/wernervogels/public/sosp/sosp-figure2-small.png)  
									_Figure 2: Partitioning and replication of keys in Dynamo ring._

2. **Preference List**:
    - The list of nodes responsible for storing a particular key is called the **preference list**.
    - Dynamo ensures that every node in the system can determine the preference list for any key, as explained in Section 4.8.
    - To handle **node failures**, the preference list includes **more than N nodes**, ensuring redundancy.
3. **Handling Virtual Nodes**:
    - Due to the use of **virtual nodes**, the first **N successor positions** for a key might be owned by **fewer than N distinct physical nodes** (i.e., a single physical node could hold multiple virtual positions).
    - To address this, the preference list is constructed by **skipping positions** in the ring to ensure that only **distinct physical nodes** are included.

By replicating data across multiple nodes and carefully constructing preference lists, Dynamo achieves **fault tolerance** and **scalability**, even in the presence of node failures or heterogeneity in the system. This replication strategy is a cornerstone of Dynamo’s ability to provide reliable and highly available storage.

### Data Versioning

Dynamo employs **data versioning** to handle **eventual consistency**, ensuring that updates are propagated asynchronously across replicas. This design allows for high availability and fault tolerance, but it introduces scenarios where multiple versions of the same data may coexist, especially during failures or concurrent updates. Here’s a detailed breakdown of how Dynamo manages data versioning

#### **Key Concepts**
1. **Eventual Consistency**:
    - A `put()` operation may return before the update is applied to all replicas.
    - A subsequent `get()` operation might return an older version of the data.
    - Under normal conditions, update propagation is bounded, but failures (e.g., server outages, network partitions) can delay updates indefinitely.
2. **Use Case: Shopping Cart**:
    - Applications like Amazon’s shopping cart can tolerate inconsistencies.
    - Operations like “Add to Cart” or “Delete Item” are treated as `put()` requests.
    - If the latest version of the cart is unavailable, changes are applied to an older version, and divergent versions are reconciled later.

#### **Versioning Mechanism**
1. **Immutable Versions**:
    - Each modification creates a **new, immutable version** of the data.
    - Multiple versions of an object can coexist in the system.

2. **Syntactic vs. Semantic Reconciliation**:
    - **Syntactic Reconciliation**: New versions typically subsume older ones, and the system can determine the authoritative version.
    - **Semantic Reconciliation**: In cases of concurrent updates or failures, version branching occurs, and the **client** must reconcile conflicting versions (e.g., merging different versions of a shopping cart).

3. **Vector Clocks**:
    - Dynamo uses **vector clocks** to track causality between versions.
    - A vector clock is a list of `(node, counter)` pairs associated with each version of an object.
    - By comparing vector clocks, Dynamo can determine if one version is an ancestor of another or if they are conflicting.


#### **How Vector Clocks Work**
1. **Writing Data**:
    - When a client updates an object, it specifies the version it is updating by passing the context (vector clock) from an earlier read.
    - The handling node increments its sequence number and updates the vector clock.
2. **Reading Data**:
    - If Dynamo detects multiple conflicting versions during a read, it returns all versions with their vector clocks.
    - The client must reconcile these versions and perform a write to collapse the branches into a single new version.
3. **Example**:
    - **D1**: Initial version with clock `[(Sx, 1)]`.
    - **D2**: Updated version with clock `[(Sx, 2)]` (descendant of D1).
    - **D3**: Updated by a different node, with clock `[(Sx, 2), (Sy, 1)]`.
    - **D4**: Updated by another node, with clock `[(Sx, 2), (Sz, 1)]`.
    - **D5**: Reconciles D3 and D4, with clock `[(Sx, 3), (Sy, 1), (Sz, 1)]`.

![](http://s3.amazonaws.com/wernervogels/public/sosp/sosp-figure3-small.png)  

											_Figure 3: Version evolution of an object over time._

#### **Handling Vector Clock Growth**

1. **Clock Truncation**:
    - Vector clocks can grow if writes are handled by many nodes (e.g., during network partitions or failures).
    - Dynamo truncates vector clocks by removing the oldest `(node, counter)` pair when the clock size exceeds a threshold (e.g., 10).
    - This can lead to inefficiencies in reconciliation but has not been a significant issue in production.
	##### **Advantages and Trade-offs**
	1. **Advantages**:
	    - Ensures that no updates are lost, even during failures.
	    - Supports applications that can tolerate and resolve inconsistencies.
	    - Provides a mechanism to track and reconcile divergent versions.
	2. **Trade-offs**:
	    - Clients must handle semantic reconciliation, increasing application complexity.
	    - Truncation of vector clocks can reduce reconciliation accuracy.

#### **Conclusion**
Dynamo’s data versioning mechanism, powered by **vector clocks**, ensures that updates are never lost and provides a robust framework for handling eventual consistency. By allowing multiple versions of data to coexist and requiring clients to reconcile conflicts, Dynamo achieves high availability and fault tolerance, making it suitable for applications like Amazon’s shopping cart. However, this design introduces complexity in reconciliation and requires careful handling of vector clock growth.

### Execution of get () and put () operations

Dynamo is designed to handle **read (`get()`)** and **write (`put()`)** operations efficiently, even in distributed and failure-prone environments. Here’s a detailed explanation of how these operations are executed:

#### **General Execution**

1. **Eligibility of Nodes**:
    - Any storage node in Dynamo can receive client `get()` and `put()` requests for any key.
    - The node handling the request is called the **coordinator**.
2. **Request Routing**:
    - Clients can use one of two strategies to select a node:
        - **Load Balancer**: Routes requests through a generic load balancer, which selects a node based on load information.
        - **Partition-Aware Client Library**: Directly routes requests to the appropriate coordinator nodes, reducing latency by skipping forwarding steps.
3. **Coordinator Selection**:
    - The coordinator is typically the **first among the top N nodes** in the preference list for the key.
    - If a request is routed through a load balancer and lands on a node **not in the top N**, the node forwards the request to the first node in the top N.

#### **Read (`get()`) Operation**

1. **Process**:
    - The coordinator requests **all existing versions** of the data for the key from the **N highest-ranked reachable nodes** in the preference list.
    - It waits for **R responses** (where **R** is the minimum number of nodes required for a successful read).
2. **Handling Multiple Versions**:
    - If the coordinator receives multiple versions of the data, it returns all versions that are **causally unrelated**.
    - The client must reconcile these versions and write the reconciled version back to Dynamo.

#### **Write (`put()`) Operation**

1. **Process**:
    - The coordinator generates a **new vector clock** for the updated version of the data.
    - It writes the new version locally and sends it (along with the new vector clock) to the **N highest-ranked reachable nodes**.
    - The write is considered successful if at least **W-1 nodes** respond (where **W** is the minimum number of nodes required for a successful write).

#### **Consistency Protocol**

1. **Quorum-like System**:
    - Dynamo uses a consistency protocol with two configurable parameters:
        - **R**: Minimum number of nodes for a successful read.
        - **W**: Minimum number of nodes for a successful write.
    - Setting **R + W > N** ensures a quorum-like system, balancing consistency and availability.
2. **Latency Considerations**:
    - The latency of a `get()` or `put()` operation is determined by the **slowest of the R or W replicas**.
    - To optimize latency, **R** and **W** are usually configured to be **less than N**.

#### **Handling Failures**

1. **Node Failures or Network Partitions**:
    - If some nodes in the preference list are down or inaccessible, Dynamo skips them and uses the **next available nodes** in the list.
    - This ensures that operations can still proceed despite failures.
2. **Replica Consistency**:
    - Dynamo’s consistency protocol ensures that updates are propagated to a sufficient number of replicas, maintaining data integrity even during failures.

#### **Example Scenarios**

1. **Successful Write**:
    - A client issues a `put()` request for key **k**.
    - The coordinator generates a new vector clock, writes the data locally, and sends it to the top N nodes.
    - If at least **W-1 nodes** acknowledge the write, it is considered successful.
2. **Successful Read**:
    - A client issues a `get()` request for key **k**.
    - The coordinator fetches all versions of the data from the top N nodes and waits for **R responses**.
    - If multiple versions are found, the client reconciles them and writes the reconciled version back.

#### **Advantages and Trade-offs**

1. **Advantages**:
    - **High Availability**: Operations can proceed even during node failures or network partitions.
    - **Flexibility**: Configurable parameters (**R**, **W**, **N**) allow tuning for consistency, availability, and latency.
    - **Scalability**: Any node can handle requests, and partition-aware routing reduces latency.
2. **Trade-offs**:
    - **Eventual Consistency**: Conflicting versions may require client-side reconciliation.
    - **Latency**: Operations depend on the slowest replica, especially when **R** or **W** is high.

#### **Conclusion**
Dynamo’s execution of `get()` and `put()` operations is designed to provide **high availability**, **scalability**, and **fault tolerance**. By leveraging a quorum-like consistency protocol and configurable parameters, Dynamo balances consistency and latency while ensuring that operations can proceed even in the presence of failures. This makes Dynamo suitable for applications like Amazon’s shopping cart, where availability and scalability are critical.

### Handling Failures: Hinted Handoff

Dynamo employs a mechanism called **hinted handoff** to maintain **high availability** and **durability** during temporary node or network failures. This approach ensures that read and write operations can proceed even when some nodes are unavailable, without enforcing strict quorum membership. Here’s a detailed explanation of how hinted handoff works:

#### **Sloppy Quorum**

1. **Traditional Quorum Limitations**:
    - A traditional quorum system would become unavailable during server failures or network partitions.
    - Durability would also be compromised under simple failure conditions.
2. **Sloppy Quorum in Dynamo**:
    - Dynamo uses a **sloppy quorum**, where read and write operations are performed on the **first N healthy nodes** from the preference list.
    - These nodes may not always be the first N nodes encountered while walking the consistent hashing ring.

#### **Hinted Handoff Mechanism**

1. **Process**:
    - If a node (e.g., node A) is temporarily down or unreachable during a write operation, the replica intended for A is sent to the **next available node** (e.g., node D).
    - This ensures that the desired number of replicas (**N**) is maintained.
2. **Metadata Hint**:
    - The replica sent to node D includes **metadata** that indicates the **intended recipient** (node A).
    - Node D stores this hinted replica in a **separate local database**.
3. **Recovery and Delivery**:
    - When node A recovers, node D detects this and attempts to **deliver the hinted replica** to node A.
    - Once the transfer succeeds, node D deletes the replica from its local store, ensuring the total number of replicas remains consistent.

#### **Benefits of Hinted Handoff**

1. **High Availability**:
    - Write and read operations are not failed due to temporary node or network failures.
    - Applications requiring the highest availability can set **W=1**, ensuring a write is accepted as long as **one node** durably stores the key.
2. **Durability**:
    - Most Amazon services in production set a **higher W** to meet desired durability levels.
    - Hinted handoff ensures that replicas are not lost during temporary failures.

#### **Handling Data Center Failures**

1. **Replication Across Data Centers**:
    - Dynamo is configured to replicate each object across **multiple data centers**.
    - The preference list for a key is constructed such that storage nodes are spread across these data centers.
2. **High-Speed Network Links**:
    - Data centers are connected through **high-speed network links**, enabling efficient replication and recovery.
3. **Failure Resilience**:
    - This replication scheme allows Dynamo to handle **entire data center failures** without causing a data outage.

#### **Example Scenario**
1. **Configuration**:
    - Consider a Dynamo configuration with **N=3**, as shown in Figure 2.
    - Nodes A, B, and C are the top 3 nodes in the preference list for a key.
2. **Node Failure**:
    - If node A is temporarily down during a write operation, the replica intended for A is sent to node D.
    - Node D stores the replica with a hint indicating it belongs to node A.
3. **Recovery**:
    - When node A recovers, node D detects this and transfers the replica to node A.
    - After the transfer, node D deletes the replica from its local store.

#### **Advantages and Trade-offs**

1. **Advantages**:
    - **High Availability**: Operations can proceed during temporary failures.
    - **Durability**: Replicas are not lost, even during node or data center failures.
    - **Scalability**: Replication across multiple data centers ensures resilience to large-scale failures.
2. **Trade-offs**:
    - **Temporary Inconsistency**: Hinted replicas may lead to temporary inconsistencies until the handoff is completed.
    - **Storage Overhead**: Nodes must temporarily store hinted replicas, increasing storage requirements.

#### **Conclusion**
Dynamo’s **hinted handoff** mechanism is a key feature that ensures **high availability** and **durability** in the face of temporary node or network failures. By using a **sloppy quorum** and replicating data across multiple data centers, Dynamo can handle failures gracefully, making it a robust and scalable storage system for applications like Amazon’s shopping cart. This approach allows Dynamo to maintain its availability and durability guarantees even during large-scale failures, such as entire data center outages.

### Handling permanent failures: Replica synchronization

While **hinted handoff** effectively handles temporary node failures, Dynamo also addresses **permanent failures** and ensures **replica synchronization** through an **anti-entropy protocol**. This mechanism detects and resolves inconsistencies between replicas, maintaining data durability and integrity. Here’s a detailed explanation of how replica synchronization works in Dynamo:

#### **Need for Replica Synchronization**
1. **Limitations of Hinted Handoff**:
    - Hinted handoff works best when node failures are **transient** and system membership changes are **infrequent**.
    - If hinted replicas become **unavailable** before being returned to the original node, data durability can be compromised.
2. **Threats to Durability**:
    - Permanent node failures, network partitions, or data corruption can lead to inconsistencies between replicas.
    - An anti-entropy protocol is necessary to **detect and resolve** these inconsistencies.

#### **Anti-Entropy Protocol Using Merkle Trees**

1. **Merkle Trees**:
    - A **Merkle tree** is a hash tree where:
        - **Leaves** are hashes of individual key values.
        - **Parent nodes** are hashes of their children.
    - Merkle trees allow efficient detection of inconsistencies between replicas without requiring the entire dataset to be transferred.
2. **Advantages of Merkle Trees**:
    - **Independent Branch Checking**: Each branch of the tree can be checked independently, reducing the need to download the entire tree or dataset.
    - **Minimized Data Transfer**: Only the differing parts of the tree need to be synchronized, reducing the amount of data transferred.
    - **Reduced Disk Reads**: Merkle trees minimize the number of disk reads during the anti-entropy process.

#### **How Merkle Trees Work in Dynamo**

1. **Tree Construction**:
    - Each node maintains a **separate Merkle tree** for each **key range** (set of keys covered by a virtual node) it hosts.
    - This allows nodes to compare whether the keys within a key range are up-to-date.
2. **Tree Comparison**:
    - Two nodes exchange the **root hash** of the Merkle tree corresponding to the key ranges they have in common.
    - If the root hashes match, the replicas are consistent, and no synchronization is needed.
    - If the root hashes differ, the nodes traverse the tree, comparing child hashes until they identify the **leaves** (keys) that are out of sync.
3. **Synchronization**:
    - Once the inconsistent keys are identified, the nodes **synchronize** the corresponding data to resolve the differences.

#### **Challenges and Solutions**
1. **Recalculating Merkle Trees**:
    - When a node **joins** or **leaves** the system, many key ranges change, requiring the Merkle trees to be **recalculated**.
    - This issue is addressed by Dynamo’s **refined partitioning scheme** (described in Section 6.2), which minimizes the impact of membership changes on key ranges.

#### **Benefits of Anti-Entropy Protocol**
1. **Durability**:
    - Ensures that replicas remain consistent even in the face of permanent failures or data corruption.
2. **Efficiency**:
    - Minimizes the amount of data transferred and the number of disk reads during synchronization.
3. **Scalability**:
    - Allows independent synchronization of key ranges, making the process scalable for large datasets.

#### **Example Scenario**

1. **Tree Construction**:
    - Node A and Node B host the same key range and maintain separate Merkle trees for it.
2. **Tree Comparison**:
    - Node A and Node B exchange the root hashes of their Merkle trees.
    - If the root hashes differ, they traverse the tree to identify inconsistent keys.
3. **Synchronization**:
    - Node A and Node B synchronize the data for the inconsistent keys, ensuring replica consistency.

#### **Conclusion**

Dynamo’s **anti-entropy protocol**, powered by **Merkle trees**, is a critical mechanism for handling **permanent failures** and ensuring **replica synchronization**. By efficiently detecting and resolving inconsistencies between replicas, Dynamo maintains **data durability** and **integrity**, even in the face of node failures, network partitions, or data corruption. This approach, combined with Dynamo’s **refined partitioning scheme**, ensures that the system remains scalable and reliable for large-scale, distributed applications.

### Membership and Failure Detection

Dynamo employs mechanisms for **ring membership**, **external discovery**, and **failure detection** to ensure the system remains operational and consistent despite node outages, additions, or removals. These mechanisms are designed to handle both **transient** and **permanent** changes in the system. Here’s a detailed breakdown:

#### **Ring Membership**

1. **Explicit Membership Changes**:
    - In Amazon’s environment, node outages (due to failures or maintenance) are often **transient** and rarely signify permanent departures.
    - To avoid unnecessary rebalancing or replica repairs, Dynamo uses an **explicit mechanism** for adding or removing nodes from the ring.
    - An **administrator** uses a command-line tool or browser to issue membership changes (e.g., joining or removing a node).
2. **Persistent Membership History**:
    - The node serving the request writes the membership change and its timestamp to a **persistent store**.
    - Membership changes form a **history**, as nodes can be removed and added back multiple times.
3. **Gossip-Based Protocol**:
    - A **gossip protocol** propagates membership changes and maintains an **eventually consistent view** of the ring.
    - Each node contacts a **random peer** every second to reconcile their membership change histories.
4. **Token Assignment**:
    - When a node starts for the first time, it chooses its set of **tokens** (virtual nodes in the consistent hash space) and maps nodes to their respective token sets.
    - This mapping is persisted on disk and initially contains only the local node and its token set.
    - Partitioning and placement information propagate via the gossip protocol, ensuring each node is aware of the token ranges handled by its peers.
5. **Key Routing**:
    - Each node can forward a key’s read/write operations directly to the correct set of nodes, based on the reconciled token mappings.

#### **External Discovery**

1. **Logical Partition Prevention**:
    - The gossip-based mechanism could temporarily result in a **logically partitioned ring** (e.g., if an administrator contacts different nodes to join the ring independently).
    - To prevent this, Dynamo uses **seed nodes**, which are discovered via an external mechanism and known to all nodes.
2. **Role of Seed Nodes**:
    - Seeds act as a **common reference point** for all nodes to reconcile their membership.
    - Logical partitions are highly unlikely because all nodes eventually reconcile their membership with a seed.
    - Seeds are typically **fully functional nodes** in the Dynamo ring and can be obtained from static configuration or a configuration service.

#### **Failure Detection**

1. **Local Failure Detection**:
    - Dynamo uses a **local notion of failure detection** to avoid communicating with unreachable peers during `get()`, `put()`, or hinted replica transfers.
    - Node A considers Node B failed if B does not respond to A’s messages, even if B is responsive to other nodes.
2. **Handling Failures**:
    - If a node detects that a peer is unresponsive, it uses **alternate nodes** to service requests that map to the failed node’s partitions.
    - The detecting node periodically retries the failed peer to check for recovery.
3. **Decentralized Failure Detection**:
    - Early designs of Dynamo used a **decentralized failure detector** to maintain a globally consistent view of failure state.
    - Later, it was determined that the **explicit node join/leave methods** and **local failure detection** eliminate the need for a global failure view.
    - Nodes are notified of permanent additions/removals via explicit methods, while temporary failures are detected locally during communication attempts.

#### **Key Advantages**

1. **High Availability**:
    - Explicit membership changes and local failure detection ensure the system remains operational during transient or permanent node outages.
2. **Efficiency**:
    - Gossip-based protocols and seed nodes minimize communication overhead and prevent logical partitions.
3. **Scalability**:
    - Each node maintains a local view of the ring and token mappings, enabling efficient routing of requests.

#### **Conclusion**

Dynamo’s mechanisms for **ring membership**, **external discovery**, and **failure detection** ensure the system remains **highly available**, **consistent**, and **scalable**. By using explicit membership changes, gossip-based protocols, and local failure detection, Dynamo handles node additions, removals, and outages efficiently, making it a robust distributed storage system for Amazon’s large-scale applications.


### Adding/Removing Storage Nodes

Dynamo’s design ensures that **adding** or **removing storage nodes** is handled efficiently, with minimal disruption to the system. This process involves reallocating key ranges and transferring data between nodes while maintaining **load balance** and **latency requirements**. Here’s a detailed explanation:

#### **Adding a New Node**

1. **Token Assignment**:
    - When a new node (e.g., node X) is added to the system, it is assigned a number of **tokens** (virtual nodes) that are randomly scattered on the consistent hashing ring.
    - These tokens determine the key ranges for which node X becomes responsible.
2. **Key Range Reallocation**:
    - For every key range assigned to node X, there may be **existing nodes** (≤ N) currently handling keys within that range.
    - Due to the allocation of key ranges to node X, some existing nodes no longer need to store certain keys and **transfer** them to node X.
3. **Example Scenario**:
    - Consider the ring in Figure 2, where node X is added between nodes A and B.
    - Node X becomes responsible for key ranges: `(F, G]`, `(G, A]`, and `(A, X]`.
    - As a result, nodes B, C, and D no longer need to store keys in these ranges and **transfer** them to node X.
4. **Load Distribution**:
    - This approach ensures that the **load of key distribution** is **uniformly spread** across storage nodes, meeting latency requirements and enabling **fast bootstrapping**.
5. **Confirmation Mechanism**:
    - A **confirmation round** between the source and destination nodes ensures that no **duplicate transfers** occur for a given key range.

#### **Removing a Node**

1. **Reverse Process**:
    - When a node is removed, the reallocation of keys happens in a **reverse process**.
    - The key ranges previously handled by the removed node are reassigned to its **successor nodes** in the ring.
2. **Key Transfer**:
    - The removed node transfers its keys to the appropriate successor nodes, ensuring no data is lost.

#### **Conclusion**

Dynamo’s mechanism for **adding** and **removing storage nodes** is a critical feature that ensures **scalability**, **load balancing**, and **efficient bootstrapping**. By reallocating key ranges and transferring data between nodes, Dynamo maintains its performance and consistency, even as the system evolves. This approach is particularly important for meeting the **latency requirements** of large-scale, highly available applications like Amazon’s shopping cart.

## Implementation

Dynamo’s implementation is designed to be **scalable**, **highly available**, and **flexible**, with components that can be tailored to specific application needs. The system is built using **Java** and consists of three main software components on each storage node:

### **1. Local Persistence Engine**

1. **Pluggable Storage Engines**:
    
    - Dynamo allows different storage engines to be plugged in, including:
        - **Berkeley Database (BDB) Transactional Data Store**
        - **BDB Java Edition**
        - **MySQL**
        - **In-memory buffer with persistent backing store**
    - The choice of storage engine depends on the application’s **access patterns** and **object size distribution**.
        - BDB is suitable for objects typically in the order of **tens of kilobytes**.
        - MySQL can handle **larger objects**.
    - Most Dynamo production instances use **BDB Transactional Data Store**.
2. **Flexibility**:
    
    - The pluggable design allows applications to choose the most appropriate storage engine for their needs, optimizing performance and resource utilization.

### **2. Request Coordination**

1. **Event-Driven Messaging Substrate**:
    
    - The request coordination component is built on an **event-driven messaging substrate**, similar to the **SEDA architecture**.
    - All communications are implemented using **Java NIO channels** for efficient I/O operations.
2. **State Machine for Requests**:
    
    - Each client request results in the creation of a **state machine** on the node that received the request.
    - The state machine handles all logic for:
        - Identifying nodes responsible for a key.
        - Sending requests.
        - Waiting for responses.
        - Handling retries.
        - Processing replies.
        - Packaging the response to the client.
    - Each state machine instance handles **exactly one client request**.
3. **Read Operation State Machine**:
    
    - **Send read requests** to the nodes.
    - **Wait for the minimum number of required responses**.
    - If too few replies are received within a time bound, **fail the request**.
    - Otherwise, **gather all data versions** and determine which ones to return.
    - If versioning is enabled, perform **syntactic reconciliation** and generate an opaque write context containing the vector clock that subsumes all remaining versions.
4. **Read Repair**:
    
    - After returning the read response, the state machine waits briefly to receive any **outstanding responses**.
    - If **stale versions** are detected, the coordinator updates those nodes with the latest version.
    - This process, called **read repair**, ensures replicas are updated opportunistically, reducing the burden on the anti-entropy protocol.
5. **Write Operation Coordination**:
    
    - Write requests are coordinated by **one of the top N nodes** in the preference list.
    - To avoid uneven load distribution and SLA violations, **any of the top N nodes** can coordinate writes.
    - Since writes often follow reads, the coordinator for a write is chosen to be the node that **replied fastest to the previous read operation** (stored in the request’s context).
    - This optimization:
        - Increases the chances of **“read-your-writes” consistency**.
        - Reduces variability in request handling performance, improving performance at the **99.9th percentile**.

### **3. Membership and Failure Detection**

1. **Gossip-Based Protocol**:
    
    - Dynamo uses a **gossip-based protocol** to propagate membership changes and maintain an eventually consistent view of the ring.
    - Each node contacts a random peer every second to reconcile membership change histories.
2. **Local Failure Detection**:
    
    - Nodes detect failures locally by monitoring responsiveness to messages.
    - If a node fails to respond, it is marked as **unreachable**, and alternate nodes are used for requests.

### **Key Advantages**

1. **Scalability**:
    - The event-driven architecture and state machine model enable efficient handling of a large number of concurrent requests.
2. **Flexibility**:
    - Pluggable storage engines allow Dynamo to adapt to different application requirements.
3. **High Availability**:
    - Read repair and decentralized coordination ensure data consistency and availability, even during failures.
4. **Optimized Performance**:
    - Choosing the fastest responder for write coordination reduces latency and improves consistency.

### **Conclusion**

Dynamo’s implementation is a testament to its design principles of **scalability**, **availability**, and **flexibility**. By leveraging **pluggable storage engines**, **event-driven coordination**, and **state machines**, Dynamo efficiently handles read and write operations while maintaining consistency and performance. The use of **read repair** and **optimized write coordination** further enhances its reliability and responsiveness, making it a robust distributed storage system for Amazon’s large-scale applications.


## Experiences & Lessons Learned

Dynamo is used by several services at Amazon, each configured differently based on their specific requirements for **reconciliation logic**, **read/write quorum characteristics**, and **performance needs**. Here’s a summary of the key patterns, configurations, and lessons learned from Dynamo’s deployment:

### **Use Cases and Configurations**

1. **Business Logic-Specific Reconciliation**:
    
    - **Description**: Each data object is replicated across multiple nodes. In case of divergent versions, the **client application** performs its own reconciliation logic.
    - **Example**: The **shopping cart service** reconciles objects by merging different versions of a customer’s shopping cart.
    - **Use Case**: Suitable for applications where business logic dictates how conflicting versions should be resolved.
2. **Timestamp-Based Reconciliation**:
    
    - **Description**: In case of divergent versions, Dynamo performs a simple **“last write wins”** reconciliation based on the largest physical timestamp.
    - **Example**: Services maintaining **customer session information** use this mode.
    - **Use Case**: Ideal for applications where the most recent update is considered authoritative.
3. **High-Performance Read Engine**:
    
    - **Description**: Some services tune Dynamo’s quorum characteristics to use it as a **high-performance read engine**. These services have a **high read request rate** and **few updates**.
    - **Configuration**: Typically, **R=1** and **W=N**.
    - **Example**: Services maintaining **product catalogs** and **promotional items** use Dynamo as an authoritative persistence cache for data stored in heavier backing stores.
    - **Use Case**: Suitable for read-heavy workloads requiring **incremental scalability** and **low-latency reads**.

### **Configurable Parameters: N, R, W**

1. **N (Replication Factor)**:
    
    - Determines the **durability** of each object. A typical value is **3**.
    - Higher N increases durability but also increases storage and network overhead.
2. **R (Read Quorum)**:
    
    - The minimum number of nodes that must respond to a **read request** for it to be successful.
    - Lower R improves **read availability** but increases the risk of returning stale data.
3. **W (Write Quorum)**:
    
    - The minimum number of nodes that must respond to a **write request** for it to be successful.
    - Lower W improves **write availability** but increases the risk of inconsistency and data loss.
4. **Trade-offs**:
    
    - **Low W and R**: Increase availability but risk inconsistency and durability issues.
    - **High W and R**: Improve consistency and durability but may reduce availability and increase latency.
    - **Common Configuration**: **(3, 2, 2)** is widely used to balance performance, durability, consistency, and availability.

### **Lessons Learned**

1. **Durability vs. Availability**:
    
    - Traditional wisdom suggests that durability and availability go hand-in-hand, but this is not always true in Dynamo.
    - Increasing **W** can reduce the **vulnerability window for durability** but may decrease **availability** by requiring more nodes to be alive for writes.
2. **Network Latency**:
    
    - Dynamo nodes are distributed across **multiple data centers** connected by high-speed links.
    - The choice of nodes and their data center locations is critical to meeting **application SLAs**, as network latencies affect response times.
3. **Incremental Scalability**:
    
    - Dynamo’s partitioning and replication mechanisms allow services to scale **incrementally** by adding nodes as needed.
4. **Flexibility**:
    
    - Dynamo’s configurable parameters (N, R, W) allow applications to tune the system for their specific **performance**, **durability**, and **availability** requirements.

### **Performance Measurements**

- Measurements were taken on a live Dynamo system with a **(3, 2, 2)** configuration, running **hundreds of nodes** with homogeneous hardware.
- Nodes were distributed across **multiple data centers**, and network latencies were factored into meeting application SLAs.

### **Conclusion**

Dynamo’s flexibility and configurability have made it a versatile storage system for a wide range of applications at Amazon. By allowing services to tune parameters like **N, R, and W**, Dynamo balances **performance**, **durability**, **consistency**, and **availability** to meet specific SLAs. The system’s ability to handle **business logic-specific reconciliation**, **timestamp-based reconciliation**, and **high-performance reads** demonstrates its adaptability to diverse workloads. Lessons learned from Dynamo’s deployment highlight the importance of **trade-offs** in distributed systems and the need for careful configuration to meet application requirements.


## Balancing Performance and Durability

Dynamo’s design prioritizes **high availability**, but **performance** is equally critical for Amazon’s services, which often set stringent SLAs (e.g., 99.9% of read/write requests must complete within **300ms**). Achieving this level of performance on **commodity hardware** with distributed operations is challenging. Here’s how Dynamo balances **performance** and **durability**:

### **Performance Challenges**

1. **Hardware Limitations**:
    
    - Dynamo runs on **commodity hardware**, which has lower I/O throughput compared to high-end enterprise servers.
    - This makes consistently high performance for read/write operations non-trivial.
2. **Distributed Operations**:
    
    - Read and write operations involve **multiple storage nodes**, and their performance is limited by the **slowest of the R or W replicas**.
3. **Latency Patterns**:
    
    - Latency exhibits a **diurnal pattern**, mirroring the request rate (higher during the day, lower at night).
    - **Write latencies** are higher than read latencies because writes always involve disk access.
    - The **99.9th percentile latencies** are around **200ms**, an order of magnitude higher than averages, due to variability in request load, object sizes, and locality patterns.
![](http://s3.amazonaws.com/wernervogels/public/sosp/sosp-figure4-small.png)  
_Figure 4: Average and 99.9 percentiles of latencies for read and write requests during our peak request season of December 2006. The intervals between consecutive ticks in the x-axis correspond to 12 hours. Latencies follow a diurnal pattern similar to the request rate and 99.9 percentile latencies are an order of magnitude higher than averages._
### **Performance Optimization: Write Buffering**

1. **In-Memory Object Buffer**:
    
    - To improve performance, Dynamo introduces an **in-memory object buffer** on each storage node.
    - Writes are stored in the buffer and periodically flushed to disk by a writer thread.
    - Reads first check the buffer; if the key is present, the object is read from memory instead of disk.
2. **Impact on Latency**:
    
    - This optimization reduces the **99.9th percentile latency** by a factor of **5** during peak traffic, even with a small buffer (e.g., 1,000 objects).
    - Write buffering also **smooths out higher percentile latencies**, improving consistency in performance.
3. **Trade-off: Durability vs. Performance**:
    
    - Write buffering trades **durability** for **performance**.
    - A server crash can result in the loss of writes queued in the buffer.
4. **Durable Write Refinement**:
    
    - To mitigate durability risks, the coordinator selects **one of the N replicas** to perform a **“durable write”** (writing to disk immediately).
    - Since the coordinator waits only for **W responses**, the durable write does not affect the overall write performance.

![](http://s3.amazonaws.com/wernervogels/public/sosp/sosp-figure5-small.png)  
_Figure 5: Comparison of performance of 99.9th percentile latencies for buffered vs. non-buffered writes over a period of 24 hours. The intervals between consecutive ticks in the x-axis correspond to one hour._
### **Performance Metrics**

1. **Diurnal Latency Patterns**:
    
    - Figure 4 shows the **average and 99.9th percentile latencies** for read/write operations over 30 days.
    - Latency follows a diurnal pattern, with higher values during peak request rates.
2. **Buffered vs. Non-Buffered Writes**:
    
    - Figure 5 compares the **99.9th percentile latencies** for buffered and non-buffered writes over 24 hours.
    - Buffered writes significantly reduce latency and smooth out performance variability.

### **Conclusion**

Dynamo’s ability to balance **performance** and **durability** is a key factor in its success as a highly available distributed storage system. By introducing **write buffering** and **durable write refinement**, Dynamo achieves low-latency operations while mitigating the risks of data loss. These optimizations, combined with Dynamo’s flexible configuration options, make it a robust solution for Amazon’s diverse and demanding workloads.

## Ensuring Uniform Load distribution

Dynamo uses **consistent hashing** to partition its key space across replicas and ensure **uniform load distribution**. However, achieving uniform load distribution is challenging, especially when the access pattern to keys is **skewed**. This section discusses the evolution of Dynamo’s partitioning strategies, their impact on load distribution, and the lessons learned.

### **Load Imbalance in Dynamo**

![](http://s3.amazonaws.com/wernervogels/public/sosp/sosp-figure6-small.png)  
_Figure 6: Fraction of nodes that are out-of-balance (i.e., nodes whose request load is above a certain threshold from the average system load) and their corresponding request load. The interval between ticks in x-axis corresponds to a time period of 30 minutes._

1. **Key Distribution vs. Access Distribution**:
    
    - A **uniform key distribution** helps achieve uniform load distribution, assuming the access pattern to keys is not highly skewed.
    - Dynamo assumes that even with a skewed access pattern, there are enough **popular keys** to spread the load uniformly across nodes.
2. **Imbalance Ratio**:
    
    - The **imbalance ratio** (fraction of nodes with request load deviating from the average by more than 15%) was measured over 24 hours.
    - The imbalance ratio **decreases with increasing load**:
        - During **low loads**, the imbalance ratio is as high as **20%** (fewer popular keys are accessed, leading to higher imbalance).
        - During **high loads**, the imbalance ratio is close to **10%** (more popular keys are accessed, spreading the load evenly).

### **Evolution of Partitioning Strategies**

![](http://s3.amazonaws.com/wernervogels/public/sosp/sosp-figure7-small.png)  
_Figure 7: Partitioning and placement of keys in the three strategies. A, B, and C depict the three unique nodes that form the preference list for the key k1 on the consistent hashing ring (N=3). The shaded area indicates the key range for which nodes A, B, and C form the preference list. Dark arrows indicate the token locations for various nodes._

1. **Strategy 1: T Random Tokens per Node and Partition by Token Value**:
    
    - **Description**: Each node is assigned **T random tokens** from the hash space. Tokens are ordered, and each pair of consecutive tokens defines a key range.
    - **Issues**:
        - **Bootstrapping**: New nodes must “steal” key ranges from existing nodes, requiring **resource-intensive scans** that slow down the process.
        - **Merkle Tree Recalculation**: Node joins/leaves require recalculating Merkle trees for new ranges, which is non-trivial in production.
        - **Archival**: Random key ranges make it difficult to take snapshots of the entire key space efficiently.
    - **Fundamental Problem**: Data partitioning and placement are **intertwined**, making it hard to add nodes without affecting partitioning.

2. **Strategy 2: T Random Tokens per Node and Equal-Sized Partitions**:
    
    - **Description**: The hash space is divided into **Q equal-sized partitions**. Each node is assigned **T random tokens**, and partitions are placed on the first **N unique nodes** encountered while walking the ring.
    - **Advantages**:
        - Decouples **partitioning** and **placement**.
        - Enables changing the placement scheme at runtime.
    - **Disadvantage**: **Worst load balancing efficiency** among the three strategies.

3. **Strategy 3: Q/S Tokens per Node, Equal-Sized Partitions**:
    
    - **Description**: The hash space is divided into **Q equal-sized partitions**, and each node is assigned **Q/S tokens** (where S is the number of nodes). Tokens are redistributed when nodes join/leave to preserve properties.
    - **Advantages**:
        - **Best load balancing efficiency**.
        - **Faster bootstrapping/recovery**: Partitions are stored in separate files, enabling efficient relocation.
        - **Ease of archival**: Partitions can be archived separately, simplifying the process.
    - **Disadvantage**: Changing node membership requires **coordination** to preserve assignment properties.

### **Comparison of Strategies**

![](http://s3.amazonaws.com/wernervogels/public/sosp/sosp-figure8-small.png)  
_Figure 8: Comparison of the load distribution efficiency of different strategies for system with 30 nodes and N=3 with equal amount of metadata maintained at each node. The values of the system size and number of replicas are based on the typical configuration deployed for majority of our services._

1. **Load Balancing Efficiency**:
    
    - **Strategy 3** achieves the **best load balancing efficiency**, while **Strategy 2** has the **worst**.
    - **Strategy 1** serves as an interim setup during migration to **Strategy 3**.
2. **Metadata Size**:
    
    - **Strategy 3** reduces the size of membership information maintained at each node by **three orders of magnitude** compared to **Strategy 1**.
3. **Bootstrapping and Archival**:
    
    - **Strategy 3** simplifies bootstrapping, recovery, and archival by using **fixed partition ranges** and **separate files**.

### **Key Takeaways**

1. **Decoupling Partitioning and Placement**:
    
    - Decoupling partitioning and placement enables **independent scaling** and **flexible placement schemes**.
2. **Equal-Sized Partitions**:
    
    - Equal-sized partitions improve **load balancing efficiency** and simplify **bootstrapping** and **archival**.
3. **Trade-offs**:
    
    - **Strategy 3** offers the best balance of **efficiency**, **simplicity**, and **scalability**, though it requires coordination for node membership changes.

### **Conclusion**

Dynamo’s partitioning strategies have evolved to address challenges in **load distribution**, **bootstrapping**, and **archival**. By decoupling **partitioning** and **placement** and adopting **equal-sized partitions**, Dynamo achieves **uniform load distribution** and **efficient scaling**. **Strategy 3** emerges as the most effective approach, balancing **performance**, **simplicity**, and **scalability** while meeting the demands of Amazon’s large-scale services.


## Divergent Versions: When and How Many?

Dynamo’s design prioritizes **availability** over **consistency**, which can lead to **divergent versions** of data items. Understanding when and how often these divergent versions occur is crucial for assessing the system’s consistency guarantees. This section discusses the scenarios that cause divergent versions and provides insights from a **live production environment**.

### **Causes of Divergent Versions**

1. **Failure Scenarios**:
    
    - **Node failures**, **data center failures**, and **network partitions** can prevent updates from propagating to all replicas, leading to divergent versions.
2. **Concurrent Writers**:
    
    - A large number of **concurrent writers** to a single data item can result in multiple nodes coordinating updates concurrently, creating divergent versions.

### **Impact of Divergent Versions**

1. **Syntactic vs. Semantic Reconciliation**:
    
    - **Syntactic Reconciliation**: Divergent versions can often be reconciled using **vector clocks** without involving the application.
    - **Semantic Reconciliation**: If vector clocks cannot reconcile the versions, the application’s **business logic** must handle the reconciliation, increasing load and complexity.
2. **Desired Outcome**:
    
    - It is preferred to keep the number of divergent versions **as low as possible** to minimize the need for semantic reconciliation and maintain system efficiency.

### **Production Insights: Shopping Cart Service**

1. **Experimental Data**:
    
    - Over a **24-hour period**, the number of versions returned to the shopping cart service was profiled:
        - **99.94%** of requests saw **exactly one version**.
        - **0.00057%** of requests saw **2 versions**.
        - **0.00047%** of requests saw **3 versions**.
        - **0.00009%** of requests saw **4 versions**.
    - This data shows that **divergent versions are rare** in practice.
2. **Key Observation**:
    
    - The increase in divergent versions is primarily caused by **concurrent writers** rather than failures.
    - **Busy robots** (automated client programs) are the main drivers of concurrent writes, rarely humans.

### **Key Takeaways**

1. **Rare Occurrence**:
    
    - Divergent versions are **rare**, with the vast majority of requests seeing **only one version**.
2. **Concurrent Writers**:
    
    - The primary cause of divergent versions is **concurrent writes**, often triggered by **automated clients**.
3. **Minimizing Reconciliation**:
    
    - Keeping the number of divergent versions low reduces the need for **semantic reconciliation**, improving system efficiency.

### **Conclusion**

Dynamo’s tradeoff of **consistency** for **availability** can lead to divergent versions, but these occurrences are **rare** in practice. Most requests see **only one version**, and the few cases of divergence are primarily caused by **concurrent writers** rather than failures. By minimizing the need for **semantic reconciliation**, Dynamo maintains its efficiency and usability, making it a robust system for Amazon’s large-scale applications.

## Client-driven or Server-driven Coordination

Dynamo’s request coordination mechanism can be implemented in two ways: **server-driven coordination** (default) or **client-driven coordination**. This section compares the two approaches, focusing on their **latency**, **load distribution**, and **efficiency**.

### **Server-Driven Coordination**

1. **Process**:
    
    - A **load balancer** uniformly assigns client requests to nodes in the ring.
    - **Read requests**: Any Dynamo node can act as a coordinator.
    - **Write requests**: Must be coordinated by a node in the key’s **preference list**, as these nodes are responsible for creating a new version stamp that causally subsumes the updated version.
2. **Advantages**:
    
    - Simplifies client-side logic, as coordination is handled by the server.
    - Ensures **uniform load distribution** across nodes.
3. **Disadvantages**:
    
    - Introduces **additional latency** due to the extra network hop when requests are assigned to a random node.
    - Load balancers and network variability increase response time, especially at higher percentiles.

### **Client-Driven Coordination**

1. **Process**:
    
    - The **state machine** is moved to the client nodes.
    - Clients use a library to perform request coordination locally.
    - Clients periodically poll a random Dynamo node (every **10 seconds**) to download the current view of Dynamo’s membership state.
    - Using this information, clients determine the **preference list** for any given key.
    - **Read requests**: Coordinated locally by the client, avoiding the extra network hop.
    - **Write requests**: Forwarded to a node in the key’s preference list or coordinated locally if Dynamo uses **timestamp-based versioning**.
2. **Advantages**:
    
    - **Reduced latency**: Eliminates the overhead of the load balancer and the extra network hop.
    - **Implicit load distribution**: Near-uniform assignment of keys to storage nodes ensures fair load distribution.
    - **Scalability**: Pull-based membership updates scale better with a large number of clients.
3. **Disadvantages**:
    
    - Clients may be exposed to **stale membership information** for up to 10 seconds.
    - Requires clients to detect and refresh stale membership information immediately.

### **Latency Improvements**

_Table 2: Performance of client-driven and server-driven coordination approaches._

|   |   |   |   |   |
|---|---|---|---|---|
||99.9th percentile read latency (ms)|99.9th percentile write latency (ms)|Average read latency (ms)|Average write latency (ms)|
|Server-driven|68.9|68.5|3.9|4.02|
|Client-driven|30.4|30.4|1.55|1.9|

1. **Experimental Data**:
    
    - Table 2 shows the **latency improvements** observed over 24 hours using client-driven coordination compared to server-driven coordination:
        - **99.9th percentile latencies**: Reduced by at least **30 milliseconds**.
        - **Average latencies**: Reduced by **3 to 4 milliseconds**.
2. **Reasons for Improvement**:
    
    - **Elimination of overhead**: Client-driven coordination removes the load balancer and extra network hop.
    - **Caching and write buffers**: Dynamo’s storage engine caches and write buffers have good hit ratios, reducing average latencies.
    - **Variability reduction**: Load balancers and network introduce additional variability, which is minimized in client-driven coordination.

### **Key Takeaways**

1. **Latency Reduction**:
    
    - Client-driven coordination significantly reduces **latency**, especially at the **99.9th percentile**.
2. **Load Distribution**:
    
    - Both approaches ensure **uniform load distribution**, but client-driven coordination achieves this implicitly.
3. **Scalability**:
    
    - Client-driven coordination scales better with a large number of clients, as it uses a **pull-based** membership update mechanism.
4. **Trade-offs**:
    
    - Client-driven coordination introduces the risk of **stale membership information**, but clients can detect and refresh this information immediately.

### **Conclusion**

Client-driven coordination offers **significant latency improvements** over server-driven coordination, especially at higher percentiles, while maintaining **uniform load distribution** and **scalability**. By eliminating the overhead of load balancers and extra network hops, client-driven coordination enhances Dynamo’s performance, making it a more efficient choice for latency-sensitive applications. However, careful handling of **stale membership information** is necessary to ensure consistency and reliability.

## Balancing background vs. foreground tasks

In Dynamo, each node performs **background tasks** (e.g., replica synchronization, data handoff) in addition to **foreground tasks** (e.g., `put`/`get` operations). Early production settings revealed that background tasks could cause **resource contention**, negatively impacting the performance of foreground operations. To address this, Dynamo introduced an **admission control mechanism** to balance background and foreground tasks. Here’s how it works:

### **Challenges**

1. **Resource Contention**:
    
    - Background tasks, such as replica synchronization and data handoff, compete for resources (e.g., disk, database locks) with foreground tasks.
    - This contention can lead to **increased latencies** and **timeouts** for foreground operations.
2. **Performance Impact**:
    
    - Uncontrolled background tasks can degrade the performance of critical `put`/`get` operations, violating **SLAs**.

### **Admission Control Mechanism**

1. **Runtime Slices**:
    
    - Background tasks use an **admission controller** to reserve **runtime slices** of shared resources (e.g., database access).
    - The controller ensures that background tasks run only when they do not significantly affect foreground operations.
2. **Feedback Mechanism**:
    
    - The admission controller monitors the performance of foreground tasks and adjusts the number of runtime slices available to background tasks.
    - This feedback loop ensures that background tasks are **intrusive only within acceptable limits**.

### **Monitoring and Control**

1. **Foreground Task Metrics**:
    
    - The controller monitors the following aspects of foreground operations:
        - **Disk operation latencies**.
        - **Failed database accesses** due to lock contention or transaction timeouts.
        - **Request queue wait times**.
2. **Threshold-Based Decisions**:
    
    - The controller checks whether the **percentiles of latencies** (or failures) in a trailing time window are close to a **desired threshold**.
    - For example, it compares the **99th percentile database read latency** (over the last 60 seconds) to a preset threshold (e.g., 50ms).
3. **Resource Availability Assessment**:
    
    - Based on the monitored metrics, the controller assesses the **resource availability** for foreground operations.
    - It then decides how many runtime slices to allocate to background tasks.

### **Key Advantages**

1. **Performance Guarantees**:
    
    - Ensures that foreground tasks meet **latency SLAs** by limiting the impact of background tasks.
2. **Dynamic Adjustment**:
    
    - The feedback mechanism dynamically adjusts resource allocation based on real-time performance, ensuring optimal balance.
3. **Efficient Resource Utilization**:
    
    - Background tasks are allowed to run only when resources are available, minimizing contention.

### **Conclusion**

Dynamo’s **admission control mechanism** effectively balances **background** and **foreground tasks**, ensuring that critical `put`/`get` operations meet performance SLAs. By monitoring foreground task metrics and dynamically adjusting resource allocation, Dynamo minimizes the impact of background tasks on system performance. This approach enhances **resource utilization** and maintains **high availability** and **low latency**, making Dynamo a robust and efficient distributed storage system.


## Discussion and Conclusions

Dynamo has been a cornerstone of Amazon’s e-commerce platform, providing **high availability**, **scalability**, and **customizability** for a variety of core services. This section summarizes the key experiences, lessons learned, and conclusions from Dynamo’s implementation and maintenance.

### **Key Experiences**

1. **High Availability**:
    
    - Dynamo has achieved **99.9995% availability**, ensuring successful responses for nearly all requests without timeouts.
    - No **data loss events** have occurred to date, demonstrating its reliability.
2. **Customizability**:
    
    - Dynamo provides **tunable parameters** (`N`, `R`, `W`) that allow service owners to customize their storage system to meet specific **performance**, **durability**, and **consistency SLAs**.
    - Unlike commercial data stores, Dynamo exposes **data consistency** and **reconciliation logic** to developers, enabling tailored solutions for specific use cases.
3. **Application Complexity**:
    
    - While exposing consistency and reconciliation logic may seem to increase application complexity, Amazon’s platform is already designed to handle **failure modes** and **inconsistencies**.
    - Porting existing applications to Dynamo was relatively straightforward, though new applications require careful analysis to choose appropriate **conflict resolution mechanisms**.
4. **Full Membership Model**:
    
    - Dynamo uses a **full membership model**, where each node actively gossips the full routing table with other nodes.
    - This works well for systems with **hundreds of nodes**, but scaling to **tens of thousands of nodes** is non-trivial due to the overhead of maintaining routing tables.
    - **Hierarchical extensions** or **O(1) DHT systems** (e.g., [14]) could address this limitation.

### **Lessons Learned**

1. **Decentralized Techniques**:
    
    - Dynamo demonstrates that **decentralized techniques** can be combined to build a **highly available system**.
    - Its success in Amazon’s challenging environment shows that **eventual consistency** can be a foundation for highly available applications.
2. **Incremental Scalability**:
    
    - Dynamo’s design allows services to **scale up or down** based on request load, providing flexibility and cost efficiency.
3. **Trade-offs**:
    
    - Dynamo’s tunable parameters allow service owners to balance **performance**, **durability**, and **consistency** based on their specific needs.
    - This flexibility is a key advantage over traditional one-size-fits-all storage systems.

### **Conclusions**

1. **Dynamo’s Success**:
    
    - Dynamo has been **highly successful** in providing **availability**, **scalability**, and **customizability** for Amazon’s e-commerce platform.
    - It has effectively handled **server failures**, **data center failures**, and **network partitions**, ensuring uninterrupted service.
2. **Eventual Consistency**:
    
    - Dynamo’s use of **eventual consistency** demonstrates that it can be a viable building block for **highly available applications**.
    - Its success challenges the notion that strong consistency is always necessary for critical systems.
3. **Future Directions**:
    
    - While Dynamo works well for medium-sized systems, scaling to **tens of thousands of nodes** requires further innovation, such as **hierarchical extensions** or **O(1) DHT systems**.

### **Final Thoughts**

Dynamo’s design and implementation have set a benchmark for **highly available** and **scalable** distributed storage systems. By combining decentralized techniques and providing tunable parameters, Dynamo offers a flexible and reliable solution for modern applications. Its success in Amazon’s demanding environment underscores the potential of **eventual consistency** and **decentralized architectures** as foundations for building robust, scalable systems.



## Links

https://www.allthingsdistributed.com/2007/10/amazons_dynamo.html