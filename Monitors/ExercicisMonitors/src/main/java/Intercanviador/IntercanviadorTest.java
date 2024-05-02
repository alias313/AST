package Intercanviador;

import java.util.ArrayList;
import java.util.Random;

public class IntercanviadorTest extends Thread {
    public static void main(String[] args) {

        Intercanviador monitor = new Intercanviador();
        Thread intercanvi;

        for (int i = 0; i < 10; i++) {
            final int I = i;
            intercanvi = new Thread(() -> {
                monitor.intercanvi(I);
/*                 try {
                    Random random = new Random();
                    Thread.sleep(random.nextInt(200));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
 */
                monitor.intercanvi(I % 2 == 0);
            });
            intercanvi.start();

        }

    }
}
