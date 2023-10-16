package com.halykmarket.merchant.telegabot.service;

import com.halykmarket.merchant.telegabot.configuration.Conversation;
import com.halykmarket.merchant.telegabot.enums.Language;
import com.halykmarket.merchant.telegabot.exceptions.ButtonNotFoundException;
import com.halykmarket.merchant.telegabot.model.standart.Button;
import com.halykmarket.merchant.telegabot.model.standart.Keyboard;
import com.halykmarket.merchant.telegabot.repository.ButtonRepo;
import com.halykmarket.merchant.telegabot.repository.KeyboardRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyboardMarkUpService {
    private final ButtonRepo buttonRepo;
    private final KeyboardRepo keyboardRepo;
    private final LanguageService languageService;

    public ReplyKeyboard select(long keyboardMarkUpId) {
        if (keyboardMarkUpId < 0) {
            return new ReplyKeyboardRemove();
        }
        if (keyboardMarkUpId == 0) return null;
        return this.getKeyboard(this.keyboardRepo.findById((int) keyboardMarkUpId)
                .orElseThrow(() -> new ButtonNotFoundException("Button with id: " + keyboardMarkUpId + " not found")));
    }

    private ReplyKeyboard getKeyboard(Keyboard keyboard) {
        var buttonIds = keyboard.getButtonIds();
        if (buttonIds == null) return null;
        String[] rows = buttonIds.split(";");
        if (keyboard.isInline()) {
            return getInlineKeyboard(rows);
        } else {
            return getReplyKeyboard(rows);
        }
    }

    private InlineKeyboardMarkup getInlineKeyboard(String[] rowIds) {
        var keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (String buttonIdsString : rowIds) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = this.getByIdAndLanguageId(Integer.parseInt(buttonId), getLanguage().getId());
                var button = new InlineKeyboardButton();
                String buttonText = buttonFromDb.getName();
                button.setText(buttonText);
                String url = buttonFromDb.getUrl();
                if (url != null) {
                    button.setUrl(url);
                } else {
                    buttonText = buttonText.length() < 64 ? buttonText : buttonText.substring(0, 64);
                    button.setCallbackData(buttonText);
                }
                row.add(button);
            }
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private ReplyKeyboard getReplyKeyboard(String[] rows) {
        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        boolean isRequestContact = false;
        for (String buttonIdsString : rows) {
            var keyboardRow = new KeyboardRow();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = this.getByIdAndLanguageId(Integer.parseInt(buttonId), getLanguage().getId());
                var button = new KeyboardButton();
                String buttonText = buttonFromDb.getName();
                button.setText(buttonText);
                button.setRequestContact(buttonFromDb.isRequestContact());
                if (buttonFromDb.isRequestContact()) isRequestContact = true;
                keyboardRow.add(button);
            }
            keyboardRowList.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        replyKeyboardMarkup.setOneTimeKeyboard(isRequestContact);
        return replyKeyboardMarkup;
    }

    private Language getLanguage() {
        if (Conversation.getCurrentChatId() == 0) return Language.RU;
        return this.languageService.getLanguage(Conversation.getCurrentChatId());
    }

    public String getButtonString(int id) {
        return this.keyboardRepo.findById(id)
                .orElseThrow(() -> new ButtonNotFoundException("Button with id: " + id + " not found"))
                .getButtonIds();
    }

    private Button getByIdAndLanguageId(int id, int langId) {
        return this.buttonRepo.findByIdAndLangId(id, langId)
                .orElseThrow(() -> new ButtonNotFoundException("Button with id: " + id + " and languageId: "
                        + langId + "not found"));
    }
}
