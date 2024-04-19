/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xarxa;

public interface Comms {

    public final static String HOST_1 = "localhost";
    public final static String HOST_2 = "localhost";
    public final static String HOST_A = "localhost";
    public final static String HOST_B = "localhost";    
    public final static int PORT_1 = 1111;
    public final static int PORT_2 = 2222;
    public final static int PORT_A = 3333;
    public final static int PORT_B = 4444;

    public final static int MIDA_CUA_RECEPCIO = 10;

    public final static int DADES = 100;
    public final static int ACK = 200;
    public final static int CONG = 300;
    
    public final static int MAX_INT_EMISSOR = 200;
    
    public static final int RTO = 2000; //timeout retransmissio en millisegons
    
    public static final int RTT = 500; //Round Trip Time
    public static final int RTT_INF = 450; //Round Trip Time inf
    public static final int RTT_SUP = 550; //Round Trip Time sup
    
    public static final int FINESTRA_CONGESTIO = 4; 
    
    public final static String FITXER = "text.txt";
   
}