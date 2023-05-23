package parsers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.Currencies;
import dto.CurrenciesPack;
import dto.CurrencyHolder;
import dto.general.NBU;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static constants.Constants.*;
import static constants.Currencies.*;


public class ParserNBU {
    private static CurrenciesPack pack = new CurrenciesPack();

    public static CurrenciesPack getCurrencyFromNBU() {
        Date date = new Date();
        if (pack.getLastUpdate().getTime() - date.getTime() < 300000) return pack;

        pack.setLastUpdate(new Date(date.getTime()));
        pack.setBankName("НБУ");

        List<CurrencyHolder> collect = getCommonCurrencies().stream()
                .map(cur -> new CurrencyHolder(
                        cur.getRate(),
                        0,
                        UAH,
                      Currencies.getById(cur.getR030())))
                .collect(Collectors.toList());
        pack.setCurrencies(collect);

        return pack;
    }

    public static List<NBU> getCommonCurrencies() {
        List<NBU> currencies = parseNBU();
        return currencies.stream()
                .filter(currency -> currency.getCc().equals(USD.name()) ||
                        currency.getCc().equals(EUR.name()) ||
                        currency.getCc().equals(GBP.name()))
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
