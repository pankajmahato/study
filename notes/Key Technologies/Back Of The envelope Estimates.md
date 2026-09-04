## Estimations

In a system design interview, sometimes you are asked to estimate system capacity or performance requirements using a back-of-the-envelope estimation. According to Jeff Dean, Google Senior Fellow, “back-of-the-envelope calculations are estimates you create using a combination of thought experiments and common performance numbers to get a good feel for which designs will meet your requirements” [1].

You need to have a good sense of scalability basics to effectively carry out back-of-the-envelope estimation. The following concepts should be well understood: power of two [2], latency numbers every programmer should know, and availability numbers.

### Power of two

Although data volume can become enormous when dealing with distributed systems, calculation all boils down to the basics. To obtain correct calculations, it is critical to know the data volume unit using the power of 2. A byte is a sequence of 8 bits. An ASCII character uses one byte of memory (8 bits). Below is a table explaining the data volume unit (Table 1).

| Power | Approximate value       | Full name  | Short name |
| ----- | ----------------------- | ---------- | ---------- |
| 0     | 1 Ten - 10 ^ 0          | 1 Byte     | 1 B        |
| 10    | 1 Thousand - 10 ^ 3     | 1 Kilobyte | 1 KB       |
| 20    | 1 Million - 10 ^ 6      | 1 Megabyte | 1 MB       |
| 30    | 1 Billion - 10 ^ 9      | 1 Gigabyte | 1 GB       |
| 40    | 1 Trillion - 10 ^ 12    | 1 Terabyte | 1 TB       |
| 50    | 1 Quadrillion - 10 ^ 15 | 1 Petabyte | 1 PB       |

### Latency numbers

Dr. Dean from Google reveals the length of typical computer operations in 2010 [1]. Some numbers are outdated as computers become faster and more powerful. However, those numbers should still be able to give us an idea of the fastness and slowness of different computer operations.

| Operation name                                | Time                    |
| --------------------------------------------- | ----------------------- |
| L1 cache reference                            | 0.5 ns                  |
| Branch mispredict                             | 5 ns                    |
| L2 cache reference                            | 7 ns                    |
| Mutex lock/unlock                             | 100 ns                  |
| Main memory reference                         | 100 ns                  |
| Compress 1K bytes with Zippy                  | 10,000 ns = 10 µs       |
| Send 2K bytes over 1 Gbps network             | 20,000 ns = 20 µs       |
| Read 1 MB sequentially from memory            | 250,000 ns = 250 µs     |
| Round trip within the same datacenter         | 500,000 ns = 500 µs     |
| Disk seek                                     | 10,000,000 ns = 10 ms   |
| Read 1 MB sequentially from the network       | 10,000,000 ns = 10 ms   |
| Read 1 MB sequentially from disk              | 30,000,000 ns = 30 ms   |
| Send packet CA (California) ->Netherlands->CA | 150,000,000 ns = 150 ms |

### Availability numbers

High availability is the ability of a system to be continuously operational for a desirably long period of time. High availability is measured as a percentage, with 100% means a service that has 0 downtime. Most services fall between 99% and 100%.

A service level agreement (SLA) is a commonly used term for service providers. This is an agreement between you (the service provider) and your customer, and this agreement formally defines the level of uptime your service will deliver. Cloud providers Amazon [4], Google [5] and Microsoft [6] set their SLAs at 99.9% or above. Uptime is traditionally measured in nines. The more the nines, the better. As shown in Table 3, the number of nines correlate to the expected system downtime.

|**Availability %**|**Downtime per day**|**Downtime per week**|**Downtime per month**|**Downtime per year**|
|---|---|---|---|---|
|99%|14.40 minutes|1.68 hours|7.31 hours|3.65 days|
|99.99%|8.64 seconds|1.01 minutes|4.38 minutes|52.60 minutes|
|99.999%|864.00|6.05 seconds|26.30 seconds|5.26 minutes|
|99.9999%|86.40 milliseconds|604.80|2.63 seconds|31.56 seconds|

### Storage

| Encoding  | Usage                           | Bytes per char | Efficiency                  | Common?           |
| --------- | ------------------------------- | -------------- | --------------------------- | ----------------- |
| **UTF-8** | Web, APIs, files, databases     | 1–4            | High (for ASCII-heavy text) | ✅ **Most common** |
| UTF-16    | Windows, Java, internal memory  | 2–4            | Moderate                    | Somewhat common   |
| UTF-32    | Specialized tools, internal use | 4              | Low                         | ❌ Rare            |
