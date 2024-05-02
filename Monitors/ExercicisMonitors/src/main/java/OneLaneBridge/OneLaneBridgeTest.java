package OneLaneBridge;

import java.util.ArrayList;
import java.util.Random;

public class OneLaneBridgeTest extends Thread {
    public static void main(String[] args) {

        BridgeAmbJusticia monitor = new BridgeAmbJusticia();
        Thread cotxe;
        int nombreCotxes = 200;
        ArrayList<Thread> cotxes = new ArrayList<>();

        for (int i = 0; i < nombreCotxes; i++) {

            final int I = i;
            cotxe = new Thread(() -> {
                boolean sentit = (I % 4 == 0);
                if (I == 0) sentit = false;
                monitor.entrar(sentit);
                try {
                    Random random = new Random();
                    Thread.sleep(random.nextInt(200));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
        
                monitor.sortir(sentit);
            });
            cotxes.add(cotxe);
        }

        for (Thread cotxeIte : cotxes) {
            cotxeIte.start();
        }
    }
}
