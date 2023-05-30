package org.currency_bot.parsers;

import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.currency_bot.constants.Currencies;
import org.currency_bot.dto.CurrenciesPack;
import org.currency_bot.dto.CurrencyHolder;
import org.currency_bot.dto.general.Monobank;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.currency_bot.constants.Constants.GSON;
import static org.currency_bot.constants.Constants.MONO_API_URL;

public class ParserMonobank {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final CurrenciesPack pack = new CurrenciesPack();
    private static final String CONTENT_TYPE = "Content-Type";
    public static final String JSON = "application/json; charset=UTF-8";

    public static CurrenciesPack getCurrencyFromMono() {
        Date date = new Date();

        if (pack.getLastUpdate().getTime() - date.getTime() < 300000) {
            return pack;
        }

        Type collectionTypeMono = TypeToken.getParameterized(List.class, Monobank.class).getType();
        List<Monobank> rez = getBankData(collectionTypeMono, MONO_API_URL);
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
                "Монобанк",
                Currencies.getById(temp.getCurrencyCodeA()),
                Currencies.getById(temp.getCurrencyCodeB()),
                temp.getRateBuy(),
                temp.getRateCross(),
                temp.getRateSell()
            );

            tempList.add(currencyHolder);
        }

        pack.setCurrencies(tempList);
        pack.setBankName("Монобанк");

        return pack;
    }

    public static List<Monobank> getBankData(Type collectionType, String url) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header(CONTENT_TYPE, JSON)
            .GET()
            .build();
        return currencyRequest(request, collectionType);
    }

    @SneakyThrows
    private static List<Monobank> currencyRequest(HttpRequest request, Type type) {
        final HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return GSON.fromJson(response.body(), type);
    }

}


