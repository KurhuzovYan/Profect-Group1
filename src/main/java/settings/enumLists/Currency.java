package settings.enumLists;

import java.util.ArrayList;
import java.util.List;

public enum Currency {
    USD("USD", false),
    EUR("EUR", false),
    GBP("GBP", false);
    private String currencyName;
    private boolean currencySelect;

    Currency(String currency, boolean select) {
        this.currencyName = currency;
        this.currencySelect = select;
    }

    public static List<Currency> getSelectedCurrencyList() {
        List<Currency> selectedCurrency = new ArrayList<>();
        for (Currency curr : Currency.values()) {
            if (curr.currencySelect) {
                selectedCurrency.add(curr);
            }
        }
        return selectedCurrency;
    }

    public static Currency convertToEnum(String text) {
        for (Currency currency : Currency.values()) {
            if (currency.name().equals(text)) {
                return currency;
            }
        }
        return null;
    }

    public String getCurrencyName() {
        return currencyName;
    }
}
