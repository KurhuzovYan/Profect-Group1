import controller.TelegramBot;
import dto.UsersSettings;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

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
        for (Map.Entry userSet : UsersSettings.settings.entrySet()) {
            Long key = (Long) userSet.getKey();
            Long chatId = UsersSettings.settings.get(key).getChatId();
            int userNotificationTime = Integer.parseInt(UsersSettings.settings.get(key).getReminder());
            if (userNotificationTime == (int) hour.toHours()) {
                TelegramBot timer = new TelegramBot();
                timer.printMessage(chatId, UsersSettings.sendMessage(chatId));
            }
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
