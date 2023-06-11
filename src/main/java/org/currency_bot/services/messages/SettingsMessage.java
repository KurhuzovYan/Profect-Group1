package org.currency_bot.services.messages;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.currency_bot.util.ButtonCreater.createSettingsButtons;
import static org.currency_bot.util.MarkupUtils.getInlineKeyboardMarkup;

public class SettingsMessage {

    public static SendMessage sendSettingsMassage(Update update) {
        return getInlineKeyboardMarkup(update, "Налаштування", createSettingsButtons());
    }
}
