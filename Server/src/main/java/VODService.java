import contracts.Bill;
import contracts.IClientBox;
import contracts.IVODService;
import contracts.movies.MovieDesc;
import contracts.movies.MovieDescExtended;
import lombok.Getter;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;

@Getter
public class VODService extends UnicastRemoteObject implements IVODService {

    private List<MovieDesc> catalog;
    public VODService(int port) throws RemoteException {
        super(port);

        this.catalog = new ArrayList<>();

        this.initialize();
    }

    private void initialize(){
        MovieDesc film1 = new MovieDesc("Chucky", "1234", "Une poupée tueuse");
        MovieDescExtended film2 = new MovieDescExtended("Chucky 2", "5678", "Une poupée tueuse 2");
        this.catalog.add(film1);
        this.catalog.add(film2);
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

        if(movieRequested != null){
            System.out.println("Le client demande à voir le film" + movieRequested.getMovieName());
            clientBox.stream(new byte[]{0,1,2});
            return new Bill(movieRequested.getMovieName(), new BigInteger("9"));
        }

        System.out.println("LE FILM DU CLIENT N'EST PAS CONNUS DU CATALOGUE !!!");
        throw new RemoteException("Le film demander n'est pas présent dans la liste ...");
    }
}
