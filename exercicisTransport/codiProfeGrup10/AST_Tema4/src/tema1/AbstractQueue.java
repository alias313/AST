package tema1;

/**
 *
 * @author juanluis
 */
public abstract class AbstractQueue<E> implements Queue<E> {
    
    protected volatile int num_elem, capacitat;
    
    public AbstractQueue(int cp){
        capacitat = cp;
    }

    @Override
    public boolean empty() {
        return num_elem == 0;
    }

    @Override
    public boolean full() {
        return num_elem == capacitat;
    }

    @Override
    public int size() {
        return num_elem;
    }
    
}
