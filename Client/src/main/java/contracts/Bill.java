package contracts;

import lombok.Getter;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
public class Bill implements Serializable {
    private String movieName;
    private BigInteger outrageousPrice;

    public Bill(String movieName, BigInteger outrageousPrice) {
        this.movieName = movieName;
        this.outrageousPrice = outrageousPrice;
    }
}
