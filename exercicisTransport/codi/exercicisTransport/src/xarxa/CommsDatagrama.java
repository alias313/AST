package xarxa;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import util.Convertir;

/**
 *
 * @author Marcel Fernandez
 */
public abstract class CommsDatagrama {

    public static void enviar(DatagramSocket socket, InetAddress dirDesti,
                               int  portDesti ,Object objecte ) {
        try {
            byte[] dadesEnviar = Convertir.aBytes(objecte);
            DatagramPacket segment = new DatagramPacket(dadesEnviar, 
                                       dadesEnviar.length, dirDesti, portDesti);
            socket.send(segment);
        } 
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static Object rebre(DatagramSocket socket){
        Object rebut=null;
        try {
            byte[] dadesRebudes = new byte[1024];
            DatagramPacket segment = new DatagramPacket(dadesRebudes, 
                                                         dadesRebudes.length);
            socket.receive(segment);
         
            byte[] objecteEnBytes =  segment.getData();
            rebut = Convertir.aObjecte(objecteEnBytes);
        } 
        catch (IOException ex) {
            ex.printStackTrace();
        }finally{
            return rebut;
        }
    }
}
