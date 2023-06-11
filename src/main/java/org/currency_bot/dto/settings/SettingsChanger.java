package org.currency_bot.dto.settings;

import org.currency_bot.constants.Currencies;
import org.currency_bot.dto.CurrencyHolder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.currency_bot.dto.CurrencyHolder.getCurrencyHolder;

public class SettingsChanger {

    public static void changeNumberOfDecimal(Map<Long, UsersSettings> settings, Long chatId, String data) {
        settings.entrySet().stream()
            .filter(entry -> entry.getValue().getChatId() == chatId)
            .forEach(entry -> entry.getValue().setNumberOfDecimal(Integer.valueOf(data)));
    }

    public static void changeBankSettings(Map<Long, UsersSettings> settings, Long chatId, String data, Date date) {
        settings.get(chatId).setBankMame(data);

        Set<CurrencyHolder> updatedCurrencies = settings.get(chatId).getCurrencies().stream()
            .map(cur -> {
                cur.setBankName(data);
                return getCurrencyHolder(data, cur.getCurrency(), date);
            })
            .collect(Collectors.toSet());

        settings.entrySet().stream()
            .filter(entry -> entry.getValue().getChatId() == chatId)
            .forEach(entry -> entry.getValue().setCurrencies(updatedCurrencies));
    }

    public static void changeCurrenciesSettings(InlineKeyboardMarkup currentMarkup, Map<Long, UsersSettings> settings,
                                                Long chatId, String data, Date date) {
        currentMarkup.getKeyboard()
            .forEach(buttons -> buttons.stream()
                .filter(button -> button.getCallbackData().equals(data))
                .forEach(button -> {
                    if (button.getText().equals(data)) {
                        button.setText(data + " ✅");
                    } else {
                        button.setText(data);
                    }
                }));

        long count = settings.get(chatId).getCurrencies().stream()
            .filter(cur -> cur.getCurrency().name().equals(data))
            .count();

        if (count == 0) {
            CurrencyHolder current = getCurrencyHolder(settings.get(chatId).getBankMame(), Currencies.getByName(data), date);
            settings.get(chatId).getCurrencies().add(current);
        } else {
            List<CurrencyHolder> collect = settings.get(chatId).getCurrencies().stream()
                .filter(cur -> cur.getCurrency().name().equals(data))
                .collect(Collectors.toList());
            settings.get(chatId).getCurrencies().remove(collect.get(0));
        }
    }
}
