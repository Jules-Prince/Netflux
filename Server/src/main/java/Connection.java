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
    private String pathOfUsersLogs ; //+ "/Server/src/main/resources/logsNetflux.txt";

    /**
     * Constructor
     * @param port
     * @throws RemoteException
     */
    public Connection(int port) throws RemoteException {
        super(port);
        this.clientList = new HashMap<>();

        this.initialize(); // Recover all clients from the backup file
    }

    /**
     * Set path of the users file
     * The reason is that if you launch the project from Netflux
     * the path will be different if you launch it from Server
     */
    public void setPathOfUserslogs(){
        this.pathOfUsersLogs = System.getProperty("user.dir");
        if(this.pathOfUsersLogs.contains("Server")){
            this.pathOfUsersLogs = this.pathOfUsersLogs.replace("/Server", "/");
        }
        this.pathOfUsersLogs += "Server/src/main/resources/logsNetflux.txt";
    }

    /**
     * Recover all clients from the backup file
     */
    private void initialize() {
        this.setPathOfUserslogs();

        try {
            File file = new File(this.getPathOfUsersLogs());
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

    /**
     * Look in the backup file of the customer accounts, if the identifiers are already there
     * @param mail
     * @param pwd
     * @return
     */
    public boolean checkIfClientCredentialsExistYet(String mail, String pwd) {
        return (this.getClientList().containsKey(mail) && this.getClientList().containsValue(pwd));
    }

    /**
     * Writes the customer account to the backup file
     * @param mail
     * @param pwd
     */
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

    /**
     * Allows you to create an account
     * @param mail
     * @param pwd
     * @return
     */
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

    /**
     * Allows you to connect to an account
     * @param mail
     * @param pwd
     * @return
     */
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
