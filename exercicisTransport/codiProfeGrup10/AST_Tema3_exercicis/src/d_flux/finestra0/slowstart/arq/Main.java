package d_flux.finestra0.slowstart.arq;

import util.Cons;
import util.Emissor;
import util.SimNet_FullDuplex;
import util.Receptor;
import util.TSocket;

public class Main {

    public static void main(String[] args) {
        SimNet_FullDuplex net = new SimNet_FullDuplex(Cons.LOSS_RATE);
        TSocket socketEnviar = new TSocketEnviar(net.getSndEnd());
        TSocket socketRebre  = new TSocketRebre(net.getRcvEnd());    
        new Thread(new Receptor(socketRebre)).start();
        new Thread(new Emissor(socketEnviar)).start();
    }
}









