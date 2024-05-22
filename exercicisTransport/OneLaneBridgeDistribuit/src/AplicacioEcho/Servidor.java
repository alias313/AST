/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package AplicacioEcho;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;

/**
 *
 * @author usuari.aula
 */
public class Servidor {
    static boolean sentitActual;
    static int cotxesSentitContrariEsperant, cotxesSentitActualEsperant;
    static int cotxesEnTransit;
    static int numberOfWorkers;
    static Lock mon = new ReentrantLock();
    static Condition sentitContrariPotPassar = mon.newCondition(); 
    static Condition sentitActualPotPassar = mon.newCondition();

    public static void main(String[] args) {
/*         ServidorEcho echo = new ServidorEcho(Comms.PORT_SERVIDOR);
        System.out.println("Servidor Eco Escoltant ... Port : " + Comms.PORT_SERVIDOR + "\n");
        new Thread(echo).start();
 */        
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

    public Treballador(AstSocket s, Pont p) {
        socket = s;
        pont = p;
    }
    public void run() {
        running = true;
        while (running) {
            try {
                String accio = socket.rebre();
                System.out.println("Receivec accio: " + accio);
                String sentit = socket.rebre();
                System.out.println("Receivec sentit: " + sentit);

                switch (accio) {
                    case Comms.ENTRAR:
                        pont.entrar(sentit);
                        socket.enviar("COTXE HA ENTRAT AL PONT");
                        break;
                    case Comms.SORTIR:
                        pont.sortir();
                        socket.enviar("COTXE HA SORTIT DEL PONT");
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

class ServidorEcho implements Runnable{
    protected ServerSocket ss;
    
    public ServidorEcho(int port){
        try {
            ss = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(ServidorEcho.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run(){
        while(true){
            try {
                PontReal pont = new PontReal();
                while(true){
                    AstSocket s = new AstSocket(ss.accept());
                    System.out.println("Received new foreign connection");
                    Servidor.numberOfWorkers++;
                    new Thread(new Worker(s, Servidor.numberOfWorkers)).start();
                }
            } catch (IOException ex) {
                Logger.getLogger(ServidorEcho.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class Worker implements Runnable {
    protected AstSocket socket;
    protected boolean sentitCotxe, validRebut, running;
    protected int workerId;
    String rebut = new String();

    public Worker(AstSocket s, int id) {
        socket = s;
        workerId = id;
    }

    public void run() {
        System.out.println("Worker " + workerId + " started and ready to receive");
        running = true;
        while (running) {
            validRebut = false;
            try {
                rebut = socket.rebre(); //semantica bloquejant
                System.out.println("Worker " + workerId + " receives following message: " + rebut);

                switch (rebut.toLowerCase()) {
                    case "north":
                        validRebut = true;
                        sentitCotxe = true;
                        break;
                    
                    case "south":
                        validRebut = true;
                        sentitCotxe = false;
                        break;
                    case "exit":
                        running = false;               
                    default:
                        break;
                }

                if (validRebut) {
                    // deja pasar si el sentido del coche es el mismo que el actual
                    // el momento que viene un coche del sentido contrario se espera
                    // y cuando se vacie el sentido actual cambia y continua la lógica

                    // El mismo thread solo indica el sentido la primera vez que se llamas
                    // porque las siguientes por fuerza es el contrario del anterior
                    this.entrar(); // blockejant
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    this.sortir(rebut);
                } else if (running) {
                    socket.enviar(rebut);
                }
            } catch (NullPointerException ex) {
                running = false;
                //System.out.println(ex);
            }
        }
        Servidor.mon.lock();
        Servidor.numberOfWorkers--;
        System.out.println("Worker " + workerId + " finished connection");
        System.out.println(Servidor.numberOfWorkers + " workers left.");
        Servidor.mon.unlock();
    }

    public void entrar() {
        Servidor.mon.lock();
        try {
            if (Servidor.cotxesEnTransit == 0 && Servidor.cotxesSentitContrariEsperant == 0) {
                Servidor.sentitActual = sentitCotxe;
            }
            while (Servidor.cotxesSentitContrariEsperant > 0) {
                Servidor.cotxesSentitActualEsperant++;
                Servidor.sentitActualPotPassar.awaitUninterruptibly();
                Servidor.cotxesSentitActualEsperant--;
            }

            while (Servidor.sentitActual != sentitCotxe) {
                Servidor.cotxesSentitContrariEsperant++;
                Servidor.sentitContrariPotPassar.awaitUninterruptibly();
                Servidor.cotxesSentitContrariEsperant--;
                if (Servidor.cotxesSentitContrariEsperant == 0) {
                    Servidor.sentitActualPotPassar.signalAll();
                }
            }

            Servidor.cotxesEnTransit++;
            System.out.println("ENTRA sentit " + sentitCotxe);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            Servidor.mon.unlock();
        }
    }

    public void sortir(String rebut) {
        Servidor.mon.lock();
        try {
            Servidor.cotxesEnTransit--;
            if (Servidor.cotxesEnTransit == 0 && Servidor.cotxesSentitContrariEsperant > 0) {
                Servidor.sentitActual = !Servidor.sentitActual;
                Servidor.sentitContrariPotPassar.signalAll();
            }
            System.out.println("SURT sentit " + sentitCotxe);
            socket.enviar(rebut);
        } finally {
            Servidor.mon.unlock();
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
            else if (sentitCotxe.equals(Comms.NORT)) sentitMeu = true;
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
            System.out.println("COTXE HA ENTRAT");
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
            if (cotxesEnTransit == 0 && cotxesSentitContrariEsperant > 0) {
                sentitActual = !sentitActual;
                sentitContrariPotPassar.signalAll();
            }
            System.out.println("COTXE HA SORTIT");
        } finally {
            mon.unlock();
        }
    }

    public void noTornar() {
        // nada
    }
}
