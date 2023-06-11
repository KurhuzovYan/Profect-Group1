package org.currency_bot.dto.settings;

import lombok.Data;
import org.currency_bot.constants.Currencies;
import org.currency_bot.dto.CurrencyHolder;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.currency_bot.constants.Currencies.EUR;
import static org.currency_bot.dto.CurrencyHolder.getCurrencyHolder;

@Data
public class UsersSettings {

    private long chatId;
    private int numberOfDecimal;
    private String bankMame;
    private Set<CurrencyHolder> currencies;
    private String reminder;

    public UsersSettings(long chatId, int numberOfDecimal, String bankMame, Set<CurrencyHolder> currencies, String reminder) {
        this.chatId = chatId;
        this.numberOfDecimal = numberOfDecimal;
        this.bankMame = bankMame;
        this.currencies = currencies;
        this.reminder = reminder;
    }

    @Override
    public String toString() {
        return "Курс в " + bankMame +
            ", кількість знаків після коми = " + numberOfDecimal +
            ", необхідні валюти = " + currencies +
            ", час сповіщення = " + reminder +
            '}';
    }

    public static void createUserWithDefaultSettings(Long chatId, Map<Long, UsersSettings> settings, Map<Long, Boolean> check) {
        Date date = new Date();
        String defaultBank = "ПриватБанк";
        CurrencyHolder defaultCurrency1 = getCurrencyHolder(defaultBank, Currencies.USD, date);
        CurrencyHolder defaultCurrency2 = getCurrencyHolder(defaultBank, EUR, date);

        settings.put(chatId, new UsersSettings(0, 2, defaultBank, Set.of(defaultCurrency1, defaultCurrency2), "9"));
        settings.get(chatId).setChatId(chatId);
        check.put(chatId, true);
    }
}

