package util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ButtonCreater {

    public static InlineKeyboardMarkup createCommonButtons() {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(getInlineKeyboardButton("Отримати інфо", "1"));
        buttons.add(getInlineKeyboardButton("Налаштування", "2"));
        InlineKeyboardMarkup markup = getInlineKeyboardMarkup(buttons);
        return markup;
    }

    public static InlineKeyboardMarkup getInlineKeyboardMarkup(List<InlineKeyboardButton> buttons) {
        return InlineKeyboardMarkup.builder()
                .keyboard(Collections.singleton(buttons))
                .build();
    }

    public static InlineKeyboardButton getInlineKeyboardButton(String textOfButton, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(textOfButton)
                .callbackData(callbackData)
                .build();
    }

}
