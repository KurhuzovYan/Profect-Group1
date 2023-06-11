package org.currency_bot.services.messages;

import org.currency_bot.dto.CurrencyHolder;
import org.currency_bot.dto.settings.UsersSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Map;

import static org.currency_bot.util.ButtonCreater.createButtonsWithCurrencies;
import static org.currency_bot.util.MarkupUtils.getInlineKeyboardMarkup;

public class CurrenciesMessage {

    public static SendMessage sendCurrenciesMassage(Map<Long, UsersSettings> settings, Long id, Update update) {
        List<List<InlineKeyboardButton>> currencies = createButtonsWithCurrencies().getKeyboard();

        settings.get(id).getCurrencies()
            .stream()
            .map(CurrencyHolder::getCurrency)
            .map(Enum::name)
            .forEach(currencyName -> {
                for (List<InlineKeyboardButton> currency : currencies) {
                    currency.stream()
                        .filter(button -> button.getText().equals(currencyName))
                        .findFirst()
                        .ifPresent(button -> button.setText(button.getText() + " ✅"));
                }
            });

        InlineKeyboardMarkup build = InlineKeyboardMarkup.builder()
            .keyboard(currencies)
            .build();

        return getInlineKeyboardMarkup(update, "Оберіть необхідні валюти", build);
    }
}
