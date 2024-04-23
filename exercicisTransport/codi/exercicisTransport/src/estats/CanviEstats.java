/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package estats;

import xarxa.EstablirXarxa;
import xarxa.EstablirXarxaDatagrama;

public class CanviEstats {
    public static void main(String[] args) {
        try {
            EstablirXarxa xarxa = new EstablirXarxaDatagrama();
            TSocketCanviEstats socketA = new TSocketCanviEstats1(xarxa.getExtrem(0));
            TSocketCanviEstats socketB = new TSocketCanviEstats1(xarxa.getExtrem(1));
            new Thread(new ExtremB(socketB)).start();
            Thread.sleep(100);
            new Thread(new ExtremA(socketA)).start();

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}
