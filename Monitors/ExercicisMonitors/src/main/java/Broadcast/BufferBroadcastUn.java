package Broadcast;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author ele
 */
public class BufferBroadcastUn {
    protected Object espai;
    protected boolean[] disponible;
    // disponible[i] indica que el i-essim consumidor
    // ha consumit l'objecte actual en espai si es true
    protected Lock mon;
    protected Condition potConsumir, potProduir;
    protected boolean consumintProduccio;
    protected int vegadesConsumit, capacitat;
    
    BufferBroadcastUn(int N) {
        mon = new ReentrantLock();
        potConsumir = mon.newCondition();
        potProduir = mon.newCondition();
        capacitat = N; // numero de procesos consumidors
        disponible = new boolean[N];
    }

    public void putValue(Object value) {
        try {
            mon.lock();
            while (consumintProduccio) {
                potProduir.awaitUninterruptibly();
            }
            espai = value;
            consumintProduccio = true;
            potConsumir.signalAll();
        } catch (Exception ex) { 
            System.out.println(ex); 
        } finally {
            mon.unlock();
        }
    }
    public Object getValue(int id) {
        try {
            mon.lock();
            while (disponible[id] || !consumintProduccio) {
                potConsumir.awaitUninterruptibly();
            }
            disponible[id] = true;
            vegadesConsumit++;
            if (vegadesConsumit == capacitat) {
                consumintProduccio = false;
                potProduir.signal();
                disponible = new boolean[capacitat];
            }
            System.out.println(espai);
            return espai;
        } catch (Exception ex) { 
            System.out.println(ex);
            return null;
        } finally {
            mon.unlock();
        }
    }
}
