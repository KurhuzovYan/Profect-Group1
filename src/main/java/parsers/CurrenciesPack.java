package parsers;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CurrenciesPack {
    private Date lastUpdate;
    private List<CurrencyHolder> currencies;

}
