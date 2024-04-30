package Pont;

public class PontTest extends Thread {
    public static void main(String[] args) {

        Pont monitor = new Pont('c');
        Thread cotxe, vaixell;
        int nombreCotxes = 500;

        for (int i = 0; i < nombreCotxes; i++) {
            if (i % 10 == 0) {
                monitor.canviar();
            }

            if (i % 5 == 0) {
                vaixell = new Thread(() -> {
                    monitor.entrar('v');
                    monitor.sortir('v');
                });
                vaixell.start();
            }
            cotxe = new Thread(() -> { 
                    monitor.entrar('c'); 
                    monitor.sortir('c');
            });
            cotxe.start();
        }
        monitor.canviar();
    }
}
