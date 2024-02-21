package practica1.CircularQ;

import java.util.Iterator;
import util.Queue;

public class CircularQueue<E> implements Queue<E> {

    private final E[] queue;
    private final int N;
    //Completar...

    public CircularQueue(int N) {
        this.N = N;
        queue = (E[]) (new Object[N]);
    }

    @Override
    public int size() {
        throw new RuntimeException("//Completar...");
    }

    @Override
    public int free() {
        throw new RuntimeException("//Completar...");
    }

    @Override
    public boolean empty() {
        throw new RuntimeException("//Completar...");
    }

    @Override
    public boolean full() {
        throw new RuntimeException("//Completar...");
    }

    @Override
    public E peekFirst() {
        throw new RuntimeException("//Completar...");
    }

    @Override
    public E get() {
        throw new RuntimeException("//Completar...");
    }

    @Override
    public void put(E e) {
        throw new RuntimeException("//Completar...");
    }

    @Override
    public String toString() {
        throw new RuntimeException("//Completar...");
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator {

        //Completar...

        @Override
        public boolean hasNext() {
            throw new RuntimeException("//Completar...");
        }

        @Override
        public E next() {
            throw new RuntimeException("//Completar...");
        }

        @Override
        public void remove() {
            throw new RuntimeException("//Completar...");
        }

    }
}
