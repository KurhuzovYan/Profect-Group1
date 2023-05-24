package constants;

import java.net.http.HttpClient;

public class Constants {

    public static final String BOT_TOKEN = "6078623462:AAFAG54arlBUkvzNbuuSFj7O7ipKkncj8v0";
    public static final String BOT_NAME = "Group1ExchangeRatesBot";
    public static final String PRIVAT_API_URL = "https://api.privatbank.ua/p24api/pubinfo?exchange";
    public static final String NBU_API_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    public static final String MONO_API_URL = "https://api.monobank.ua/bank/currency";
    public static final HttpClient CLIENT = HttpClient.newHttpClient();
}
 