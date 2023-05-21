package util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;



public class ButtonCreater {

    public static InlineKeyboardMarkup createCommonButtons() {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(getInlineKeyboardButton("Отримати інфо", "1"));
        buttons.add(getInlineKeyboardButton("Налаштування", "2"));
        InlineKeyboardMarkup markup = getInlineKeyboardMarkup(buttons);
        return markup;
    }

    public static InlineKeyboardMarkup createSettingsButtons() {
        List<InlineKeyboardButton> settingsButtons = new ArrayList<>();

        settingsButtons.add(getInlineKeyboardButton("Кількість знаків після коми", "3"));
        settingsButtons.add(getInlineKeyboardButton("Банк", "4"));
        settingsButtons.add(getInlineKeyboardButton("Валюти", "5"));
        settingsButtons.add(getInlineKeyboardButton("Час оповіщень", "6"));

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (InlineKeyboardButton button: settingsButtons) {
            buttons.add(Arrays.asList(button));
        }

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(buttons)
                .build();

        return markup;
    }

    public static InlineKeyboardMarkup createButtonWithDigitsAfterDot() {
        List<InlineKeyboardButton> digitsButtons = new ArrayList<>();

        digitsButtons.add(getInlineKeyboardButton("2", "TwoDigitsAfterDot"));
        digitsButtons.add(getInlineKeyboardButton("3", "ThreeDigitsAfterDot"));
        digitsButtons.add(getInlineKeyboardButton("4", "FourDigitsAfterDot"));

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (InlineKeyboardButton button: digitsButtons) {
            buttons.add(Arrays.asList(button));
        }

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(buttons)
                .build();

        return markup;
    }

    private static InlineKeyboardMarkup getInlineKeyboardMarkup(List<InlineKeyboardButton> buttons) {
        return InlineKeyboardMarkup.builder()
                .keyboard(Collections.singleton(buttons))
                .build();
    }

    private static InlineKeyboardButton getInlineKeyboardButton(String textOfButton, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(textOfButton)
                .callbackData(callbackData)
                .build();
    }
}
