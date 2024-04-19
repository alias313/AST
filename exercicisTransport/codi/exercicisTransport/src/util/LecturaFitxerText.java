/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcel Fernandez
 */
public class LecturaFitxerText {
    protected FileReader fr;
    
    public LecturaFitxerText(String fitxer){
        try {
            fr = new FileReader(fitxer);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LecturaFitxerText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void tancar(){
        try {
            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(LecturaFitxerText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public char llegirCaracter(){
        char[] b = new char[1];
        try {
            fr.read(b,0,1);
        } catch (IOException ex) {
            Logger.getLogger(LecturaFitxerText.class.getName()).log(Level.SEVERE, null, ex);
        }
        return b[0];        
    }
    
}
