package b_establiment_desconnexio;

import util.SimNet_FullDuplex;
import util.TSocket;


public class EstablimentDesconnexio {

  public static void main(String[] args) {
    try {
      
      SimNet_FullDuplex net = new SimNet_FullDuplex();
      TSocket socketClient   = new TSocketEstablimentDesconnexio(net.getSndEnd());
      TSocket socketServidor = new TSocketEstablimentDesconnexio(net.getRcvEnd());
      
      new Thread(new ExtremServidor(socketServidor)).start();
      
      Thread.sleep(100);
      
      new Thread(new ExtremClient(socketClient)).start();

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
