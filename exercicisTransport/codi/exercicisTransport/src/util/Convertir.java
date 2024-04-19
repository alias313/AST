package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class Convertir {

    public static byte[] aBytes(Object obj) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(out);
            os.writeObject(obj);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return out.toByteArray();
    }

    public static Object aObjecte(byte[] data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(in);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Object res = null;
        try {
            res = is.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return res;
    }
}
