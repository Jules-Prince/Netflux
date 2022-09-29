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
import java.util.List;
import java.util.Scanner;

public class Client {
    public static final int PORT_CLIENTBOX = 10006;
    private List<MovieDesc> movieDescList;
    private IClientBox clientBox;
    private Bill bill;

    void run() throws RemoteException {
        Registry reg = LocateRegistry.getRegistry(2001);
        String isbn;
        clientBox = new ClientBox(PORT_CLIENTBOX);

        try {
            // Connection to Netflux
            IVODService vodService = (IVODService) reg.lookup("Netflux");

            // View catalog
            movieDescList = vodService.viewCatalog();
            this.printMovieList(movieDescList);

            // Client choice for the movie
            isbn = this.myChoice();

            // Play the movie
            bill = vodService.playmovie(isbn, clientBox);

            // Bill
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
        System.out.println("====================");
        System.out.println("Your bill :");
        System.out.println("For " + bill.getMovieName());
        System.out.println("Price : " + bill.getOutrageousPrice() + " $");
    }

    private String myChoice(){
        Scanner clavier = new Scanner(System.in);
        System.out.println("Entrer l'ISBN de votre choix :");
        String myISBN = clavier.nextLine();
        return myISBN;
    }
}
