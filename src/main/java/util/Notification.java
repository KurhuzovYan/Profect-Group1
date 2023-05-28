package util;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Notification extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = "6078623462:AAFAG54arlBUkvzNbuuSFj7O7ipKkncj8v0";

    @Override
    public String getBotUsername() {
        return "Group1ExchangeRatesBot";
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Обработка событий, если она нужна ?
    }

    public static ReplyKeyboardMarkup createReminderButtons() {
        List<KeyboardButton> listOfButtons = List.of("9", "10", "11", "12", "13", "14", "15", "16", "17", "18").stream()
                .map(button -> new KeyboardButton(button))
                .collect(Collectors.toList());

        List<KeyboardRow> rows = new ArrayList<>(
                Arrays.asList(new KeyboardRow(),
                        new KeyboardRow(),
                        new KeyboardRow(),
                        new KeyboardRow()
                ));

        for (int i = 0; i < listOfButtons.size(); i++) {
            if (i <= 2) rows.get(0).add(listOfButtons.get(i));
            else if (i >= 3 && i <= 5) rows.get(1).add(listOfButtons.get(i));
            else if (i >= 6 && i <= 8) rows.get(2).add(listOfButtons.get(i));
            else if (i >= 9 && i <= 10) rows.get(3).add(listOfButtons.get(i));
        }

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(rows);
        keyboardMarkup.setResizeKeyboard(true);

        return keyboardMarkup;
    }

    public void sendNotification(long chatId, String message) {
        SendMessage notificationMessage = new SendMessage();
        notificationMessage.setChatId(String.valueOf(chatId));
        notificationMessage.setText(message);
        notificationMessage.setReplyMarkup(createReminderButtons());

        try {
            execute(notificationMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
