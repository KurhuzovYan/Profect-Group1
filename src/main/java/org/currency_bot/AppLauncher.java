package org.currency_bot;

import org.currency_bot.services.TelegramBotService;
import org.currency_bot.services.Timer;

public class AppLauncher {

    public static void main(String[] args) {
        Timer timer = new Timer();
        Thread messageInTime = new Thread(timer);
        messageInTime.start();
        TelegramBotService bot = new TelegramBotService();
    }
}
