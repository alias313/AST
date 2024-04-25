package util;

public interface Queue<E extends Object> extends Iterable<E> {
    public int size();
    public int free();
    public boolean hasFree(int n);
    public boolean empty();
    public boolean full();
    public E peekFirst();
    public E peekLast();
    public E get();
    public void put(E e);
}
