package org.currency_bot.util;

import org.currency_bot.dto.CurrencyHolder;
import org.currency_bot.dto.CurrenciesPack;
import org.currency_bot.parsers.ParserMonobank;
import org.currency_bot.parsers.ParserNBU;
import org.currency_bot.parsers.ParserPrivatBank;

import java.util.List;

public class CurrencyConversionButton {

    public static String convertAmount(String amount) {
        try {
            double inputAmount = Double.parseDouble(amount);

            // Получение данных о валютах
            CurrenciesPack monobankCurrencies = ParserMonobank.getCurrencyFromMono();
            CurrenciesPack nbuCurrencies = ParserNBU.getCurrencyFromNBU();
            CurrenciesPack privatBankCurrencies = ParserPrivatBank.getCurrencyFromPrivatBank();

            // Выполнение конвертации
            double monobankRate = getCurrencyRate(monobankCurrencies);
            double nbuRate = getCurrencyRate(nbuCurrencies);
            double privatBankRate = getCurrencyRate(privatBankCurrencies);

            double convertedMonobankAmount = inputAmount * monobankRate;
            double convertedNbuAmount = inputAmount * nbuRate;
            double convertedPrivatBankAmount = inputAmount * privatBankRate;

            // Формирование ответа
            StringBuilder response = new StringBuilder();
            response.append("Конвертація здійснена:\n");
            response.append("Сума: ").append(inputAmount).append(" UAH\n");
            response.append("Курси:\n");
            response.append("Монобанк: 1 UAH = ").append(monobankRate).append(" ").append(getCurrencyCode(monobankCurrencies)).append("\n");
            response.append("НБУ: 1 UAH = ").append(nbuRate).append(" ").append(getCurrencyCode(nbuCurrencies)).append("\n");
            response.append("ПриватБанк: 1 UAH = ").append(privatBankRate).append(" ").append(getCurrencyCode(privatBankCurrencies)).append("\n");
            response.append("Результати:\n");
            response.append("Монобанк: ").append(convertedMonobankAmount).append(" ").append(getCurrencyCode(monobankCurrencies)).append("\n");
            response.append("НБУ: ").append(convertedNbuAmount).append(" ").append(getCurrencyCode(nbuCurrencies)).append("\n");
            response.append("ПриватБанк: ").append(convertedPrivatBankAmount).append(" ").append(getCurrencyCode(privatBankCurrencies)).append("\n");

            return response.toString();
        } catch (NumberFormatException e) {
            return "Некоректна сума. Будь ласка, введіть число.";
        }
    }

    private static double getCurrencyRate(CurrenciesPack currenciesPack) {
        List<CurrencyHolder> currencies = currenciesPack.getCurrencies();
        if (!currencies.isEmpty()) {
            return currencies.get(0).getCross();
        }
        return 0.0;
    }

    private static String getCurrencyCode(CurrenciesPack currenciesPack) {
        List<CurrencyHolder> currencies = currenciesPack.getCurrencies();
        if (!currencies.isEmpty()) {
            return currencies.get(0).getCurrency().name();
        }
        return "";
    }
}
