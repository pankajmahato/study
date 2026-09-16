## Overview

An **object store** (or **object storage system**) is a way of managing and persisting data as discrete units called **objects**, each of which bundles together:
1. **The data itself** (the object’s payload, which can be any file—image, document, video, log file, backup, etc.).
2. **A globally unique identifier** (usually a key or URI) that you use to retrieve that object.
3. **Rich, user-defined metadata**—arbitrary key/value pairs that describe or classify the object.

All data is stored in one large repository which may be distributed across multiple physical storage devices, instead of being divided into files or folders.

It is easier to understand object-based storage when you compare it to more traditional forms of storage – file and block storage.

![storage types including object storage](https://cloudian.com/wp-content/uploads/2021/08/maxresdefaultsmall.jpg)

### File Storage

File storage stores data in folders. This method, also known as hierarchical storage, simulates how paper documents are stored. When data needs to be accessed, a computer system must look for it using its path in the folder structure.
File storage uses TCP/IP as its transport, and devices typically use the NFS protocol in Linux and SMB in Windows.

- **Managing Local Files**: File storage simplifies file sharing and organization, especially in smaller environments where ease of use is key. 
- **Document Collaboration**: It enables real-time document collaboration, making it easier for teams to work together without complicated workflows. 
- **Backup & Disaster Recovery** : File storage is reliable for managing backups and ensuring data recovery in case of unexpected events.

### Block Storage

Block storage splits a file into separate data blocks, and stores each of these blocks as a separate data unit. Each block has an address, and so the storage system can find data without needing a path to a folder. This also allows data to be split into smaller pieces and stored in a distributed manner. Whenever a file is accessed, the storage system software assembles the file from the required blocks.
Block storage uses FC or iSCSI for transport, and devices operate as direct attached storage or via a storage area network (SAN).

- **Databases**: Block storage's low-latency design ensures fast and efficient data retrieval, making it perfect for database-driven applications. 
- **Server Storage**: It excels at distributing data across multiple volumes, making it ideal for virtualized systems and server environments. 
- **Email Servers**: With its ability to handle vast amounts of data, block storage is perfect for managing high-performance email servers.

### Object Storage

In object storage systems, data blocks that make up a file or “object”, together with its metadata, are all kept together. Extra metadata is added to each object, which makes it possible to access data with no hierarchy. All objects are placed in a unified address space. In order to find an object, users provide a unique ID.
Object-based storage uses TCP/IP as its transport, and devices communicate using HTTP and REST APIs.
Metadata is an important part of object storage technology. Metadata is determined by the user, and allows flexible analysis and retrieval of the data in a storage pool, based on its function and characteristics.
The main advantage of object storage is that you can group devices into large storage pools, and distribute those pools across multiple locations. This not only allows unlimited scale, but also improves resilience and high availability of the data.

- **Video Surveillance**: With the ability to handle massive volumes of video data, object storage is perfect for video surveillance systems. 
- **Backup & Recovery**: Object storage provides the scalability and security needed for reliable data backup and [ransomware recovery](https://objectfirst.com/guides/ransomware/ransomware-data-recovery/). 
- **Media & Entertainment**: In industries with large multimedia assets, object storage's scalability ensures efficient management and fast access to content libraries.

### Key Features of Object Stores

- **Flat, Global Namespace**
    - Each object lives in a single, flat address space (often called a “bucket” or “container”).
    - You don’t worry about directories or inode hierarchies; you simply put and get by key.

- **Built-in Metadata**
    - Every object can carry rich, customizable metadata (e.g., content type, creation date, tags, user notes).
    - This makes it trivial to classify, search, or automate lifecycle policies based on metadata.

- **Massive Scalability**
    - Designed to handle billions (or more) of objects and petabytes (or exabytes) of data.
    - Partitioning, sharding, and distributed hashing under the covers let you grow capacity seamlessly.

- **RESTful/HTTP-based APIs**
    - Standard interfaces (S3, OpenStack Swift, Azure Blob) let you `PUT`/`GET`/`DELETE` objects over HTTP.
    - SDKs exist in virtually every language, making integration straightforward.

- **High Durability & Availability**
    - Data is replicated (often three or more copies) across multiple fault domains (disks, racks, or even regions).
    - Providers quote “11 nines” of annual durability, meaning loss of data is extremely unlikely.

- **Eventual Consistency (or tunable consistency)**
    - Many object stores are eventually consistent—writes propagate asynchronously for speed and scale.
    - Some systems let you choose stronger consistency (e.g., read-after-write) for critical workloads.

- **Cost-Effective Storage Tiers**
    - Hot (frequently accessed), cool/infrequent, and archive/deep-freeze tiers let you trade latency for cost.
    - Lifecycle policies can auto-transition objects between tiers based on age or access patterns.

- **Versioning & Lifecycle Management**
    - You can enable versioning to keep every change to an object as a separate, addressable version.
    - Lifecycle rules let you automatically expire, delete, or transition old versions and unused objects.

- **Access Control & Security**
    - Per-object ACLs or bucket policies control who can read, write, or delete.
    - Integration with IAM (identity management) and support for signed URLs lets you grant time-limited access.
    - Encryption at rest and in transit is often built-in or pluggable by key-management services.

- **Multi-Region & Geo-Replication**
    - Some object stores let you replicate objects automatically between geographic regions for disaster recovery or locality.
    - You can read from the closest region to minimize latency.

- **Event Notifications & Integration**
    - You can configure notifications (e.g., SNS, SQS, Lambda) on object events (create, delete, restore).
    - This enables event-driven architectures: image-processing pipelines, ETL jobs, cache invalidation, etc.

- **Strong Ecosystem & Tooling**
    - Built-in support in big data tools (Hadoop, Spark), backup/restore systems, container platforms (Kubernetes PVs), and CI/CD pipelines.
    - CLI tools and GUI dashboards let you browse, audit, and manage at scale.

### Common Use Cases

- **Static website hosting** (serve HTML, CSS, JS directly from object storage).
- **Backups & archives** (snapshots of databases, system images).
- **Media repositories** (images, video, audio).
- **Big data lakes** (raw logs, analytics data, machine-learning training sets).
- **Container images** (OCI registries often back images with object storage).
- **Content distribution** (origin storage for CDNs).

## Architecture & Features

![](https://miro.medium.com/v2/resize:fit:826/1*EfGkQYt_uW8yNG3X7hVjSA.png)


### OBJECT STORAGE COMPONENTS
There are **_five major components_** to the Object Storage Architecture.

#### 1. Object
**Object Storage breaks data into self-describing “objects,” each carrying its own payload plus enough metadata so it can manage itself.** Here’s how the pieces fit:
1. **Object ID & Interface**
    - Every object lives on the OSD under a **96-bit object identifier**.
    - You read or write using the triplet **`<objectID, offset, length>`**, not a filesystem path.

2. **Three Object Flavors**
    - **Root Object**: Identifies the device itself (its capacity, health, etc.).
    - **Group Object**: Acts like a “directory” or container, grouping related objects.
    - **User Object**: Holds actual application data (the file’s bytes).
	1. **User Object Breakdown**
	    - **Application Data**
	        - The raw file contents you `Open`/`Read`/`Write`—exactly what you’d see in a traditional file.
	    - **Storage Attributes**
	        - Device‐managed metadata (object ID, block pointers, length, allocated size)—analogous to inodes.
	    - **User Attributes**
	        - Arbitrary key/value pairs for ACLs, ownership, tags, checksums—opaque to the device but essential for catalogs and policy engines.

By unifying payload and per‐object metadata, and exposing a simple byte‐range API, object stores give you maximum flexibility (different RAID levels, QoS, lifecycle rules) on a per‐object basis, while keeping all metadata in a scalable service.

#### 2. OSD (Object-based Storage Device)
An **Object‐based Storage Device (OSD)** is essentially a “smart” disk—combining storage media, a CPU, RAM, and a network interface—to natively store and serve **objects** (keyed byte‐ranges) instead of raw blocks. Rather than sitting behind a RAID controller, OSDs plug directly into an Ethernet fabric (often using iSCSI or a dedicated object protocol) so compute nodes can talk to many disks in parallel, massively boosting throughput.

Under the hood, each OSD provides four core services:
1. **Data Store**
    - Persists and retrieves object payloads by `<objectID, offset, length>`.
    - Bytes are never accessed as generic blocks—only via object IDs.

2. **Intelligent Layout**
    - Uses onboard CPU & RAM to decide how to stripe or place object fragments on disk sectors.
    - Optimizes read/write patterns, compaction, and local caching without host intervention.

3. **Metadata Management**
    - Keeps per‐object metadata (block pointers, logical size, location) locally—offloading inode‐style work from any central server.
    - The metadata service only needs to map object IDs to OSDs; the OSD itself handles sector‐level details.

4. **Security & Access Control**
    - Enforces authorization on every operation, supports encryption/decryption at rest, and can validate signed requests.
    - Ensures only permitted clients may read or write specific objects.

By embedding storage logic into the device, OSDs eliminate the traditional metadata‐server and RAID bottlenecks, enabling truly parallel, scalable object storage architectures (as offered by vendors like HP and Dell).

#### 3. Installable File System

An **Installable File System** sits on each compute node and translates familiar POSIX file operations (e.g., open, read, write) into object‐store calls, striping data across multiple OSDs for parallel I/O. Mounted like a regular filesystem, it:
- Presents a standard directory/file view to applications
- Converts file reads/writes into `<objectID, offset, length>` operations under the hood
- Distributes each file’s data as objects across the OSD cluster for high throughput
- Handles caching and consistency locally, while talking directly to OSDs (no central filer)

Tools like **s3fs-fuse** implement this: they let you mount an S3 bucket as `/mnt/data`, yet all file activity is mapped to S3 object PUT/GET calls and automatically striped for performance.

#### 4. Metadata Server

A **Metadata Server (MDS)** sits between clients and OSDs to coordinate shared access while keeping client caches consistent. Its responsibilities include:
- **Namespace Management**: Tracks the global file‐and‐directory layout (who owns which object IDs, ACLs, directory structure).
- **Authentication & Authorization**: Verifies identities, enforces permissions on every file or directory operation.
- **Cache Consistency**: Issues leases or invalidations so that multiple compute‐node caches stay coherent.
- **Capacity & Load Management**: Monitors overall storage usage, provision new OSDs, and balances requests across them.
- **Scaling**: By limiting itself to only ~10% of the total metadata workload (VFS‐style directory and ACL operations) and rest handled by Inode/block‐level work (≈90%), the MDS can be deployed as a cluster that scales independently of the OSDs, ensuring that namespace operations never bottleneck even as data volume and I/O rates grow.

#### 5. Network Fabric

**Network Fabric** is the backbone that interconnects compute nodes, Metadata Servers, and OSDs into one cohesive storage system. Key points:
- **Unified Connectivity**: All clients, OSDs, and metadata services communicate over the same TCP/IP network, eliminating separate SAN or NAS networks.
- **Commodity Hardware**: Leveraging gigabit (or faster) Ethernet brings down both capital and operational costs—no more expensive Fibre Channel switches or HBAs.
- **High Performance**: Modern Ethernet speeds (10 GbE, 25 GbE, 100 GbE) meet or exceed traditional storage‐specific transports, often with lower latency when properly configured.
- **Transport-Agnostic**: While most deployments use Ethernet, any TCP/IP-capable fabric (InfiniBand, Myrinet, etc.) works—so you can optimize for latency or throughput without changing the object-storage software stack.
- **Simplified Management**: Network teams already know how to build, secure, and scale IP networks, so storage traffic can be treated just like any other data center traffic, with VLANs, QoS, monitoring, and failover baked in.

### Two Major Benefits of this Architecture

1. **Direct, Parallel I/O for High Throughput**
    - Compute nodes communicate straight to intelligent Object Storage Devices (OSDs) over the network—no middleman file server.
    - Objects can be striped or multipart-uploaded/downloaded (e.g. S3’s multipart API), so adding more OSDs or parallel connections scales bandwidth and IOPS nearly linearly.

2. **Distributed Metadata, No Single Bottleneck**
    - Metadata responsibilities (namespace/catalog vs. physical placement) are split and sharded across many services, not centralized.
    - Clients never all hammer one metadata server—lookups, listings, and ACL checks scale out.
    - Data itself is sliced and replicated across multiple disks, preserving availability and performance even under heavy shared-file workloads.

### How data is accessed in Parallel

Object storage replaces the old model—where a file server intermediated every I/O—with a network‐attached **Object Storage Device (OSD)** that combines raw media (disk or tape) and onboard logic.

- **Intelligent disks**: Each OSD understands object commands (PUT/GET) and manages its own local data.
- **Direct I/O**: Clients (compute nodes) talk straight to the relevant OSDs—no central file server or metadata‐server hop—so latency and contention drop.
- **Striping & linear scale**: Files (objects) are split (“striped”) across many OSDs. As you add more devices, aggregate bandwidth and IOPS climb in proportion, delivering true parallel performance.

### What happens behind the scene at Distributed Metadata

Traditional storage relies on a single, monolithic metadata server that handles two jobs:
1. **Logical namespace management** (the VFS layer)—tracking filenames, directories, and the virtual view of data.
2. **Physical layout control** (the inode layer)—deciding where on disk each block of data lives.

Object storage breaks these duties apart and spreads them across many nodes: the “logical” metadata (object listings, buckets, keys) is handled separately from the “physical” metadata (where each object’s data actually resides). By distributing both the VFS-like catalog work (only about 10% of a traditional NFS server’s effort) and the heavier inode-style block management (the remaining 90%), object stores eliminate the central-metadata-server bottleneck. AWS S3, Google Cloud Storage, and Azure Blob all use this object-based architecture to scale metadata operations and storage I/O independently.

### Operations

#### Read
When a client wants to read data, it follows a two‐phase protocol:

1. **Metadata Lookup & Token Issuance**
    - The client first asks the Metadata Server for the object’s layout.
    - The server responds with the list of OSD locations (object shards or replicas) **plus a capability token**—a signed, time‐limited credential that encodes “which object IDs and byte ranges you may read (or write), and for how long.”

2. **Direct Reads from OSDs**
    - Armed with that token, the client talks straight to the appropriate OSD(s), sending `<objectID, offset, length>` along with its capability.
    - Each OSD verifies the token, then streams the requested bytes back to the client—no further involvement of the Metadata Server.

This separation lets the Metadata Server handle only lightweight namespace and authorization logic, while the bulk data moves in parallel directly from many OSDs, maximizing throughput and minimizing bottlenecks.

#### Write
Write operations mirror the read path, with the client first obtaining layout and authorization, then streaming data in parallel to the OSDs:

1. **Metadata Lookup & Capability Issuance**
    - The client asks the Metadata Server where and how to place the new or updated object (or object parts).
    - The server replies with:
        - **Target OSD locations** (which shards or replicas to write).
        - A **security capability** (a signed token granting “write” permission for those object IDs, byte ranges, and for a limited time).

2. **Direct Writes to OSDs**
    - The client sends its `<objectID, offset, length, data>` write requests directly to each designated OSD, including the capability token.
    - Each OSD verifies the token, writes the data to its local storage (and possibly encodes or replicates it), then responds with an acknowledgment.

3. **Commit & Cleanup**
    - Once all OSDs have successfully stored their fragments, the client (or Metadata Server, depending on the protocol) issues a final “commit” or “complete multipart upload” call back to the Metadata Server.
    - The Metadata Server then marks the object as durable and visible to other clients and revokes or expires the capability.

By splitting authorization and layout (Metadata Server) from the heavy data path (OSDs), writes scale out in parallel and avoid a central I/O bottleneck.


### Performance Comparison: Traditional NAS vs. Object Storage

1. **Write Path Burden**
    - **Traditional NAS/File Server**: Every write goes through a central filer head, which must decide how to map file writes into disk blocks and optimize placement across tracks and sectors. Under heavy load—especially with many clients checkpointing simultaneously—this filer head becomes a major bottleneck.
    - **Object Storage (OSD-based)**: Clients send their data straight to the OSDs, which each have onboard CPU and memory to manage their own local placement and replication. There’s no intermediate “filer head,” so a single client can write in parallel to multiple OSDs, and many clients can do so simultaneously without overloading any single metadata server or controller.
        
2. **Peak and Sustained Throughput**
    - **Checkpoint and Burst Workloads**: In HPC or large-scale compute (e.g. MPI jobs hitting a barrier and checkpointing), object storage can absorb massive bursts across dozens or hundreds of OSDs at once—peak write rates that traditional SAN/NAS can’t match.
    - **Aggregate Sustained I/O**: For long-running analytics or streaming workloads, the distributed, peer-to-peer nature of OSDs delivers far higher overall bandwidth. No one component ever has to serialize all I/O.
        
3. **Modern Analytics & ML Integration**
    - **Externalized Data**: Systems like Snowflake or many ML platforms store their “persistent” data in object stores (e.g. S3) and pull it into local caches or compute nodes as needed.
    - **Overhead vs. Optimization**: Reading terabytes off S3 is slower than a local disk read, but private networking (e.g. AWS PrivateLink), regional co-location of compute and storage, and smart pre-fetching can dramatically reduce latency. This design trades off a bit of raw speed for virtually unlimited scale and simplified durability.

**Bottom Line:**  
By offloading both block-placement logic and replication to intelligent OSDs—and eliminating a centralized filer head—object storage architectures achieve peak write speeds and sustained aggregate throughput that no traditional SAN/NAS solution can match, making them ideal for checkpoint-heavy HPC, large-scale analytics, and massively parallel ML pipelines.


### Scalability in Object Store

- **OSD‐Driven Load Distribution**
    - Each intelligent OSD handles ~90% of the I/O and block‐management work locally—placing data, replicating, and servicing reads/writes—so the Metadata Server only ever handles the remaining ~10% (namespace operations, ACL checks).
    - Because the Metadata Server’s work is both light and “embarrassingly parallel,” you can scale it out into a cluster without hitting a hard bottleneck.

- **Parallel, High-Bandwidth Fabric**
    - Clients talk directly to many OSDs over multiple high-bandwidth, multipath network links (e.g. 10 / 25 / 100 Gb Ethernet), so adding more devices or NICs increases aggregate throughput linearly.
    - There’s no single controller or RAID head to serialize I/O—every OSD can serve data in parallel.

- **Geo-Replication & Global Access**
    - Active replication of objects (or erasure-coded fragments) across geographically distributed data centers lets users in any region read the same objects locally, reducing latency and providing disaster resilience.
    - You can add OSD clusters in new regions and hook them into the same object-store namespace, scaling capacity and read/write footprint worldwide.

Together, these design choices let object storage **grow horizontally**—in capacity, IOPS, and throughput—without ever re-architecting the core metadata or control plane.

### Security in Object Store

1. **Mutual Authentication**
    - **Clients (compute nodes)** authenticate to the object store using IAM credentials (access/secret keys), role‐based tokens, multi‐factor/session tokens, or federated SSO via external identity providers.
    - **Storage nodes (OSDs & Metadata Servers)** likewise prove their identity—ensuring clients talk only to genuine, trusted devices.

2. **Fine-Grained Authorization**
    - Every operation (`GET`, `PUT`, `DELETE`, etc.) is checked against ACLs, roles, and allowed “verbs” on buckets or objects.
    - Capabilities or signed tokens carry explicit permissions (which object IDs, byte-ranges, and actions are permitted, and for how long).

3. **Data & Command Integrity**
    - **CRC or checksums** on each object—and often on every network frame—guard against corruption in transit or on disk.
    - OSDs verify checksums before acknowledging writes and after reading data to clients.

4. **Encryption In-Flight and At-Rest**
    - **In-flight**: Traffic between clients, metadata servers, and OSDs runs over IPsec or TLS, ensuring confidentiality and preventing man-in-the-middle attacks.
    - **At-rest**: Objects can be encrypted with AES keys managed by a KMS; OSDs handle encryption/decryption transparently, protecting data on physical media.

By combining **robust authentication**, **policy-driven authorization**, **end-to-end integrity checks**, and **strong encryption**, object stores ensure that only authorized clients and devices can access data—and that all data and commands remain confidential and unmodified.

### Summary

In summary, while cloud providers often keep the full details of their object‐storage internals under wraps, public white papers reveal its core strengths and trade-offs. Object storage excels at scalable, durable, and secure handling of massive, unstructured data sets—decoupling storage from compute, distributing both data and metadata, and providing rich access controls. However, it isn’t optimized for extremely high-IO, low-latency workloads the way block storage or local SSDs are.

Before adopting it, you should:
- Review your provider’s specific implementation and recommended best practices
- Verify that your access patterns (throughput, latency, concurrency) match what object storage delivers
- Confirm you can meet your security, compliance, and backup requirements
- Ensure your architecture leverages caching or edge-compute where needed to mask any performance gaps

By balancing these considerations—security, scalability, cost, and performance—you can determine whether object storage is the right foundation for your application’s needs.

### Key Amazon S3 Features

- **Object Model & Namespace**
    - Store up to 5 TB per object, each addressed by a unique key within a bucket.
    - Flat, global namespace: no directories—just bucket + key.

- **Storage Classes & Lifecycle**
    - Tier data automatically across Standard, Standard-IA, One Zone-IA, Glacier (Instant / Flexible / Deep Archive), and Intelligent-Tiering.
    - Define lifecycle rules (by prefix, tag, or age) to transition or expire objects.

- **Security & Access Control**
    - Fine-grained IAM policies, bucket policies, ACLs, and Block Public Access.
    - Server-side encryption (SSE-S3, SSE-KMS, SSE-C, DSSE-KMS) and client-side encryption.
    - VPC Gateway/Interface endpoints and PrivateLink for private, AWS-backed connectivity.

- **Strong Consistency**
    - Read-after-write and list consistency guarantee that once a write succeeds, subsequent reads/list operations immediately see the update—no stale data.
        
- **High Performance & Scalability**
    - Virtually unlimited throughput: each prefix supports ≥ 3,500 PUT/POST/DELETE and ≥ 5,500 GET requests per second.
    - Parallel multipart uploads/downloads stripe data across many servers.

- **Versioning & Object Lock**
    - Preserve every version of an object, recover from accidental overwrite or delete.
    - Object Lock / WORM mode for immutable, compliance-grade storage.

- **Data Protection & Durability**
    - 11 nines (99.999999999%) durability by default, automatically replicating data across multiple AZs.
    - Integrity checks (CRC/SHA) on every transfer, automatic self-healing.

- **Data Management & Analytics**
    - Object tagging for cost-allocation, lifecycle, and access policies.
    - Inventory reports for object metadata, Storage Class Analysis to guide transitions.
    - In-place querying via S3 Select, Athena, and Redshift Spectrum.

- **Event Notifications**
    - Trigger Lambda, SNS, or SQS on object create/delete or prefix/suffix filters—build serverless pipelines and workflows.
        
- **Audit & Monitoring**
    - Server access logging and CloudTrail data events for every API call.
    - CloudWatch metrics (1-minute granularity) and alarms at bucket or prefix level.
    - Cost allocation tags and Cost Explorer integration for budgeting.

- **Cross-Region & Same-Region Replication**
    - Automatically replicate objects (and metadata/tags) between buckets in same or different regions for DR, locality, and compliance.
    - Replication Time Control (RTC) for SLA-backed replication visibility.

- **S3 Access Points & Object Lambda**
    - Create custom hostnames with tailored network and policy controls per application.
    - Inject custom Lambda logic on GET/HEAD to transform data on-the-fly (e.g. redact, thumbnail, filter).