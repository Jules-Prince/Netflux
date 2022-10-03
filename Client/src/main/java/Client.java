import contracts.Bill;
import contracts.IClientBox;
import contracts.IConnection;
import contracts.IVODService;
import contracts.box.ClientBox;
import contracts.movies.MovieDesc;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Struct;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static final int PORT_CLIENTBOX = 10006;
    public static final Scanner CLAVIER = new Scanner(System.in);
    private List<MovieDesc> movieDescList;
    private IClientBox clientBox;

    void run() throws RemoteException {
        Registry reg = LocateRegistry.getRegistry(2001);
        clientBox = new ClientBox(PORT_CLIENTBOX);
        Bill bill = null;
        String isbn;
        boolean valid = false;

        try {
            // Connection to Netflux
            IVODService vodService = this.connectToServer(reg);

            // Get catalog
            movieDescList = vodService.viewCatalog();

            while (!valid) {
                // Print catalog
                this.printMovieList(movieDescList);

                // Client choice for the movie Serializable
                isbn = this.myChoice();

                // Play the movie
                try {
                    // get bill
                    System.out.println("Le film : ");
                    bill = vodService.playmovie(isbn, clientBox);
                    valid = true;
                } catch (RemoteException e) {
                    System.out.println(e.detail);
                    System.out.println("Recommencer ...\n\n");
                }
            }

            // Print Bill
            this.printBill(bill);

        } catch (NoSuchObjectException e) {
            System.out.println("Aie aie aie, probleme avec l'objet ...");
            e.printStackTrace();
        }
    }

    public IVODService connectToServer(Registry reg) {
        try {
            IConnection connection = (IConnection) reg.lookup("Connection");

            System.out.println("Entrez vos identifiant (mail pwd):");
            String logs = CLAVIER.nextLine();
            String mail = logs.split(" ")[0];
            String pwd = logs.split(" ")[1];
            if (connection.checkIfClientCredentialsExistYet(mail, pwd)) {
                System.out.println("Bienvenue "+ mail +" sur Netflux la plateforme de VOD n°1 ");
                return connection.login(mail, pwd);
            } else {
                System.out.println("Nouveau sur la plateforme ? Souhaitez vous inscrire (0 non; 1 oui)?");
                String answer = CLAVIER.nextLine();
                if (answer.equals("0") || answer.equals("non")) {
                    return this.connectToServer(reg);
                } else if (answer.equals("1") || answer.equals("oui")) {
                    System.out.println("Vous venez de vous inscrire avec l'identifiant : " + mail);
                    if (!connection.signIn(mail, pwd))
                        throw new RuntimeException("Erreur avec le fichier de log");
                    System.out.println("Bienvenue "+ mail +" sur Netflux la plateforme de VOD n°1 ");
                    return connection.login(mail, pwd);
                } else {
                    return this.connectToServer(reg);
                }
            }
        } catch (RemoteException e) {
            System.out.println(e.detail);
            System.out.println("Recommencer ...\n\n");
        } catch (NotBoundException e) {
            System.out.println("Aie aie aie, probleme avec l'objet ...");
            throw new RuntimeException(e);
        }
        return null;
    }

    private void printMovieList(List<MovieDesc> movieDescList) {
        for (int i = 0; i < movieDescList.size(); i++) {
            System.out.println(movieDescList.get(i));
        }
    }

    private void printBill(Bill bill){
        System.out.println("\n\n\n====================");
        System.out.println("Your bill :");
        System.out.println("For " + bill.getMovieName());
        System.out.println("Price : " + bill.getOutrageousPrice() + " $");
    }

    private String myChoice() {
        System.out.println("Entrer l'ISBN de votre choix :");
        String myISBN = CLAVIER.nextLine();
        return myISBN;
    }
}
