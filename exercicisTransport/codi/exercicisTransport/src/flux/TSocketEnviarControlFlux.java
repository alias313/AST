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
    protected int numeroSequencia;
    // Si el missatge a transmitir es més gran que la finestra de recepcio missatgeCapFinestraRecepcio es false
    protected boolean missatgeCapFinestraRecepcio;
    protected Condition potTransmitir;

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
            numeroSequencia = seguentEnviar;
            //Per enviar segments: xarxa.enviar(seg);
            while (seguentEnviar == seguentASerReconegut + finestraRecepcio) {
                System.out.println("BUFFER FULL");
                System.out.println("seguentEnviar: " + seguentEnviar);
                System.out.println("seguentAserReconegut: " + seguentASerReconegut);
                potTransmitir.awaitUninterruptibly();
            }
            
            Segment seg = new Segment(Comms.DADES, numeroSequencia, c);
            xarxa.enviar(seg);
            seguentEnviar++;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mon.unlock();
        }
    }

    @Override
    public void processarMissatge(Segment ack) {
        mon.lock();
        try {
            seguentASerReconegut = ack.getNumSeq();
            finestraRecepcio = ack.getFinestra();
            potTransmitir.signal();
            System.out.println("ACK rebut -> " + "finestra recepcio: " + finestraRecepcio);

        } finally {
            mon.unlock();
        }
    }
}
