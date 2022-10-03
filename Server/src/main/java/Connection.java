import contracts.IConnection;
import contracts.IVODService;
import lombok.Getter;
import lombok.Setter;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Connection extends UnicastRemoteObject implements IConnection {
    private Map<String, String> clientList;

    public Connection(int port) throws RemoteException {
        super(port);
        this.clientList = new HashMap<>();
    }

    public boolean checkIfClientCredentialsExistYet(String mail, String pwd) {
        return (this.getClientList().containsKey(mail) && this.getClientList().containsValue(pwd));
    }

    @Override
    public boolean signIn(String mail, String pwd) {
        if (this.checkIfClientCredentialsExistYet(mail, pwd)) {
            return false;
        } else {
            this.getClientList().put(mail, pwd);
            return true;
        }
    }

    @Override
    public IVODService login(String mail, String pwd) {
        if (!this.checkIfClientCredentialsExistYet(mail, pwd)) {
            return null;
        } else {
            try {
                return new VODService(Server.PORT_VODSERVICE);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
