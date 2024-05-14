package tema4.b.cuaRemota_v2.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import tema1.Queue;
import tema4.b.cuaRemota_v2.util.Comms;
import tema4.b.cuaRemota_v2.util.Missatge;

/**
 *
 * @author juanluis
 */
public class cuaRemota_stub implements Queue {

    Socket sc;
    ObjectInputStream ois;
    ObjectOutputStream oos;

    public cuaRemota_stub() {
        try {
          sc  = new Socket(Comms.HOST, Comms.PORT);
          ois = new ObjectInputStream(sc.getInputStream());
          oos = new ObjectOutputStream(sc.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(Object elem) {
        try {
          Missatge m = new Missatge();
          m.setType(Missatge.Type.PUT);
          m.setContent(elem);
          oos.writeObject(m);
          oos.flush();
          ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object get() {
        try {
          Missatge m = new Missatge();
          m.setType(Missatge.Type.GET);
          oos.writeObject(m);
          oos.flush();
          Missatge reply = (Missatge) ois.readObject();
          return reply.getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void close() {
        try {
          Missatge m = new Missatge();
          m.setType(Missatge.Type.CLOSE);
          oos.writeObject(m);
          oos.flush();
          sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean full() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean empty() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
