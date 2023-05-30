package org.currency_bot.controller;

import com.google.gson.reflect.TypeToken;
import org.currency_bot.constants.Currencies;
import org.currency_bot.dto.CurrenciesPack;
import org.currency_bot.dto.CurrencyHolder;
import org.currency_bot.dto.UsersSettings;
import org.currency_bot.services.FinalSender;
import lombok.Data;
import lombok.SneakyThrows;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.currency_bot.services.commands.StartCommand;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static org.currency_bot.constants.Constants.*;
import static org.currency_bot.constants.Currencies.*;
import static org.currency_bot.parsers.ParserMonobank.getCurrencyFromMono;
import static org.currency_bot.parsers.ParserNBU.getCurrencyFromNBU;
import static org.currency_bot.parsers.ParserPrivatBank.getCurrencyFromPrivatBank;
import static org.currency_bot.util.ButtonCreater.*;

@Data
public class TelegramBot extends TelegramLongPollingCommandBot {

    private static CurrenciesPack pack;
    private static Date date;
    private Map<Long, UsersSettings> settings;
    private Map<Long, Boolean> check;
    private FinalSender defaultSettings;

    public TelegramBot() {
        register(new StartCommand());

        date = new Date();
        settings = FinalSender.settings;
        check = new HashMap<>();
    }

    private static void handler(String data, InlineKeyboardMarkup markup) {
        markup.getKeyboard().forEach(buttons ->
                buttons.stream()
                        .filter(button -> button.getCallbackData().equals(data))
                        .forEach(button -> button.setText(button.getText() + " ✅")));
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
    @SneakyThrows
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()) {

            try (FileReader reader = new FileReader("src/main/java/resources/entities.json")) {
                Type type = new TypeToken<Map<Long, UsersSettings>>() {
                }.getType();
                Map<Long, UsersSettings> savedSettings = GSON.fromJson(reader, type);
                settings.putAll(savedSettings);
            }

            Long idFromCallbackQuery = update.getCallbackQuery().getFrom().getId();
            final CallbackQuery callbackQuery = update.getCallbackQuery();
            final String data = callbackQuery.getData();

            if (!settings.containsKey(idFromCallbackQuery)) {
                createUserWithDefaultSettings(idFromCallbackQuery);
            }

            switch (data) {
                case "Info":
                    String defaultReminder = settings.get(idFromCallbackQuery).getReminder().equals("викл.") ?
                            "\n\nЩоденне оповіщення: викл." : "\n\nЩоденне оповіщення о " + settings.get(idFromCallbackQuery).getReminder() + ":00";

                    List<CurrencyHolder> allUsers = settings.get(idFromCallbackQuery).getCurrencies().stream()
                            .collect(Collectors.toList());

                    Double[] courses = allUsers.stream()
                            .map(currency -> new Double[]{currency.getBuy(), currency.getCross(), currency.getSale()})
                            .flatMap(Arrays::stream)
                            .toArray(Double[]::new);

                    StringBuilder resultBuilder = new StringBuilder();

                    for (int i = 0; i < settings.get(idFromCallbackQuery).getCurrencies().size(); i++) {
                        String buy = courses[i * 3] != 0 ? "\nКупівля : " + String.format("%." + settings.get(idFromCallbackQuery).getNumberOfDecimal() + "f", allUsers.get(i).getBuy()) : "";
                        String cross = courses[i * 3 + 1] != 0 ? "\nКрос: " + String.format("%." + settings.get(idFromCallbackQuery).getNumberOfDecimal() + "f", allUsers.get(i).getCross()) : "";
                        String sale = courses[i * 3 + 2] != 0 ? "\nПродаж: " + String.format("%." + settings.get(idFromCallbackQuery).getNumberOfDecimal() + "f", allUsers.get(i).getSale()) : "";

                        resultBuilder.append("\n\nВалютна пара: ")
                                .append(allUsers.get(i).getCurrency().name())
                                .append("/UAH")
                                .append(buy)
                                .append(cross)
                                .append(sale);
                    }
                    getInlineKeyboardMarkup(update, "Курс в " + settings.get(idFromCallbackQuery).getBankMame() + resultBuilder + defaultReminder, createCommonButtons());
                    break;
                case "Settings":
                    getInlineKeyboardMarkup(update, "Налаштування", createSettingsButtons());
                    break;
                case "NumberOfDecimal":
                    getInlineKeyboardMarkup(update, "Оберіть кількість знаків після коми", createButtonsWithNumberOfDecimalPlaces());
                    break;
                case "Bank":
                    getInlineKeyboardMarkup(update, "Оберіть необхідний банк", createButtonsWithBanks());
                    break;
                case "Currencies":
                    getInlineKeyboardMarkup(update, "Оберіть необхідні валюти", createButtonsWithCurrencies());
                    check.put(idFromCallbackQuery, true);
                    break;
                case "Time":
                    getReplyKeyboardMarkup(update, "Оберіть час щоденного оповіщення", createReminderButtons());
                    break;
                case "Confirm":
                    execute(DeleteMessage.builder()
                            .chatId(idFromCallbackQuery)
                            .messageId(update.getCallbackQuery().getMessage().getMessageId())
                            .build());
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

                settings.entrySet().stream()
                        .filter(entry -> entry.getValue().getChatId() == idFromCallbackQuery)
                        .forEach(entry -> entry.getValue().setNumberOfDecimal(Integer.valueOf(current)));

            } else if (data.equals(USD.name()) || data.equals(EUR.name()) || data.equals(GBP.name())) {

                if (check.get(idFromCallbackQuery)) {
                    Set<CurrencyHolder> newSet = new HashSet<>();
                    settings.get(idFromCallbackQuery).setCurrencies(newSet);
                    check.put(idFromCallbackQuery, false);
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

                settings.entrySet().stream()
                        .filter(entry -> entry.getKey().equals(idFromCallbackQuery))
                        .findFirst()
                        .ifPresent(entry -> {
                            CurrencyHolder current = getCurrencyHolder(entry.getValue().getBankMame(), Currencies.getByName(data));
                            boolean matcher = entry.getValue().getCurrencies().stream()
                                    .allMatch(currencies -> !currencies.getCurrency().name().equals(data));

                            if (entry.getValue().getCurrencies().isEmpty() || matcher) {
                                entry.getValue().getCurrencies().add(current);
                            } else {
                                entry.getValue().getCurrencies().remove(current);
                            }
                        });

                execute(getEditMessageReplyMarkup(replyMarkup, callbackQuery));

            } else if (data.equals("ПриватБанк") || data.equals("Монобанк") || data.equals("НБУ")) {

                InlineKeyboardMarkup markup = createButtonsWithBanks();
                handler(data, markup);
                execute(getEditMessageReplyMarkup(markup, callbackQuery));
                settings.get(idFromCallbackQuery).setBankMame(data);

                Set<CurrencyHolder> updatedCurrencies = settings.get(idFromCallbackQuery).getCurrencies().stream()
                        .map(cur -> {
                            cur.setBankName(data);
                            return getCurrencyHolder(data, cur.getCurrency());
                        })
                        .collect(Collectors.toSet());

                settings.entrySet().stream()
                        .filter(entry -> entry.getValue().getChatId() == idFromCallbackQuery)
                        .forEach(entry -> entry.getValue().setCurrencies(updatedCurrencies));
            }

        } else if (update.getMessage().hasText()) {
            Long idFromUpdateMessage = update.getMessage().getChat().getId();
            String textFromMessage = update.getMessage().getText();

            if (!settings.containsKey(idFromUpdateMessage)) {
                createUserWithDefaultSettings(idFromUpdateMessage);
            }

            boolean checkTime = List.of("9", "10", "11", "12", "13", "14", "15", "16", "17", "18").stream()
                    .anyMatch(time -> time.equals(textFromMessage));

            if (checkTime) {
                settings.entrySet().stream()
                        .filter(entry -> entry.getValue().getChatId() == idFromUpdateMessage)
                        .forEach(entry -> entry.getValue().setReminder(textFromMessage));
                getMessageWithFinalSettings(update, idFromUpdateMessage, "\nЩоденне оповіщення о " + settings.get(idFromUpdateMessage).getReminder() + ":00");
            } else if (textFromMessage.equals("Вимкнути оповіщення")) {
                getMessageWithFinalSettings(update, idFromUpdateMessage, "\nЩоденне оповіщення: викл.");
                settings.get(idFromUpdateMessage).setReminder("викл.");
            }
        }
        File file = new File("src/main/java/resources/entities.json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(settings));
            writer.flush();
        }
    }

