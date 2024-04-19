/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package estats;

public class ExtremA implements Runnable {

    protected TSocketCanviEstats socket;

    public ExtremA(TSocketCanviEstats s)  {
        socket = s;
    }

    public void run() {
        System.out.println("ExtremA: Inici");
        socket.inicia();        
        System.out.println("ExtremA: Fi");
    }
}
