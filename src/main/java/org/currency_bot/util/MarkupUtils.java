package org.currency_bot.util;

import lombok.SneakyThrows;
import org.currency_bot.dto.settings.UsersSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Map;

import static org.currency_bot.util.ButtonCreater.createCommonButtons;

public class MarkupUtils {
    @SneakyThrows
    public static SendMessage getMessageWithFinalSettings(Map<Long, UsersSettings> settings, Long chatId, String message) {
        return SendMessage.builder()
            .text("Дякуємо!\nВаші налаштування прийняті \uD83D\uDE4C\uD83C\uDFFB " +
                "\nОчікуйте повідомлення з актуальною інформацією:" +
                "\n\nКількість знаків після коми: " + settings.get(chatId).getNumberOfDecimal() +
                "\nБанк: " + settings.get(chatId).getBankMame() +
                "\nНеобхідні валюти: " + settings.get(chatId).getCurrencies() + message +
                "\n\nЩоб отримати інформацію одразу, написніть:\n \"Отримати інфо\" \uD83D\uDC47\uD83C\uDFFB")
            .chatId(chatId)
            .replyMarkup(createCommonButtons())
            .build();
    }

    public static InlineKeyboardMarkup getMarkupWithSelectedSettings(List<List<InlineKeyboardButton>> buttons, String textOfButton, String setText) {
        buttons.stream()
            .flatMap(List::stream)
            .filter(button -> button.getText().equals(textOfButton))
            .forEach(button -> button.setText(setText));

        return InlineKeyboardMarkup.builder()
            .keyboard(buttons)
            .build();
    }

    public static EditMessageReplyMarkup getEditMessageReplyMarkup(InlineKeyboardMarkup markup, CallbackQuery callbackQuery) {
        return EditMessageReplyMarkup.builder()
            .chatId(callbackQuery.getMessage().getChatId().toString())
            .messageId(callbackQuery.getMessage().getMessageId())
            .replyMarkup(markup)
            .build();
    }

    @SneakyThrows
    public static SendMessage getInlineKeyboardMarkup(Update update, String text, InlineKeyboardMarkup markup) {
        return SendMessage.builder()
            .text(text)
            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
            .replyMarkup(markup)
            .build();
    }

    @SneakyThrows
    public static SendMessage getReplyKeyboardMarkup(Update update, String text, ReplyKeyboardMarkup markup) {
        return SendMessage.builder()
            .text(text)
            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
            .replyMarkup(markup)
            .build();
    }

    public static DeleteMessage getDeleteMessage(Long id, Update update){
        return DeleteMessage.builder()
            .chatId(id)
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .build();
    }
}