    @SneakyThrows
    public void getMessageWithFinalSettings(Update update, Long idFromUpdateMessage, String message) {
        execute(SendMessage.builder()
                .text("Дякуємо!\nВаші налаштування прийняті \uD83D\uDE4C\uD83C\uDFFB " +
                        "\nОчікуйте повідомлення з актуальною інформацією:" +
                        "\n\nКількість знаків після коми: " + settings.get(idFromUpdateMessage).getNumberOfDecimal() +
                        "\nБанк: " + settings.get(idFromUpdateMessage).getBankMame() +
                        "\nНеобхідні валюти: " + settings.get(idFromUpdateMessage).getCurrencies() + message +
                        "\n\nЩоб отримати інформацію одразу, написніть:\n \"Отримати інфо\" \uD83D\uDC47\uD83C\uDFFB")
                .chatId(update.getMessage().getChatId().toString())
                .replyMarkup(createCommonButtons())
                .build());
    }

    private CurrencyHolder getCurrencyHolder(String bankName, Currencies currencies) {
        List<CurrenciesPack> currentPack = Arrays.asList(getCurrencyFromPrivatBank(), getCurrencyFromNBU(), getCurrencyFromMono()).stream()
                .filter(pack -> pack.getBankName().equals(bankName))
                .collect(Collectors.toList());

        return currentPack.stream()
                .flatMap(pack -> pack.getCurrencies().stream())
                .filter(holder -> holder.getCurrency().equals(currencies))
                .map(cur -> new CurrencyHolder(
                        date,
                        bankName,
                        currencies,
                        Currencies.UAH,
                        cur.getBuy(),
                        cur.getCross(),
                        cur.getSale()))
                .findFirst()
                .orElse(null);
    }

    private void createUserWithDefaultSettings(Long chatId) {
        String defaultBank = "ПриватБанк";
        CurrencyHolder defaultCurrency1 = getCurrencyHolder(defaultBank, Currencies.USD);
        CurrencyHolder defaultCurrency2 = getCurrencyHolder(defaultBank, EUR);

        settings.put(chatId, new UsersSettings(0, 2, defaultBank,
                Set.of(defaultCurrency1, defaultCurrency2), "9"));
        settings.get(chatId).setChatId(chatId);
        check.put(chatId, true);
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

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    public void printMessage(Long chatID, String messageText) throws TelegramApiException {
        execute(SendMessage.builder()
                .text(messageText)
                .chatId(chatID)
                .build());
    }
}