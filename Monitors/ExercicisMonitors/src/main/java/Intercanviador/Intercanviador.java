package Intercanviador;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Intercanviador {
    protected Lock mon;
    protected Condition sonDos, intercanviAcabat;
    protected int arribats;
    protected Object res1, res2, espai;

    public Intercanviador() {
        mon = new ReentrantLock();
        sonDos = mon.newCondition();
        intercanviAcabat = mon.newCondition();
    }

    public Object intercanvi(Object elem) {
        try {
            mon.lock();
            Object tmp = null;
            while (arribats == 2) {
                intercanviAcabat.awaitUninterruptibly();
            }
            System.out.println("ENTRA: " + elem);
            arribats++;
            if (arribats == 1) {
                espai = elem;
                sonDos.awaitUninterruptibly();
                tmp = espai;
                arribats = 0;
                intercanviAcabat.signal();
                intercanviAcabat.signal();
            } else {
                tmp = espai;
                espai = elem;
                sonDos.signal();
            }
            System.out.println("RETURNED: " + tmp);
            return tmp;
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        } finally {
            mon.unlock();
        }
    }
}
