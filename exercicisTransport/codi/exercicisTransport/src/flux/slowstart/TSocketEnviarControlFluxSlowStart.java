package flux.slowstart;

import xarxa.Comms;
import xarxa.Segment;
import xarxa.TSocket;
import xarxa.Xarxa;

public class TSocketEnviarControlFluxSlowStart extends TSocket {

    protected int seguentEnviar, seguentASerReconegut;
    protected int finestraRecepcio, finestraCongestio, finestraPermesa;

    public TSocketEnviarControlFluxSlowStart(Xarxa x) {
        super(x);
        finestraRecepcio = Comms.MIDA_CUA_RECEPCIO;
        finestraCongestio = 1;
        finestraPermesa = 1;
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
            
 
            
            
            
            
//            System.out.println("ACK rebut -> " + "finestra permesa: "
//                    + finestraPermesa);
        } finally {
            mon.unlock();
        }
    }

}
