package dto;

import lombok.Data;

import java.util.*;
@Data
public class UsersSettings {
    private long chatId;
    private int numberOfDecimal;
    private String bankMame;
    private Set<CurrencyHolder> currencies;
    private String reminder, level;

    public UsersSettings(long chatId, int numberOfDecimal, String bankMame, Set<CurrencyHolder> currencies, String reminder, String level) {
        this.chatId = chatId;
        this.numberOfDecimal = numberOfDecimal;
        this.bankMame = bankMame;
        this.currencies = currencies;
        this.reminder = reminder;
        this.level = level;
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

