package OneLaneBridge;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BridgeAmbJusticia {
    protected boolean sentitActual;
    protected int cotxesSentitContrariEsperant, cotxesSentitActualEsperant;
    protected int cotxesEnTransit;
    protected Lock mon;
    protected Condition sentitContrariPotPassar, sentitActualPotPassar;

    public BridgeAmbJusticia() {
        mon = new ReentrantLock();
        sentitContrariPotPassar = mon.newCondition();
        sentitActualPotPassar = mon.newCondition();
    }

    public void entrar(boolean sentitMeu) {
        try {
            mon.lock();
            if (cotxesEnTransit == 0 && cotxesSentitContrariEsperant == 0) {
                sentitActual = sentitMeu;
            }
            while (cotxesSentitContrariEsperant > 0) {
                cotxesSentitActualEsperant++;
                sentitActualPotPassar.awaitUninterruptibly();
                cotxesSentitActualEsperant--;
            }

            while (sentitActual != sentitMeu) {
                cotxesSentitContrariEsperant++;
                sentitContrariPotPassar.awaitUninterruptibly();
                cotxesSentitContrariEsperant--;
                if (cotxesSentitContrariEsperant == 0) {
                    sentitActualPotPassar.signalAll();
                }
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
            if (cotxesEnTransit == 0 && cotxesSentitContrariEsperant > 0) {
                sentitActual = !sentitActual;
                sentitContrariPotPassar.signalAll();
            }
            System.out.println("SURT sentit " + sentitMeu);
        } finally {
            mon.unlock();
        }
    }
}
