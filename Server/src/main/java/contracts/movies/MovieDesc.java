package contracts.movies;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieDesc {

    private String movieName;
    private String isbn;
    private String synopsis;

    public MovieDesc(String movieName, String isbn, String synopsis) {
        this.movieName = movieName;
        this.isbn = isbn;
        this.synopsis = synopsis;
    }
}
