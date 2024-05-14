package tema4.b.cuaRemota_v2.servidor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import tema1.Queue;
import tema4.b.cuaRemota_v2.util.Missatge;

/**
 *
 * @author juanluis
 */
public class cuaRemota_worker implements Runnable {
    
    Socket sc;
    Queue q;

    public cuaRemota_worker(Socket sc, Queue q) {
        this.sc = sc;
        this.q  = q;
    }

    @Override
    public void run() {

        try {

            //s'han de creuar les instanciacions de oos i ois respecte al client:
            ObjectOutputStream oos = new ObjectOutputStream(sc.getOutputStream());
            ObjectInputStream  ois = new ObjectInputStream(sc.getInputStream());
            
            while (true) {
                
                Missatge m = (Missatge) ois.readObject();
                
                switch(m.getType()){
                    case PUT:
                        q.put(m.getContent());
                        Missatge reply = new Missatge();
                        reply.setType(Missatge.Type.RESPONSE);
                        oos.writeObject(reply);
                        oos.flush();
                        break;
                    case GET:
                        Object content = q.get();
                        reply = new Missatge();
                        reply.setType(Missatge.Type.RESPONSE);
                        reply.setContent(content);
                        oos.writeObject(reply);
                        oos.flush();
                        break;
                    case CLOSE:
                        sc.close();
                        return;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
