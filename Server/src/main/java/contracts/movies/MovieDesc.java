package contracts.movies;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MovieDesc implements Serializable {

    private String movieName;
    private String isbn;
    private String synopsis;
    private byte[] datasMovie;
    private int cpt;

    public MovieDesc(String movieName, String isbn, String synopsis) {
        this.movieName = movieName;
        this.isbn = isbn;
        this.synopsis = synopsis;
        int

        this.cpt = 0;
        for(int i = 0; i < 100; i++){
            this.datasMovie
        }
    }

    @Override
    public String toString() {
        return this.getIsbn()
                + "   |   "
                + this.getMovieName()
                + "   |   "
                + this.getSynopsis();
    }
}
