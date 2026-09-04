## Overview

![](https://miro.medium.com/v2/resize:fit:875/1*3oWBDvi2ktR4rD8FAXKXUg.png)

### **_Question :-_** How does any Video goes from Streamer‘s end ?

The streamer starts the video-stream. The source could be any video and audio wired up to an encoder, something like the popular open-source OBS Software.

**Note:** Some platforms like You tube provide easy to use software to stream from a browser with a webcam OR directly from a mobile phone camera.

![](https://miro.medium.com/v2/resize:fit:875/1*TVUfd3vdiKieOljaYnF8tA.png)

### **_Question :-_** Who performs the encoding and Why ?

**Encoding of video is performed by the client (the streamer)**.

#### Explanation:
- **Encoder Software or Hardware:** Streamers use encoding software (like OBS, Streamlabs, XSplit) or hardware encoders to convert their raw video and audio signals into a compressed digital format suitable for streaming.
- **Encoding Process:** This process involves compressing high-quality video/audio into formats like H.264 or H.265, and packaging it into streaming protocols like RTMP.
	- **_Question:-_** Explain something about the RTMP ?
		- RTMP is a TCP based protocol. It started long time ago as the video-streaming protocol for Adobe Flash.
		- The Encoders can easily speak RTMP OR it’s secure variant RTMPS.
- **Client-Side Responsibility:** The client (streamer) handles this encoding task because it requires real-time processing and low-latency transmission, and it's typically more efficient to perform this close to the source.

#### Why is encoding client-side?
- **Latency:** Performing encoding on the client minimizes delay between capture and transmission.
- **Resource Utilization:** The streamer’s device (PC, gaming console, etc.) handles this heavy lifting.
- **Flexibility:** Streamers can adjust quality settings (bitrate, resolution) based on their bandwidth.

### **_Question:-_** What is the best way to upload data from streamers end ?

- Most Live-Streaming platforms provide the **_point of presence_** servers worldwide.

![](https://miro.medium.com/v2/resize:fit:403/1*bAyryd4QYMjylfw8PLV2Gg.png)

- The Streamer connects to the closest **_Point of Presence_** Server.

![](https://miro.medium.com/v2/resize:fit:875/1*LAmoPclqnFi5EiE_ngZG_w.png)

PoP servers **bring the data ingestion point closer to the source (streamers)**, reducing latency, balancing load, and improving overall performance of the streaming platform. This setup ensures smoother streaming experiences both for streamers (upload side) and viewers.

### **_Question:-_** What is a PoP ?

A **Point of Presence (PoP)** is a physical location or facility that serves as an access point for users to connect to a larger network, typically the internet or a private network. It is a critical component in telecommunications and networking infrastructure, often used by Internet Service Providers (ISPs), Content Delivery Networks (CDNs), and cloud service providers.

#### Key Features of a PoP:

1. **Access Point**: It provides a gateway for users or devices to connect to a network.
2. **Hardware**: Includes routers, switches, servers, and other networking equipment.
3. **Connectivity**: Links local networks to larger networks, such as the internet or a wide area network (WAN).
4. **Redundancy**: Often designed with backup systems to ensure reliability and uptime.
5. **Scalability**: Supports increasing traffic and user demand by adding more resources.

#### Functions of a PoP:

- **Traffic Routing**: Directs data packets between users and the network.
- **Content Caching**: Stores frequently accessed data locally to reduce latency (common in CDNs).
- **Network Optimization**: Improves performance by reducing the distance data travels.
- **Security**: Implements firewalls, intrusion detection systems, and other security measures.

#### Examples of PoP Usage:

- **ISPs**: Provide internet access to customers by connecting them to the ISP's network via PoPs.
- **CDNs**: Deliver content (e.g., videos, images) faster by caching it at PoPs closer to users.
- **Cloud Providers**: Enable users to access cloud services with lower latency by placing PoPs in strategic locations.

In summary, a PoP is a critical infrastructure element that enhances network performance, reliability, and accessibility for end-users.

### **_Question:-_** How does Streamer connects to the closest PoP Server and what happens after?

The connection between a streamer and the closest Point of Presence (PoP) server is typically established through a combination of network infrastructure, DNS routing, and protocols designed for low-latency, reliable data transfer. Here's an overview of how this connection generally works:

#### 1. **Initial Discovering of the Closest PoP**

- **DNS-Based Resolution:**
    
    - When the streamer’s broadcasting software (like OBS) starts streaming, it contacts a designated URL or domain name provided by the streaming platform.
    - This domain is often configured with DNS records that resolve to the IP addresses of multiple PoPs.
    - The DNS system uses various algorithms (like round-robin, latency-based routing, or geographic proximity) to resolve the URL to the "best" (closest or most optimal) PoP IP address.
- **Geo-Location & Routing:**
    
    - Advanced CDNs and streaming platforms use geolocation databases to predict which PoP is nearest to the streamer based on their IP address.
    - Some platforms use **Anycast routing**, where the same IP address is advertised from multiple locations, and network routing automatically directs the streamer to the closest PoP based on internet engineering techniques.

#### 2. **Establishing a Connection**

- Once the DNS resolves to a specific PoP:
    - The streamer’s encoder software makes a connection to the selected PoP server using a streaming protocol such as **RTMP**.
    - The connection process involves TCP handshakes (or QUIC/UDP if supported), establishing a reliable channel for continuous video upload.

#### 3. **Stream Upload & Transmission**

- After connection:
    - The streamer encodes the video and sends the data streams directly to that PoP server.
    - Once the stream reaches to the closest POP Server, it is transmitted over a fast and reliable backbone network, for further processing to the Platform/Data-Centre.
    
![](https://miro.medium.com/v2/resize:fit:875/1*oNzgaHVrmfZznyPo4utJeQ.png)


### **_Question:-_** What’s the main goal of transmitting the video to the Platform ?

**_Answer →_** The main goal of transmitting the video to the platform is to offer the video-stream in different qualities and bit-rates.

![](https://miro.medium.com/v2/resize:fit:875/1*Dja0E7Eb_DRySRTJWNJftQ.png)

**_Note:_** The exact processing steps may vary from platform to platform and output streaming formats.

1. **Real-time Content Capture and Transmission**
    - Enable live streaming so viewers can watch the content as it is being created.
    - Minimize latency to ensure a near real-time experience for viewers.
    - The incoming video-stream is transcoded to different resolutions and bit-rates.
2. **Content Processing & Transcoding**
    - The platform processes incoming video streams for quality adjustment (transcoding into multiple bitrates and resolutions).
    - Prepare the stream for adaptive bitrate streaming to viewers with different network conditions.
    - The transcoded stream is divided into smaller video-segments, which are of few-seconds in length.
    - This step of Transcoding/Segmentation is highly compute intensive and therefore the Input stream is usually transcoded to different formats in parallel.

![](https://miro.medium.com/v2/resize:fit:875/1*iN98MuSde2UB3U3cNtr0gQ.png)
	
![](https://miro.medium.com/v2/resize:fit:875/1*V0Xpj5GObHuxih5woL7gWA.png)

1. **Distribution to Viewers**
    - Make the stream accessible globally via Content Delivery Networks (CDNs), ensuring viewers receive a smooth, high-quality experience regardless of their location.
    - Manage bandwidth and server load efficiently.
2. **Archiving and On-Demand Content**
    - Store streams for future on-demand viewing, clips, or highlights.
3. **Monetization & Engagement**
    - Enable features like chat, reactions, subscriptions, and donations by transmitting interactions alongside video content.

**Question:-** What’s the Adaptive BitRate Streaming ?

**_Answer →_** Modern Video Players automatically choose the best video resolution and bit-rate, based on quality of viewer’s internet connection and can adjust on the fly, by requesting different bit-rates as the network condition changes.


### **_Question:-_** What happens after the steps starting from transcoding ?

1. **Transcoding Process:**
    - **Objective:** Convert the original stream into multiple bitrates and formats for adaptive streaming.

2. **Packaging:**
    - **Action:** Collect video segments from the transcoding process and package them into different live streaming formats that video players can understand.

3. **Streaming Formats:** (After packaging we stream using these formats)
    - **HLS (HTTP Live Streaming):**
        - **Popularity:** The most common format for live streaming, invented by Apple in 2009. It's widely used even today.
        - **Components:** Consists of a manifest file and a series of video chunks.
        - **Manifest File:** Serves as a directory for the video player, indicating available formats and locations of video chunks.
    - **DASH (Dynamic Adaptive Streaming over HTTP):**
        - **Overview:** Another popular streaming format. Note that Apple devices do not natively support DASH.

![](https://miro.medium.com/v2/resize:fit:875/1*FgfsMmVaHvDaxb1m_DpPYA.png)

4. **Content Delivery and Latency Reduction:**
    - **CDN Caching:**
        - **Purpose:** Cache the resulting HLS files and video chunks to reduce "Last-Mile-Latency" to viewers.

![](https://miro.medium.com/v2/resize:fit:875/1*3404OG2-qiqltJvXAVbG0Q.png)


5. **Video Delivery to Viewers:**
    - **Final Step:** Video arrives at the viewer’s video player, completing the end-to-end live streaming process.

![](https://miro.medium.com/v2/resize:fit:875/1*FkdAI5Gf9EVABRgHJXW30w.png)

6. **Latency Considerations:**
    - **Glass-to-Glass Latency:** Typically around 20 seconds.
    - **Optimization Strategies:**
        - Streamers and platforms can tune various factors, potentially sacrificing video quality to reduce latency.
        - Streamers should optimize their local setup to minimize latency from camera to streaming platform.

![](https://miro.medium.com/v2/resize:fit:875/0*JmS2byQDQYFioHGd.png)

7. **Challenges for Live Streaming Platforms:**
    - **Infrastructure Requirements:** Need for robust architecture to handle high traffic and ensure reliability.
    - **Latency Management:** Balancing stream quality and latency.
    - **Compatibility:** Ensuring streams are compatible across different devices and formats.
    - **Security and Compliance:** Protecting content and adhering to legal standards.

### **_Question →_** What are various challenges in case of Live streaming ?

#### 1. **Network Latency and Bandwidth Management**

- **Latency:** Achieving low latency is crucial for real-time interaction between streamers and viewers. Balancing latency with video quality is a major challenge.
- **Bandwidth:** High-quality streams require significant bandwidth. Managing bandwidth efficiently to prevent buffering while accommodating users with varying network speeds is essential.

#### 2. **Scalability**

- Platforms must handle thousands or even millions of concurrent viewers, requiring scalable infrastructure to ensure consistent performance.
- Spikes in viewer numbers, particularly during popular events, must be managed smoothly.

#### 3. **Content Delivery Network (CDN) Strategy**

- Efficient distribution of streams globally using CDNs to minimize latency and optimize load is critical.
- Selecting optimal CDN locations (PoPs) based on viewer distribution is necessary.

#### 4. **Adaptive Bitrate Streaming**

- Ensuring seamless transitions between different video quality levels to match changing network conditions requires robust algorithms.
- Keeping latency low while switching streams without interruption is challenging.

#### 5. **Device and Format Compatibility**

- Supporting a wide range of devices (smartphones, tablets, desktops, smart TVs) and stream formats (HLS, DASH) requires extensive compatibility testing and adjustments.

#### 6. **Security and Privacy**

- Protecting streams from piracy, unauthorized recording, and distribution.
- Ensuring viewer privacy and securing data transmission.

#### 7. **Content Moderation**

- Implementing effective moderation tools to manage inappropriate content, comments, or behavior during live streams.
- Balancing user freedom and platform policies requires careful consideration and robust support systems.

#### 8. **Monetization**

- Establishing reliable and user-friendly monetization options (ads, subscriptions, donations) without negatively impacting viewer experience.
- Handling payment processing, virtual currencies, and revenue distribution to content creators.

#### 9. **Infrastructure and Cost Management**

- Ensuring cost-effective use of server resources, data storage, and bandwidth.
- Managing infrastructure to handle peak loads efficiently without unnecessary expenses during off-peak times.

#### 10. **Viewer Engagement and Interactivity**

- Incorporating interactive elements like chat, polls, and Q&A without increasing latency.
- Developing features that keep viewers engaged and encourage community building.

Addressing these challenges requires a combination of technological innovation, strategic planning, and ongoing performance optimization to deliver seamless and engaging live streaming experiences.

### **_Question →_** What are the various choices of Protocol for Broadcasting ?

#### 1. **WebRTC (Web Real-Time Communication)**

- **Transport Layer**: Based on UDP (User Datagram Protocol), which is a lossy protocol.
    
    **Advantages**:
    - **Low Latency**: Provides real-time communication with minimal delay, making it suitable for interactive applications like video conferencing.
    - **Direct Peer-to-Peer**: Enables direct peer-to-peer connections, reducing server load and potentially improving speed.
    
    **Challenges**:
    - **Loss of Data**: Since UDP does not guarantee delivery, packet loss can occur, leading to degraded video/audio quality.
    - **Limited Browser Support**: Although widely supported in modern browsers, inconsistencies can exist, and older browsers may not support WebRTC.
    - **Network Limitations**: NAT and firewall configurations can disrupt connectivity. WebRTC employs ICE and TURN for NAT traversal, but these are not foolproof.
    - **Resource Consumption**: High CPU, memory, and bandwidth usage, especially on devices handling multiple streams or high-resolution media.

#### 2. **RTMP (Real-Time Messaging Protocol)**

- **Transport Layer**: Built on top of TCP (Transmission Control Protocol), which is a lossless protocol.
    
    **Advantages**:
    - **Latency Characteristics**: Designed for live streaming, it offers appropriate latency levels for broadcasting.
    - **Industry Adoption**: Long history of use with extensive client and server library support, allowing easier integration and deployment.
    - **Reliability**: TCP ensures no packet loss, maintaining video quality.
    
    **Challenges**:
    - **Deprecation by Adobe**: Originally developed by Adobe, it's less favored now compared to HTTP-based protocols (like HLS or DASH).
    - **Library Size and Simplicity**: Compact (around 100 KB library size) and lightweight, but newer protocols offer more features for adaptive streaming.

#### Conclusion

Each protocol serves specific needs within broadcasting:
- **WebRTC** is ideal for applications demanding low latency and peer-to-peer communication but may encounter connectivity and quality challenges.
- **RTMP** remains a robust choice for traditional streaming setups due to its reliability and widespread support, though newer protocols may provide enhanced features for modern streaming requirements.

### **_Question →_** What is Complete Workflow of Live Streaming Platform?

![[live_streaming.png]]

#### 1. **Video Encoding Properties**

Before streaming begins, content creators must encode their videos with proper properties:

- **Aspect Ratio:** Defines the proportional width and height (e.g., 16:9, 4:3, 1:1), affecting display on different devices.
- **Video Codec:** Typically H.264/AVC, providing high-quality compression.
- **Audio Codec:** Often AAC, ensuring clear and efficient audio transmission.

---

#### 2. **Streaming Ingestion Process**

##### a. **Client Initiation**

- The streamer’s app connects to an **API-Server** to start streaming.
- During this handshake, the client receives:
    - **StreamId** — to stay connected to the same POP.
    - **Security Tokens** — for authentication.
    - **URI** — which points to the specific Point of Presence (POP).

##### b. **Connecting to the Nearest POP**

- The client establishes a connection with the closest **POP Server**, selected via DNS, geolocation, or routing.
- Protocols like **RTMP** or **WebRTC** facilitate real-time data transfer.

##### c. **POP Server Receives Stream**

- The **POP terminates incoming connections**.
    
- **Role:** Cache the stream locally (for playback, not storage).
    
- **Note:** At this stage, viewers can start **watching the stream live** — **they do not need to wait for storage or long-term processing**. This means:
    **_Viewers can see the livestream almost immediately as the stream is ingested and processed_**. The media chunks are rapidly created and delivered in real time, enabling live viewing **before** the content is stored permanently.

#### d. **Relaying to Data Center**

- The **POP forwards** the stream to **Data Center servers**.
- **Data Center Functions:**
    - Terminating POP connection.
    - Authenticating and associating the stream.
    - **Encoding & transcoding** into various qualities and formats.
    - Creating **manifests** (e.g., MPEG-DASH).
    - Segmenting videos into small chunks (e.g., 1 second).
    - **Storing media for VOD** (for later on-demand playback).

---

#### 3. **Encoding & Storage at Data Center**

- The Data Center performs **transcoding**, converting incoming streams into multiple quality levels.
- During this process, the stream is **segmented into small chunks** (e.g., 1 second segments).
- **At this stage, the media is packaged into adaptive streaming formats**:
    - **HLS** (HTTP Live Streaming): creates a manifest file (`.m3u8`) and media chunks.
    - **DASH** (Dynamic Adaptive Streaming over HTTP): creates an MPD manifest and segments.
- These formats **facilitate distributed, adaptive, real-time playback**.

---

#### 4. **Content Delivery & Player Playback (Viewer Side)**

##### a. **Initial Customer Connection**

- When a viewer wishes to watch a live stream:
    - They connect to the **nearest POP** based on proximity.
    - The **POP checks** its local cache for the **manifest file**.
    - If **not available**, it **requests** the manifest from the **Data Center**.
    - The Data Center fetches the latest manifest from **Encoding Hosts**, caches it, and responds.
    - The **POP caches** the manifest and **delivers** it to the viewer.

#### **_Important_**:
- **The viewer can start watching the stream immediately** — **even before it is stored permanently** — because media chunks are generated and streamed **live** in real time.
- This deepens the experience: **viewers see the live content as it happens**, not only after storage.

##### b. **Handling Multiple Viewers**

- **Same POP (e.g., India):**
    - Multiple viewers share the **local cache**, reducing load on Data Center.
- **Different POPs (e.g., USA):**
    - Request manifests from local Data Center caches.
    - Cache and serve repeatedly to scale efficiently.

---

#### 5. **Adaptive Streaming and Video Progression**

- The client downloads media segments based on the manifest (e.g., MPEG-DASH).
- Ways the video plays smoothly:
    - **Segment Requests:** Client requests small chunks, adjusting quality dynamically based on network conditions.
    - **Manifest Updates:** The server updates the manifest regularly as new segments are created during live streams.

---

#### 6. **Handling Live Stream Dynamics & Challenges**

##### a. **Stream Continuity & Updates**
- The manifest files are continually updated in real time via HTTP push.
- The media segments are generated dynamically during streaming.

##### b. **Thundering Herd Problem**
- When many clients request the manifest simultaneously (e.g., popular streams):
    - The **cache-waiting-time** mechanism helps prevent overload by staggering requests to Data Centers.
    - The POP serves requests from its cache whenever possible to reduce loads.

##### c. **Latency Considerations**

- Typical **end-to-end latency (Glass-to-Glass)** is around 20 seconds.
- To reduce latency:
    - Streamers should optimize their local setup (camera to platform).
    - Caching at POPs and CDN layers reduces Last-Mile Latency.

#### Code (Meramid)
```
flowchart LR

    subgraph Pre-Streaming Setup

        A[Video Encoding Properties - Aspect Ratio, Codec, etc.] --> B[Client App - Streamer]

    end


    subgraph Streaming Ingestion Process

        B --> C{API-Server - Stream Initiation}

        C -- StreamId, Security Tokens, URI --> B

        B --> D(Nearest POP Server)

        D -- RTMP/WebRTC --> E(POP Server Receives Stream & Terminates Connection)

        E -- Caches Stream - Live Playback --> F{Viewers Can Watch Live Immediately}

        E --> G(Data Center Servers)

    end

  

	    subgraph Data Center Processing & Storage
	
	        G --> H{Authenticates and Associates Stream}
	
	        H --> I{Encoding & Transcoding - Multiple Qualities}
	
	        I --> J{Segmenting into Chunks - e.g., 1 sec}
	
	        J --> K{Manifest Creation - HLS/DASH}
	
	        K --> L(Storing Media for VOD)

    end

  

    subgraph Content Delivery & Playback - Viewer Side

        M[Viewer Connects to Nearest POP] --> N{POP Checks Local Cache for Manifest}

        N -- Manifest Available --> O(POP Delivers Manifest to Viewer)

        N -- Manifest Not Available --> P(Requests Manifest from Data Center)

        P --> Q(Data Center Fetches/Caches Manifest)

        Q --> P

        P --> O

        O --> R{Viewer Starts Watching Immediately}

    end

  

    subgraph Adaptive Streaming & Dynamics

        R --> S{Client Requests Segments}

        S --> T{Adjusts Quality Dynamically - Network Conditions}

        K --> U{Manifest Updates in Real-time - HTTP Push}

        U --> S

        V[Multiple Viewers] --> W{Same POP - Local Cache}

        V --> X{Different POPs - Local Data Center Cache}

    end

    style A fill:#f9f,stroke:#333,stroke-width:2px

    style L fill:#ccf,stroke:#333,stroke-width:2px

    style R fill:#ccf,stroke:#333,stroke-width:2px

    style D fill:#ccf,stroke:#333,stroke-width:2px
```
