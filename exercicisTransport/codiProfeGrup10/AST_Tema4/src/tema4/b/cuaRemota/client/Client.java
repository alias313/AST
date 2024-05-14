package tema4.b.cuaRemota.client;

import tema1.Queue;


/**
 *
 * @author juanluis
 */
public class Client {

    public static void main(String[] args) {
        Queue b = new cuaRemota_stub();
        
        for (int i = 0; i < 10; i++) {
            b.put(new Integer(i));
            System.out.println("enviat: "+i);
            try {Thread.sleep((int)(Math.random()*3000));}catch(Exception e){e.printStackTrace();}
            System.out.println("\t\t\trecuperat: "+(int)b.get());
        }
        
        ((cuaRemota_stub)b).close();
    }
}
