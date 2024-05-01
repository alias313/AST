package Pont;

import java.util.Random;

public class PontTest extends Thread {
    public static void main(String[] args) {

        Pont monitor = new Pont('c');
        Thread cotxe, vaixell;
        int nombreCotxes = 200;

        for (int i = 0; i < nombreCotxes; i++) {
            if (i % 10 == 0) {
                monitor.canviar();
            }

            if (i % 5 == 0) {
                vaixell = new Thread(() -> {
                    monitor.entrar('v');
                    try {
                        Random random = new Random();
                        Thread.sleep(random.nextInt(200));
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
            
                    monitor.sortir('v');
                });
                vaixell.start();
            }
            cotxe = new Thread(() -> { 
                monitor.entrar('c');
                try {
                    Random random = new Random();
                    Thread.sleep(random.nextInt(200));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                monitor.sortir('c');
            });
            cotxe.start();
            //System.out.println("FINISHED ITERATION NUMBER ----------- " + i);
        }
        monitor.canviar();
        System.out.println("SLEEP");
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        System.out.println("AWAKEN");
    }
}
