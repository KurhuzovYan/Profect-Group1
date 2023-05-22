package parsers;

import lombok.Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@Data
public class CurrenciesPack {
    private Date lastUpdate = new Date(1970, Calendar.JANUARY,1);
    private String bankName;
    private ArrayList<CurrencyHolder> currencies;

}
