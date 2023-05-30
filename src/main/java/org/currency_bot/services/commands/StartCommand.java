package org.currency_bot.services.commands;

import lombok.SneakyThrows;
import org.currency_bot.util.ButtonCreater;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;


public class StartCommand extends BotCommand {

    public StartCommand() {
        super("start", "description");
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        SendMessage message = new SendMessage();

        message.setText("Вітаю, " + user.getFirstName() + " \uD83D\uDC4B\uD83C\uDFFB " +
                "\nЦей бот допоможе відстежувати актуальні курси валют обранних Вами банків." +
                "\n\nОберіть наступну дію \uD83D\uDC47\uD83C\uDFFB");
        message.setChatId(chat.getId());
        message.setReplyMarkup(ButtonCreater.createCommonButtons());

        absSender.execute(message);
    }
}
