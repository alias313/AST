/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AplicacioEcho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author usuari.aula
 */
public class AstSocket {
    protected Socket s;
    protected BufferedReader entradaTxt;
    protected PrintWriter sortidaTxt;
    
    public AstSocket(String ipServidor, int portServidor){
        try {
            
            s = new Socket(ipServidor, portServidor);
            entradaTxt = new BufferedReader(new InputStreamReader(s.getInputStream()));        
            sortidaTxt = new PrintWriter(s.getOutputStream(),true);
            
        } catch (IOException ex) {
            Logger.getLogger(AstSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public AstSocket(Socket sc){
        try {
            
            s = sc;
            entradaTxt = new BufferedReader(new InputStreamReader(s.getInputStream()));        
            sortidaTxt = new PrintWriter(s.getOutputStream(),true);
            
        } catch (IOException ex) {
            Logger.getLogger(AstSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    public void enviar(String str){
        sortidaTxt.println(str);
    }
    
    public String rebre(){
        try {
            
            return entradaTxt.readLine();
        
        } catch (IOException ex) {
            Logger.getLogger(AstSocket.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
