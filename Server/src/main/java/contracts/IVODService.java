package contracts;

import contracts.movies.MovieDesc;

import java.util.List;

public interface IVODService {
    List<MovieDesc> viewCatalog();
    void playmovie(String isbn);
}
