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
                            .text("Кількість знаків після коми")
                            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                            .replyMarkup(createButtonWithDigitsAfterDot())
                            .build());
                    break;
            }

            if (data.equals("TwoDigitsAfterDot") || data.equals("ThreeDigitsAfterDot") || data.equals("FourDigitsAfterDot")) {
                InlineKeyboardMarkup markup = createButtonWithDigitsAfterDot();

                markup.getKeyboard().forEach(buttons ->
                        buttons.stream()
                                .filter(button -> button.getCallbackData().equals(data))
                                .forEach(button -> button.setText(button.getText() + " ✅")));


                EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                editMarkup.setChatId(callbackQuery.getMessage().getChatId().toString());
                editMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
                editMarkup.setReplyMarkup(markup);

                execute(editMarkup);
            }
            System.out.println(data);
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
