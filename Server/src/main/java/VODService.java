import contracts.Bill;
import contracts.IClientBox;
import contracts.IVODService;
import contracts.movies.MovieDesc;
import lombok.Getter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;

@Getter
public class VODService extends UnicastRemoteObject implements IVODService{

    private final List<MovieDesc> catalog;
    public VODService(int port) throws RemoteException {
        super(port);

        this.catalog = new ArrayList<>();

        this.initialize();
    }

    private void initialize(){
        this.catalog.add(new MovieDesc("Chucky", "1234", "Une poupée tueuse"));
        this.catalog.add(new MovieDesc("Chucky2", "4567", "Une poupée tueuse 2"));
        this.catalog.add(new MovieDesc("Star Wars", "5282", "Sabre laser, Dark Vador, je suis ton pere"));
        this.catalog.add(new MovieDesc("Seigneur des anneaux", "6548", "Des elfes, des nains, des hommes, des orks... et un anneau"));
        this.catalog.add(new MovieDesc("Evil Dead 2", "6666", "Le film d'horreur culte"));
        this.catalog.add(new MovieDesc("La ligne verte", "8795", "De l'émotion, avec une fin discutable"));
        this.catalog.add(new MovieDesc("Lucky Luck", "9875", "Le ccow boy qui tirai plus vite que son ombre"));
        this.catalog.add(new MovieDesc("Casper", "1234", "Un fantome, un classique "));
    }

    @Override
    public List<MovieDesc> viewCatalog() {
        return this.getCatalog();
    }

    private MovieDesc searchMovieByIbsn(String ibsn){
        for(MovieDesc movie : this.getCatalog()){
            if(movie.getIsbn().equals(ibsn))
                return movie;
        }

        return null;
    }

    @Override
    public Bill playmovie(String isbn, IClientBox clientBox) throws RemoteException {
        MovieDesc movieRequested = this.searchMovieByIbsn(isbn);
        char finish = '*';
        boolean end = false;
        int cpt = 0;

        if(movieRequested != null){
            System.out.println("Le client demande à voir le film" + movieRequested.getMovieName());

            while(!end) {
                try {
                    Thread.sleep(400);
                    clientBox.stream(new byte[]{(byte) movieRequested.getSynopsis().charAt(cpt)});
                    cpt ++;
                    if(cpt == movieRequested.getSynopsis().length()) {
                        clientBox.stream(new byte[]{(byte) finish});
                        end = true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return new Bill(movieRequested.getMovieName(), new BigInteger("9"));
        }

        System.out.println("LE FILM DU CLIENT N'EST PAS CONNUS DU CATALOGUE !!!");
        throw new RemoteException("Le film demander n'est pas présent dans la liste ...");
    }
}
