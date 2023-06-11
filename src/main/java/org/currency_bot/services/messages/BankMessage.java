package org.currency_bot.services.messages;

import org.currency_bot.dto.settings.UsersSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Map;

import static org.currency_bot.util.ButtonCreater.createButtonsWithBanks;
import static org.currency_bot.util.MarkupUtils.getInlineKeyboardMarkup;
import static org.currency_bot.util.MarkupUtils.getMarkupWithSelectedSettings;

public class BankMessage {

    public static SendMessage sendBankMassage(Map<Long, UsersSettings> settings, Long id, Update update) {
        List<List<InlineKeyboardButton>> banks = createButtonsWithBanks().getKeyboard();
        InlineKeyboardMarkup markupWithSelectedBank = getMarkupWithSelectedSettings(banks,
            settings.get(id).getBankMame(),
            settings.get(id).getBankMame() + " ✅");
        return getInlineKeyboardMarkup(update, "Оберіть необхідний банк", markupWithSelectedBank);
    }
}
