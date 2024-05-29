package P2;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GestorPool_test {

    public static void main(String[] args) {
        int N = 20;
        int[] tipus = {0, 0, 1, 1, 2};

        GestorPool gestor = new GestorPool();

        for (int i = 0; i < N; i++) {
            new Sol_licitant(gestor, tipus[i % 5]).start();
        }
    }
}

class Sol_licitant extends Thread {

    GestorPool gestor;
    int meu_tipus;

    public Sol_licitant(GestorPool gestor, int meu_tipus) {
        this.gestor = gestor;
        this.meu_tipus = meu_tipus;
    }

    @Override
    public void run() {

        //while (true) {
            gestor.demana(meu_tipus);
            espera();
            gestor.allibera(meu_tipus);
            espera();
        //}
    }

    private void espera() {
        try {
            sleep((int) (Math.random() * 300));
        } catch (InterruptedException ex) {
            Logger.getLogger(Sol_licitant.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
