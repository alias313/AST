package practica1.CircularQ;

import java.util.Iterator;
import util.Queue;

public class CircularQueue<E> implements Queue<E> {

    private final E[] queue;
    private final int N;
    private int primer, ultim, numElems;

    public CircularQueue(int N) {
        this.N = N;
        queue = (E[]) (new Object[N]);
    }

    @Override
    public int size() {
        return numElems;
    }

    @Override
    public int free() {
        return N - numElems;
    }

    @Override
    public boolean empty() {
        if (numElems == 0) return true;
        return false;
    }

    @Override
    public boolean full() {
        if (ultim == primer && !empty()) return true;
        return false;
    }

    @Override
    public E peekFirst() {
        return queue[primer];
    }

    @Override
    public E get() {
        E resultat = queue[primer];
        queue[primer] = null;
        primer = ( primer + 1) % N;
        numElems--;
        return resultat;
    }

    @Override
    public void put(E e) {
        if (!full()) {
            queue[ultim] = e;
            ultim = (ultim + 1) % N;
            numElems++;
        }
    }

    @Override
    public String toString() {
        String queueString = new String();
        for (int i = 0; i < N; i++) {
            if (queue[i] != null) {
                queueString += queue[i].toString();
                queueString += ", ";
            }
            else {
                queueString += "null, ";
            }
        }
        
        return queueString;
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator {

        int currentIndex;

        @Override
        public boolean hasNext() {
            return !(currentIndex == ultim);
        }

        @Override
        public E next() {
            if (this.hasNext()) {
                E resultat = queue[currentIndex];
                currentIndex = (currentIndex + 1) % N;
                return resultat;
            }
            return null;
        }
        
        @Override
        public void remove() {
            int itemsLeft;
            currentIndex = (currentIndex - 1 + N) % N;
            
            if (currentIndex < ultim) {
                itemsLeft = ultim - currentIndex;
            } else {
                itemsLeft = numElems - currentIndex + ultim + 1;
            }

            for (int i = 0; i < itemsLeft-1; i++) {
                queue[(currentIndex+i)%N] = queue[(currentIndex+i+1)%N];
            }
            ultim = ( ultim - 1 + N ) % N;
            queue[ultim] = null;
            numElems--;
        }

    }
}
