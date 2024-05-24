package practica3;

import utils.Receiver;
import utils.Sender;
import utils.SimNet;

public class Test {

    public static void main(String[] args) {
        SimNet net = new SimNet_Loss(0.2);
        new Sender(new TSocketSend(net), 20, 20, 100).start();
        new Receiver(new TSocketRecv(net), 10, 30).start();
    }
}
