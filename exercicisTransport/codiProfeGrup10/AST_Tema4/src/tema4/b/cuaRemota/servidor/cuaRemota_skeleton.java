package tema4.b.cuaRemota.servidor;

import java.net.ServerSocket;
import java.net.Socket;
import tema1.Queue;
import tema2.CircularQueue_amb_monitors;

/**
 *
 * @author juanluis
 */
public class cuaRemota_skeleton {
    
    public static void main(String[] args) {
        try {
            
            ServerSocket ss = new ServerSocket(Comms.PORT);
            Queue q = new CircularQueue_amb_monitors(20);

            while(true){
                System.out.println("esperant connexions de clients...");
                Socket sc = ss.accept();
                cuaRemota_worker worker = new cuaRemota_worker(sc, q);
                Thread th = new Thread(worker);
                th.start();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
