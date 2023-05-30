package org.currency_bot.services;

import org.currency_bot.controller.TelegramBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.currency_bot.services.FinalSender.sendMessage;
import static org.currency_bot.services.FinalSender.settings;

public class Timer implements Runnable {

    public static void timer() throws InterruptedException, TelegramApiException {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime startDays = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime timeSendMessage = LocalDateTime.now().withMinute(0).withSecond(0);
        if (timeSendMessage.isBefore(startTime)) {
            timeSendMessage = timeSendMessage.plusHours(1);
        }
        Duration timeToSendMess = Duration.between(startTime, timeSendMessage);
        Thread.sleep(timeToSendMess.toMillis());
        Duration hour = Duration.between(startDays, timeSendMessage);
        for (Map.Entry userSet : settings.entrySet()) {
            Long key = (Long) userSet.getKey();
            Long chatId = settings.get(key).getChatId();
            int userNotificationTime = isDigit(settings.get(key).getReminder()) ? Integer.parseInt(settings.get(key).getReminder()) : 0;
            if (userNotificationTime == (int) hour.toHours()) {
                TelegramBot timer = new TelegramBot();
                timer.printMessage(chatId, sendMessage(chatId));
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
