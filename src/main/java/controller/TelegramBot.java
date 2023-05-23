package controller;

import static constants.Constants.*;
import static util.ButtonCreater.*;

import lombok.SneakyThrows;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import services.commands.StartCommand;


import static constants.Currencies.*;

public class TelegramBot extends TelegramLongPollingCommandBot {

    public TelegramBot() {
        register(new StartCommand());
    }

    @Override
    @SneakyThrows
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            final CallbackQuery callbackQuery = update.getCallbackQuery();
            final String data = callbackQuery.getData();

            switch (data) {
                case "1":
                    execute(SendMessage.builder()
                            .text("Курс в ПриватБанк: USD/UAH \nПокупка: 36.56 \nПродажа: 37.45")
                            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                            .replyMarkup(createCommonButtons())
                            .build());
                    break;
                case "2":
                    execute(SendMessage.builder()
                            .text("Налаштування")
                            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                            .replyMarkup(createSettingsButtons())
                            .build());
                    break;
                case "3":
                    execute(SendMessage.builder()
                            .text("Оберіть кількість знаків після коми")
                            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                            .replyMarkup(createButtonsWithNumberOfDecimalPlaces())
                            .build());
                    break;
                case "4":
                    execute(SendMessage.builder()
                            .text("Оберіть необхідний банк")
                            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                            .replyMarkup(createButtonsWithBanks())
                            .build());
                    break;
                case "5":
                    execute(SendMessage.builder()
                            .text("Оберіть необхідну валюту або декілька")
                            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                            .replyMarkup(createButtonsWithCurrencies())
                            .build());
                    break;
            }

            if (data.equals("TwoDigitsAfterDot") || data.equals("ThreeDigitsAfterDot") || data.equals("FourDigitsAfterDot")) {
                InlineKeyboardMarkup markup = createButtonsWithNumberOfDecimalPlaces();

                markup.getKeyboard().forEach(buttons ->
                        buttons.stream()
                                .filter(button -> button.getCallbackData().equals(data))
                                .forEach(button -> button.setText(button.getText() + " ✅")));

                execute(getEditMessageReplyMarkup(markup, callbackQuery));

            } else if (data.equals(USD.name()) || data.equals(EUR.name()) || data.equals(GBP.name())) {
                InlineKeyboardMarkup replyMarkup = callbackQuery.getMessage().getReplyMarkup();

                replyMarkup.getKeyboard().forEach(buttons ->
                        buttons.stream()
                                .filter(button -> button.getCallbackData().equals(data))
                                .forEach(button -> {
                                    if (button.getText().equals(data)) {
                                        button.setText(data + " ✅");
                                    } else {
                                        button.setText(data);
                                    }
                                }));

                execute(getEditMessageReplyMarkup(replyMarkup, callbackQuery));
            } else if (data.equals("ПриватБанк") || data.equals("Монобанк") || data.equals("НБУ")) {
                InlineKeyboardMarkup markup = createButtonsWithBanks();

                markup.getKeyboard().forEach(buttons ->
                        buttons.stream()
                                .filter(button -> button.getCallbackData().equals(data))
                                .forEach(button -> button.setText(button.getText() + " ✅")));

                execute(getEditMessageReplyMarkup(markup, callbackQuery));
            }
        }

    }

    public static EditMessageReplyMarkup getEditMessageReplyMarkup(InlineKeyboardMarkup markup, CallbackQuery
            callbackQuery) {
        return EditMessageReplyMarkup.builder()
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .replyMarkup(markup)
                .build();
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

}
