package contracts.box;

import contracts.IClientBox;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientBox extends UnicastRemoteObject implements IClientBox {
    private int display;

    public ClientBox(int port) throws RemoteException {
        super(port);
        this.display = 0;
    }

    @Override
    public void stream(byte[] chunk) throws RemoteException{
        System.out.print(chunk + " ");
        this.display = this.display + 1;
        if(this.display == 6){
            this.display = 0;
            System.out.println("\n");
        }
    }
}
