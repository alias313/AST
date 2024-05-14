

package tema4.a.eco.servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author juanluis
 */
public class EchoServerMultiThread {
    
    public static void main(String[] args) {
        try {
            
            ServerSocket ss = new ServerSocket(2000);
            
            while(true){
                
                System.out.println("a la espera de connexio d'algun client");
                Socket sc = ss.accept();
                EchoWorker worker = new EchoWorker(sc);
                Thread th = new Thread(worker);
                th.start();
                
            }
            
        }
         catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
