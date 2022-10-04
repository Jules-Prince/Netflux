import contracts.IConnection;
import contracts.IVODService;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

@Getter
@Setter
public class Connection extends UnicastRemoteObject implements IConnection {
    private Map<String, String> clientList;
    private static final String PATH_OF_USERS_LOGS = new File("src/main/resources/logsNetflux.txt").getAbsolutePath();

    public Connection(int port) throws RemoteException {
        super(port);
        this.clientList = new HashMap<>();

        this.initialize();
    }

    private void initialize() {
        try {
            File file = new File(Connection.PATH_OF_USERS_LOGS);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String mail = line.split(" ")[0];
                String pwd = line.split(" ")[1];
                this.getClientList().put(mail, pwd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfClientCredentialsExistYet(String mail, String pwd) {
        return (this.getClientList().containsKey(mail) && this.getClientList().containsValue(pwd));
    }

    private void saveNewUserCredentials(String mail, String pwd) {
        try {
            Files.writeString(
                    Path.of(System.getProperty("user.dir"), "logsNetflux.txt"),
                    "\n" + mail + " " + pwd + System.lineSeparator(),
                    CREATE, APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean signIn(String mail, String pwd) {
        if (this.checkIfClientCredentialsExistYet(mail, pwd)) {
            return false;
        } else {
            this.getClientList().put(mail, pwd);
            this.saveNewUserCredentials(mail, pwd);
            return true;
        }
    }

    @Override
    public IVODService login(String mail, String pwd) {
        if (this.checkIfClientCredentialsExistYet(mail, pwd)) {
            try {
                return new VODService(Server.PORT_VODSERVICE);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }
}
