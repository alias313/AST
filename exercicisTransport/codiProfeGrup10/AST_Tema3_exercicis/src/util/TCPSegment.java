package util;

/**
 *
 * @author juanluis
 */
public class TCPSegment {
  
  boolean syn, fin, psh, ack;
  int seqNum  = -1;
  int ackNum  = -1;
  int sackNum = -1;
  int wnd     = -1;
  int data    = -1;

  public boolean isSyn() {
    return syn;
  }

  public void setSyn(boolean syn) {
    this.syn = syn;
  }

  public boolean isFin() {
    return fin;
  }

  public void setFin(boolean fin) {
    this.fin = fin;
  }

  public boolean isPsh() {
    return psh;
  }

  public void setPsh(boolean psh) {
    this.psh = psh;
  }

  public boolean isAck() {
    return ack;
  }

  public void setAck(boolean ack) {
    this.ack = ack;
  }

  public int getSeqNum() {
    return seqNum;
  }

  public void setSeqNum(int seqNum) {
    this.seqNum = seqNum;
  }

  public int getAckNum() {
    return ackNum;
  }

  public void setAckNum(int ackNum) {
    this.ackNum = ackNum;
  }

  public int getSackNum() {
    return sackNum;
  }

  public void setSackNum(int sackNum) {
    this.sackNum = sackNum;
  }

  public int getWnd() {
    return wnd;
  }

  public void setWnd(int wnd) {
    this.wnd = wnd;
  }

  public int getData() {
    return data;
  }

  public void setData(int data) {
    this.data = data;
  }
  
  @Override
  public String toString(){
    if(ack && sackNum!=-1){
      return "TCPSegment: { "+
                   "Type=ACK"+
                   ", ackNum="+this.ackNum+
                   ", window="+this.wnd+
                   ", sackNum="+this.sackNum+
                   " }";
    }
    if(ack && sackNum==-1){
      return "TCPSegment: { "+
                   "Type=ACK"+
                   ", ackNum="+this.ackNum+
                   ", window="+this.wnd+
                   " }";
    }
    if(psh){
      return "TCPSegment: { "+
                   "Type=PSH"+
                   ", seqNum="+this.seqNum+
                   ", data="+this.data+
                   " }";
    }
    if(syn){
      return "TCPSegment: { "+
                   "Type=SYN"+
                   ", seqNum="+this.seqNum+
                   " }";
    }
    if(fin){
      return "TCPSegment: { "+
                   "Type=FIN"+
                   ", seqNum="+this.seqNum+
                   " }";
    }
    else{
      return "unknown segment";
    }
  }
  
}
