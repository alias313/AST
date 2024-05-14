package tema4.b.cuaRemota.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import tema1.Queue;

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
            oos.writeInt(Comms.PUT);
            oos.writeObject(elem);
            oos.flush();
            int resultat = ois.readInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object get() {
        try {
            oos.writeInt(Comms.GET);
            oos.flush();
            Object resultat = ois.readObject();
            return resultat;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void close() {
        try {
            oos.writeInt(Comms.FIN);
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
