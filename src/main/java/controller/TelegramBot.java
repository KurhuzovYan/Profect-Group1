package controller;

import static constants.Constants.*;
import static util.ButtonCreater.*;

import constants.Currencies;
import dto.CurrenciesPack;
import dto.CurrencyHolder;
import dto.UsersSettings;
import lombok.Data;
import lombok.SneakyThrows;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import services.commands.StartCommand;


import static parsers.ParserNBU.*;
import static parsers.ParserMonobank.*;
import static parsers.ParserPrivatBank.*;

import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

import static constants.Currencies.*;

@Data
public class TelegramBot extends TelegramLongPollingCommandBot {

    private UsersSettings settings;
    private boolean check;
    private static CurrenciesPack pack;
    private static Date date = new Date();

    public TelegramBot() {
        register(new StartCommand());

        String defaultBank = "ПриватБанк";
        CurrencyHolder defaultCurrency1 = getCurrencyHolder(defaultBank, USD);
        CurrencyHolder defaultCurrency2 = getCurrencyHolder(defaultBank, EUR);
        settings = new UsersSettings(2, defaultBank, Set.of(defaultCurrency1, defaultCurrency2), "9");
        check = true;
    }

    @Override
    @SneakyThrows
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            final CallbackQuery callbackQuery = update.getCallbackQuery();
            final String data = callbackQuery.getData();

            switch (data) {
                case "Info":
                    String defaultReminder = settings.getReminder().equals("Вимкнути оповіщення") ?
                            "\n\nЩоденне сповіщення: викл." : "\n\nЩоденне сповіщення о " + settings.getReminder() + ":00";

                    List<CurrencyHolder> allUsers = settings.getCurrencies().stream()
                            .collect(Collectors.toList());

                    Double[] courses = allUsers.stream()
                            .map(currency -> new Double[]{currency.getBuy(), currency.getCross(), currency.getSale()})
                            .flatMap(Arrays::stream)
                            .toArray(Double[]::new);

                    StringBuilder resultBuilder = new StringBuilder();

                    for (int i = 0; i < settings.getCurrencies().size(); i++) {
                        String buy = courses[i * 3] != 0 ? "\nКупівля : " + String.format("%." + settings.getNumberOfDecimal() + "f", allUsers.get(i).getBuy()) : "";
                        String cross = courses[i * 3 + 1] != 0 ? "\nКрос: " + String.format("%." + settings.getNumberOfDecimal() + "f", allUsers.get(i).getCross()) : "";
                        String sale = courses[i * 3 + 2] != 0 ? "\nПродаж: " + String.format("%." + settings.getNumberOfDecimal() + "f", allUsers.get(i).getSale()) : "";

                        resultBuilder.append("\n\nВалютна пара: ")
                                .append(allUsers.get(i).getCurrency().name())
                                .append("/UAH")
                                .append(buy)
                                .append(cross)
                                .append(sale);
                    }

                    getInlineKeyboardMarkup(update, "Курс в " + settings.getBankMame() + resultBuilder + defaultReminder, createCommonButtons());
                    break;
                case "Settings":
                    getInlineKeyboardMarkup(update, "Налаштування", createSettingsButtons());
                    check = true;
                    break;
                case "NumberOfDecimal":
                    getInlineKeyboardMarkup(update, "Оберіть кількість знаків після коми", createButtonsWithNumberOfDecimalPlaces());
                    break;
                case "Bank":
                    getInlineKeyboardMarkup(update, "Оберіть необхідний банк", createButtonsWithBanks());
                    break;
                case "Currencies":
                    getInlineKeyboardMarkup(update, "Оберіть необхідні валюти", createButtonsWithCurrencies());
                    break;
                case "Time":
                    getReplyKeyboardMarkup(update, "Оберіть час щоденного сповіщення", createReminderButtons());
                    break;
            }

