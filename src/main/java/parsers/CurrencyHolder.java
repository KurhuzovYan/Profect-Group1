package parsers;

import lombok.Data;

import java.util.Date;

@Data
public class CurrencyHolder {
    private Date currencyDate;
    private double  saleRateNB, rateCross, purchaseRateNB;
    private Currencies baseCurrency, currency;


    public CurrencyHolder(Date currencyDate, double saleRateNB, double rateCross, double purchaseRateNB, Currencies baseCurrency, Currencies currency) {
        this.currencyDate = currencyDate;
        this.saleRateNB = saleRateNB;
        this.rateCross = rateCross;
        this.purchaseRateNB = purchaseRateNB;
        this.baseCurrency = baseCurrency;
        this.currency = currency;
    }
}
