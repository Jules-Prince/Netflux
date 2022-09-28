import contracts.IVODService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static final int PORT_RMIREGISTERY = 2001;
    public static final int PORT_VODSERVICE = 666;


    public static void main(String[] args) {
        try {
            IVODService obj = new VODService(PORT_VODSERVICE);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry(PORT_RMIREGISTERY);
            registry.rebind("Netflux", obj);

            System.out.println("Netflux has been launched correctly");

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
