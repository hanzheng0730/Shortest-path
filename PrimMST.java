
import java.util.LinkedList;

public class PrimMST {
    private Edge[] edgeTo;        // edgeTo[v] = shortest edge from tree vertex to non-tree vertex
    private double[] distTo;      // distTo[v] = weight of shortest such edge
    private boolean[] marked;     // marked[v] = true if v on tree, false otherwise
    private IndexMinPQ pq;

    /* start constructing the MST */
    public PrimMST(RouteGraph G) {
        edgeTo = new Edge[G.V()];
        distTo = new double[G.V()];
        marked = new boolean[G.V()];
        pq = new IndexMinPQ(G.V());
        
        for (int v = 0; v < G.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;

        // run from each vertex to find minimum spanning forest
        for (int v = 0; v < G.V(); v++)
            if (!marked[v]) prim(G, v);
    }

    /* Prim's algorithm */
    private void prim(RouteGraph G, int s) {
        distTo[s] = 0.0;
        pq.insert(s, distTo[s]);    // insert s into the PQ
        while (!pq.isEmpty()) {
            int v = pq.delMin();    // find vertext v with the min distTo
            scan(G, v);             // scan vertex v
        }
    }

    /* check all v's neighbors to look for edges to add to tree next */
    private void scan(RouteGraph G, int v) {
        marked[v] = true;
        for (Edge e : G.adj(v)) {
            int w = e.other(v);
            if (marked[w]) continue;         // v-w is obsolete edge
            if (e.distance() < distTo[w]) {
                distTo[w] = e.distance();    
                edgeTo[w] = e;
                if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
                else                pq.insert(w, distTo[w]);
            }
        }
    }

    /* return all edges in the MST as an iterator */
    public Iterable<Edge> edges() {
        LinkedList<Edge> mst = new LinkedList<Edge>();
        for (int v = 0; v < edgeTo.length; v++) {
            Edge e = edgeTo[v];
            if (e != null) {
                mst.add(e);
            }
        }
        return mst;
    }

    /* compute the total weight for testing */
    public double weight() {
        double weight = 0.0;
        for (Edge e : edges())
            weight += e.distance();
        return weight;
    }
}
