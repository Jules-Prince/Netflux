package contracts;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IConnection extends Remote {

    boolean checkIfClientCredentialsExistYet(String mail, String pwd) throws RemoteException;
    boolean signIn (String mail, String pwd) throws RemoteException;
    IVODService login(String mail, String pwd) throws RemoteException;
}
