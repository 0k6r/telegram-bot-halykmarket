package com.halykmarket.merchant.telegabot.util;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetDeleteMessages {

    private static final Map<Long, Set<Integer>> messagesWithKeyboardIds = new HashMap<>();
    private static final Map<Long, Set<Integer>> messagesIds = new HashMap<>();

    public static void deleteKeyboard(long chatId, DefaultAbsSender bot) {
        try {
            if (messagesWithKeyboardIds.get(chatId) != null) {
                for (var id : messagesWithKeyboardIds.get(chatId)) {
                    try {
                        var emrm = new EditMessageReplyMarkup();
                        emrm.setChatId(String.valueOf(chatId));
                        emrm.setMessageId(id);
                        emrm.setReplyMarkup(null);
                        bot.execute(emrm);
                    } catch (TelegramApiException ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        }
        messagesWithKeyboardIds.put(chatId, new HashSet<>());
    }

    public static void deleteMessage(long chatId, DefaultAbsSender bot) {
        try {
            if (messagesIds.get(chatId) != null) {
                for (var id : messagesIds.get(chatId)) {
                    try {
                        bot.execute(new DeleteMessage(String.valueOf(chatId), id));
                    } catch (TelegramApiException ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        }
        messagesWithKeyboardIds.put(chatId, new HashSet<>());
    }

    public static void addKeyboard(long chatId, int messageId) {
        add(chatId, messageId);
    }

    private static void add(long chatId, int messageId) {
        if (messagesWithKeyboardIds.get(chatId) == null) {
            var set = new HashSet<Integer>();
            set.add(messageId);
            messagesWithKeyboardIds.put(chatId, set);
        } else {
            messagesWithKeyboardIds.get(chatId).add(messageId);
        }
    }
}
