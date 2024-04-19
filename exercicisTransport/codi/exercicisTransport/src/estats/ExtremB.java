/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package estats;

public class ExtremB implements Runnable {

    protected TSocketCanviEstats socket;

    public ExtremB(TSocketCanviEstats s) {
            socket = s;
    }

    public void run() {
            System.out.println("ExtremB: Inici");             
            socket.espera();
            System.out.println("ExtremB: Fi");
    }
}
