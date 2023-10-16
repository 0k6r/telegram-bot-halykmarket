package com.halykmarket.merchant.telegabot.configuration;

import com.halykmarket.merchant.telegabot.exceptions.*;
import com.halykmarket.merchant.telegabot.repository.PropertiesRepo;
import com.halykmarket.merchant.telegabot.util.UpdateUtil;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private PropertiesRepo propertiesRepo;

    @Autowired
    private Conversation conversation;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            this.conversation.handleUpdate(update, this);
        } catch (TelegramApiException | SQLException | FileNotFoundException e) {
            log.error("Error " + e);
        } catch (IOException | MessageNotFoundException | KeyboardNotFoundException | ButtonNotFoundException |
                 CommandNotFoundException | DocumentException e) {
            e.printStackTrace();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendMessageToUser(String chatId, String message) {
        var sendMessage = new SendMessage();
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
        return this.propertiesRepo.findById(1)
                .orElseThrow(() -> new UserNotFoundException("Bot user not found")).getValue();
    }

    @Override
    public String getBotToken() {
        return propertiesRepo.findById(2)
                .orElseThrow(() -> new UserNotFoundException("Bot token user not found")).getValue();
    }
}
