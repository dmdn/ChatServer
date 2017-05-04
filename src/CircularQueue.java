import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;


//Decorator for Queue, which when you add an extra element will pull the first out of the queue.
public class CircularQueue<E> implements Queue<E> {
    private final Queue<E> queue;
    private final int limit;

    public CircularQueue(Queue<E> queue, int limit) {
        if (queue.size() > limit) {
            throw new IllegalArgumentException("Limit is already exceeded");
        }
        this.queue = queue;
        this.limit = limit;
    }

    public boolean offer(E e) {
        if (queue.size() == limit) {
            queue.poll();
        }

        return queue.offer(e);
    }

    public boolean add(E e) {
        return this.offer(e);
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public boolean contains(Object o) {
        return queue.contains(o);
    }

    public Iterator<E> iterator() {
        return queue.iterator();
    }

    public E remove() {
        return queue.remove();
    }

    public Object[] toArray() {
        return queue.toArray();
    }

    public E poll() {
        return queue.poll();
    }

    public E element() {
        return queue.element();
    }

    public E peek() {
        return queue.peek();
    }

    public <T> T[] toArray(T[] a) {
        return queue.toArray(a);
    }

    //Returns the element from the beginning of the queue with deletion.
    //If the queue is empty, throws a NoSuchElementException
    public boolean remove(Object o) {
        return queue.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return queue.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        return queue.addAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return queue.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return queue.retainAll(c);
    }

    public void clear() {
        queue.clear();
    }

    public boolean equals(Object o) {
        return queue.equals(o);
    }

    public int hashCode() {
        return queue.hashCode();
    }
}