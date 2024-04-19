package flux.finestra0;

import java.util.concurrent.TimeUnit;
import ast.util.Timer;
import xarxa.Comms;
import xarxa.Segment;
import xarxa.TSocket;
import xarxa.Xarxa;

public class TSocketEnviarControlFluxFinestra0 extends TSocket {

    protected int seguentEnviar, seguentReconeixer, finestraRecepcio;
    protected Segment segmentSondejar;

    protected Timer timer;
    protected Timer.Task tascaTimer;

    public TSocketEnviarControlFluxFinestra0(Xarxa x) {
        super(x);
        finestraRecepcio = Comms.MIDA_CUA_RECEPCIO;
        timer = new Timer();
    }

    @Override
    public void enviar(Object c) {
        try {
        mon.lock();

            while (((seguentEnviar - seguentReconeixer) >= finestraRecepcio)
                     && finestraRecepcio!=0) {
                appCV.await();
            }

            // si la finestra es zero, el segmentSondejaro que envio anira sent
            // descartat en recepcio, m'he d'esperar fins que sigui
            // reconegut i aixo passara quan :
            // seguentEnviar == seguentReconeixer
            //
            // sino
            //
            // Envio el segment, no cal que el guardi perque no hi ha perdues

            if(finestraRecepcio==0){
              throw new RuntimeException("Part a completar");






            }else{
              throw new RuntimeException("Part a completar");




            }
        } catch (Exception ex) { System.out.println(ex); }
        finally {
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
            assert (finestraRecepcio == 0 && segmentSondejar != null);
            throw new RuntimeException("Part a completar");
            
            

//            System.out.println("reenviat : " + segmentSondejar);
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
