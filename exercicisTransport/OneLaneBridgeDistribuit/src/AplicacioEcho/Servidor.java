/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package AplicacioEcho;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author usuari.aula
 */
public class Servidor {
    public static void main(String[] args) {
        ServidorPont sPont = new ServidorPont(Comms.PORT_SERVIDOR);
        System.out.println("Servidor Pont Escoltant ... Port : " + Comms.PORT_SERVIDOR + "\n");
        new Thread(sPont).start();
    }    
}

class ServidorPont implements Runnable {
    protected ServerSocket ss;
    
    public ServidorPont(int port){
        try {
            ss = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(ServidorPont.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run(){
        while(true){
            try {
                PontReal pont = new PontReal();
                while(true){
                    AstSocket s = new AstSocket(ss.accept());
                    System.out.println("Received new foreign connection");
                    new Thread(new Treballador(s, pont)).start();
                }
            } catch (IOException ex) {
                Logger.getLogger(ServidorPont.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

class Treballador implements Runnable {
    protected AstSocket socket;
    protected Pont pont;
    protected boolean running;
    protected String sentitActual;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public Treballador(AstSocket s, Pont p) {
        socket = s;
        pont = p;
    }
    public void run() {
        running = true;
        while (running) {
            try {
                String accio = socket.rebre();
                //System.out.println("Receivec accio: " + accio);
                String sentit = socket.rebre();
                //System.out.println("Receivec sentit: " + sentit);

                String ANSI_CURRENT = new String();
                switch (accio) {
                    case Comms.ENTRAR:
                        sentitActual = sentit;
                        if (sentitActual.equals(Comms.NORT)) ANSI_CURRENT = ANSI_BLUE;
                        else if (sentitActual.equals(Comms.SUR)) ANSI_CURRENT = ANSI_RED;
                        pont.entrar(sentit);
                        socket.enviar(ANSI_CURRENT + "COTXE HA ENTRAT AL PONT PER " + sentitActual + ANSI_RESET);
                        break;
                    case Comms.SORTIR:
                        if (sentitActual.equals(Comms.NORT)) ANSI_CURRENT = ANSI_BLUE;
                        else if (sentitActual.equals(Comms.SUR)) ANSI_CURRENT = ANSI_RED;
                        pont.sortir();
                        socket.enviar(ANSI_CURRENT + "\t\t\t\t\tCOTXE HA SORTIT DEL PONT PER " + sentitActual + ANSI_RESET);
                        break;
                    case Comms.NEVER:
                        running = false;
                        socket.enviar("CONNEXIO TERMINADA");
                        break;
                }
            } catch (NullPointerException ex) {
                System.out.println(ex);
            }
        }
    }
}

class PontReal implements Pont {
    protected boolean sentitActual;
    protected int cotxesSentitContrariEsperant, cotxesSentitActualEsperant;
    protected int cotxesEnTransit;
    protected Lock mon;
    protected Condition sentitContrariPotPassar, sentitActualPotPassar;

    public PontReal() {
        mon = new ReentrantLock();
        sentitContrariPotPassar = mon.newCondition();
        sentitActualPotPassar = mon.newCondition();
    }

    public void entrar(String sentitCotxe) {
        mon.lock();
        try {
            boolean sentitMeu = false; // default
            if (sentitCotxe.equals(Comms.NORT)) sentitMeu = true;
            else if (sentitCotxe.equals(Comms.SUR)) sentitMeu = false;
            else {
                System.out.println("FORMAT DE SENTIT INCORRECTE");
                System.exit(1);
            }

            if (cotxesEnTransit == 0 && cotxesSentitContrariEsperant == 0) {
                sentitActual = sentitMeu;
            }
            while (cotxesSentitContrariEsperant > 0) {
                cotxesSentitActualEsperant++;
                sentitActualPotPassar.awaitUninterruptibly();
                cotxesSentitActualEsperant--;
            }

            while (sentitActual != sentitMeu) {
                cotxesSentitContrariEsperant++;
                sentitContrariPotPassar.awaitUninterruptibly();
                cotxesSentitContrariEsperant--;
                if (cotxesSentitContrariEsperant == 0) {
                    sentitActualPotPassar.signalAll();
                }
            }

            cotxesEnTransit++;
            System.out.print("COTXE HA ENTRAT SENTIT ");
            if (sentitActual) System.out.println("NORT");
            else if (!sentitActual) System.out.println("SUR");
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            mon.unlock();
        }
    }

    public void sortir() {
        mon.lock();
        try {
            cotxesEnTransit--;
            System.out.print("COTXE HA SORTIT SENTIT ");
            if (sentitActual) System.out.println("NORT");
            else if (!sentitActual) System.out.println("SUR");
            if (cotxesEnTransit == 0 && cotxesSentitContrariEsperant > 0) {
                sentitActual = !sentitActual;
                sentitContrariPotPassar.signalAll();
            }
        } finally {
            mon.unlock();
        }
    }

    public void noTornar() {
        // nada
    }
}
