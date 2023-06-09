package org.currency_bot.constants;

public enum Currencies {
    UAH("Українська Гривня", 980),
    USD("Доллар США", 840),
    EUR("Євро", 978),
    GBP("Британьський Фунт Стерлінгів", 826),
    UNKNOWN("UNKNOWN", 0);

    private final String currencyName;
    private final int currencyIdentifier;

    Currencies(String currencyName, int currencyIdentifier) {
        this.currencyName = currencyName;
        this.currencyIdentifier = currencyIdentifier;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public int getCurrencyIdentifier() {
        return currencyIdentifier;
    }

    public static Currencies getById(int currencyIdentifier) {
        for (Currencies e : values()) {
            if (e.currencyIdentifier == currencyIdentifier) return e;
        }
        return UNKNOWN;
    }

    public static Currencies getByName(String currency) {
        for (Currencies e : values()) {
            if (e.name().equals(currency)) return e;
        }
        return UNKNOWN;
    }
}
