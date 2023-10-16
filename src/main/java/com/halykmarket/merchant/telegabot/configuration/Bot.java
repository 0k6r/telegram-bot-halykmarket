package com.halykmarket.merchant.telegabot.configuration;

import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.halykmarket.merchant.telegabot.exceptions.ButtonNotFoundException;
import com.halykmarket.merchant.telegabot.exceptions.CommandNotFoundException;
import com.halykmarket.merchant.telegabot.exceptions.KeyboardNotFoundException;
import com.halykmarket.merchant.telegabot.exceptions.MessageNotFoundException;
import com.halykmarket.merchant.telegabot.repository.PropertiesRepo;
import com.halykmarket.merchant.telegabot.repository.TelegramBotRepositoryProvider;
import com.halykmarket.merchant.telegabot.util.Const;
import com.halykmarket.merchant.telegabot.util.UpdateUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
@Slf4j
public class Bot extends TelegramLongPollingBot {

    private PropertiesRepo propertiesRepo = TelegramBotRepositoryProvider.getPropertiesRepo();
    private Map<Long, Conversation> conversations = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        Conversation conversation = getConversation(update);
        try {
            conversation.handleUpdate(update, this);
        } catch (TelegramApiException | SQLException | FileNotFoundException e) {
            log.error("Error " + e);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessageNotFoundException e) {
            e.printStackTrace();
        } catch (KeyboardNotFoundException e) {
            e.printStackTrace();
        } catch (ButtonNotFoundException e) {
            e.printStackTrace();
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private Conversation getConversation(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Conversation conversation = conversations.get(chatId);
        if (conversation == null) {
            log.info("InitNormal new conversation for '{}'", chatId);
            conversation = new Conversation();
            conversations.put(chatId, conversation);
        }
        return conversation;
    }

    public void sendMessageToUser(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getBotUsername() {
        return propertiesRepo.findById(Const.BOT_NAME).get().getValue();
    }

    @Override
    public String getBotToken() {
        return propertiesRepo.findById(Const.BOT_TOKEN).get().getValue();
    }
}
