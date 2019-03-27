
import java.io.*;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.LinkedList;

public class RouteGraph {
    private int V;                                // number of verticies
    private int E;                                // number of edges
    private EdgeList [] adj;                      // adjacency list
    private String [] cities;                     // a list of city names
    private HashMap<String, Integer> cityIndex;   // a map from city name to city id
    private HashMap<String, Integer> edgeIndex;   // a map from existing edge key to 1
    
    /* a defualt constructor */
    public RouteGraph(){
    }
    
    /* construct a graph by reading a input file containing routes information */
    public void load(String filename){
        try {
            // set up input file stream
            FileInputStream fis =new FileInputStream(filename);
            BufferedReader in=new BufferedReader(new InputStreamReader(fis));
            
            // read vertices number
            V = Integer.parseInt(in.readLine());
            if(V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
            
            // initialization
            E = 0;
            adj =  new EdgeList[V];
            cities = new String[V];
            cityIndex = new HashMap<String, Integer>(V);
            edgeIndex = new HashMap<String, Integer>(V*4); // assume the graph is sparse
    
            // read city names
            for(int v = 0; v < V; v++){
                String c = in.readLine();
                cities[v] = c;
                cityIndex.put(c,v);
                adj[v] = new EdgeList();
            }
            
            // read routes information
            String line;
            while((line=in.readLine())!=null){
                StringTokenizer st=new StringTokenizer(line, " ,\t");
                int v = Integer.parseInt(st.nextToken())-1;           // source city index
                int w = Integer.parseInt(st.nextToken())-1;           // destination city index
                double distance = Double.parseDouble(st.nextToken()); // distance
                double price = Double.parseDouble(st.nextToken());    // price
                Edge e =new Edge(v, w, distance, price);              // create a edge
                addEdge(e);                                           // add edge e to the graph
            }
            
            in.close();
            
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    /* add an edge to the graph */
    public boolean addEdge(Edge e){
        int v = e.v();
        int w = e.w();
        
        // do not add e if it is already exists
        if(edgeIndex.containsKey(e.key())){
            System.out.println("Action ignored: edge already exists!");
            return false;
        } else {
            edgeIndex.put(e.key(),1);
            validateVertex(v);
            validateVertex(w);
            adj[v].add(e);
            adj[w].add(e);
            E++;
            return true;
        }
    }

    /* list all direct routes (including the reversed duplicates that facilitate user's search) */
    public void list(){
        System.out.format("%-15s%15s\t%s\t\t%s\n","From","To","Miles","Price");
        
        // for each city, print all possible direct routes
        for(int v = 0; v < V; v++){
            for(Edge e : adj(v)){
                int w = e.other(v);
                System.out.format("%-15s%15s\t%.1f\t\t%.1f\n",cities[v],cities[w],e.distance(),e.price());
            }
        }
    }
    
    /* compute and print a minimal spaning tree */
    public void getMST(){
        // create a PrimMST object and compute the MST
        PrimMST mst = new PrimMST(this);
        
        // print edges in the MST
        for (Edge e : mst.edges())
            System.out.format("%-15s%15s\t%.2f\n",cities[e.v()],cities[e.w()],e.distance());
    }
    
    /* compute and print the shortest path based on distance */
    public void getDistanceSP(String src, String dest){
        // get city ids using city names
        int v = cityIndex.get(src).intValue();
        int w = cityIndex.get(dest).intValue();
	
        // create a ShortestPath object with weight-option 0 (distance)
        ShortestPath sp = new ShortestPath(this,0);
	
        // compute shortest path from vertex v using Dijkstra's algorithm
        sp.dijkstraSP(v);

        // print each edge of a shortest path in order
        if(sp.hasPathTo(w)){
            System.out.println("From "+src+" to "+dest+" (Total miles: "+sp.distTo(w)+")");
            int from = v;
            for(Edge e : sp.pathTo(w)){
                int to = e.other(from);
                System.out.println(cities[from]+"-->"+cities[to]+" ("+e.distance()+" mils)");
                from = to;
            }
            System.out.println();
        } else {
            System.out.println("No path found from "+src+" to "+dest);
        }
    }
    
    /* compute and print the shortest path based on price */
    public void getPriceSP(String src, String dest){
        // get city ids using city names
        int v = cityIndex.get(src).intValue();
        int w = cityIndex.get(dest).intValue();

        // create a ShortestPath object with weight-option 0 (distance)
        ShortestPath sp = new ShortestPath(this,1);
	
        // compute shortest path from vertex v using Dijkstra's algorithm
        sp.dijkstraSP(v);

        // print each edge of a shortest path in order
        if(sp.hasPathTo(w)){
            System.out.println("From "+src+" to "+dest+" (Total cost: $"+sp.distTo(w)+")");
            int from = v;
            for(Edge e : sp.pathTo(w)){
                int to = e.other(from);
                System.out.println(cities[from]+"-->"+cities[to]+" (cost $"+e.price()+")");
                from = to;
            }
            System.out.println();
        } else {
            System.out.println("No path found from "+src+" to "+dest);
        }


    }
    
    /* compute and print the shortest path based on number of hops */
    public void getHopsSP(String src, String dest){
        // get city ids using city names
        int v = cityIndex.get(src).intValue();
        int w = cityIndex.get(dest).intValue();

        // create a ShortestPath object with weight-option 2 (number of hops)
        ShortestPath sp = new ShortestPath(this,2);
	
        // compute shortest path from v using BFS search
        sp.bfsSP(v);

        // print each edge of a shortest path in order
        if (sp.hasPathTo(w)){
            System.out.println("From "+src+" to "+dest+" (Total number of hops: "+(int)sp.distTo(w)+")");
            int from = v;
            for (Edge e : sp.pathTo(w)){
                int to = e.other(from);
                System.out.println(cities[from]+"-->"+cities[to]);
                from = to;
            }
            System.out.println();
        } else {
            System.out.println("No path found from "+src+" to "+dest);
        }
    }
    
    /* compute and print all routes cost max or less using a recursive / backtrack / pruning algorithm */
    public void getAffordable(double max){
        // for each vertext v, find all affordable routes starting from v
        for(int v = 0; v < V; v++){
            LinkedList<Edge> path = new LinkedList<Edge>();  // current path
            getAffordable(v,v,0,max,path,new boolean[V]);    // recursive call, see below
        }
    }

    /* the current path started from vertext "origin" to vertext "current", with total cost = sum 
        all vertices in the current path are marked and can not be used to extend the current path */
    public void getAffordable(int origin, int current, double sum, double max, LinkedList<Edge> path,boolean [] marked) {
        // stop extending the current path when reach the max cost
        if(sum > max) return;
        
        // print the current path if it is not empty
        if(!path.isEmpty()){
            int from = origin; // trace from the origin vertex to the current vertex
            for(Edge e : path){
                int to = e.other(from);
                System.out.println(cities[from]+"-->"+cities[to]+" (cost $"+e.price()+")");
                from = to;
            }
            System.out.println("Cost: $"+sum); // print total cost of this path
            System.out.println();
        }
	
        // mark the current vertext to prevent its future use
        marked[current]=true;
	
        //  extend the current path by trying all adjacent edges
        for(Edge e : adj(current)){
            int next = e.other(current);
            if(!marked[next]){ // only consider un-visited vertices
                path.add(e);   // extend the current path by add edge e
                // update the total cost, and recursively find new paths starting with next
                getAffordable(origin,next,sum+e.price(),max,path,marked);
                path.removeLast(); // trace back
            }
        }
    } 
    
    /* add a new edge, return false if the edge already exists */
    public boolean add(String src, String dest, double distance, double price) {
        int v = cityIndex.get(src).intValue();
        int w = cityIndex.get(dest).intValue();
        Edge e =new Edge(v, w, distance, price);
        return addEdge(e);
    }
    
    /* remove an existing edge, return false if the edge does not exists  */
    public boolean remove(String src, String dest) {
        int v = cityIndex.get(src).intValue();
        int w = cityIndex.get(dest).intValue();
        Edge e = new Edge(v,w,0,0);  // create a dummy edge
        boolean done = adj[w].remove(e) && adj[v].remove(e);
        if(done) edgeIndex.remove(e.key()); // update the edge index
        return done;
    }
    
    /* output the current routes information to a file */
    public void write(String filename) {
        try{
            // set up output file stream
            FileOutputStream outfile=new FileOutputStream(filename);
            PrintWriter out=new PrintWriter(outfile);

            // print cities
            out.println(V);
            for (int v = 0; v < V; v++)
                out.println(cities[v]);
            
            // for each city, print all direct routes from it (avoid duplicates)
            for(int v = 0; v < V; v++){
                for(Edge e : adj(v))
                    if(e.w() > v) // avoid duplicates (since v is always less than w)
                        out.println(e);
            }
            out.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    // getter
    public int V() {
        return V;
    }
    
    public int E() {
        return E;
    }
    
    public Iterable<Edge> adj(int v) {
        validateVertex(v);
        return adj[v];
    }
    
    // check if a vertex is valid
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
    }
    
    // check if a city exists
    public boolean hasCity(String city) {
        return cityIndex.containsKey(city);
    }
}

