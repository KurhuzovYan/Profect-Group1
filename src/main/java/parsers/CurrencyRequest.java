package parsers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;

class Test {
    public static void main(String[] args) {
        CurrencyRequest test = new CurrencyRequest();
        test.getCurrencyFromMono();
        System.out.println("TEST");
    }
}


public class CurrencyRequest {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();
    private static final String CONTENT_TYPE = "Content-Type";
    public static final String JSON = "application/json; charset=UTF-8";

    public void getCurrencyFromMono() {
        String monoURL = "https://api.monobank.ua/bank/currency";

        Type collectionTypeMono = new TypeToken<Collection<ResponseMono>>() {
        }.getType();
        ArrayList<ResponseMono> rez = getBankData(collectionTypeMono, monoURL);
        var nuznie = rez.stream()
                .filter(o -> o.getCurrencyCodeA() == 840 & o.getCurrencyCodeB() == 980
                        | o.getCurrencyCodeA() == 978 & o.getCurrencyCodeB() == 980
                        | o.getCurrencyCodeA() == 826)
                .toList();

        System.out.println(nuznie);

        System.out.println("TEST");
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


