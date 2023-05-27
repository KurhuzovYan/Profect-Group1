package dto;

import lombok.Data;

import java.util.*;
@Data
public class UsersSettings {
    private int numberOfDecimal;
    private String bankMame;
    private Set<CurrencyHolder> currencies = new HashSet<>();
    private String reminder;

    public UsersSettings(int numberOfDecimal, String bankMame, Set<CurrencyHolder> currencies, String reminder) {
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

