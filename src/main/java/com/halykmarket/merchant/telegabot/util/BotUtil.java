package com.halykmarket.merchant.telegabot.util;

import com.halykmarket.merchant.telegabot.enums.Language;
import com.halykmarket.merchant.telegabot.enums.ParseMode;
import com.halykmarket.merchant.telegabot.exceptions.MessageNotFoundException;
import com.halykmarket.merchant.telegabot.repository.MessageRepo;
import com.halykmarket.merchant.telegabot.service.KeyboardMarkUpService;
import com.halykmarket.merchant.telegabot.service.LanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotUtil {
    private final LanguageService languageService;
    private final KeyboardMarkUpService keyboardMarkUpService;
    private final MessageRepo messageRepo;
    private final DefaultAbsSender bot;
    private long chatId;

    public void editMessage(String text, long chatId, int messageId) {
        EditMessageText new_message = EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(text).build();
        try {
            bot.execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public int sendMessage(SendMessage sendMessage) throws TelegramApiException {
        try {
            return bot.execute(sendMessage).getMessageId();
        } catch (TelegramApiRequestException e) {
            if (e.getApiResponse().contains("Bad Request: can't parse entities")) {
                sendMessage.setParseMode(null);
                sendMessage.setText(sendMessage.getText() + "\nBad tags");
                return bot.execute(sendMessage).getMessageId();
            } else throw e;
        }
    }

    public int sendMessage(String text, long chatId) throws TelegramApiException {
        return sendMessage(text, chatId, ParseMode.HTML);
    }

    public int sendMessage(String text, long chatId, ParseMode parseMode) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        if (parseMode == ParseMode.WITHOUT) {
            sendMessage.setParseMode(null);
        } else {
            sendMessage.setParseMode(parseMode.name());
        }
        return sendMessage(sendMessage);
    }

    public int sendMessage(long messageId, long chatId) throws TelegramApiException {
        return sendMessage(messageId, chatId, null, null);
    }

    public int sendMessage(long messageId, long chatId, Contact contact, String photo) throws TelegramApiException {
        int result = 0;
        this.chatId = chatId;
        var message = messageRepo.findByIdAndLanguageId((int) messageId, getLanguage().getId())
                .orElseThrow(() -> new MessageNotFoundException("Message with id: " + messageId +
                        " and languageId: " + getLanguage().getId() + " not found"));
        var sendMessage = new SendMessage();
        sendMessage.setText(message.getName());
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setParseMode(ParseMode.HTML.name());
        if (message.getKeyboardId() != null && this.keyboardMarkUpService.select(message.getKeyboardId()) != null) {
            if (this.keyboardMarkUpService.select(message.getKeyboardId()) != null) {
                sendMessage.setReplyMarkup(this.keyboardMarkUpService.select(message.getKeyboardId()));
            }
        } else {
            if (this.keyboardMarkUpService.select(0) != null) {
                sendMessage.setReplyMarkup(this.keyboardMarkUpService.select(message.getKeyboardId()));
            }
        }
        boolean isCaption = false;
        if (photo != null) {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(chatId));
            sendPhoto.setPhoto(new InputFile(photo));
            if (message.getName().length() < 200) {
                sendPhoto.setCaption(message.getName());
                isCaption = true;
            }
            try {
                result = bot.execute(sendPhoto).getMessageId();
            } catch (TelegramApiException e) {
                log.debug("Can't send photo", e);
                isCaption = false;
            }
        }
        if (!isCaption) result = bot.execute(sendMessage).getMessageId();
        if (contact != null) sendContact(chatId, contact);
        return result;
    }

    public int sendMessageWithKeyboard(String text, ReplyKeyboard keyboard, long chatId) throws TelegramApiException {
        return sendMessageWithKeyboard(text, keyboard, chatId, 0);
    }

    private int sendMessageWithKeyboard(String text, ReplyKeyboard keyboard, long chatId, int replyMessageId)
            throws TelegramApiException {
        var sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML.name());
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboard);
        if (replyMessageId != 0) sendMessage.setReplyToMessageId(replyMessageId);
        return sendMessage(sendMessage);
    }

    public void sendContact(long chatId, Contact contact) throws TelegramApiException {
        var sendContact = new SendContact();
        sendContact.setChatId(String.valueOf(chatId));
        sendContact.setFirstName(contact.getFirstName());
        sendContact.setLastName(contact.getLastName());
        sendContact.setPhoneNumber(contact.getPhoneNumber());

        bot.execute(sendContact).getMessageId();
    }

    public void deleteMessage(long chatId, int messageId) {
        try {
            bot.execute(new DeleteMessage(String.valueOf(chatId), messageId));
        } catch (TelegramApiException ignored) {
        }
    }

    public boolean hasContactOwner(final Update update) {
        return (update.hasMessage() && update.getMessage().hasContact()) &&
                Objects.equals(update.getMessage().getFrom().getId(), update.getMessage().getContact().getUserId());
    }

    private Language getLanguage() {
        if (chatId == 0) return Language.RU;
        return this.languageService.getLanguage(chatId);
    }

}
