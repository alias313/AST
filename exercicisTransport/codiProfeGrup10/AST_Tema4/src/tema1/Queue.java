package tema1;


/**
 *
 * @author juanluis
 */
public interface Queue<E> {
    boolean empty();
    boolean full();
    int size();
    E get();
    void put(E value);
}
