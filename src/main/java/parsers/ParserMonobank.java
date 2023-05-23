package parsers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.Currencies;
import dto.CurrencyHolder;
import dto.general.Monobank;
import dto.CurrenciesPack;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ParserMonobank {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();
    private static final String CONTENT_TYPE = "Content-Type";
    public static final String JSON = "application/json; charset=UTF-8";

    public CurrenciesPack getCurrencyFromMono(CurrenciesPack pack) {
        Date date = new Date();

        if (pack.getLastUpdate().getTime() - date.getTime() < 300000) {
            return pack;
        }


        String monoURL = "https://api.monobank.ua/bank/currency";

        Type collectionTypeMono = new TypeToken<Collection<Monobank>>() {
        }.getType();
        ArrayList<Monobank> rez = getBankData(collectionTypeMono, monoURL);
        List<Monobank> needed = rez.stream()
                .filter(o -> o.getCurrencyCodeA() == 840 & o.getCurrencyCodeB() == 980
                        | o.getCurrencyCodeA() == 978 & o.getCurrencyCodeB() == 980
                        | o.getCurrencyCodeA() == 826)
                .toList();

        pack.setLastUpdate(new Date(date.getTime()));
        ArrayList<CurrencyHolder> tempList = new ArrayList<>();


        for (int i = 0; i < needed.size(); i++) {
            Monobank temp = needed.get(i);

            CurrencyHolder currencyHolder = new CurrencyHolder(
                    temp.getDate(),
                    temp.getRateSell(),
                    temp.getRateCross(),
                    temp.getRateBuy(),
                    Currencies.getById(temp.getCurrencyCodeB()),
                    Currencies.getById(temp.getCurrencyCodeA())
                    );

            tempList.add(currencyHolder);
        }

        pack.setCurrencies(tempList);
        pack.setBankName("MonoBank");

        return pack;
    }

    public <K> ArrayList<K> getBankData(Type collectionType, String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(CONTENT_TYPE, JSON)
                .GET()
                .build();
        return currencyRequest(request, collectionType);
    }

    private <E> E currencyRequest(HttpRequest request, Type type) {
        final HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return GSON.fromJson(response.body(), type);
    }

}


