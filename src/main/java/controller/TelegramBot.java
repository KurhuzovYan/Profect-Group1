package controller;

import static constants.Constants.*;
import static util.ButtonCreater.*;

import constants.Currencies;
import dto.CurrenciesPack;
import dto.CurrencyForUser;
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

    public TelegramBot() {
        register(new StartCommand());

        String bankName = "ПриватБанк";
        CurrencyForUser usd = getCurrencyForUser(bankName, USD);
        CurrencyForUser eur = getCurrencyForUser(bankName, EUR);
        settings = new UsersSettings(2, bankName, Set.of(usd, eur), 0);
        check = true;
    }

    @Override
    @SneakyThrows
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            final CallbackQuery callbackQuery = update.getCallbackQuery();
            final String data = callbackQuery.getData();
            List<String> allNamesOfCurrencies = settings.getCurrencies().stream()
                    .map(currencyForUser -> currencyForUser.getCurrency().name())
                    .collect(Collectors.toList());
            List<CurrencyForUser> allUsers = settings.getCurrencies().stream()
                    .collect(Collectors.toList());
            Double[] doubles = allUsers.stream()
                    .map(currency -> new Double[]{currency.getBuy(), currency.getCross(), currency.getSale()})
                    .flatMap(Arrays::stream)
                    .toArray(Double[]::new);


            switch (data) {
                case "Info":
                    String buy = doubles[0] != 0 ? "\nПокупка: " + String.format("%." + settings.getNumberOfDecimal() + "f", allUsers.get(0).getBuy()) : "";
                    String cross = doubles[1] != 0 ? "\nКросс: " + String.format("%." + settings.getNumberOfDecimal() + "f", allUsers.get(0).getCross()) : "";
                    String sale = doubles[2] != 0 ? "\nПродажа: " + String.format("%." + settings.getNumberOfDecimal() + "f", allUsers.get(0).getSale()) : "";

                    if (allNamesOfCurrencies.size() == 1) {
                        String result = ":\n\nВалютна пара: " + allNamesOfCurrencies.get(0) + "/UAH " + buy + cross + sale;
                        getInlineKeyboardMarkup(update, "Курс в " + settings.getBankMame() + result, createCommonButtons());
                        check = true;
                    } else if (allNamesOfCurrencies.size() == 2) {
                        String result = ":\n\nВалютна пара: " + allNamesOfCurrencies.get(0) + "/UAH " + buy + cross + sale;
                        String result1 = "\n\nВалютна пара: " + allNamesOfCurrencies.get(1) + "/UAH " + buy + cross + sale;
                        getInlineKeyboardMarkup(update, "Курс в " + settings.getBankMame() + result + result1, createCommonButtons());
                        check = true;
                    } else {
                        String result = ":\n\nВалютна пара: " + allNamesOfCurrencies.get(0) + "/UAH " + buy + cross + sale;
                        String result1 = "\n\nВалютна пара: " + allNamesOfCurrencies.get(1) + "/UAH " + buy + cross + sale;
                        String result2 = "\n\nВалютна пара: " + allNamesOfCurrencies.get(2) + "/UAH " + buy + cross + sale;
                        getInlineKeyboardMarkup(update, "Курс в " + settings.getBankMame() + result + result1 + result2, createCommonButtons());
                        check = true;
                    }
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
                    break;
                case "Time":
                    getReplyKeyboardMarkup(update, "Оберіть час cповіщення", createReminderButtons());
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
                    Set<CurrencyForUser> newSet = new HashSet<>();
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

                CurrencyForUser current = getCurrencyForUser(settings.getBankMame(), getByName(data));

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

            }
        }
        settings.setReminder(Integer.valueOf(update.getMessage().getText()));
        if (update.getMessage().hasText()) {
            SendMessage message = new SendMessage();

            message.setText("Очікуйте повідомлення з обранними налаштуваннями: \n" +
                    "\nКількість знаків після коми: " + settings.getNumberOfDecimal() +
                    "\nБанк: " + settings.getBankMame() +
                    "\nНеобхідні валюти: " + settings.getCurrencies() +
                    "\nЧас cповіщення: " + settings.getReminder() + ":00\n" +
                    "\nЩоб отримати інформацию одразу, написніть:\n \"Отримати інфо\" \uD83D\uDC47");
            message.setChatId(update.getMessage().getChatId());
            message.setReplyMarkup(createCommonButtons());

            execute(message);
        }

        try (FileWriter writer = new FileWriter("users/Entity-" + update.getMessage().getFrom().getUserName() + ".json")) {
            writer.write(GSON.toJson(settings));
        }
    }

    //    private static String getConclusionText(String text, ){
//
//    }
    private CurrencyForUser getCurrencyForUser(String bankName, Currencies currencies) {
        List<CurrenciesPack> currentPack = Arrays.asList(getCurrencyFromPrivatBank(), getCurrencyFromNBU(), getCurrencyFromMono()).stream()
                .filter(pack -> pack.getBankName().equals(bankName))
                .collect(Collectors.toList());

        List<List<Double>> collect = currentPack.stream()
                .flatMap(pack -> pack.getCurrencies().stream()
                        .filter(holder -> holder.getCurrency().equals(currencies))
                        .map(cur -> List.of(cur.getSaleRateNB(), cur.getRateCross(), cur.getPurchaseRateNB())))
                .collect(Collectors.toList());
        List<Double> doubles = collect.get(0);

        CurrencyForUser currencyForUser = new CurrencyForUser(bankName,
                currencies,
                UAH,
                doubles.get(0),
                doubles.get(1),
                doubles.get(2));
        return currencyForUser;

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
