package b_establiment;

import util.SimNet_FullDuplex;
import util.TSocket;

public class Establiment {

  public static void main(String[] args) {
    try {
      
      SimNet_FullDuplex net = new SimNet_FullDuplex();
      TSocket socketClient   = new TSocketEstabliment(net.getSndEnd());
      TSocket socketServidor = new TSocketEstabliment(net.getRcvEnd());
      
      new Thread(new ExtremServidor(socketServidor)).start();
      
      Thread.sleep(100);
      
      new Thread(new ExtremClient(socketClient)).start();

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
