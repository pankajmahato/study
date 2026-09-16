## Overview

**Geospatial indexing** is a technique used in databases to efficiently **store, query, and retrieve data based on geographic coordinates** (e.g., latitude and longitude). It enables fast operations like finding:

- Nearby locations (e.g., "drivers within 5 km")    
- Points within a certain area (e.g., "restaurants inside this city boundary")
- Distances between coordinates

🧭 Why Geospatial Indexing?
Imagine you’re building:
- A **food delivery app** and want to find drivers near a restaurant
- A **map service** showing nearby gas stations
- A **real estate portal** listing properties in a region

Without geospatial indexing, these location-based queries would be slow, especially at scale.

Most geospatial indexing systems transform geographic coordinates into **indexable structures**, such as:

- **R-trees / Quad-trees / Google S2** (traditional spatial data structures)
- **Geohashing**: Encodes latitude/longitude into a string; nearby areas have similar prefixes

Even though the underlying implementations of those approaches are different, the highlevel
idea is the same, that is, to divide the map into smaller areas and build indexes
for fast search. Among those, geohash, quadtree, and Google S2 are most widely used
in real-world applications.

## Geospatial indexing Techniques

### Geohash
Geohash is better than the evenly divided grid option. It works by reducing the two dimensional
longitude and latitude data into a one-dimensional string of letters and digits.
Geohash algorithms work by recursively dividing the world into smaller and smaller
grids with each additional bit. Let's go over how geohash works at a high level.

