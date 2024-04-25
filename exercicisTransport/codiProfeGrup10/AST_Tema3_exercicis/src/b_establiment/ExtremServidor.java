package b_establiment;

import util.TSocket;

public class ExtremServidor implements Runnable {

  protected TSocket socket;

  public ExtremServidor(TSocket s) {
    socket = s;
  }

  public void run() {
    
    System.out.println("\t\t\tExtrem servidor: Inici establiment");
    
    socket.espera();
    
    System.out.println("\t\t\tExtrem servidor: Fi establiment");
    
  }
}
