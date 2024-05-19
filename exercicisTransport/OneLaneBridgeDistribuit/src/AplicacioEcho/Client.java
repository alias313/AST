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
    protected boolean running = true;
    public static void main(String[] args){
        new Thread(new ClientEcho(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR)).start();
    }
    
    
}

class ClientEcho extends Client implements Runnable{
    protected AstSocket socket;
    protected FilTeclat teclatTreaballador;
    protected FilSocket socketTreballador;
    protected MonitorSync missatgeRebut;
    
    public ClientEcho(String ip, int port){
        socket = new AstSocket(ip, port);
        missatgeRebut = new MonitorSync();
        teclatTreaballador = new  FilTeclat(socket, missatgeRebut);
        socketTreballador = new FilSocket(socket, missatgeRebut);

    }
    
    public void run(){

        new Thread(teclatTreaballador).start(); 
        new Thread(socketTreballador).start();
    }
}

class FilSocket extends Client implements Runnable{
    protected AstSocket socket;
    protected MonitorSync missatgeRebut;    

    public FilSocket(AstSocket sc, MonitorSync mon){
        socket = sc;
        missatgeRebut = mon;
    }    
    
    public void run(){
        while(running){
            String txtEcho = socket.rebre();  //semantica bloquejant
            System.out.println("Echo rebut : " + txtEcho);
            System.out.println("");
            missatgeRebut.avisa();
            if (txtEcho.equals("Exit")) running = false;
        }
    }
}

class FilTeclat extends Client implements Runnable{
    protected AstSocket socket;
    protected BufferedReader entradaUsuari;
    protected MonitorSync missatgeRebut;
    
    public FilTeclat(AstSocket sc, MonitorSync mon){
        socket = sc;
        entradaUsuari = new BufferedReader (new InputStreamReader(System.in));
        missatgeRebut = mon;
    }
    public void run(){
        while(running){
            try {
                System.out.println("entra miss : ");
                
                String txtUsuari = entradaUsuari.readLine(); //semantica bloquejant

                System.out.println("");
                
                socket.enviar(txtUsuari);
                
                missatgeRebut.espera();
                
            } catch (IOException ex) {
                Logger.getLogger(FilTeclat.class.getName()).log(Level.SEVERE, null, ex);
            }
            
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
