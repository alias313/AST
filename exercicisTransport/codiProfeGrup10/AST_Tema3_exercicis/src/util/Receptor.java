package util;

public class Receptor implements Runnable {

  protected TSocket sc;

  public Receptor(TSocket sc) {
    this.sc = sc;
  }

  public void run() {
    try {
      int i=0;
      while (true) {
        try{Thread.sleep(Cons.TEMPS_ENTRE_LECT);}catch(Exception e){e.printStackTrace();}
        int tmp = sc.rebre();
        System.out.println("++++++++++++++ rebut : " + tmp);
        if(tmp!=i){System.err.println("++++++++++++++ DADA INCORRECTA!!! hauria de ser: " + i);}
        i++;
      }
    } catch (Exception e) {

      e.printStackTrace(System.err);
    }
  }

}
