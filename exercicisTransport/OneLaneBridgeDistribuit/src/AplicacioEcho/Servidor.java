/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package AplicacioEcho;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;

/**
 *
 * @author usuari.aula
 */
public class Servidor {
    protected boolean sentitActual;

    public static void main(String[] args) {
        ServidorEcho echo = new ServidorEcho(Comms.PORT_SERVIDOR);
        System.out.println("Servidor Eco Escoltant ... Port : "+Comms.PORT_SERVIDOR + "\n");
        new Thread(echo).start();
        
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
                while(true){
                    AstSocket s = new AstSocket(ss.accept());
                    String rebut = s.rebre(); //semantica bloquejant
                    System.out.println("rebut : " + rebut +"\n");
                    s.enviar(rebut);
                }
            
            } catch (IOException ex) {
                Logger.getLogger(ServidorEcho.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class Worker implements Runnable {
    protected AstSocket socket;
    protected ArrayList<AstSocket> listaSockets;

    public Worker(AstSocket s, ArrayList<AstSocket> list, int port) {
        socket = s;
        listaSockets = list;
    }

    public void run() {
        // deja pasar si el sentido del coche es el mismo que el actual
        // el momento que viene un coche del sentido contrario se espera
        // y cuando se vacie el sentido actual cambia y continua la lógica
    }
}
