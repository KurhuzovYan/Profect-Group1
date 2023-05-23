package dto.general;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class NBU {

    private int r030;
    private String txt;
    private double rate;
    private String cc;
    private String exchangedate;

    @Override
    public String toString() {
        return "NBU{" +
                "r030=" + r030 +
                ", txt='" + txt + '\'' +
                ", rate=" + rate +
                ", cc='" + cc + '\'' +
                ", exchangedate='" + exchangedate + '\'' +
                '}';
    }
}
