package com.halykmarket.merchant.telegabot.service;

import com.halykmarket.merchant.telegabot.command.Command;
import com.halykmarket.merchant.telegabot.command.CommandFactory;
import com.halykmarket.merchant.telegabot.enums.Language;
import com.halykmarket.merchant.telegabot.exceptions.CommandNotFoundException;
import com.halykmarket.merchant.telegabot.model.standart.Button;
import com.halykmarket.merchant.telegabot.repository.ButtonRepo;
import com.halykmarket.merchant.telegabot.util.Const;
import com.halykmarket.merchant.telegabot.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class CommandService {
    private final ButtonRepo buttonRepo;
    private final LanguageService languageService;
    private long chatId;

    public Command getCommand(Update update) throws CommandNotFoundException {
        chatId = UpdateUtil.getChatId(update);
        Message updateMessage = update.getMessage();
        String inputtedText;
        if (update.hasCallbackQuery()) {
            inputtedText = update.getCallbackQuery().getData().split(Const.SPLIT)[0];
            updateMessage = update.getCallbackQuery().getMessage();
            try {
                if (inputtedText != null && inputtedText.startsWith(Const.ID_MARK)) {
                    try {
                        return getCommandById(Integer.parseInt(inputtedText.split(Const.SPLIT)[0]
                                .replaceAll(Const.ID_MARK, "")));
                    } catch (Exception e) {
                        inputtedText = updateMessage.getText();
                    }
                }
            } catch (Exception ignored) {}
        } else {
            try {
                inputtedText = updateMessage.getText();
            } catch (Exception e) {
                throw new CommandNotFoundException(new Exception("No data is available"));
            }
        }
        Button button = buttonRepo.findByNameAndLangId(inputtedText, getLanguage().getId())
                .orElseThrow();
        return getCommand(button);
    }

    protected Language getLanguage() {
        if (chatId == 0) return Language.RU;
        return languageService.getLanguage(chatId);
    }

    // сохраняет комманд айди из бд (кнопка)
    private Command getCommand(Button button) throws CommandNotFoundException {
        if (button == null || button.getCommandId() == 0) {
            throw new CommandNotFoundException(new Exception("No data is available"));
        }
        Command command = CommandFactory.getCommand(button.getCommandId());
        command.setId(button.getCommandId());
        command.setMessageId(button.getMessageId() == null ? 0 : button.getMessageId());
        return command;
    }

    private Command getCommandById(int id) { return CommandFactory.getCommand(id); }
}
