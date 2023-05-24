package parsers.impl;

import dto.CurrenciesPack;
import dto.CurrencyForUser;

import static parsers.ParserPrivatBank.*;
import static parsers.ParserNBU.*;
import static parsers.ParserMonobank.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HandlerForParsers {
    public static List<List<CurrencyForUser>> getCurrenciesForUser() {
        List<CurrenciesPack> currenciesPacks = Arrays.asList(getCurrencyFromPrivatBank(), getCurrencyFromNBU(), getCurrencyFromMono());
        List<List<CurrencyForUser>> currencies = new ArrayList<>();

        currenciesPacks.stream().forEach(pack -> {
            List<CurrencyForUser> current = pack.getCurrencies().stream().map(currency ->
                    new CurrencyForUser(pack.getBankName(),
                            currency.getCurrency(),
                            currency.getBaseCurrency(),
                            currency.getSaleRateNB(),
                            currency.getRateCross(),
                            currency.getPurchaseRateNB()))
                    .collect(Collectors.toList());
            currencies.add(current);
        });

        return currencies;
    }
}
