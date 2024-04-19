package flux;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import xarxa.Comms;
import xarxa.Segment;
import xarxa.TSocket;
import xarxa.Xarxa;



public class TSocketEnviarControlFlux extends TSocket {

    protected int seguentEnviar, seguentASerReconegut, finestraRecepcio;
    protected Lock mon = new ReentrantLock();
    protected Condition potTransmitir = mon.newCondition();
    protected int numeroSequencia;

    public TSocketEnviarControlFlux(Xarxa x) {
        super(x);
        finestraRecepcio = Comms.MIDA_CUA_RECEPCIO;
    }

    @Override
    public void enviar(Object c) {
        try {
            mon.lock();
            //Per enviar segments: xarxa.enviar(seg);
            while (seguentEnviar - seguentASerReconegut < finestraRecepcio) {
                potTransmitir.awaitUninterruptibly();
            }
            
            //this.Segment(Comms.DADES, numeroSequencia,);

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
            
            

            
//            System.out.println("ACK rebut -> " + "finestra recepcio: "
//                    + finestraRecepcio);

        } finally {
            mon.unlock();
        }
    }
}
