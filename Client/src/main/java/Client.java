import contracts.Bill;
import contracts.IClientBox;
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
    public static final int PORT_CLIENTBOX = 10002;
    private static List<MovieDesc> movieDescList;
    private static IClientBox clientBox;

    public static void main(String[] args) throws RemoteException {
        if (System.getSecurityManager()==null)
            System.setSecurityManager(new SecurityManager());

        Registry reg = LocateRegistry.getRegistry(2001);
        clientBox = new ClientBox(PORT_CLIENTBOX);
        Bill bill = null;
        String isbn;
        boolean valid = false;

        try {
            // Connection to Netflux
            IVODService vodService = (IVODService) reg.lookup("Netflux");

            // Get catalog
            movieDescList = vodService.viewCatalog();

            while (!valid) {
                // Print catalog
                printMovieList(movieDescList);

                // Client choice for the movie
                isbn = myChoice();

                // Play the movie
                try {
                    // get bill
                    bill = vodService.playmovie(isbn, clientBox);
                    valid = true;
                } catch (RemoteException e) {
                    System.out.println(e.detail);
                    System.out.println("Recommencer ...\n\n");
                }
            }

            // Print Bill
            printBill(bill);

        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (NoSuchObjectException e){
            System.out.println("Aie aie aie, probleme avec l'objet ...");
            e.printStackTrace();
        }
    }

    private static void printMovieList(List<MovieDesc> movieDescList){
        for(int i = 0; i < movieDescList.size(); i++){
            System.out.println(movieDescList.get(i));
        }
    }

    private static void printBill(Bill bill){
        System.out.println("====================");
        System.out.println("Your bill :");
        System.out.println("For " + bill.getMovieName());
        System.out.println("Price : " + bill.getOutrageousPrice() + " $");
    }

    private static String myChoice(){
        Scanner clavier = new Scanner(System.in);
        System.out.println("Entrer l'ISBN de votre choix :");
        String myISBN = clavier.nextLine();
        return myISBN;
    }
}
