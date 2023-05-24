package dto.general;

import lombok.Data;

@Data
public class PrivatBank {
    private String ccy;
    private String base_ccy;
    private double buy;
    private double sale;

}
