package tema2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import tema1.CircularQueue;

/**
 *
 * @author juanluis
 */
public class CircularQueue_amb_monitors<E> extends CircularQueue<E> {

    Lock l;
    Condition cond_plena, cond_buida;

    public CircularQueue_amb_monitors(int cp) {
        super(cp);
        l = new ReentrantLock();
        cond_plena = l.newCondition();
        cond_buida = l.newCondition();
    }

    public void put(E value) {
        l.lock();
        try {
            while (full()) {
                cond_plena.awaitUninterruptibly();
            }
            super.put(value);
            cond_buida.signal();
        } finally {
            l.unlock();
        }
    }

    public E get() {
        E tmp;
        l.lock();
        try {
            while (empty()) {
                cond_buida.awaitUninterruptibly();
            }
            tmp = super.get();
            cond_plena.signal();
        } finally {
            l.unlock();
        }
        return tmp;
    }

}
