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
    protected Lock mon;
    protected Condition potTransmitir;
    protected int numeroSequencia;
    // Si el missatge a transmitir es més gran que la finestra de recepcio missatgeCapFinestraRecepcio es false
    protected boolean missatgeCapFinestraRecepcio;

    public TSocketEnviarControlFlux(Xarxa x) {
        super(x);
        finestraRecepcio = Comms.MIDA_CUA_RECEPCIO;
        mon = new ReentrantLock();
        potTransmitir = mon.newCondition();
    }

    @Override
    public void enviar(Object c) {
        try {
            mon.lock();
            //Per enviar segments: xarxa.enviar(seg);
            while (seguentEnviar - seguentASerReconegut > finestraRecepcio) {
                missatgeCapFinestraRecepcio = false;
                potTransmitir.awaitUninterruptibly();
            }
            missatgeCapFinestraRecepcio = true;
            
            Segment seg = new Segment(Comms.DADES, numeroSequencia, c);
            xarxa.enviar(seg);

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
            if (segment.getTipus() == Comms.DADES) {

            } else if (segment.getTipus() == Comms.ACK) {
                int novaFinestraRecepcio = segment.getFinestra();
                int tamanySegmentTransit = seguentEnviar - seguentASerReconegut;
                if (tamanySegmentTransit > finestraRecepcio && tamanySegmentTransit <= novaFinestraRecepcio) {
                    potTransmitir.signal();
                }
                seguentASerReconegut = segment.getNumSeq();
                finestraRecepcio = novaFinestraRecepcio;
            }
//            System.out.println("ACK rebut -> " + "finestra recepcio: "
//                    + finestraRecepcio);

        } finally {
            mon.unlock();
        }
    }
}
