package P2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GestorPool {

    private final int NR = 5;
    private int recursosDemanats, tipus0Esperant, tipus1Esperant;
    private boolean esperant;
    private Lock lock;
    private Condition potDemanar, tipus0, tipus1;
    private Map<Integer, Integer> recursPerTipus= new HashMap<>();

    public GestorPool() {
        lock = new ReentrantLock();
        potDemanar = lock.newCondition();
        tipus0 = lock.newCondition();
        tipus1 = lock.newCondition();
        recursPerTipus.put(0, 2);
        recursPerTipus.put(1, 3);
        recursPerTipus.put(2, 5);
    }


    public void demana(int tipus) {
        lock.lock();
        try {
            recursosDemanats += recursPerTipus.get(tipus);
            System.out.println("demana ("+tipus+") ----> ("+recursPerTipus.get(tipus)+" recursos)");
            
            while (esperant) {
                potDemanar.awaitUninterruptibly();
            }

            if (recursosDemanats < NR) {
                switch (tipus) {
                    case 0:
                        tipus0Esperant++;
                        tipus0.awaitUninterruptibly();
                        tipus0Esperant--;
                        break;
                    case 1:
                        tipus1Esperant++;
                        tipus1.awaitUninterruptibly();
                        tipus1Esperant--;
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            } else {
                switch (tipus) {
                    case 0:
                        if (tipus1Esperant > 0) tipus1.signal();
                        else tipus0.awaitUninterruptibly();
                        break;
                    case 1:
                        if (tipus0Esperant > 0) tipus0.signal();
                        else tipus1.awaitUninterruptibly();
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            }
            System.out.println("assignats -------> ** "+recursPerTipus.get(tipus)+" recursos");
        } finally {
            lock.unlock();
        }
    }

    public void allibera(int tipus) {
        lock.lock();
        try {
            recursosDemanats -= NR;
        } finally {
            lock.unlock();
        }    
    }

}
