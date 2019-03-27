
import java.util.NoSuchElementException;

public class IndexMinPQ{
    private int max;                           // the maximum number of items
    private int n;                             // current number of items on PQ
    private int[] pq;                          // min heap of vertices
    private int [] qp;                         // indirection map from vertics_id to PQ index
    private double [] keys;                    // distTo values of vertices

    /* Constructor */
    public IndexMinPQ(int max){
        if (max < 0) throw new IllegalArgumentException();
        this.max = max;
        n = 0;       
        keys = new double[max+1];
        pq = new int[max+1];
        qp = new int[max+1];
        for(int i=0;i<=max;i++) qp[i] = -1;
    }

    // insert a new item to PQ
    public void insert(int i, double key) {
        if (i < 0 || i >= max) throw new IndexOutOfBoundsException();
        if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
        n++;
        qp[i] = n;
        pq[n] = i;
        keys[i] = key;
        swim(n);
    }

    // delete the min item from the PQ
    public int delMin() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        int min = pq[1];
        exch(1, n--);
        sink(1);
        qp[min] = -1;
        keys[min] = 0;
        pq[n+1] = -1;
        return min;
    }

    // decrease the key
    public void decreaseKey(int i, double key) {
        if (i < 0 || i >= max) throw new IndexOutOfBoundsException();
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        if (keys[i]<= key)
            throw new IllegalArgumentException("Calling decreaseKey() with given argument would not strictly decrease the key");
        keys[i] = key;
        swim(qp[i]);
    }

    // helper functions
    public boolean isEmpty() {
        return n == 0;
    }

    public boolean contains(int i) {
        if (i < 0 || i >= max) throw new IndexOutOfBoundsException();
        return qp[i] != -1;
    }

    private boolean greater(int i, int j) {
        return keys[pq[i]] > (keys[pq[j]]);
    }

    private void exch(int i, int j) {
        int swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
        qp[pq[i]] = i;
        qp[pq[j]] = j;
    }

    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }
}
