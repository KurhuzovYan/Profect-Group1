package dto;

import constants.Currencies;
import lombok.Data;

import java.util.Date;

@Data
public class CurrencyHolder {
    private Date currencyDate;
    private String bankName;
    private Currencies currency, baseCurrency;
    private double buy, cross, sale;


    public CurrencyHolder(Date currencyDate, String bankName, Currencies currency, Currencies baseCurrency, double buy, double cross, double sale) {
        this.currencyDate = currencyDate;
        this.bankName = bankName;
        this.currency = currency;
        this.baseCurrency = baseCurrency;
        this.buy = buy;
        this.cross = cross;
        this.sale = sale;
    }

    @Override
    public String toString() {
        return String.valueOf(currency) + "/UAH";
    }
}
