package dto;

import constants.Currencies;
import lombok.Data;

@Data
public class CurrencyForUser {
    private String bankName;
    private Currencies currency, base;
    private double sale, cross, buy;

    public CurrencyForUser(String bankName, Currencies currency, Currencies base, double sale, double rate, double buy) {
        this.bankName = bankName;
        this.currency = currency;
        this.base = base;
        this.sale = sale;
        this.cross = rate;
        this.buy = buy;
    }

    @Override
    public String toString() {
        return String.valueOf(currency) + "/UAH";
    }
}
