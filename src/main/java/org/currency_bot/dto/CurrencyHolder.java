package org.currency_bot.dto;

import lombok.Data;
import org.currency_bot.constants.Currencies;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.currency_bot.parsers.ParserMonobank.getCurrencyFromMono;
import static org.currency_bot.parsers.ParserNBU.getCurrencyFromNBU;
import static org.currency_bot.parsers.ParserPrivatBank.getCurrencyFromPrivatBank;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyHolder holder = (CurrencyHolder) o;
        return Double.compare(holder.buy, buy) == 0 && Double.compare(holder.cross, cross) == 0 && Double.compare(holder.sale, sale) == 0 && Objects.equals(currencyDate, holder.currencyDate) && Objects.equals(bankName, holder.bankName) && currency == holder.currency && baseCurrency == holder.baseCurrency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyDate, bankName, currency, baseCurrency, buy, cross, sale);
    }

    @Override
    public String toString() {
        return currency + "/UAH";
    }

    public static CurrencyHolder getCurrencyHolder(String bankName, Currencies currencies, Date date) {
        List<CurrenciesPack> currentPack = Arrays.asList(getCurrencyFromPrivatBank(), getCurrencyFromNBU(), getCurrencyFromMono()).stream()
            .filter(pack -> pack.getBankName().equals(bankName))
            .collect(Collectors.toList());

        return currentPack.stream()
            .flatMap(pack -> pack.getCurrencies().stream())
            .filter(holder -> holder.getCurrency().equals(currencies))
            .map(cur -> new CurrencyHolder(date, bankName, currencies, Currencies.UAH, cur.getBuy(), cur.getCross(), cur.getSale()))
            .findFirst()
            .orElse(null);
    }
}
