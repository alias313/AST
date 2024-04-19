package xarxa;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class EstablirXarxaDatagrama implements EstablirXarxa{
  
    protected Xarxa[] extrems;
    
    public EstablirXarxaDatagrama(){
        try {
            extrems = new Xarxa[2];
            extrems[0]=new XarxaDatagrama(Comms.PORT_1, Comms.PORT_2, 
                                         InetAddress.getByName(Comms.HOST_2));
            extrems[1]=new XarxaDatagrama(Comms.PORT_2, Comms.PORT_1,
                                         InetAddress.getByName(Comms.HOST_1));
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
    }
    
    public Xarxa getExtrem(int extrem){
        return extrems[extrem];
    }
}


