package com.halykmarket.merchant.telegabot.configuration;

import com.example.hmtelegabot.repository.*;
import com.halykmarket.merchant.telegabot.repository.*;
import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.halykmarket.merchant.telegabot.command.Command;
import com.halykmarket.merchant.telegabot.enums.Language;
import com.halykmarket.merchant.telegabot.exceptions.ButtonNotFoundException;
import com.halykmarket.merchant.telegabot.exceptions.CommandNotFoundException;
import com.halykmarket.merchant.telegabot.exceptions.KeyboardNotFoundException;
import com.halykmarket.merchant.telegabot.exceptions.MessageNotFoundException;
import com.halykmarket.merchant.telegabot.model.standart.Message;
import com.halykmarket.merchant.telegabot.service.CommandService;
import com.halykmarket.merchant.telegabot.service.KeyboardMarkUpService;
import com.halykmarket.merchant.telegabot.service.LanguageService;
import com.halykmarket.merchant.telegabot.util.DateUtil;
import com.halykmarket.merchant.telegabot.util.SetDeleteMessages;
import com.halykmarket.merchant.telegabot.util.UpdateUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

@Slf4j
public class Conversation {

    private Long chatId;
    private CommandService commandService = new CommandService();
    private MessageRepo messageRepo = TelegramBotRepositoryProvider.getMessageRepo();
    private KeyboardMarkUpService keyboardMarkUpService = new KeyboardMarkUpService();
    private UsersRepo usersRepo = TelegramBotRepositoryProvider.getUsersRepo();
    private ComeTimeRepo comeTimeRepo = TelegramBotRepositoryProvider.getComeTimeRepo();
    private OutTimeRepo outTimeRepo = TelegramBotRepositoryProvider.getOutTimeRepo();
    private static long currentChatId;
    private Command command;

    public void handleUpdate(Update update, DefaultAbsSender bot) throws TelegramApiException, SQLException, IOException, KeyboardNotFoundException, ButtonNotFoundException, MessageNotFoundException, CommandNotFoundException, DocumentException, SchedulerException {
        printUpdate(update);
        chatId = UpdateUtil.getChatId(update);
        currentChatId = chatId;
        checkLanguage(chatId);
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
                if (usersRepo.findByChatId(chatId) != null) {
                    replyKeyboard = keyboardMarkUpService.select(1);
                    message = messageRepo.findByIdAndLanguageId(2, getLanguage().getId());
                } else {
                    replyKeyboard = keyboardMarkUpService.select(5);
                    message = messageRepo.findByIdAndLanguageId(9, getLanguage().getId());
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
            boolean commandFinished = command.execute();
            if (commandFinished) clear();
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
        if (LanguageService.getLanguage(chatId) == null) LanguageService.setLanguage(chatId, Language.RU);
    }

    public static long getCurrentChatId() {
        return currentChatId;
    }

    private Language getLanguage() {
        if (chatId == 0) return Language.RU;
        return LanguageService.getLanguage(chatId);
    }

    private void clear() {
        command.clear();
        command = null;
    }
}
