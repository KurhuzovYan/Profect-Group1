package dto;

import constants.Currencies;
import lombok.Data;

@Data
public class CurrencyForUser {
    private String bankName;
    private Currencies currency;
    private Currencies base;
    private double sale;
    private double buy;

    public CurrencyForUser(String bankName, Currencies currency, Currencies base, double sale, double buy) {
        this.bankName = bankName;
        this.currency = currency;
        this.base = base;
        this.sale = sale;
        this.buy = buy;
    }
}
