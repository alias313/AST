package tema4.b.cuaRemota_v2.servidor;

import tema4.b.cuaRemota_v2.util.Comms;
import java.net.ServerSocket;
import tema2.CircularQueue_amb_monitors;
import tema1.Queue;

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
                new Thread(new cuaRemota_worker(ss.accept(),q)).start();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
