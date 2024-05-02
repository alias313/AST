package OneLaneBridge;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BridgeSenseJusticia {
    protected boolean sentitActual;
    //protected int cotxesSentitContrariEsperant, cotxesSentitActualEsperant;
    protected int cotxesEnTransit, cotxesEsperant;
    protected Lock mon;
    //protected Condition sentitContrariPotPassar, sentitActualPotPassar;
    protected Condition cotxePotPassar;

    public BridgeSenseJusticia() {
        mon = new ReentrantLock();
        cotxePotPassar = mon.newCondition();
    }

    public void entrar(boolean sentitMeu) {
        try {
            mon.lock();
            if (cotxesEnTransit == 0 && cotxesEsperant == 0) {
                sentitActual = sentitMeu;
            }
            while (sentitActual != sentitMeu) {
                cotxesEsperant++;
                cotxePotPassar.awaitUninterruptibly();
                cotxesEsperant--;
            }
            cotxesEnTransit++;
            System.out.println("ENTRA sentit " + sentitMeu);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            mon.unlock();
        }
    }

    public void sortir(boolean sentitMeu) {
        try {
            mon.lock();
            cotxesEnTransit--;
            if (cotxesEnTransit == 0) {
                sentitActual = !sentitMeu;
                cotxePotPassar.signalAll();
            }
            System.out.println("SURT sentit " + sentitMeu);
        } finally {
            mon.unlock();
        }
    }
}