![](https://miro.medium.com/v2/resize:fit:779/0*X7ylKMlheKiF-858.jpeg)

The precision factor determines the size of the cell. For instance, a precision factor of one creates a cell 5,000km high and 5,000km wide, a precision factor of six creates a cell 0.61km high and 1.22km wide, and a precision factor of nine creates a cell 4.77m high and 4.77m wide (cells are not always square).

![](https://miro.medium.com/v2/resize:fit:811/0*ljRTLZ5FylxTIZVG.png)

Repeat this subdivision until the grid size is within the precision desired. Geohash usually
uses base32 representation [15]. Let's take a look at two examples.

• geohash of the Google headquarter (length = 6 ):
	1001 10110 01001 10000 11011 11010 (base32 in binary) -t
	9q9hvu (base32)
• geohash of the Facebook headquarter (length = 6):
	1001 10110 01001 10001 10000 10111 (base32 in binary) -t
	9q9jhr (base32)

Geohash has 12 precisions (also called levels) as shown in Table 1.4. The precision factor
determines the size of the grid. We are only interested in geohashes with lengths between
4 and 6. This is because when it's longer than 6, the grid size is too small, while if it is
smaller than 4, the grid size is too large (see Table 1.4).

 📏 Table 1.4: Geohash Length to Grid Size Mapping

| Geohash Length | Grid Width × Height     |
| -------------- | ----------------------- |
| 1              | 5,009.4 km × 4,092.6 km |
| 2              | 1,252.3 km × 624.1 km   |
| 3              | 156.5 km × 156 km       |
| 4              | 39.1 km × 19.5 km       |
| 5              | 4.9 km × 4.9 km         |
| 6              | 1.2 km × 609.4 m        |
| 7              | 152.9 m × 152.4 m       |
| 8              | 38.2 m × 19 m           |
| 9              | 4.8 m × 4.8 m           |
| 10             | 1.2 m × 59.5 cm         |
| 11             | 14.9 cm × 14.9 cm       |
| 12             | 3.7 cm × 1.9 cm         |

---
📍 Table 1.5: Radius to Geohash Length Mapping

| Radius (Kilometers) | Approximate Radius (Miles) | Geohash Length |
| ------------------- | -------------------------- | -------------- |
| 0.5 km              | 0.31 miles                 | 6              |
| 1 km                | 0.62 miles                 | 5              |
| 2 km                | 1.24 miles                 | 5              |
| 5 km                | 3.1 miles                  | 4              |
| 20 km               | 12.42 miles                | 4              |

---

#### Issues with GeoHashing

✅ **Geohash Boundary Issues**

Boundary Issue 1: No Shared Prefix for Nearby Points

- **Description**: Two geographically close points may have completely different geohash prefixes.
- **Cause**: Geohash divides the world into binary halves — points on either side of the equator or prime meridian may land in different geohash trees.
- **Example**: La Roche-Chalais (`u000`) and Pomerol (`ezzz`) are just 30 km apart, but share no prefix.
- **Impact**: Prefix-based geohash queries like  
    `SELECT * FROM geohash_index WHERE geohash LIKE '9q8zn%'`  
    may **miss nearby locations**.

Boundary Issue 2: Shared Prefix but in Different Cells

- **Description**: Two points may share a long geohash prefix but still fall into **adjacent, not identical** geohash cells.
- **Impact**: A query using a single geohash cell will **exclude valid nearby points** just across the boundary.
- **Solution**: Always query the **central geohash and its 8 neighbors** to cover edge cases.

✅ **Not enough businesses**

Now let's tackle the bonus question. What should we do if there are not enough businesses
returned from the current grid and all the neighbors combined?

**Option 1**: only return businesses within the radius. This option is easy to implement, but
the drawback is obvious. It doesn't return enough results to satisfy a user's needs.

**Option 2**: increase the search radius. We can remove the last digit of the geohash and
use the new geohash to fetch nearby businesses. If there are not enough businesses,
we continue to expand the scope by removing another digit. This way, the grid size is
gradually expanded until the result is greater than the desired number of

### Quadtree

Another popular solution is quadtree. A quad tree is a data structure that is commonly
used to partition a two-dimensional space by recursively subdividing it into four
quadrants (grids) until the contents of the grids meet certain criteria. For example, the,
criterion can be to keep subdividing until the number of businesses in the grid is not more
than 100. This number is arbitrary as the actual number can be determined by business
needs. With a quadtree, we build an in-memory tree structure to answer queries. Note
that quadtree is an in-memory data structure and it is not a database solution.

#### When are Quadtrees used for?

Quadtrees are used for scaling an internet service to handle thousands of requests every second. This requires an excellent caching strategy. When geolocation is the main parameter of those requests, conventional caching techniques and procedures fall through. Quadtrees are used to understand high cache hit ratios, while also keeping the responses relevant, even in intense areas.

##### Dynamic grids

While **10km** cells are overlarge to use in urban areas, there are many less dense areas outside of cities where two locations a couple of kilometres apart would have an equivalent list of **20** nearby Xone businesses. Ideally, we’d be able to use large, imprecise cells in sparse areas and smaller, more granular cells in dense areas.

Often a _quadtree is represented as a grid_, with each square representing a node within the tree. The subsequent image visualizes the method of subdividing nodes during a quadtree, starting with a uni-root node and finishing with a tree of depth:

Quadtree as a Grid

We use quadtrees to keep a balance among the precision and validity of results. But let’s say we wish to create a tree in which each leaf node is exact enough to search for a specific location within the node in the same group.

We can do this by developing a tree that begins with one node that is shown to the entire world and subsequently divides until every node satisfies this eligibility. The best case is when a [tree](https://how.dev/answers/what-is-a-tree) that is precise has few nodes. This can attenuate the number of entries in the cache.

The following image shows the finished quadtree near Denver, CO to represent how quadtree depth changes with population intensity:

![Colorado, USA](https://how.dev/cdn-cgi/image/format=auto,width=3000,quality=75/api/edpresso/shot/5839198214422528/image/6753039278407680 "Colorado, USA")

Colorado, USA

A quadtree is a data structure that is commonly used to partition a two-dimensional space by recursively subdividing it into four quadrants (grids) until the contents of the grids meet certain criteria (see the first diagram). 

![](https://substackcdn.com/image/fetch/w_1456,c_limit,f_auto,q_auto:good,fl_progressive:steep/https%3A%2F%2Fbucketeer-e05bbc84-baa3-437e-9518-adb32be77984.s3.amazonaws.com%2Fpublic%2Fimages%2F4fafbfe9-be63-42d2-95d2-2241fb185b49_1959x2502.jpeg)

A quadtree is an **in-memory data structure** and it is not a database solution. It runs on each LBS (Location-Based Service, see last week’s post) server, and the data structure is built at server start-up time.

**How to get nearby businesses with quadtree?**  
- Build the quadtree in memory. 
- After the quadtree is built, start searching from the root and traverse the tree, until we find the leaf node where the search origin is. 
- If that leaf node has 100 businesses, return the node. Otherwise, add businesses from its neighbors until enough businesses are returned.

✅ **Operational Considerations for Quadtree**
1. **⏱️ Long Startup Time**
    - Building a quadtree for ~200 million businesses can take **several minutes**.
    - During this time, the server **cannot serve traffic**, risking service unavailability.
2. **🚀 Incremental Rollout**
    - To avoid widespread downtime:
        - Deploy updates to a **small subset of servers** at a time.
        - Use **blue/green deployment**, but watch out—loading large data on all new servers at once can **overload the database**.
3. **📆 Updating Data Over Time**
    - As businesses are **added/removed**, quadtree updates are needed.
    
    Two Approaches to update Quadtree:
    - **(a) Incremental Rebuilds (Preferred)**:
        - Rebuild the tree **gradually across servers**.
        - Some servers may temporarily serve **stale data**—usually acceptable.
        - Staleness can be managed by a policy where updates **only go live the next day**, and **nightly cache jobs** refresh the tree.
        - ⚠️ Downside: Large batch updates can **invalidate many cache keys**, stressing the cache layer.
    - **(b) Live Updates (Complex)**:
        - Dynamically modify the quadtree as data changes.
        - Requires **locking and thread-safety**, adding **significant complexity** to the implementation.


### Google S2

Google S2 geometry library is another big player in this field. Similar to Quadtree,
it is an in-memory solution. It maps a sphere to a 1D index based on the Hilbert curve
(a space-filling curve) . The Hilbert curve has a very important property: two points
that are close to each other on the Hilbert curve are close in 1D space (Figure 1.16). Search
on 1D space is much more efficient than on 2D. Interested readers can play with an online
tool for the Hilbert curve.

S2 is a complicated library and you are not expected to explain its internals during an
interview. But because it's widely used in companies such as Google, Tinder, etc., we
will briefly cover its advantages.

• S2 is great for geofencing because it can cover arbitrary areas with varying levels
(Figure 1.17). According to Wikipedia, "A geofence is a virtual perimeter for a real world
geographic area. A geo-fence could be dynamically generated- as in a radius
around a point location, or a geo-fence can be a predefined set of boundaries (such
as school zones or neighborhood boundaries)"
Geofencing allows us to define perimeters that surround the areas of interest and to
send notifications to users who are out of the areas. This can provide richer functionalities
than just returning nearby businesses.

• Another advantage of S2 is its Region Cover algorithm. Instead of having a fixed
level (precision) as in geohash, we can specify min level, max level, and max cells in
S2. The result returned by S2 is more granular because the cell sizes are flexible. If
you want to learn more, take a look at the S2 tool.


### Geohash vs quadtree

Before we conclude this section, let's do a quick comparison between geohash and
quadtree.

#### Geohash
• Easy to use and implement. No need to build a tree.
• Supports returning businesses within a specified radius.
• When the precision (level) of geohash is fixed, the size of the grid is fixed as well It
cannot dynamically adjust the grid size, based on population density. More complex
logic is needed to support this.
• Updating the index is easy.

#### Quadtree
• Slightly harder to implement because it needs to build the tree.
• Supports fetching k-nearest businesses. Sometimes we just want to return k-nearest
businesses and don't care if businesses are within a specified radius. For example,
when you are traveling and your car is low on gas, you just want to find the nearest
k gas stations. These gas stations may not be near you, but the app needs to return
the nearest k results. For this type of query, a quadtree is a good fit because its
subdividing process is based on the number k and it can automatically adjust the
query range until it returns k results.
• It can dynamically adjust the grid size based on population density (see the Denver
example in Figure 1.15).
• Updating the index is more complicated than geohash. A quad tree is a tree structure.
If a business is removed, we need to traverse from the root to the leaf node, to remove
the business. For example, if we want to remove the business with ID = 2, we
have to travel from the root all the way down to the leaf node, as shown in Figure
1.19. Updating the index takes O(log n), but the implementation is complicated if the
data structure is accessed by a multi-threaded program, as locking is required. Also,
rebalancing the tree can be complicated. Rebalancing is necessary if, for example,
a leaf node