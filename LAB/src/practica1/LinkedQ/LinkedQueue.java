package practica1.LinkedQ;

import java.util.Iterator;
import util.Queue;

public class LinkedQueue<E> implements Queue<E> {

  //Completar

  @Override
  public int size() {
    throw new RuntimeException("//Completar...");
  }

  @Override
  public int free() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean empty() {
    throw new RuntimeException("//Completar...");
  }

  @Override
  public boolean full() {
    return false;
  }

  @Override
  public E peekFirst() {
    throw new RuntimeException("//Completar...");
  }

  @Override
  public E get() {
    throw new RuntimeException("//Completar...");
  }

  @Override
  public void put(E e) {
    throw new RuntimeException("//Completar...");
  }


  @Override
  public String toString() {
    throw new RuntimeException("//Completar...");
  }

  @Override
  public Iterator<E> iterator() {
    return new MyIterator();
  }

  class MyIterator implements Iterator {

    //Completar...

    @Override
    public boolean hasNext() {
      throw new RuntimeException("//Completar...");
    }

    @Override
    public E next() {
      throw new RuntimeException("//Completar...");
    }

    @Override
    public void remove() {
      throw new RuntimeException("//Completar...");
    }

  }
}
