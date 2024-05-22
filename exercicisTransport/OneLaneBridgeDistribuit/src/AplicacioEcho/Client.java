/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AplicacioEcho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author usuari.aula
 */
public class Client {
    public static void main(String[] args){
/*         new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, false)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, false)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, false)).start();

        for (int i = 0; i < 20; i++) {
            double rand = Math.random();
            if (rand > 0.3) {
                new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, false)).start();
            } else {
                new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
            }
        }
 */
        PontRepresentant pont = new PontRepresentant(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR);
        new Thread(new Cotxe(pont, Comms.NORT)).start();
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
        pont.entrar(sentit);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        pont.sortir();
        pont.noTornar();
    }
}

class ClientCar implements Runnable{
    protected AstSocket socket;
    protected FilTeclat teclatTreaballador;
    protected FilSocket socketTreballador;
    protected MonitorSync missatgeRebut;
    
    public ClientCar(String ip, int port, boolean sentit){
        socket = new AstSocket(ip, port);
        missatgeRebut = new MonitorSync();
        teclatTreaballador = new  FilTeclat(socket, missatgeRebut, sentit);
        socketTreballador = new FilSocket(socket, missatgeRebut);

    }
    
    public void run(){

        new Thread(teclatTreaballador).start(); 
        new Thread(socketTreballador).start();
    }
}

class FilSocket implements Runnable{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    protected AstSocket socket;
    protected MonitorSync missatgeRebut;    

    public FilSocket(AstSocket sc, MonitorSync mon){
        socket = sc;
        missatgeRebut = mon;
    }    
    
    public void run(){
        while(true){
            String txtEcho = socket.rebre();  //semantica bloquejant
            if (txtEcho.equals("north")) System.out.println(ANSI_BLUE + "Car heading north crossed bridge" + ANSI_RESET);
            else if (txtEcho.equals("south")) System.out.println(ANSI_RED + "\t\t\t\t\t\tCar heading south crossed bridge" + ANSI_RESET);
            else {
                System.out.println("Echo rebut : " + txtEcho);
                System.out.println("");
                missatgeRebut.avisa();
            }
        }
    }
}

class FilTeclat implements Runnable{
    protected AstSocket socket;
    protected BufferedReader entradaUsuari;
    protected MonitorSync missatgeRebut;
    protected String sentitCar;
    
    public FilTeclat(AstSocket sc, MonitorSync mon, boolean sentit){
        socket = sc;
        entradaUsuari = new BufferedReader (new InputStreamReader(System.in));
        missatgeRebut = mon;
        if (sentit) sentitCar = "north";
        else if (!sentit) sentitCar = "south";
    }

    public FilTeclat(AstSocket sc, MonitorSync mon){
        socket = sc;
        entradaUsuari = new BufferedReader (new InputStreamReader(System.in));
        missatgeRebut = mon;
        sentitCar = "chat";
    }

    public void run(){
        try {
            if (sentitCar.equals("north")) socket.enviar("north");
            else if (sentitCar.equals("south")) socket.enviar("south");
            else {
                while (true) {
                    System.out.println("entra miss : ");
                
                    String txtUsuari = entradaUsuari.readLine(); //semantica bloquejant

                    System.out.println("");
                    socket.enviar(txtUsuari);

                    if (txtUsuari.toLowerCase().equals("exit")) System.exit(0);
                }
            }
            
            missatgeRebut.espera();
        } catch (IOException ex) {
            Logger.getLogger(FilTeclat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class MonitorSync{
    protected Lock mon;
    protected Condition echoRebut;
    
    public MonitorSync(){
        mon = new ReentrantLock();
        echoRebut = mon.newCondition();
    }
    
    public void espera(){
        mon.lock();
        echoRebut.awaitUninterruptibly();
        mon.unlock();
    }
    
    public void avisa(){
        mon.lock();
        echoRebut.signal();
        mon.unlock();
    }    
}
