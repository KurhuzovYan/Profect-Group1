package util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import static constants.Currencies.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ButtonCreater {

    public static InlineKeyboardMarkup createCommonButtons() {
        List<InlineKeyboardButton> buttons = new ArrayList<>(Arrays.asList(
                getInlineKeyboardButton("Отримати інфо", "Info"),
                getInlineKeyboardButton("Налаштування", "Settings")
        ));
        return getSingleInlineKeyboardMarkup(buttons);
    }

    public static InlineKeyboardMarkup createSettingsButtons() {
        List<InlineKeyboardButton> settingsButtons = new ArrayList<>(Arrays.asList(
                getInlineKeyboardButton("Кількість знаків після коми", "NumberOfDecimal"),
                getInlineKeyboardButton("Банк", "Bank"),
                getInlineKeyboardButton("Валюти", "Currencies"),
                getInlineKeyboardButton("Час оповіщень", "Time"),
                getInlineKeyboardButton("До стартового меню", "Menu")
        ));
        return getInlineKeyboardMarkup(getLists(settingsButtons));
    }

    public static InlineKeyboardMarkup createButtonsWithNumberOfDecimalPlaces() {
        List<InlineKeyboardButton> digitsButtons = new ArrayList<>(Arrays.asList(
                getInlineKeyboardButton("2", "2"),
                getInlineKeyboardButton("3", "3"),
                getInlineKeyboardButton("4", "4")
        ));
        return getInlineKeyboardMarkup(getLists(digitsButtons));
    }

    public static InlineKeyboardMarkup createButtonsWithCurrencies() {
        List<InlineKeyboardButton> currenciesButtons = new ArrayList<>(Arrays.asList(
                getInlineKeyboardButton(USD.name(), "USD"),
                getInlineKeyboardButton(EUR.name(), "EUR"),
                getInlineKeyboardButton(GBP.name(), "GBP"),
                getInlineKeyboardButton("Зберігти", "save-currencies")
        ));
        return getInlineKeyboardMarkup(getLists(currenciesButtons));
    }

    public static InlineKeyboardMarkup createButtonsWithBanks() {
        List<InlineKeyboardButton> banksButtons = new ArrayList<>(Arrays.asList(
                getInlineKeyboardButton("ПриватБанк", "ПриватБанк"),
                getInlineKeyboardButton("Монобанк", "Монобанк"),
                getInlineKeyboardButton("НБУ", "НБУ")
        ));
        return getInlineKeyboardMarkup(getLists(banksButtons));
    }

    public static ReplyKeyboardMarkup createReminderButtons() {
        List<KeyboardButton> listOfButtons = Stream.of("9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "Вимкнути оповіщення")
                .map(KeyboardButton::new)
                .toList();

        List<KeyboardRow> rows = new ArrayList<>(
                Arrays.asList(new KeyboardRow(),
                        new KeyboardRow(),
                        new KeyboardRow(),
                        new KeyboardRow()
                ));

        for (int i = 0; i < listOfButtons.size(); i++) {
            if (i <= 2) rows.get(0).add(listOfButtons.get(i));
            else if (i <= 5) rows.get(1).add(listOfButtons.get(i));
            else if (i <= 8) rows.get(2).add(listOfButtons.get(i));
            else if (i <= 10) rows.get(3).add(listOfButtons.get(i));
        }

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .build();
    }

    private static List<List<InlineKeyboardButton>> getLists(List<InlineKeyboardButton> listButtons) {
        return listButtons.stream()
                .map(Collections::singletonList)
                .collect(Collectors.toList());
    }

    private static InlineKeyboardMarkup getSingleInlineKeyboardMarkup(List<InlineKeyboardButton> buttons) {
        return InlineKeyboardMarkup.builder()
                .keyboard(Collections.singleton(buttons))
                .build();
    }

    private static InlineKeyboardMarkup getInlineKeyboardMarkup(List<List<InlineKeyboardButton>> buttons) {
        return InlineKeyboardMarkup.builder()
                .keyboard(buttons)
                .build();
    }

    private static InlineKeyboardButton getInlineKeyboardButton(String textOfButton, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(textOfButton)
                .callbackData(callbackData)
                .build();
    }
}
