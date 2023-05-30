package org.currency_bot.parsers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.currency_bot.constants.Currencies;
import org.currency_bot.dto.CurrenciesPack;
import org.currency_bot.dto.CurrencyHolder;
import org.currency_bot.dto.general.NBU;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.currency_bot.constants.Constants.CLIENT;
import static org.currency_bot.constants.Constants.NBU_API_URL;

public class ParserNBU {

    private static final CurrenciesPack pack = new CurrenciesPack();

    public static CurrenciesPack getCurrencyFromNBU() {
        Date date = new Date();
        if (pack.getLastUpdate().getTime() - date.getTime() < 300000) return pack;

        pack.setLastUpdate(new Date(date.getTime()));
        pack.setBankName("НБУ");

        List<CurrencyHolder> collect = getCommonCurrencies().stream()
            .map(cur -> new CurrencyHolder(
                date,
                "НБУ",
                Currencies.getById(cur.getR030()),
                Currencies.UAH,
                0,
                cur.getRate(),
                0))
            .collect(Collectors.toList());
        pack.setCurrencies(collect);

        return pack;
    }

    public static List<NBU> getCommonCurrencies() {
        List<NBU> currencies = parseNBU();
        return currencies.stream()
            .filter(currency -> currency.getCc().equals(Currencies.USD.name()) ||
                currency.getCc().equals(Currencies.EUR.name()) ||
                currency.getCc().equals(Currencies.GBP.name()))
            .collect(Collectors.toList());
    }

    @SneakyThrows
    private static List<NBU> parseNBU() {

        HttpRequest request = HttpRequest
            .newBuilder(URI.create(NBU_API_URL))
            .GET()
            .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        Type type = TypeToken.getParameterized(List.class, NBU.class).getType();
        List<NBU> currencies = new Gson().fromJson(response.body(), type);

        return currencies;
    }
}
