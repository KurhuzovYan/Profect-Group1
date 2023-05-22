package parsers;

import lombok.Data;

import java.util.Currency;
import java.util.Date;

@Data
public class CurrencyHolder {
    private Date currencyDate;
    private double  saleRateNB, purchaseRateNB;
    private Currencies baseCurrency, currency;

    public CurrencyHolder() {
    }

    public CurrencyHolder(Date currencyDate, double saleRateNB, double purchaseRateNB, int baseCurrency, int currency) {
        this.currencyDate = currencyDate;
        this.saleRateNB = saleRateNB;
        this.purchaseRateNB = purchaseRateNB;
        this.baseCurrency = Currencies(baseCurrency);
        this.currency = new Currencies(currency);
    }
}
