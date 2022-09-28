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

    public MovieDesc(String movieName, String isbn, String synopsis) {
        this.movieName = movieName;
        this.isbn = isbn;
        this.synopsis = synopsis;
    }

    @Override
    public String toString() {
        return this.getIsbn()
                + "  -----  "
                + this.getMovieName()
                + " " + this.getSynopsis();
    }
}
