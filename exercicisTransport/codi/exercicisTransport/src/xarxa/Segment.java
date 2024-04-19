/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xarxa;

import java.io.Serializable;


public class Segment implements Serializable{
    protected int tipus;
    protected int numeroSequencia;
    protected int finestra;
    protected Object dades;

    public Segment(int tipus, int numeroSequencia, Object dades){
        this.tipus = tipus;
        this.numeroSequencia = numeroSequencia;
        this.dades = dades;
        this.finestra = -1;
    }
   
    
    public Segment(int tipus, int numeroSequencia, int finestra, Object dades){
        this.tipus = tipus;
        this.numeroSequencia = numeroSequencia;
        this.dades = dades;
        this.finestra = finestra;
    }
    
    public int getNumSeq(){
        return numeroSequencia;
    }
    
    public Object getDades(){
        return dades;
    }
    
    public int getFinestra(){
        return finestra;
    }
    
    public int getTipus(){
        return tipus;
    }
    
    public String toString(){
        return new String("tipus: "+ tipus +" numSeq: " + numeroSequencia + 
                          " valor: " + dades + " finestra: " + finestra);
    }
}
