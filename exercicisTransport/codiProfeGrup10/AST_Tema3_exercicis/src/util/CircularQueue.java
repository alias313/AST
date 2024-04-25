package util;

import java.util.Iterator;

public class CircularQueue<E> implements Queue<E> {

    private final E[] queue;
    private final int N;
    private int numElem, head, tail;

    public CircularQueue(int N) {
        this.N = N;
        queue = (E[]) (new Object[N]);
    }

    @Override
    public int size() {
        return numElem;
    }

    @Override
    public int free() {
        return N - numElem;
    }

    @Override
    public boolean hasFree(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n>=0!");
        }
        return n <= free();
    }

    @Override
    public boolean empty() {
        return numElem == 0;
    }

    @Override
    public boolean full() {
        return numElem == N;
    }

    @Override
    public E peekFirst() {
        if (numElem == 0) {
            return null;
        }
        return queue[head];
    }

    @Override
    public E peekLast() {
        if (numElem == 0) {
            return null;
        }
        return queue[(head + numElem - 1) % N];
    }

    @Override
    public E get() {
        if (numElem == 0) {
            throw new IllegalStateException("Empty queue.");
        }
        E e = queue[head];
        head = (head + 1) % N;
        numElem = numElem - 1;
        return e;
    }

    @Override
    public void put(E e) {
        if (numElem == N) {
            throw new IllegalStateException("Full queue.");
        }
        queue[tail] = e;
        tail = (tail + 1) % N;
        numElem = numElem + 1;
    }

    public int get(E[] e, int offset, int length) {
        int i = 0;
        for (; i < length && numElem > 0; i++) {
            e[offset + i] = get();
        }
        return i;
    }

    public int put(E[] e, int offset, int length) {
        int i = 0;
        for (; i < length && numElem < N; i++) {
            put(e[offset + i]);
        }
        return i;
    }

    @Override
    public String toString() {
        if (numElem == 0) {
            return "[]";
        }
        String str = "[";
        for (int i = 0; i < numElem - 1; i++) {
            str = str + queue[(head + i) % N] + ",";
        }
        str = str + queue[(head + numElem - 1) % N] + "]";
        return str;
    }
    
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator {

        int index = 0;

        @Override
        public boolean hasNext() {
            return index < numElem;
        }

        @Override
        public E next() {
          if(index==numElem){
            throw new IllegalStateException("no next element");
          }
          E tmp = queue[(head+index) % N];
          index++;
          return tmp;
        }

        @Override
        public void remove() {
          if(index<0){
            throw new IllegalStateException("no remove possible");
          }
          index--;
          for (int i = index; i < numElem; i++) {
            queue[(head+i) % N] = queue[(head+1+i) % N];
          }
          tail = tail - 1;
          if (tail == -1 ){tail = N - 1;}
          numElem--;
        }

    }
}
