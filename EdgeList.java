
import java.util.Iterator;
import java.util.NoSuchElementException;

public class EdgeList implements Iterable{
    private Node first;    // beginning of linked list
    private int n;         // number of elements in list
    
    // helper node class
    private static class Node {
        private Edge item;
        private Node next;
    }
    
    // initializes an empty list.
    public EdgeList() {
        first = null;
        n = 0;
    }
    
    public boolean isEmpty() {
        return first == null;
    }

    public int size() {
        return n;
    }
    
    // Add an edge to this list.
    public void add(Edge item) {
        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.next = oldfirst;
        n++;
    }
    
    // Remove an edge from the list
    public boolean remove(Edge item) {
        // search for the node
        Node current = first;
        while(current!=null){
            // copy the first to the current and remove the first (since the order is not important)
            if(item.equals(current.item)){
                current.item=first.item;
                first=first.next;
                n--;
                return true;
            }
            current = current.next;
        }
        return false;
    }
    
    // make EdgeList iteratable
    public Iterator iterator() {
        return new ListIterator(first);
    }
    
    private class ListIterator implements Iterator {
        private Node current;
                   
        public ListIterator(Node first) {
            current = first;
        }
                   
        public boolean hasNext()  { return current != null;                     }
        public void remove()      { throw new UnsupportedOperationException();  }
                   
        public Edge next() {
            if (!hasNext()) throw new NoSuchElementException();
            Edge item = current.item;
            current = current.next;
            return item;
        }
    }
}

