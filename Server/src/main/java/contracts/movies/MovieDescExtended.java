package contracts.movies;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class MovieDescExtended extends MovieDesc{
    private String synopsis;
    private byte[] teaser;

    public MovieDescExtended(String movieName, String isbn, String synopsis) {
        super(movieName, isbn, synopsis);
    }

    @Override
    public String toString() {
        return "MovieDescExtended{" +
                ", ISBN='" + super.getIsbn() + '\'' +
                "synopsis='" + synopsis + '\'' +
                ", teaser=" + Arrays.toString(teaser) +
                '}';
    }
}
