package com.shivam151990;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Solution {
    public int spanningTree(int V, int[][] edges) {
        boolean[] vis = new boolean[V];
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{0, 0});
        List<List<int[]>> adjList = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adjList.add(new ArrayList<>());
        }

        for (int[] ed : edges) {
            int u = ed[0];
            int v = ed[1];
            int wt = ed[2];
            adjList.get(u).add(new int[]{v, wt});
            adjList.get(v).add(new int[]{u, wt});
        }
        int tot = 0;
        while (!pq.isEmpty()) {
            int curNode = pq.peek()[0];
            int curWt = pq.peek()[1];
            pq.poll();

            vis[curNode] = true;
            tot += curWt;

            for (int[] adj : adjList.get(curNode)) {
                int adjNode = adj[0];
                int adjWt = adj[1];

                if (!vis[adjNode]) {
                    pq.offer(new int[]{adjNode, adjWt});
                }
            }
        }
        return tot;
    }
}