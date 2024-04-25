package b_establiment_desconnexio;

import util.TCPSegment;
import util.TSocket;
import util.SimNet;

/**
                           +-------------+
                           |    CLOSED   |------------
                           +-------------+             \
                      espera()    |                     \
                     ---------    |                      |   inicia()
                                  V                      | ------------
                            +------------+               |  envia SYN
                            |   LISTEN   |               |
                            +------------+         +---------------+
                                  |                |   SYN_SENT    |
                                  |                +---------------+
                                  |                      |           
                rep SYN           |                      |  rep SYN_ACK                                   
           -------------------    |                      | -------------       
              envia SYN_ACK       |                      |   envia ACK
                                  |                      |           
                        +-----------------+              |
                        |     SYN_RCVD    |              |  
                        +-----------------+              |
                                  |                      |
      rep ACK        rep PUSH     |                      |           
    -----------  / ------------   |                      |
                    envia ACK     |                      |
                                  V                      /
                           +--------------+             /
                           |  ESTABLISHED |<------------
                           +--------------+
                                |     |
                       CLOSE    |     |    rep FIN
                      -------   |     |    -------
 +---------+        envia FIN  /       \   envia ACK        +---------+
 |  FIN    |<-----------------           ------------------>|  CLOSE  |
 |  WAIT   |------------------           -------------------|  WAIT   |
 +---------+          rep FIN  \       /   CLOSE            +---------+
                      -------   |      |  -------
                      envia ACK |      |  envia FIN 
                                V      V
                              +----------+
                              |  CLOSED  |
                              +----------+
 */

public class TSocketEstablimentDesconnexio extends TSocket {
  
  protected final static int  CLOSED      = 00,
                              LISTEN      = 10,
                              SYN_SENT    = 20,
                              SYN_RCVD    = 30,
                              ESTABLISHED = 40,
                              FIN_WAIT    = 50,
                              CLOSE_WAIT  = 60;
  
  public final static int     CLIENT   = 1,
                              SERVIDOR = 2;

  protected int estat;
  protected int seguentEnviar;
  protected int role;

  public TSocketEstablimentDesconnexio(SimNet ch) {
    super(ch);
    estat = CLOSED;
  }

  @Override
  public void inicia() {
    role = CLIENT;
    lk.lock();
    try {
      System.out.println("enviat : SYN");
      
      sendSYN(seguentEnviar);
      seguentEnviar++;
      
      estat = SYN_SENT;
      
      while (estat != ESTABLISHED) {
        appCV.awaitUninterruptibly();
      }
      
    } finally {
      lk.unlock();
    }
  }

  @Override
  public void espera() {
    role = SERVIDOR;
    lk.lock();
    try {
        
      estat = LISTEN;
      
      while (estat != ESTABLISHED) {
        appCV.awaitUninterruptibly();
      }
      
    } finally {
      lk.unlock();
    }
  }
  
  @Override
  public void tanca(){
    lk.lock();
    try {
      switch (estat) {
        
        case ESTABLISHED:
        case CLOSE_WAIT: {
          
          if (estat == ESTABLISHED) {
            estat = FIN_WAIT;
          } else {
            estat = CLOSED;
          }
          
          if(role==CLIENT){System.out.println("enviat : FIN");}
          else{System.out.println("\t\t\tenviat : FIN");}
          
          sendFIN(seguentEnviar);
          seguentEnviar++;
          
          break;
        }  
        default:
          System.out.println("crida a close des de estat no permes");
          break;
      }
    } finally {
      lk.unlock();
    }
  }

  @Override
  public void processarSegment(TCPSegment seg) {
    lk.lock();
    try {
      switch (estat) {
        case LISTEN: {
          if (seg.isSyn()) {
            System.out.println("\t\t\trebut : SYN");
            System.out.println("\t\t\tenviat : SYN+ACK");
            
            sendSYN_ACK(seguentEnviar,seg.getSeqNum()+1);
            seguentEnviar++;
            
            estat = SYN_RCVD;
          }
          break;
        }
        case SYN_SENT: {
          if (seg.isSyn() && seg.isAck()) {
            System.out.println("rebut : SYN+ACK");
            System.out.println("enviat : ACK");
            
            sendACK(seg.getSeqNum()+1);
            
            estat = ESTABLISHED;
            appCV.signal();
          }
          break;
        }
        case SYN_RCVD: {
          if (seg.isAck()) {
            System.out.println("\t\t\trebut : ACK");
            
            estat = ESTABLISHED;
            appCV.signal();
          }
          break;
        }
        case ESTABLISHED:
        case FIN_WAIT:
        case CLOSE_WAIT: {
          if (seg.isPsh()) {
            System.out.println("\t\t\trebut : PUSH");
            System.out.println("\t\t\tenviat : ACK");
            
            sendACK(seg.getSeqNum()+1);
            
          }
          else if (seg.isAck()) {
            if(role==CLIENT){
              System.out.println("rebut : ACK");
              System.out.println("client acaba");
            }
            else{
              System.out.println("\t\t\trebut : ACK");
              System.out.println("\t\t\tservidor acaba");
            }
          }
          else if (seg.isFin()){
            if(role==CLIENT)
              System.out.println("rebut : FIN");
            else
              System.out.println("\t\t\trebut : FIN");
            
            if (estat == ESTABLISHED) {
              estat = CLOSE_WAIT;
            } else if (estat == FIN_WAIT) {
              estat = CLOSED;
            }
            
            if(role==CLIENT)
              System.out.println("enviat : ACK");
            else
              System.out.println("\t\t\tenviat : ACK");
            
            sendACK(seg.getSeqNum()+1);
            
          }
          else{
            System.out.println("aixo, no hauria de passar!!!");
          }
          break;
        }
        case CLOSED: {
          if (seg.isAck()) {
            if(role==CLIENT){
              System.out.println("rebut : ACK");
              System.out.println("client acaba");
            }
            else{
              System.out.println("\t\t\trebut : ACK");
              System.out.println("\t\t\tservidor acaba");
            }
          }
          break;
        }
      }
    } finally {
      lk.unlock();
    }
  }
  
  @Override
  public void enviar(int val) {
    lk.lock();
    try {
      System.out.println("enviat : PUSH");
      sendPSH(seguentEnviar,val);
      seguentEnviar++;
    } finally {
      lk.unlock();
    }
  }
  
  private void sendSYN(int numSeq){
    TCPSegment seg = new TCPSegment();
    seg.setSyn(true);
    seg.setSeqNum(numSeq);
    network.send(seg);
  }
  
  private void sendSYN_ACK(int numSeq, int numAck){
    TCPSegment seg = new TCPSegment();
    seg.setSyn(true);
    seg.setAck(true);
    seg.setSeqNum(numSeq);
    seg.setAckNum(numAck);
    network.send(seg);
  }
  
  private void sendACK(int numACK){
    TCPSegment seg = new TCPSegment();
    seg.setAck(true);
    seg.setAckNum(numACK);
    network.send(seg);
  }
  
  private void sendPSH(int numSeq, int data){
    TCPSegment seg = new TCPSegment();
    seg.setPsh(true);
    seg.setSeqNum(numSeq);
    seg.setData(data);
    network.send(seg);
  }
  
  private void sendFIN(int numSeq){
    TCPSegment seg = new TCPSegment();
    seg.setFin(true);
    seg.setSeqNum(numSeq);
    network.send(seg);
  }

}
