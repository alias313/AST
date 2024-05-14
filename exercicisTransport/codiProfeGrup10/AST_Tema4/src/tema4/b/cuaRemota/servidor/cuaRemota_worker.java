package tema4.b.cuaRemota.servidor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import tema1.Queue;

/**
 *
 * @author juanluis
 */
public class cuaRemota_worker implements Runnable {
    
    Socket sc;
    Queue q;

    public cuaRemota_worker(Socket sc, Queue q) {
        this.sc = sc;
        this.q = q;
    }

    @Override
    public void run() {

        try {

            //s'han de creuar les instanciacions de oos i ois respecte al client:
            ObjectOutputStream oos = new ObjectOutputStream(sc.getOutputStream());
            ObjectInputStream  ois = new ObjectInputStream(sc.getInputStream());
            
            while (true) {
                
                int solicitud = ois.readInt();
                
                switch(solicitud){
                    case Comms.PUT:
                        Object elem = ois.readObject();
                        q.put(elem);
                        oos.writeInt(Comms.OK);
                        oos.flush();
                        break;
                    case Comms.GET:
                        Object elem2 = q.get();
                        oos.writeObject(elem2);
                        oos.flush();
                        break;
                    case Comms.FIN:
                        sc.close();
                        return;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
