package b_establiment;

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
                     rep ACK      |                      |           
                   -----------    |                      |
                                  |                      |
                                  V                      /
                           +--------------+             /
                           |  ESTABLISHED |<------------
                           +--------------+
 */

public class TSocketEstabliment extends TSocket {
  
  protected final static int  CLOSED      = 00,
                              LISTEN      = 10,
                              SYN_SENT    = 20,
                              SYN_RCVD    = 30,
                              ESTABLISHED = 40;

  protected int estat;
  int num_seq;

  public TSocketEstabliment(SimNet ch) {
    super(ch);
    estat = CLOSED;
    num_seq = 0;
  }

  public void inicia() {
    lk.lock();
    try {
      
        
        sendSYN(num_seq);
        num_seq++;
        estat = SYN_SENT;
        
        while(estat != ESTABLISHED){
            appCV.awaitUninterruptibly();
        }
       
    } finally {
      lk.unlock();
    }
  }

  public void espera() {
    lk.lock();
    try {
       
     estat = LISTEN;
     while(estat != ESTABLISHED){
            appCV.awaitUninterruptibly();
        }
     
    } finally {
      lk.unlock();
    }
  }

  @Override
  public void processarSegment(TCPSegment seg) {
    
    lk.lock();
    try {
       
     switch(estat){
         
         case LISTEN:
             
             if(seg.isSyn()){
                 estat = SYN_RCVD;
                 sendSYN_ACK(num_seq, seg.getSeqNum()+1);
                 num_seq ++;
             }
             
             break;
             
         case SYN_SENT:
             
             if(seg.isSyn() && seg.isAck()){
                 estat = ESTABLISHED;
                 sendACK(seg.getSeqNum()+1);
                 appCV.signal();
             }
             
             break;
             
         case SYN_RCVD:
             
             if(seg.isAck()){
                 estat = ESTABLISHED;
                 appCV.signal();
             }
             
             break;
             
         case ESTABLISHED:
             
             if(seg.isPsh()){
                 System.out.println("\t\t\them rebut la dada: "+seg.getData());
                 sendACK(seg.getSeqNum()+1);
             }
             
             if(seg.isAck()){
                 System.out.println("hem rebut la confirmacio de la dada enviada");
             }
             
             break;
     }
     
    } finally {
      lk.unlock();
    }
  }
  
  @Override
  public void enviar(int val) {
    lk.lock();
    try {
      System.out.println("enviat : PSH");
      sendPSH(1,val);
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
}