            if (data.equals("2") || data.equals("3") || data.equals("4")) {
                InlineKeyboardMarkup markup = createButtonsWithNumberOfDecimalPlaces();
                handler(data, markup);
                execute(getEditMessageReplyMarkup(markup, callbackQuery));

                String current = markup.getKeyboard().stream()
                        .flatMap(buttons -> buttons.stream()
                                .filter(button -> button.getText().equals(data + " ✅"))
                                .map(button -> button.getText().replaceAll(" ✅", "")))
                        .collect(Collectors.joining());

                settings.setNumberOfDecimal(Integer.valueOf(current));

            } else if (data.equals(USD.name()) || data.equals(EUR.name()) || data.equals(GBP.name())) {
                if (check) {
                    Set<CurrencyHolder> newSet = new HashSet<>();
                    settings.setCurrencies(newSet);
                    check = false;
                }

                InlineKeyboardMarkup replyMarkup = callbackQuery.getMessage().getReplyMarkup();

                replyMarkup.getKeyboard().forEach(buttons ->
                        buttons.stream()
                                .filter(button -> button.getCallbackData().equals(data))
                                .forEach(button -> {
                                    if (button.getText().equals(data)) {
                                        button.setText(data + " ✅");
                                    } else {
                                        button.setText(data);
                                    }
                                }));

                CurrencyHolder current = getCurrencyHolder(settings.getBankMame(), getByName(data));

                boolean matcher = settings.getCurrencies().stream()
                        .allMatch(currencies -> !currencies.getCurrency().name().equals(data));

                if (matcher) settings.getCurrencies().add(current);
                else settings.getCurrencies().remove(current);

                execute(getEditMessageReplyMarkup(replyMarkup, callbackQuery));

            } else if (data.equals("ПриватБанк") || data.equals("Монобанк") || data.equals("НБУ")) {
                InlineKeyboardMarkup markup = createButtonsWithBanks();
                handler(data, markup);
                execute(getEditMessageReplyMarkup(markup, callbackQuery));
                settings.setBankMame(data);

                Set<CurrencyHolder> updatedCurrencies = settings.getCurrencies().stream()
                        .map(cur -> {
                            cur.setBankName(data);
                            return getCurrencyHolder(data, cur.getCurrency());
                        })
                        .collect(Collectors.toSet());

                settings.setCurrencies(updatedCurrencies);
            }
        } else if (update.getMessage().hasText()) {
            settings.setReminder(update.getMessage().getText());
            String updateReminder = settings.getReminder().equals("Вимкнути оповіщення") ?
                    "\nЩоденне сповіщення: викл." : "\nЩоденне сповіщення о " + settings.getReminder() + ":00";

            execute(SendMessage.builder()
                    .text("Очікуйте повідомлення з обранними налаштуваннями: \n" +
                            "\nКількість знаків після коми: " + settings.getNumberOfDecimal() +
                            "\nБанк: " + settings.getBankMame() +
                            "\nНеобхідні валюти: " + settings.getCurrencies() + updateReminder +
                            "\n\nЩоб отримати інформацію одразу, написніть:\n \"Отримати інфо\" \uD83D\uDC47")
                    .chatId(update.getMessage().getChatId())
                    .replyMarkup(createCommonButtons())
                    .build());
        }

        try (FileWriter writer = new FileWriter("users/Entity-" + update.getMessage().getFrom().getUserName() + ".json")) {
            writer.write(GSON.toJson(settings));
        }
    }

    private CurrencyHolder getCurrencyHolder(String bankName, Currencies currencies) {
        List<CurrenciesPack> currentPack = Arrays.asList(getCurrencyFromPrivatBank(), getCurrencyFromNBU(), getCurrencyFromMono()).stream()
                .filter(pack -> pack.getBankName().equals(bankName))
                .collect(Collectors.toList());

        CurrencyHolder currencyHolder = currentPack.stream()
                .flatMap(pack -> pack.getCurrencies().stream())
                .filter(holder -> holder.getCurrency().equals(currencies))
                .map(cur -> new CurrencyHolder(
                        date,
                        bankName,
                        currencies,
                        UAH,
                        cur.getBuy(),
                        cur.getCross(),
                        cur.getSale()))
                .findFirst()
                .orElse(null);

        return currencyHolder;
    }

    private static void handler(String data, InlineKeyboardMarkup markup) {
        markup.getKeyboard().forEach(buttons ->
                buttons.stream()
                        .filter(button -> button.getCallbackData().equals(data))
                        .forEach(button -> button.setText(button.getText() + " ✅")));
    }

    @SneakyThrows
    private void getInlineKeyboardMarkup(Update update, String text, InlineKeyboardMarkup markup) {
        execute(SendMessage.builder()
                .text(text)
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .replyMarkup(markup)
                .build());
    }

    @SneakyThrows
    private void getReplyKeyboardMarkup(Update update, String text, ReplyKeyboardMarkup markup) {
        execute(SendMessage.builder()
                .text(text)
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .replyMarkup(markup)
                .build());
    }

    public static EditMessageReplyMarkup getEditMessageReplyMarkup(InlineKeyboardMarkup markup, CallbackQuery
            callbackQuery) {
        return EditMessageReplyMarkup.builder()
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .replyMarkup(markup)
                .build();
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
