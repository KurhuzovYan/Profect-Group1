package org.currency_bot.controller;

import lombok.Data;
import lombok.SneakyThrows;
import org.currency_bot.dto.settings.UsersSettings;
import org.currency_bot.services.commands.StartCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

import static org.currency_bot.constants.Currencies.*;
import static org.currency_bot.services.messages.BankMessage.sendBankMassage;
import static org.currency_bot.services.messages.CurrenciesMessage.sendCurrenciesMassage;
import static org.currency_bot.services.messages.NumberOfDecimalMessage.sendNumbersMassage;
import static org.currency_bot.services.messages.SettingsMessage.sendSettingsMassage;
import static org.currency_bot.services.messages.InfoMessage.sendInfoMassage;
import static org.currency_bot.dto.settings.UsersSettings.createUserWithDefaultSettings;
import static org.currency_bot.services.ReadAndWrite.readSavedSettings;
import static org.currency_bot.services.ReadAndWrite.writeSettingsToFile;
import static org.currency_bot.dto.settings.SettingsChanger.*;
import static org.currency_bot.util.ButtonCreater.*;
import static org.currency_bot.util.MarkupUtils.*;

@Data
public class TelegramBot extends TelegramLongPollingCommandBot {

    private Map<Long, UsersSettings> settings;
    private Map<Long, Boolean> checker;

    public TelegramBot() {
        register(new StartCommand());
        settings = new HashMap<>();
        checker = new HashMap<>();
    }

    @Override
    @SneakyThrows
    public void processNonCommandUpdate(Update update) {
        readSavedSettings(settings);
        if (update.hasCallbackQuery()) {
            final Date currentDate = new Date();
            final Long idFromCallbackQuery = update.getCallbackQuery().getFrom().getId();
            final CallbackQuery callbackQuery = update.getCallbackQuery();
            final String data = callbackQuery.getData();
            final InlineKeyboardMarkup currentMarkup = callbackQuery.getMessage().getReplyMarkup();

            checkAvailableUser(idFromCallbackQuery);

            switch (data) {
                case "Info" -> execute(getInlineKeyboardMarkup(update, sendInfoMassage(settings, idFromCallbackQuery), createCommonButtons()));
                case "Settings" -> execute(sendSettingsMassage(update));
                case "NumberOfDecimal" -> execute(sendNumbersMassage(settings, idFromCallbackQuery, update));
                case "Bank" -> execute(sendBankMassage(settings, idFromCallbackQuery, update));
                case "Currencies" -> {
                    execute(sendCurrenciesMassage(settings, idFromCallbackQuery, update));
                    checker.put(idFromCallbackQuery, true);
                }
                case "Time" -> execute(getReplyKeyboardMarkup(update, "Оберіть час щоденного оповіщення", createReminderButtons()));
                case "Confirm" -> execute(getDeleteMessage(idFromCallbackQuery, update));
            }

            if (List.of("2", "3", "4").contains(data)) {
                printInformation(callbackQuery, createButtonsWithNumberOfDecimalPlaces());
                changeNumberOfDecimal(settings, idFromCallbackQuery, data);
            } else if (List.of("ПриватБанк", "Монобанк", "НБУ").contains(data)) {
                printInformation(callbackQuery, createButtonsWithBanks());
                changeBankSettings(settings, idFromCallbackQuery, data, currentDate);
            } else if (List.of(USD.name(), EUR.name(), GBP.name()).contains(data)) {
                changeCurrenciesSettings(currentMarkup, settings, idFromCallbackQuery, data, currentDate);
                execute(getEditMessageReplyMarkup(currentMarkup, callbackQuery));
            }

        } else if (update.getMessage().hasText()) {
            final Long idFromUpdateMessage = update.getMessage().getChat().getId();
            final String textFromMessage = update.getMessage().getText();

            checkAvailableUser(idFromUpdateMessage);

            if (List.of("9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20").contains(textFromMessage)) {
                settings.entrySet().stream()
                    .filter(entry -> entry.getValue().getChatId() == idFromUpdateMessage)
                    .forEach(entry -> entry.getValue().setReminder(textFromMessage));
                String currentReminder = "\nЩоденне оповіщення о " + settings.get(idFromUpdateMessage).getReminder() + ":00";
                execute(getMessageWithFinalSettings(settings, idFromUpdateMessage, currentReminder));
            } else if (textFromMessage.equals("Вимкнути оповіщення")) {
                execute(getMessageWithFinalSettings(settings, idFromUpdateMessage, "\nЩоденне оповіщення: вимк."));
                settings.get(idFromUpdateMessage).setReminder("вимк.");
            }
        }
        writeSettingsToFile(settings);
    }

    @SneakyThrows
    private void printInformation(CallbackQuery callbackQuery, InlineKeyboardMarkup markup) {
        handler(callbackQuery.getData(), markup);
        execute(getEditMessageReplyMarkup(markup, callbackQuery));
    }

    private void checkAvailableUser(Long chatId) {
        if (!settings.containsKey(chatId)) {
            createUserWithDefaultSettings(chatId, settings, checker);
        }
    }

    private static void handler(String data, InlineKeyboardMarkup markup) {
        markup.getKeyboard()
            .forEach(buttons -> buttons.stream()
                .filter(button -> button.getCallbackData().equals(data))
                .forEach(button -> button.setText(button.getText() + " ✅"))
            );
    }

    public void printMessageByReminder(Long chatID, String messageText) throws TelegramApiException {
        execute(SendMessage.builder()
            .text(messageText)
            .chatId(chatID)
            .build());
    }

    @Override
    public String getBotUsername() {
        return System.getenv("BOT_NAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }
}
