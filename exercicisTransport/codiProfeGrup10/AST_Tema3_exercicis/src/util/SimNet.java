package util;

public interface SimNet {
    public void send(TCPSegment seg);
    public TCPSegment receive();
    public int getMTU();
}
