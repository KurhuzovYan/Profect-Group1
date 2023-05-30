package org.currency_bot.dto;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
public class CurrenciesPack {

    private Date lastUpdate = new Date(1970, Calendar.JANUARY, 1);
    private String bankName;
    private List<CurrencyHolder> currencies;

}
