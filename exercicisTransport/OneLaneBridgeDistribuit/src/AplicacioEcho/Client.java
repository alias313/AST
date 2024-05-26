/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AplicacioEcho;

public class Client {
    public static void main(String[] args){
        String sentitDecidit;
        for (int i = 0; i < 5; i++) {
            PontRepresentant pont = new PontRepresentant(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR);
            double rand = Math.random();

            if (rand > 0.5) sentitDecidit = Comms.NORT;
            else sentitDecidit = Comms.SUR;

            new Thread(new Cotxe(pont, sentitDecidit)).start();
        }
    }
    
    
}

class PontRepresentant implements Pont {
    protected AstSocket socket;

    public PontRepresentant(String ip, int port) {
        socket = new AstSocket(ip, port);
    }

    public void entrar(String sentitCotxe) {
        enviarPeticio(Comms.ENTRAR, sentitCotxe);
    }

    public void sortir() {
        enviarPeticio(Comms.SORTIR, null);
    }

    public void noTornar() {
        enviarPeticio(Comms.NEVER, null);
    }

    protected void enviarPeticio(String accio, String sentit) {				
		socket.enviar(accio);
        socket.enviar(sentit);
		String resposta = socket.rebre(); // bloquejant
        System.out.println(resposta);
	}
}

class Cotxe implements Runnable {
    protected Pont pont;
    protected String sentit;

    public Cotxe (Pont p, String sentitOrdenat) {
        pont = p;
        sentit = sentitOrdenat;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            pont.entrar(sentit);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            pont.sortir();

            double rand = Math.random();
            if (rand > 0.5) {
                sentit = Comms.NORT;
            } else {
                sentit = Comms.SUR;
            }
        }
        pont.noTornar();
    }
}
