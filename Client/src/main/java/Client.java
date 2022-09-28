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

    void run() throws RemoteException {
        Registry reg = LocateRegistry.getRegistry(2001);
        String isbn;
        clientBox = new ClientBox(PORT_CLIENTBOX);

        try {
            IVODService vodService = (IVODService) reg.lookup("Netflux");
            movieDescList = vodService.viewCatalog();
            this.print(movieDescList);
            isbn = this.myChoice();
            vodService.playmovie(isbn, clientBox);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (NoSuchObjectException e){
            System.out.println("Aie aie aie, probleme avec l'objet ...");
            e.printStackTrace();
        }



    }

    private void print(List<MovieDesc> movieDescList){
        for(int i = 0; i < movieDescList.size(); i++){
            System.out.println(movieDescList.get(i));
        }
    }

    private String myChoice(){
        Scanner clavier = new Scanner(System.in);
        System.out.println("Entrer l'ISBN de votre choix :");
        String myISBN = clavier.nextLine();
        return myISBN;
    }
}
