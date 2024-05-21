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
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
/*         new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
        new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();

        for (int i = 0; i < 20; i++) {
            double rand = Math.random();
            if (rand > 0.3) {
                new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, false)).start();
            } else {
                new Thread(new ClientCar(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR, true)).start();
            }
        }
 */
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
    protected AstSocket socket;
    protected MonitorSync missatgeRebut;    

    public FilSocket(AstSocket sc, MonitorSync mon){
        socket = sc;
        missatgeRebut = mon;
    }    
    
    public void run(){
        while(true){
            String txtEcho = socket.rebre();  //semantica bloquejant
            System.out.println("Echo rebut : " + txtEcho);
            System.out.println("");
            missatgeRebut.avisa();
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
