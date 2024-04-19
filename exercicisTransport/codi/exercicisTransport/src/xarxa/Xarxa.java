package xarxa;

/**
 *
 * @author Marcel Fernandez
 */
public interface Xarxa {
    public void enviar(Object objecte);
    public Object rebre();
    public void close();
}
