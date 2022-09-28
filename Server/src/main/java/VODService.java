import contracts.IVODService;
import contracts.movies.MovieDesc;
import lombok.Getter;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class VODService extends UnicastRemoteObject implements IVODService {

    private List<MovieDesc> catalog;
    public VODService(int port) throws RemoteException {
        super(port);

        this.catalog = new ArrayList<>();

        this.initialize();
    }

    private void initialize(){
        this.catalog.add(new MovieDesc("Chucky", "1234", "Une poupée tueuse"));
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
    public void playmovie(String isbn) {
        MovieDesc movieRequested = this.searchMovieByIbsn(isbn);
        if(movieRequested != null){
            System.out.println("Le client demande à voir le film" + movieRequested.getMovieName());
        }
        else {
            //TODO implements next error
            System.out.println("LE FILM DU CLIENT N'EST PAS CONNUS DU CATALOGUE !!!");
        }
    }
}
