package org.currency_bot.services;

import org.currency_bot.dto.CurrencyHolder;
import org.currency_bot.dto.UsersSettings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinalSender {

    public static final Map<Long, UsersSettings> settings = new HashMap<>();

    public static String sendMessage(Long chatId) {
        String defaultReminder = settings.get(chatId).getReminder().equals("вимк.") ?
            "\n\nЩоденне оповіщення: вимк." : "\n\nЩоденне оповіщення о " + settings.get(chatId).getReminder() + ":00";

        List<CurrencyHolder> allUsers = settings.get(chatId).getCurrencies().stream().toList();

        Double[] courses = allUsers.stream()
            .map(currency -> new Double[]{currency.getBuy(), currency.getCross(), currency.getSale()})
            .flatMap(Arrays::stream)
            .toArray(Double[]::new);

        StringBuilder resultBuilder = new StringBuilder("Курс в " + settings.get(chatId).getBankMame());

        for (int i = 0; i < settings.get(chatId).getCurrencies().size(); i++) {
            String buy = courses[i * 3] != 0 ? "\nКупівля : " + String.format("%." + settings.get(chatId).getNumberOfDecimal() + "f", allUsers.get(i).getBuy()) : "";
            String cross = courses[i * 3 + 1] != 0 ? "\nКрос: " + String.format("%." + settings.get(chatId).getNumberOfDecimal() + "f", allUsers.get(i).getCross()) : "";
            String sale = courses[i * 3 + 2] != 0 ? "\nПродаж: " + String.format("%." + settings.get(chatId).getNumberOfDecimal() + "f", allUsers.get(i).getSale()) : "";

            resultBuilder.append("\n\nВалютна пара: ")
                .append(allUsers.get(i).getCurrency().name())
                .append("/UAH")
                .append(buy)
                .append(cross)
                .append(sale);
        }
        resultBuilder.append(defaultReminder);
        return resultBuilder.toString();
    }
}
