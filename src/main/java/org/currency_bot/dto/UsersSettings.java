package org.currency_bot.dto;

import lombok.Data;

import java.util.Set;

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
}

