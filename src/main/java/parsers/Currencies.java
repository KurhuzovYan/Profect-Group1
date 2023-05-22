package parsers;

public enum Currencies {
    UAH("Українська Гривня",0),
    USD("Доллар США",1),
    EUR("Євро",2),
    GBF("Британьський Фунт Стерлінгів",3);

    private String currencyName;
    private int currencyIdentifier;

    Currencies(String currencyName, int currencyIdentifier) {
        this.currencyName = currencyName;
        this.currencyIdentifier = currencyIdentifier;
    }
    Currencies(String currencyName) {
        this.currencyName = currencyName;
    }
    Currencies( int currencyIdentifier) {
        this.currencyIdentifier = currencyIdentifier;
    }


    public String getCurrencyName() {
        return currencyName;
    }

    public int getCurrencyIdentifier() {
        return currencyIdentifier;
    }
}
