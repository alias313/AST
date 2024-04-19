package flux.finestra0.stopandwait.arq;

import java.util.concurrent.TimeUnit;
import ast.util.Timer;
import xarxa.Comms;
import xarxa.Segment;
import xarxa.TSocket;
import xarxa.Xarxa;

public class TSocketEnviarControlFluxStopAndWaitARQ extends TSocket {

    protected int seguentEnviar, seguentASerReconegut;
    protected int finestraRecepcio, finestraPermesa;
    protected Segment segTimeout;

    protected Timer timer;
    protected Timer.Task tascaTimer;

    public TSocketEnviarControlFluxStopAndWaitARQ(Xarxa x) {
        super(x);
        finestraRecepcio = Comms.MIDA_CUA_RECEPCIO;
        finestraPermesa = 1;
        timer = new Timer();
    }

    @Override
    public void enviar(Object c) {
        try {
            mon.lock();
            throw new RuntimeException("Part a completar");
            
            
            
            
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mon.unlock();
        }
    }

    @Override
    public void processarMissatge(Segment segment) {
        mon.lock();
        try {
            // Simula perdua de d'ack
            double llindar = Math.random();
            if (llindar > 0.8) {
                System.out.println("ack perdut : " + segment);
                return;
            }

            throw new RuntimeException("Part a completar");
            
            
            
            
            
            
            
            
            
            
            
        } finally {
            mon.unlock();
        }
    }

    /**
     * Timer expira
     */
    protected void timeout() {
        mon.lock();
        try {
            throw new RuntimeException("Part a completar");


            
            
//            System.out.println("reenviat : " + segTimeout);
        } finally {
            mon.unlock();
        }
    }

    protected void startRTO() {
        if (tascaTimer != null) {
            tascaTimer.cancel();
        }
        tascaTimer = timer.startAfter(
                new Runnable() {
                    @Override
                    public void run() {
                        timeout();
                    }
                },
                Comms.RTO, TimeUnit.MILLISECONDS);
    }

    protected void stopRTO() {
        if (tascaTimer != null) {
            tascaTimer.cancel();
        }
        tascaTimer = null;
    }

}
