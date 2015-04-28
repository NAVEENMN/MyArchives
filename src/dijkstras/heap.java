package dijkstras;
import java.util.Arrays;
public class heap<T extends Comparable<T>> {
    private static final int limit = 10;
    private T[] list;
    private int len;
    
    
    @SuppressWarnings("unchecked")
	public heap () { 
        list = (T[])new Comparable[limit];  
        len = 0;
    }
    
    
    public void insert(T value) {
        // grow list if needed
        if (len >= list.length - 1) {
            list = this.resize();
        }        
        
        // place element into heap at bottom
        len++;
        int index = len;
        list[index] = value;
        
        heapup();
    }
    
    
    public boolean isEmpty() {
        return len == 0;
    }

    
    public T min() {
        if (this.isEmpty()) {
            throw new IllegalStateException();
        }
        
        return list[1];
    }

    
    public T remove() {
    	T result = min();
    	list[1] = list[len];
    	list[len] = null;
    	len--;
    	heapdown();
    	return result;
    }
    
    
    public String toString() {
        return Arrays.toString(list);
    }

    
    private void heapdown() {
        int index = 1;
        
        // bubble down
        while (hasLeftChild(index)) {
            // which of my children is smaller?
            int smallerChild = leftIndex(index);
            
            // bubble with the smaller child, if I have a smaller child
            if (hasRightChild(index)
                && list[leftIndex(index)].compareTo(list[rightIndex(index)]) > 0) {
                smallerChild = rightIndex(index);
            } 
            
            if (list[index].compareTo(list[smallerChild]) > 0) {
                swap(index, smallerChild);
            } else {
                // otherwise, get outta here!
                break;
            }
            
            // make sure to update loop counter/index of where last el is put
            index = smallerChild;
        }        
    }
    
    
    private void heapup() {
        int index = this.len;
        
        while (hasParent(index)
                && (get_parent(index).compareTo(list[index]) > 0)) {
            // get_parent/child are out of order; swap them
            swap(index, parentIndex(index));
            index = parentIndex(index);
        }        
    }
    
    
    private boolean hasParent(int i) {
        return i > 1;
    }
    
    
    private int leftIndex(int i) {
        return i * 2;
    }
    
    
    private int rightIndex(int i) {
        return i * 2 + 1;
    }
    
    
    private boolean hasLeftChild(int i) {
        return leftIndex(i) <= len;
    }
    
    
    private boolean hasRightChild(int i) {
        return rightIndex(i) <= len;
    }
    
    
    private T get_parent(int i) {
        return list[parentIndex(i)];
    }
    
    
    private int parentIndex(int i) {
        return i / 2;
    }
    
    
    private T[] resize() {
        return Arrays.copyOf(list, list.length * 2);
    }
    
    
    private void swap(int index1, int index2) {
        T tmp = list[index1];
        list[index1] = list[index2];
        list[index2] = tmp;        
    }
}