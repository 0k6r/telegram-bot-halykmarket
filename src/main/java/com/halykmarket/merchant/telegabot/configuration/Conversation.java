package com.halykmarket.merchant.telegabot.configuration;

import com.halykmarket.merchant.telegabot.command.Command;
import com.halykmarket.merchant.telegabot.enums.Language;
import com.halykmarket.merchant.telegabot.exceptions.ButtonNotFoundException;
import com.halykmarket.merchant.telegabot.exceptions.CommandNotFoundException;
import com.halykmarket.merchant.telegabot.exceptions.KeyboardNotFoundException;
import com.halykmarket.merchant.telegabot.exceptions.MessageNotFoundException;
import com.halykmarket.merchant.telegabot.model.standart.Message;
import com.halykmarket.merchant.telegabot.repository.*;
import com.halykmarket.merchant.telegabot.service.CommandService;
import com.halykmarket.merchant.telegabot.service.KeyboardMarkUpService;
import com.halykmarket.merchant.telegabot.service.LanguageService;
import com.halykmarket.merchant.telegabot.util.DateUtil;
import com.halykmarket.merchant.telegabot.util.SetDeleteMessages;
import com.halykmarket.merchant.telegabot.util.UpdateUtil;
import com.itextpdf.text.DocumentException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class Conversation {
    private final CommandService commandService;
    private final MessageRepo messageRepo;
    private final KeyboardMarkUpService keyboardMarkUpService;
    private final UsersRepo usersRepo;
    private final LanguageService languageService;
    @Getter
    private static long currentChatId;
    private Long chatId;
    private Command command;

    public void handleUpdate(final Update update, final DefaultAbsSender bot)
            throws TelegramApiException, SQLException, IOException, KeyboardNotFoundException, ButtonNotFoundException,
            MessageNotFoundException, CommandNotFoundException, DocumentException, SchedulerException {
        this.printUpdate(update);
        chatId = UpdateUtil.getChatId(update);
        currentChatId = chatId;
        this.checkLanguage(chatId);
        try {
            command = commandService.getCommand(update);
            if (command != null) {
                SetDeleteMessages.deleteKeyboard(chatId, bot);
                SetDeleteMessages.deleteMessage(chatId, bot);
            }
        } catch (CommandNotFoundException e) {
            if (command == null) {
                ReplyKeyboard replyKeyboard;
                Message message;
                if (usersRepo.existsByChatId(chatId)) {
                    replyKeyboard = keyboardMarkUpService.select(1);
                    message = messageRepo.findByIdAndLanguageId(2, this.getLanguage().getId())
                            .orElseThrow(() -> new MessageNotFoundException("Message with id: " + 2 +
                                    " and languageId: " + this.getLanguage().getId() + " not found"));
                } else {
                    replyKeyboard = keyboardMarkUpService.select(5);
                    message = messageRepo.findByIdAndLanguageId(9, this.getLanguage().getId())
                            .orElseThrow(() -> new MessageNotFoundException("Message with id: " + 9 +
                                    " and languageId: " + this.getLanguage().getId() + " not found"));
                }
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(String.valueOf(chatId));
                sendMessage.setText(message.getName());
                sendMessage.setReplyMarkup(replyKeyboard);
                bot.execute(sendMessage);
            }
        }
        if (command != null) {
            if (command.isInitNormal(update, bot)) {
                clear();
                return;
            }
            if (command.execute()) {
                clear();
            }
        }
    }

    private void printUpdate(Update update) {
        if (update.hasMessage()) {
            String dataMessage = DateUtil.getDbMmYyyyHhMmSs(new Date((long) update.getMessage().getDate() * 1000));
            log.info("New update get {} -> send response {}", dataMessage, DateUtil.getDbMmYyyyHhMmSs(new Date()));
            log.info(UpdateUtil.toString(update));
        }
    }

    private void checkLanguage(long chatId) {
        if (this.languageService.getLanguage(chatId) == null){
            this.languageService.setLanguage(chatId, Language.RU);
        }
    }

    private Language getLanguage() {
        if (chatId == 0) {
            return Language.RU;
        }
        return languageService.getLanguage(chatId);
    }

    private void clear() {
        command.clear();
        command = null;
    }
}
