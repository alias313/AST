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
    protected boolean sentitActual;
    protected int cotxesSentitContrariEsperant, cotxesSentitActualEsperant;
    protected int cotxesEnTransit;
    protected int numberOfWorkers;
    protected Lock mon = new ReentrantLock();
    protected Condition sentitContrariPotPassar, sentitActualPotPassar = mon.newCondition();

    public static void main(String[] args) {
        ServidorEcho echo = new ServidorEcho(Comms.PORT_SERVIDOR);
        System.out.println("Servidor Eco Escoltant ... Port : "+Comms.PORT_SERVIDOR + "\n");
        new Thread(echo).start();
        
    }    
}

class ServidorEcho extends Servidor implements Runnable{
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
                while(true){
                    AstSocket s = new AstSocket(ss.accept());
                    numberOfWorkers++;
                    new Thread(new Worker(s, numberOfWorkers)).start();
                }
            } catch (IOException ex) {
                Logger.getLogger(ServidorEcho.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class Worker extends Servidor implements Runnable {
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
                System.out.println("Recives following message: " + rebut);

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
                    this.sortir(rebut);
                } else if (running) {
                    socket.enviar(rebut);
                }
            } catch (NullPointerException ex) {
                running = false;
                System.out.println(ex);
            }
        }
        numberOfWorkers--;
        System.out.println("Worker " + workerId + " finished connection");
    }

    public void entrar() {
        try {
            mon.lock();
            if (cotxesEnTransit == 0 && cotxesSentitContrariEsperant == 0) {
                sentitActual = sentitCotxe;
            }
            while (cotxesSentitContrariEsperant > 0) {
                cotxesSentitActualEsperant++;
                sentitActualPotPassar.awaitUninterruptibly();
                cotxesSentitActualEsperant--;
            }

            while (sentitActual != sentitCotxe) {
                cotxesSentitContrariEsperant++;
                sentitContrariPotPassar.awaitUninterruptibly();
                cotxesSentitContrariEsperant--;
                if (cotxesSentitContrariEsperant == 0) {
                    sentitActualPotPassar.signalAll();
                }
            }

            cotxesEnTransit++;
            System.out.println("ENTRA sentit " + sentitCotxe);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            mon.unlock();
        }
    }

    public void sortir(String rebut) {
        try {
            mon.lock();
            cotxesEnTransit--;
            if (cotxesEnTransit == 0 && cotxesSentitContrariEsperant > 0) {
                sentitActual = !sentitActual;
                sentitContrariPotPassar.signalAll();
            }
            System.out.println("SURT sentit " + sentitCotxe);
            socket.enviar(rebut);
        } finally {
            mon.unlock();
        }
    }
}
