package controller;

import constants.Currencies;
import dto.CurrenciesPack;
import dto.CurrencyHolder;
import dto.UsersSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import services.commands.StartCommand;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static constants.Constants.*;
import static constants.Currencies.*;
import static parsers.ParserMonobank.getCurrencyFromMono;
import static parsers.ParserNBU.getCurrencyFromNBU;
import static parsers.ParserPrivatBank.getCurrencyFromPrivatBank;
import static util.ButtonCreater.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class TelegramBot extends TelegramLongPollingCommandBot {

    private static Map<Long, UsersSettings> settings;
    private Map<Long, Boolean> check;
    private UsersSettings defaultSettings;
    private static CurrenciesPack pack;
    private static Date date;

    public TelegramBot() {
        register(new StartCommand());

        date = new Date();
        settings = new HashMap<>();
        check = new HashMap<>();
    }

    @Override
    @SneakyThrows
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            Long idFromCallbackQuery = update.getCallbackQuery().getFrom().getId();
            final CallbackQuery callbackQuery = update.getCallbackQuery();
            final String data = callbackQuery.getData();

            if (!settings.containsKey(idFromCallbackQuery)) {
                createUserWithDefaultSettings(idFromCallbackQuery);
            }

            final String level = settings.get(idFromCallbackQuery).getLevel();

            if (level.equals("info")) {
                switch (data) {
                    case "Info" -> showInfo(update, idFromCallbackQuery);
                    case "Settings" -> {
                        getInlineKeyboardMarkup(update, "Налаштування", createSettingsButtons());
                        settings.get(idFromCallbackQuery).setLevel("Settings");
                    }

                }
            }

            if (level.equals("Settings")) {
                switch (data) {
                    case "NumberOfDecimal" -> {
                        getInlineKeyboardMarkup(update, "Оберіть кількість знаків після коми", createButtonsWithNumberOfDecimalPlaces());
                        settings.get(idFromCallbackQuery).setLevel("Settings-NumberOfDecimal");
                    }
                    case "Bank" -> {
                        getInlineKeyboardMarkup(update, "Оберіть необхідний банк", createButtonsWithBanks());
                        settings.get(idFromCallbackQuery).setLevel("Settings-Bank");
                    }
                    case "Currencies" -> {
                        getInlineKeyboardMarkup(update, "Оберіть необхідні валюти", createButtonsWithCurrencies());
                        settings.get(idFromCallbackQuery).setCurrencies(null);
                        settings.get(idFromCallbackQuery).setLevel("Settings-Currencies");
                    }
                    case "Time" -> {
                        getReplyKeyboardMarkup(update, "Оберіть час щоденного сповіщення", createReminderButtons());
                        settings.get(idFromCallbackQuery).setLevel("Settings-Time");
                    }
                    case "Menu" -> {
                        StringBuilder backToMenuText = new StringBuilder();
                        backToMenuText.append("Збережено наступні налаштування: \n")
                                .append("\nКількість знаків після коми: ")
                                .append(settings.get(idFromCallbackQuery).getNumberOfDecimal())
                                .append("\nБанк: ")
                                .append(settings.get(idFromCallbackQuery).getBankMame())
                                .append("\nНеобхідні валюти: ")
                                .append(settings.get(idFromCallbackQuery).getCurrencies())
                                .append(settings.get(idFromCallbackQuery).getReminder().equals("Вимкнути оповіщення") ?
                                        "\nЩоденне сповіщення: викл." : "\nЩоденне сповіщення о " + settings.get(idFromCallbackQuery).getReminder() + ":00")
                                .append("\n\nЩоб отримати актуальну інформацію зараз, натисніть:\n \"Отримати інфо\" \uD83D\uDC47");
                        getInlineKeyboardMarkup(update, backToMenuText.toString(), createCommonButtons());
                        settings.get(idFromCallbackQuery).setLevel("info");
                    }
                }
            }

            if (level.equals("Settings-NumberOfDecimal")) {
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
                            .forEach(entry -> entry.getValue().setNumberOfDecimal(Integer.parseInt(current)));

                    getInlineKeyboardMarkup(update, "Налаштування", createSettingsButtons());
                    settings.get(idFromCallbackQuery).setLevel("Settings");
                    writeEntitiesToDisk();
                }
            }

            if (level.equals("Settings-Bank")) {
                if (data.equals("ПриватБанк") || data.equals("Монобанк") || data.equals("НБУ")) {

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

                    getInlineKeyboardMarkup(update, "Налаштування", createSettingsButtons());
                    settings.get(idFromCallbackQuery).setLevel("Settings");
                    writeEntitiesToDisk();
                }
            }

            if (level.equals("Settings-Currencies")) {
                if (data.equals(USD.name()) || data.equals(EUR.name()) || data.equals(GBP.name()) || data.equals("save-currencies")) {

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
                                        if (button.getText().equals(data) && !data.equals("save-currencies")) {
                                            button.setText(data + " ✅");
                                        } else {
                                            button.setText(data);
                                        }
                                    }));

                    if (data.equals("save-currencies")) {
                        if(settings.get(idFromCallbackQuery).getCurrencies().isEmpty()){
                            getInlineKeyboardMarkup(update, "Оберіть необхідні валюти\n Потрібно обрати принаймні одну валюту.", createButtonsWithCurrencies());
                            return;
                        }

                        getInlineKeyboardMarkup(update, "Налаштування", createSettingsButtons());
                        settings.get(idFromCallbackQuery).setLevel("Settings");
                        writeEntitiesToDisk();
                    } else {

                        settings.entrySet().stream()
                                .filter(entry -> entry.getKey().equals(idFromCallbackQuery))
                                .findFirst()
                                .ifPresent(entry -> {
                                    CurrencyHolder current = getCurrencyHolder(entry.getValue().getBankMame(), getByName(data));
                                    boolean matcher = entry.getValue().getCurrencies().stream()
                                            .noneMatch(currencies -> currencies.getCurrency().name().equals(data));

                                    if (entry.getValue().getCurrencies().isEmpty() || matcher) {
                                        entry.getValue().getCurrencies().add(current);
                                    } else {
                                        entry.getValue().getCurrencies().remove(current);
                                    }
                                });

                        execute(getEditMessageReplyMarkup(replyMarkup, callbackQuery));
                    }

                }
            }
        } else if (update.getMessage().hasText()) {
            Long idFromUpdateMessage = update.getMessage().getChat().getId();

            if (!settings.containsKey(idFromUpdateMessage)) {
                createUserWithDefaultSettings(idFromUpdateMessage);
            }

            final String level = settings.get(idFromUpdateMessage).getLevel();

            if (level.equals("Settings-Time")) {

                settings.entrySet().stream()
                        .filter(entry -> entry.getValue().getChatId() == idFromUpdateMessage)
                        .forEach(entry -> entry.getValue().setReminder(update.getMessage().getText()));

                String updateReminder = settings.get(idFromUpdateMessage).getReminder().equals("Вимкнути оповіщення") ?
                        "\nЩоденне сповіщення: вимкнено" : "\nЩоденне сповіщення о " + settings.get(idFromUpdateMessage).getReminder() + ":00";

                execute(SendMessage.builder()
                        .text("Для Вас збережено наступні налаштування: \n" +
                                "\nКількість знаків після коми: " + settings.get(idFromUpdateMessage).getNumberOfDecimal() +
                                "\nБанк: " + settings.get(idFromUpdateMessage).getBankMame() +
                                "\nНеобхідні валюти: " + settings.get(idFromUpdateMessage).getCurrencies() + updateReminder +
                                "\n\nЩоб отримати актуальну інформацію зараз, натисніть:\n \"Отримати інфо\" \uD83D\uDC47")
                        .chatId(update.getMessage().getChatId().toString())
                        .replyMarkup(createCommonButtons())
                        .build());

                settings.get(idFromUpdateMessage).setLevel("info");
                writeEntitiesToDisk();
            }
        }


    }

    public static void setInitialUserLevel(Long chatId) {
        settings.get(chatId).setLevel("info");

    }

    private void writeEntitiesToDisk() {
        try (FileWriter writer = new FileWriter("entities.json")) {
            writer.write(GSON.toJson(settings));
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showInfo(Update update, Long idFromCallbackQuery) {
        String defaultReminder = settings.get(idFromCallbackQuery).getReminder().equals("Вимкнути оповіщення") ?
                "\n\nЩоденне сповіщення: викл." : "\n\nЩоденне сповіщення о " + settings.get(idFromCallbackQuery).getReminder() + ":00";
        List<CurrencyHolder> allUsers = settings.get(idFromCallbackQuery).getCurrencies().stream()
                .toList();

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
    }

    private CurrencyHolder getCurrencyHolder(String bankName, Currencies currencies) {
        List<CurrenciesPack> currentPack = Stream.of(getCurrencyFromPrivatBank(), getCurrencyFromNBU(), getCurrencyFromMono())
                .filter(pack -> pack.getBankName().equals(bankName))
                .toList();

        return currentPack.stream()
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
    }

    private void createUserWithDefaultSettings(Long chatId) {
        String defaultBank = "ПриватБанк";
        CurrencyHolder defaultCurrency1 = getCurrencyHolder(defaultBank, USD);
        CurrencyHolder defaultCurrency2 = getCurrencyHolder(defaultBank, EUR);

        settings.put(chatId, new UsersSettings(0, 2, defaultBank,
                Set.of(defaultCurrency1, defaultCurrency2), "9", "info"));
        settings.get(chatId).setChatId(chatId);
        check.put(chatId, true);
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
