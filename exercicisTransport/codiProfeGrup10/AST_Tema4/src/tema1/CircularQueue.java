package tema1;

/**
 *
 * @author juanluis
 */
public class CircularQueue<E> extends AbstractQueue<E> {
    
    private E[] la_cua;
    private int primer_lliure, primer_ocupat;
    
    public CircularQueue(int cp){
        super(cp);
        la_cua = (E[])new Object[cp];
        primer_lliure = 0;
        primer_ocupat = 0;
    }

    @Override
    public E get() {
        if(empty()){ throw new IllegalStateException("cua buida");}
        else{
            E tmp;
            tmp = la_cua[primer_ocupat];
            la_cua[primer_ocupat] = null;
            primer_ocupat++;
            primer_ocupat = primer_ocupat % capacitat;
            
            //num_elem--;
            
            int tmp_comptador = num_elem;
            //System.out.println("al get: num_elem = "+num_elem);
            tmp_comptador--;
            num_elem = tmp_comptador;
            
            return tmp;
        }
    }

    @Override
    public void put(E value) {
        if(full()){ throw new IllegalStateException("cua plena");}
        else{
            la_cua[primer_lliure] = value;
            primer_lliure++;
            primer_lliure = primer_lliure % capacitat;
            
            //num_elem++;
            
            int tmp_comptador = num_elem;
            //System.out.println("al put: num_elem = "+num_elem);
            retard();
            tmp_comptador++;
            num_elem = tmp_comptador;
        }
    }
    
    private void retard(){
        try{Thread.sleep((int)(Math.random()*100));        }
        catch(Exception e){e.printStackTrace();}
    }
    
}
