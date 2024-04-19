package flux.finestra0.stopandwait;

import java.util.concurrent.TimeUnit;
import ast.util.Timer;
import xarxa.Comms;
import xarxa.Segment;
import xarxa.TSocket;
import xarxa.Xarxa;

public class TSocketEnviarFluxFinestra0StopandWait extends TSocket {

    protected int seguentEnviar, seguentASerReconegut;
    protected int finestraRecepcio;
    protected int finestraPermesa; // Valors possibles {0,1}
    protected Segment segmentTimeout;

    protected Timer timer;
    protected Timer.Task tascaTimer;

    public TSocketEnviarFluxFinestra0StopandWait(Xarxa x) {
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


            
            
//            System.out.println("reenviat : " + segmentTimeout);
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
