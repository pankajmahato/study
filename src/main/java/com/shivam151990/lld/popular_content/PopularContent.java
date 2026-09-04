package com.shivam151990.lld.popular_content;

import java.util.*;

public class PopularContent {
    private final Map<Integer, Integer> popularityMap;   // contentId -> popularity
    private final TreeMap<Integer, Set<Integer>> popularityBuckets; // popularity -> set of contentIds

    public PopularContent() {
        this.popularityMap = new HashMap<>();
        this.popularityBuckets = new TreeMap<>();
    }

    // Increase popularity by 1
    public void increasePopularity(int contentId) {
        updatePopularity(contentId, 1);
    }

    // Decrease popularity by 1 (not below 0)
    public void decreasePopularity(int contentId) {
        updatePopularity(contentId, -1);
    }

    // Helper to update popularity in both maps
    private void updatePopularity(int contentId, int delta) {
        int oldPopularity = popularityMap.getOrDefault(contentId, 0);
        int newPopularity = Math.max(0, oldPopularity + delta);

        // Remove from old bucket
        if (oldPopularity > 0) {
            Set<Integer> oldSet = popularityBuckets.get(oldPopularity);
            oldSet.remove(contentId);
            if (oldSet.isEmpty()) {
                popularityBuckets.remove(oldPopularity);
            }
        }

        // Update map
        popularityMap.put(contentId, newPopularity);

        // Add to new bucket if > 0
        if (newPopularity > 0) {
            popularityBuckets.computeIfAbsent(newPopularity, k -> new HashSet<>()).add(contentId);
        }
    }

    // Return most popular contentId or -1 if none
    public int getMostPopular() {
        if (popularityBuckets.isEmpty()) return -1;
        Map.Entry<Integer, Set<Integer>> entry = popularityBuckets.lastEntry(); // max popularity
        return entry.getValue().iterator().next(); // return any contentId with that popularity
    }

    // For debugging
    public void printState() {
        System.out.println("PopularityMap: " + popularityMap);
        System.out.println("PopularityBuckets: " + popularityBuckets);
    }

    public static void main(String[] args) {
        PopularContent pc = new PopularContent();

        pc.increasePopularity(1); // content 1 -> popularity 1
        pc.increasePopularity(2); // content 2 -> popularity 1
        pc.increasePopularity(1); // content 1 -> popularity 2
        pc.increasePopularity(3); // content 3 -> popularity 1
        pc.decreasePopularity(2); // content 2 -> popularity 0

        System.out.println("Most Popular: " + pc.getMostPopular()); // should return 1
        pc.printState();
    }
}

