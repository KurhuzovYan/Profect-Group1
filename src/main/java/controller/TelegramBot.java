package controller;

import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import static constants.Constants.*;


public class TelegramBot extends TelegramLongPollingCommandBot {

    @Override
    public void processNonCommandUpdate(Update update) {
        var msg = update.getMessage();
        System.out.println(msg.getText());
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
