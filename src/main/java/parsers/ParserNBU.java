package parsers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.general.NBU;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

import static constants.Constants.*;
import static constants.Currencies.*;

public class ParserNBU {
    @SneakyThrows
    public static List<NBU> parseNBU() {

        HttpRequest request = HttpRequest
                .newBuilder(URI.create(NBU_API_URL))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        Type type = TypeToken.getParameterized(List.class, NBU.class).getType();
        List<NBU> currencies = new Gson().fromJson(response.body(), type);

        return currencies;
    }

    public static List<NBU> getCommonCurrencies() {
        List<NBU> currencies = parseNBU();
        return currencies.stream()
                .filter(currency -> currency.getCc().equals(USD.name()) ||
                        currency.getCc().equals(EUR.name()) ||
                        currency.getCc().equals(GBP.name()))
                .collect(Collectors.toList());
    }

}
