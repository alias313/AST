package b_establiment;

import util.TSocket;

public class ExtremClient implements Runnable {

  protected TSocket socket;

  public ExtremClient(TSocket s) {
    socket = s;
  }

  public void run() {
    
    System.out.println("Extrem client:   Inici establiment");
    
    socket.inicia();
    
    System.out.println("Extrem client:   Fi establiment");
    
    try{Thread.sleep(1000);}catch(Exception e){e.printStackTrace();}
    
    System.out.println("Extrem client:   envia dada");
    
    socket.enviar(0);
    
  }
}
