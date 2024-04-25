package util;

public class Emissor implements Runnable {

  protected TSocket sc;

  public Emissor(TSocket sc) {
    this.sc = sc;
  }

  public void run() {
    try {
      int i = 0;
      while (true) {
        sc.enviar(i);
        System.out.println("------------------ enviat : " + i);
        i++;
        try{Thread.sleep(Cons.TEMPS_ENTRE_ESCR);}catch(Exception e){e.printStackTrace();}
      }
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }

}
