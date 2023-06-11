package org.currency_bot.services;

import org.currency_bot.controller.TelegramBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.currency_bot.services.messages.InfoMessage.sendInfoMassage;
import static org.currency_bot.services.ReadAndWrite.readSavedSettings;


public class Timer implements Runnable {

    private static TelegramBot bot = new TelegramBot();

    public static void timer() throws InterruptedException, TelegramApiException {
        readSavedSettings(bot.getSettings());

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime startDays = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime timeSendMessage = LocalDateTime.now().withMinute(0).withSecond(0);
        if (timeSendMessage.isBefore(startTime)) {
            timeSendMessage = timeSendMessage.plusHours(1);
        }
        Duration timeToSendMess = Duration.between(startTime, timeSendMessage);
        Thread.sleep(timeToSendMess.toMillis());
        Duration hour = Duration.between(startDays, timeSendMessage);
        for (Map.Entry userSet : bot.getSettings().entrySet()) {
            Long key = (Long) userSet.getKey();
            Long chatId = bot.getSettings().get(key).getChatId();
            int userNotificationTime = isDigit(bot.getSettings().get(key).getReminder()) ? Integer.parseInt(bot.getSettings().get(key).getReminder()) : 0;
            if (userNotificationTime == (int) hour.toHours()) {
                TelegramBot timer = new TelegramBot();
                timer.printMessageByReminder(chatId, sendInfoMassage(bot.getSettings(), chatId));
            }
        }
    }

    private static boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                timer();
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
