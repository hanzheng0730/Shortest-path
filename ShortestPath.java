/*************************************************************************
 *  Description: compute shortest path                                   *
 *               option 0 - distance - Dijkstra's algorithm              *
 *               option 1 - price    - Dijkstra's algorithm              *
 *               option 2 - hops     - BFS algorithm                     *
 *                                                                       *
 *************************************************************************/

import java.util.LinkedList;
import java.util.Deque;

public class ShortestPath {
    private Edge[] edgeTo;        // edgeTo[v] = shortest edge from tree vertex to non-tree vertex
    private double[] distTo;      // distTo[v] = weight of shortest such edge
    private boolean[] marked;     // marked[v] = true if v on tree, false otherwise
    private IndexMinPQ pq;
    private int option;
    private RouteGraph G;
    
    /* constructor */
    public ShortestPath(RouteGraph G, int option){
        this.G = G;
        this.option = option;
        edgeTo = new Edge[G.V()];
        distTo = new double[G.V()];
        marked = new boolean[G.V()];
        for (int v = 0; v < G.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;
    }

    /* run Dijkstra's algorithm to find the shortest path by distance or price */
    public void dijkstraSP(int s){
        pq = new IndexMinPQ(G.V());        // a priority queue of vertices
        distTo[s] = 0;
        pq.insert(s, distTo[s]);           // add s to the PQ
        while(!pq.isEmpty()){
            int v = pq.delMin();           // find and remove the min vertex
            for (Edge e : G.adj(v))        // scan all adj edges
                relax(e, v);
        }
    }
    
    // relax edge e and update pq if changed
    private void relax(Edge e, int v) {
        int w = e.other(v);
        if (distTo[w] > distTo[v] + e.weight(option)) {
            distTo[w] = distTo[v] + e.weight(option);
            edgeTo[w] = e;
            if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
    }

    /* run BFS algorithm to find the shortest path by number of hops */
    public void bfsSP(int s){
        LinkedList<Integer> q = new LinkedList<Integer>(); // a queue of vertex
        distTo[s] = 0;
        marked[s] = true;
        q.add(s);
	
        while(!q.isEmpty()){
            int v = q.poll(); // retrieve and remove the head
            for(Edge e : G.adj(v)){        // scan all adjacent edges
                int w = e.other(v);
                if(!marked[w]){
                    edgeTo[w]=e;           // find a shortest edge
                    distTo[w]=distTo[v]+1; // increase hops number by 1
                    marked[w]=true;
                    q.add(w);
                }
            }
        }
    }

    public double distTo(int v) {
        return distTo[v];
    }

    public boolean hasPathTo(int v) {
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    // return the shortest path as a stack of edges
    public Iterable<Edge> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        Deque<Edge> path = new LinkedList<Edge>();
        Edge e = edgeTo[v];
        int from = e.other(v);
        path.push(e);
        while((e=edgeTo[from]) != null){
            path.push(e);
            from = e.other(from);
        }
        return path;
    }
}
