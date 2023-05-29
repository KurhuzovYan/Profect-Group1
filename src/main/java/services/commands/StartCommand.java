package services.commands;

import lombok.SneakyThrows;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import static util.ButtonCreater.*;
import controller.TelegramBot;



public class StartCommand extends BotCommand {

    public StartCommand() {
        super("start", "description");
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        SendMessage message = new SendMessage();

        message.setText("Вітаю " + user.getFirstName() +
                ". \nЦей бот допоможе відстежувати актуальні курси валют." +
                "\nОберіть наступну дію \uD83D\uDC47\n");
        message.setChatId(chat.getId());
        message.setReplyMarkup(createCommonButtons());

        absSender.execute(message);
        TelegramBot.setInitialUserLevel(chat.getId());
    }
}
