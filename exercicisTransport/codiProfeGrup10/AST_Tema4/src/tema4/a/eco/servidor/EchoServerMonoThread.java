

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
public class EchoServerMonoThread {
    
    public static void main(String[] args) {
        try {
            
            ServerSocket ss = new ServerSocket(2000);
            
            while(true){
                
                System.out.println("a la espera de connexio d'algun client");
                Socket sc = ss.accept();
                BufferedReader sc_br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                PrintWriter    sc_pr = new PrintWriter(sc.getOutputStream(), true);
                
                while(true){
                    
                    String line = sc_br.readLine();
                    sc_pr.println(line);
                    if(line.equals("fi")) break;
                    
                }
                
                sc.close();
                
            }
            
        }
         catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
