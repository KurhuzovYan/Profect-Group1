package dto;

import lombok.Data;

import java.util.*;
@Data
public class UserSettings {
    private long chatId;
    private int numberOfDecimal;
    private String bankMame;
    private Set<CurrencyHolder> currencies = new HashSet<>();
    private String reminder;

    public UserSettings(long chatId, int numberOfDecimal, String bankMame, Set<CurrencyHolder> currencies, String reminder) {
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

