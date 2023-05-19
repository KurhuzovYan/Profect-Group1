package servises;

import com.google.gson.Gson;
import com.sun.jdi.ClassType;
import org.jsoup.select.Evaluator;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CurrencyRequest {
    private static final String URL = "https://api.monobank.ua/bank/currency";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();
    private static final String CONTENT_TYPE = "Content-Type";
    public static final String JSON = "application/json; charset=UTF-8";

    public static void main(String[] args) throws IOException, InterruptedException {
         getMonoData();
        System.out.println("new");
    }

    public static T getMonoData() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header(CONTENT_TYPE, JSON)
                .GET()
                .build();
        return getResponse(request, ResponseMono.class);
    }

    private static Object getResponse(HttpRequest request, E outputClassType) throws IOException, InterruptedException {
        final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return GSON.fromJson(response.body(), outputClassType.getClass());
    }

}


