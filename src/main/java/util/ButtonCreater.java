package util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static constants.Currencies.*;

import java.util.*;
import java.util.stream.Collectors;


public class ButtonCreater {

    public static InlineKeyboardMarkup createCommonButtons() {
        List<InlineKeyboardButton> buttons = new ArrayList<>(Arrays.asList(
                getInlineKeyboardButton("Отримати інфо", "1"),
                getInlineKeyboardButton("Налаштування", "2")
        ));
        return getSingleInlineKeyboardMarkup(buttons);
    }

    public static InlineKeyboardMarkup createSettingsButtons() {
        List<InlineKeyboardButton> settingsButtons = new ArrayList<>(Arrays.asList(
                getInlineKeyboardButton("Кількість знаків після коми", "3"),
                getInlineKeyboardButton("Банк", "4"),
                getInlineKeyboardButton("Валюти", "5"),
                getInlineKeyboardButton("Час cповіщень", "6")
        ));
        return getInlineKeyboardMarkup(getLists(settingsButtons));
    }

    public static InlineKeyboardMarkup createButtonsWithNumberOfDecimalPlaces() {
        List<InlineKeyboardButton> digitsButtons = new ArrayList<>(Arrays.asList(
                getInlineKeyboardButton("2", "TwoDigitsAfterDot"),
                getInlineKeyboardButton("3", "ThreeDigitsAfterDot"),
                getInlineKeyboardButton("4", "FourDigitsAfterDot")
        ));
        return getInlineKeyboardMarkup(getLists(digitsButtons));
    }

    public static InlineKeyboardMarkup createButtonsWithCurrencies() {
        List<InlineKeyboardButton> currenciesButtons = new ArrayList<>(Arrays.asList(
                getInlineKeyboardButton(USD.name(), "USD"),
                getInlineKeyboardButton(EUR.name(), "EUR"),
                getInlineKeyboardButton(GBP.name(), "GBP")
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
