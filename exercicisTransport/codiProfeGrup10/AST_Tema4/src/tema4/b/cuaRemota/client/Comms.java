package tema4.b.cuaRemota.client;

/**
 *
 * @author juanluis
 */
public interface Comms {
    String HOST = "127.0.0.1";
    int PORT = 2000;
    
    int PUT = 1;
    int GET = 2;
    int OK  = 3;
    int FIN = 4;
}
