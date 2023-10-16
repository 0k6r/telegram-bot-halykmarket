package com.halykmarket.merchant.telegabot.command.impl;

import com.halykmarket.merchant.telegabot.service.LanguageService;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.halykmarket.merchant.telegabot.command.Command;
import com.halykmarket.merchant.telegabot.enums.Language;
import com.halykmarket.merchant.telegabot.util.Const;

public class id003_SelectionLanguage extends Command {

    @Override
    public boolean execute() throws TelegramApiException {
        deleteMessage(updateMessageId);
        chosenLanguage();
        toDeleteMessage(sendMessage(Const.WELCOME_TEXT_WHEN_START));
        return EXIT;
    }

    private void chosenLanguage() {
        if (isButton(Const.RU_LANGUAGE)) LanguageService.setLanguage(chatId, Language.RU);
        if (isButton(Const.KZ_LANGUAGE)) LanguageService.setLanguage(chatId, Language.KZ);
    }
}
