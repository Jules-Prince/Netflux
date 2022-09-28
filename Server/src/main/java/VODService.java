import contracts.IVODService;
import contracts.movies.MovieDesc;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Array;
import java.util.Arrays;
import java.util.List;

public class VODService extends UnicastRemoteObject implements IVODService {
    protected VODService(int port) throws RemoteException {
        super(port);
    }

    @Override
    public List<MovieDesc> viewCatalog() {
        return List.of(new MovieDesc("Chucky", "1234", "Une poupée tueuse"));
    }

    @Override
    public void playmovie(String isbn) {

    }
}
