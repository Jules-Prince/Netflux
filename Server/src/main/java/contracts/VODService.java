package contracts;

import contracts.movies.MovieDesc;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class VODService extends UnicastRemoteObject implements IVODService{
    protected VODService(int port) throws RemoteException {
        super(port);
    }

    @Override
    public List<MovieDesc> viewCatalog() {
        return null;
    }

    @Override
    public void playmovie(String isbn) {

    }
}
