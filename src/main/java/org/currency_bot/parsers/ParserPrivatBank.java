package org.currency_bot.parsers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.currency_bot.constants.Currencies;
import org.currency_bot.dto.CurrenciesPack;
import org.currency_bot.dto.CurrencyHolder;
import org.currency_bot.dto.general.PrivatBank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.currency_bot.constants.Constants.PRIVAT_API_URL;

public class ParserPrivatBank {

    private static final CurrenciesPack pack = new CurrenciesPack();

    public static List<PrivatBank> sendRequest(String endpoint) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(PRIVAT_API_URL + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        Type type = TypeToken.getParameterized(List.class, PrivatBank.class).getType();
        List<PrivatBank> currencies = new Gson().fromJson(String.valueOf(response), type);

        return currencies;
    }

    public static CurrenciesPack getCurrencyFromPrivatBank() {
        List<PrivatBank> currencies = sendRequest("&coursid=11");
        List<PrivatBank> gbp = sendRequest("&coursid=12").stream()
            .filter(currency -> currency.getCcy().equals(Currencies.GBP.name()))
            .collect(Collectors.toList());
        currencies.add(gbp.get(0));

        Date date = new Date();
        if (pack.getLastUpdate().getTime() - date.getTime() < 300000) return pack;

        pack.setLastUpdate(new Date(date.getTime()));
        pack.setBankName("ПриватБанк");

        List<CurrencyHolder> collect = currencies.stream()
            .map(cur -> new CurrencyHolder(
                date,
                "ПриватБанк",
                Currencies.getByName(cur.getCcy()),
                Currencies.UAH,
                cur.getBuy(),
                0,
                cur.getSale()))
            .collect(Collectors.toList());
        pack.setCurrencies(collect);

        return pack;
    }
}
