package util;

import constants.Currencies;
import dto.CurrenciesPack;
import dto.CurrencyHolder;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import parsers.ParserMonobank;
import parsers.ParserNBU;
import parsers.ParserPrivatBank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrencyCalculator extends TelegramLongPollingBot {
    private Map<Long, UserSettings> userSettingsMap;

    public CurrencyCalculator() {
        userSettingsMap = new HashMap<>();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            // Обработка команд пользователя
            if (messageText.equalsIgnoreCase("/start")) {
                userSettingsMap.put(chatId, new UserSettings());
                sendReplyMessage(chatId, "Выберите валюту (USD, EUR, GBP):");
            } else {
                UserSettings userSettings = userSettingsMap.get(chatId);
                if (userSettings != null) {
                    handleUserInput(chatId, messageText, userSettings);
                }
            }
        }
    }

    private void handleUserInput(long chatId, String userInput, UserSettings userSettings) {
        if (userSettings.getSelectedCurrency() == null) {
            // Обработка выбора валюты
            if (isValidCurrency(userInput)) {
                userSettings.setSelectedCurrency(userInput.toUpperCase());
                sendReplyMessage(chatId, "Выберите банк (ПриватБанк, Монобанк, НБУ):");
            } else {
                sendReplyMessage(chatId, "Неверная валюта. Попробуйте еще раз:");
            }
        } else if (userSettings.getSelectedBank() == null) {
            // Обработка выбора банка
            if (isValidBank(userInput)) {
                userSettings.setSelectedBank(userInput);
                sendReplyMessage(chatId, "Введите сумму для конверсии:");
            } else {
                sendReplyMessage(chatId, "Неверный банк. Попробуйте еще раз:");
            }
        } else if (userSettings.getAmount() == null) {
            // Обработка ввода суммы
            Double amount = parseAmount(userInput);
            if (amount != null) {
                userSettings.setAmount(amount);
                // Получение курса валюты от выбранного банка
                double exchangeRate = getExchangeRate(userSettings.getSelectedCurrency(), userSettings.getSelectedBank());
                // Вычисление конверсии
                double convertedAmount = calculateConversion(userSettings.getAmount(), exchangeRate);
                // Отправка результата пользователю
                sendReplyMessage(chatId, String.format("Сумма %.2f %s равна %.2f грн", userSettings.getAmount(), userSettings.getSelectedCurrency(), convertedAmount));
            } else {
                sendReplyMessage(chatId, "Неверная сумма. Попробуйте еще раз:");
            }
        }
    }

    private void sendReplyMessage(long chatId, String text) {
        SendMessage message = new SendMessage()
                .setChatId(String.valueOf(chatId))
                .setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidCurrency(String currency) {
        // Проверка валидности выбранной валюты (USD, EUR, GBP)
        return currency.matches("USD|EUR|GBP");
    }

    private boolean isValidBank(String bank) {
        // Проверка валидности выбранного банка (ПриватБанк, Монобанк, НБУ)
        return bank.matches("ПриватБанк|Монобанк|НБУ");
    }

    private Double parseAmount(String amountStr) {
        // Парсинг введенной суммы
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private double getExchangeRate(String currency, String bank) {
        double exchangeRate = 0.0;

        // Получение актуального курса валюты от выбранного банка
        if (bank.equalsIgnoreCase("ПриватБанк")) {
            CurrenciesPack privatBankCurrencies = ParserPrivatBank.getCurrencyFromPrivatBank();
            exchangeRate = getCurrencyRate(privatBankCurrencies.getCurrencies(), currency);
        } else if (bank.equalsIgnoreCase("Монобанк")) {
            CurrenciesPack monobankCurrencies = ParserMonobank.getCurrencyFromMono();
            exchangeRate = getCurrencyRate(monobankCurrencies.getCurrencies(), currency);
        } else if (bank.equalsIgnoreCase("НБУ")) {
            CurrenciesPack nbuCurrencies = ParserNBU.getCurrencyFromNBU();
            exchangeRate = getCurrencyRate(nbuCurrencies.getCurrencies(), currency);
        }

        return exchangeRate;
    }

    private static double getCurrencyRate(List<CurrencyHolder> currencyList, String currency) {
        Currencies toCurrency = Currencies.getByName(currency.toUpperCase());
        if (toCurrency == null) {
            return 0.0; // Если указана некорректная валюта, возвращаем значение по умолчанию
        }

        for (CurrencyHolder currencyHolder : currencyList) {
            if (currencyHolder.getCurrency() == toCurrency) {
                return currencyHolder.getPurchaseRateNB();
            }
        }
        return 0.0; // Если не удалось найти курс валюты, возвращаем значение по умолчанию
    }




    private double calculateConversion(double amount, double exchangeRate) {
        return amount * exchangeRate;
    }

    @Override
    public String getBotUsername() {
        return "Group1ExchangeRatesBot";
    }

    @Override
    public String getBotToken() {
        return "6078623462:AAFAG54arlBUkvzNbuuSFj7O7ipKkncj8v0";
    }

    private static class UserSettings {
        private String selectedCurrency;
        private String selectedBank;
        private Double amount;

        public String getSelectedCurrency() {
            return selectedCurrency;
        }

        public void setSelectedCurrency(String selectedCurrency) {
            this.selectedCurrency = selectedCurrency;
        }

        public String getSelectedBank() {
            return selectedBank;
        }

        public void setSelectedBank(String selectedBank) {
            this.selectedBank = selectedBank;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }
}
