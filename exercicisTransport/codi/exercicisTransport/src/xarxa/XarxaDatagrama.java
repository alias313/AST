package xarxa;

/**
 *
 * @author Marcel Fernandez
 */

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class XarxaDatagrama implements Xarxa{

    protected DatagramSocket socket; 
    protected InetAddress dirDesti;
    protected int portOrigen, portDesti;

    public XarxaDatagrama(int portOrigen, int portDesti, InetAddress hostDesti) {
        try {
            this.portOrigen = portOrigen;
            this.portDesti = portDesti;
            socket = new DatagramSocket(portOrigen);
            dirDesti = hostDesti;
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
            socket.close();
    }

    public void enviar(Object objecte) {
        CommsDatagrama.enviar(socket, dirDesti, portDesti, objecte);
    }
    
    public Object rebre(){
        Object res = CommsDatagrama.rebre(socket);
        return res;
    }
}

