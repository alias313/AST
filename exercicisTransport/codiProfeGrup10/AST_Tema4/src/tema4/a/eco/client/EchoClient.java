package tema4.a.eco.client;

import java.net.Socket;
import java.io.*;

/**
 *
 * @author juanluis
 */
public class EchoClient {

    public static void main(String[] args) {
        try {
            
            Socket sc = new Socket("127.0.0.1", 2000);
            BufferedReader sc_br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            PrintWriter    sc_pr = new PrintWriter(sc.getOutputStream(), true);
            BufferedReader ky_br = new BufferedReader(new InputStreamReader(System.in));
            
            while(true){
                
                String line = ky_br.readLine();
                sc_pr.println(line);
                String eco  = sc_br.readLine();
                System.out.println(eco);
                if(eco.equals("fi")) break;
                
            }
            
            sc.close();

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
