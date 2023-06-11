package org.currency_bot.services.messages;

import org.currency_bot.dto.CurrencyHolder;
import org.currency_bot.dto.settings.UsersSettings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class InfoMessage {

    public static String sendInfoMassage(Map<Long, UsersSettings> settings, Long id) {
        String defaultReminder = settings.get(id).getReminder().equals("вимк.") ?
            "\n\nЩоденне оповіщення: вимк." : "\n\nЩоденне оповіщення о " + settings.get(id).getReminder() + ":00";

        List<CurrencyHolder> allUsers = settings.get(id).getCurrencies().stream().toList();

        Double[] courses = allUsers.stream()
            .map(currency -> new Double[]{currency.getBuy(), currency.getCross(), currency.getSale()})
            .flatMap(Arrays::stream)
            .toArray(Double[]::new);

        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 0; i < settings.get(id).getCurrencies().size(); i++) {
            String buy = courses[i * 3] != 0 ? "\nКупівля : " + String.format("%." + settings.get(id).getNumberOfDecimal() + "f", allUsers.get(i).getBuy()) : "";
            String cross = courses[i * 3 + 1] != 0 ? "\nКрос: " + String.format("%." + settings.get(id).getNumberOfDecimal() + "f", allUsers.get(i).getCross()) : "";
            String sale = courses[i * 3 + 2] != 0 ? "\nПродаж: " + String.format("%." + settings.get(id).getNumberOfDecimal() + "f", allUsers.get(i).getSale()) : "";

            resultBuilder.append("\n\nВалютна пара: ")
                .append(allUsers.get(i).getCurrency().name()).
                append("/UAH")
                .append(buy)
                .append(cross)
                .append(sale);
        }
       String result =  "Курс в " + settings.get(id).getBankMame() + resultBuilder + defaultReminder;
       return result;
    }
}
