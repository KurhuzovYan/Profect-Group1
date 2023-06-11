package org.currency_bot.services.messages;

import org.currency_bot.dto.settings.UsersSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Map;

import static org.currency_bot.util.ButtonCreater.createButtonsWithNumberOfDecimalPlaces;
import static org.currency_bot.util.MarkupUtils.getInlineKeyboardMarkup;
import static org.currency_bot.util.MarkupUtils.getMarkupWithSelectedSettings;

public class NumberOfDecimalMessage {

    public static SendMessage sendNumbersMassage(Map<Long, UsersSettings> settings, Long id, Update update) {
        List<List<InlineKeyboardButton>> decimals = createButtonsWithNumberOfDecimalPlaces().getKeyboard();
        InlineKeyboardMarkup markupWithSelectedNumberOfDecimal = getMarkupWithSelectedSettings(decimals,
            String.valueOf(settings.get(id).getNumberOfDecimal()),
            settings.get(id).getNumberOfDecimal() + " ✅");
        return getInlineKeyboardMarkup(update, "Оберіть кількість знаків після коми", markupWithSelectedNumberOfDecimal);
    }
}
