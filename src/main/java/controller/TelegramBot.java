package controller;

import static constants.Constants.*;
import static util.ButtonCreater.*;

import lombok.SneakyThrows;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import services.commands.StartCommand;
import util.ButtonCreater;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


public class TelegramBot extends TelegramLongPollingCommandBot {

    private ButtonCreater buttonCreater;

    public TelegramBot() {
        register(new StartCommand());
    }

    @Override
    @SneakyThrows
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            final CallbackQuery callbackQuery = update.getCallbackQuery();
            final String data = callbackQuery.getData();
            final Message message = update.getMessage();


            switch (data) {
                case "1":
                    execute(SendMessage.builder()
                            .text("Курс в ПриватБанк: USD/UAH \nПокупка: 36.56 \nПродажа: 37.45")
                            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                            .replyMarkup(createCommonButtons())
                            .build());
                    break;
                case "2":
                    List<List<InlineKeyboardButton>> buttonsCase2 = new ArrayList<>();
                    List<InlineKeyboardButton> buttons1 = new ArrayList<>();
                    List<InlineKeyboardButton> buttons2 = new ArrayList<>();
                    List<InlineKeyboardButton> buttons3 = new ArrayList<>();
                    List<InlineKeyboardButton> buttons4 = new ArrayList<>();
                    buttons1.add(getInlineKeyboardButton("Кількість знаків після коми", "3"));
                    buttons2.add(getInlineKeyboardButton("Банк", "4"));
                    buttons3.add(getInlineKeyboardButton("Валюти", "5"));
                    buttons4.add(getInlineKeyboardButton("Час оповіщень", "6"));
                    buttonsCase2.add(buttons1);
                    buttonsCase2.add(buttons2);
                    buttonsCase2.add(buttons3);
                    buttonsCase2.add(buttons4);

                    execute(SendMessage.builder()
                            .text("Налаштування")
                            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttonsCase2).build())
                            .build());
            }
        }
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
