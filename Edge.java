
public class Edge{
    private int v;           // vertex with smaller id
    private int w;           // vertex with bigger id
    private double distance; // distance weight
    private double price;    // price weight
    private String key;      // "v:w" is a key
    
    // constructor
    public Edge(int v, int w, double distance, double price){
        if (v < 0) throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer");
        if (w < 0) throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer");
        if (v == w) throw new IllegalArgumentException("No loop edge allowed");
        if (Double.isNaN(distance)) throw new IllegalArgumentException("Distance is NaN");
        if (Double.isNaN(price)) throw new IllegalArgumentException("Price is NaN");
        
        // enforce v is less than w in order to format printing
        if(v < w){
            this.v=v;
            this.w=w;
        } else {
            this.v=w;
            this.w=v;
        }
        
        this.distance = distance;
        this.price = price;
        this.key = v+":"+w;
    }
    
    // getters
    public int v() {
        return v;
    }
    
    public int w() {
        return w;
    }
    
    public double distance() {
        return distance;
    }
    
    public double price() {
        return price;
    }

    public double weight(int option) {
        double w = 0;
        if(option == 0) w = distance;
        if(option == 1) w = price;
        return w;
    }
    
    public String key() {
        return key;
    }
    
    public boolean equals(Edge e) {
        return this.v==e.v() && this.w==e.w() || this.v==e.w() && this.w==e.v();
    }
    
    public int other(int vertex) {
        if      (vertex == v) return w;
        else if (vertex == w) return v;
        else throw new IllegalArgumentException("Illegal endpoint");
    }
    
    // a string for displaying edge info
    public String toString() {
        return String.format("%d %d %.2f %.2f", v+1, w+1, distance, price);
    }
}

