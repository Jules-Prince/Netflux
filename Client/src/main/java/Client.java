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
            IVODService vodService;
            IConnection connection = (IConnection) reg.lookup("Netflux");
            System.out.println("Entrez vos identifiant (mail pwd):");
            String logs = CLAVIER.nextLine();
            String mail = logs.split(" ")[0];
            String pwd = logs.split(" ")[1];
            if(connection.checkIfClientCredentialsExistYet(mail, pwd)){
                //Login est trouvé
                vodService = connection.login(mail, pwd);
                System.out.println("Vous etes connecté");
            } else {
                connection.signIn(mail, pwd);
                System.out.println("Vous etes inscrit");
                vodService = connection.login(mail, pwd);
                System.out.println("Vous etes connecté");
            }

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

        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (NoSuchObjectException e){
            System.out.println("Aie aie aie, probleme avec l'objet ...");
            e.printStackTrace();
        }
    }

    private void printMovieList(List<MovieDesc> movieDescList){
        for(int i = 0; i < movieDescList.size(); i++){
            System.out.println(movieDescList.get(i));
        }
    }

    private void printBill(Bill bill){
        System.out.println("\n\n\n====================");
        System.out.println("Your bill :");
        System.out.println("For " + bill.getMovieName());
        System.out.println("Price : " + bill.getOutrageousPrice() + " $");
    }

    private String myChoice(){
        System.out.println("Entrer l'ISBN de votre choix :");
        String myISBN = CLAVIER.nextLine();
        return myISBN;
    }
}
