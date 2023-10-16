package com.halykmarket.merchant.telegabot.command;

import com.halykmarket.merchant.telegabot.enums.Language;
import com.halykmarket.merchant.telegabot.enums.WaitingType;
import com.halykmarket.merchant.telegabot.exceptions.ButtonNotFoundException;
import com.halykmarket.merchant.telegabot.exceptions.CommandNotFoundException;
import com.halykmarket.merchant.telegabot.exceptions.KeyboardNotFoundException;
import com.halykmarket.merchant.telegabot.exceptions.MessageNotFoundException;
import com.halykmarket.merchant.telegabot.model.standart.Button;
import com.halykmarket.merchant.telegabot.repository.*;
import com.halykmarket.merchant.telegabot.service.KeyboardMarkUpService;
import com.halykmarket.merchant.telegabot.service.LanguageService;
import com.halykmarket.merchant.telegabot.util.BotUtil;
import com.halykmarket.merchant.telegabot.util.SetDeleteMessages;
import com.halykmarket.merchant.telegabot.util.UpdateUtil;
import com.itextpdf.text.DocumentException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@NoArgsConstructor
public abstract class Command {

    @Getter
    @Setter
    protected long id;
    protected Long chatId;
    protected Update update;
    @Getter
    @Setter
    protected long messageId;
    protected String markChange;
    protected int updateMessageId;
    protected DefaultAbsSender bot;
    protected int lastSentMessageID;
    protected static BotUtil botUtils;
    protected String updateMessageText;
    protected String updateMessagePhoto;
    protected String updateMessagePhone;
    protected String updateMessageVideo;
    protected String updateMessageDocument;
    protected WaitingType waitingType = WaitingType.START;
    protected String editableTextOfMessage;
    protected final static String linkEdit = "/linkId";
    protected static final String next = "\n";
    protected static final String space = " ";
    protected final static boolean EXIT = true;
    protected final static boolean COMEBACK = false;
    protected Message updateMessage;


    protected KeyboardMarkUpService keyboardMarkUpService = new KeyboardMarkUpService();
    protected UsersRepo usersRepo = TelegramBotRepositoryProvider.getUsersRepo();
    protected MessageRepo messageRepo = TelegramBotRepositoryProvider.getMessageRepo();
    protected ButtonRepo buttonRepo = TelegramBotRepositoryProvider.getButtonRepo();
    protected AdminRepo adminRepo = TelegramBotRepositoryProvider.getAdminRepo();
    protected KeyboardRepo keyboardRepo = TelegramBotRepositoryProvider.getKeyboardRepo();
    protected PropertiesRepo propertiesRepo = TelegramBotRepositoryProvider.getPropertiesRepo();
    protected LanguageUserRepo languageUserRepo = TelegramBotRepositoryProvider.getLanguageUserRepo();
    protected ComeTimeRepo comeTimeRepo = TelegramBotRepositoryProvider.getComeTimeRepo();
    protected OutTimeRepo outTimeRepo = TelegramBotRepositoryProvider.getOutTimeRepo();
//    protected ReminderRepo reminderRepo = TelegramBotRepositoryProvider.getReminderRepo();


    //-----------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------


    public abstract boolean execute() throws TelegramApiException, IOException, SQLException, FileNotFoundException, MessageNotFoundException, KeyboardNotFoundException, ButtonNotFoundException, CommandNotFoundException, DocumentException, SchedulerException;

    protected void editMessage(String text, int messageId) throws TelegramApiException {
        botUtils.editMessage(text, chatId, messageId);
    }

    protected Language getLanguage() {
        if (chatId == 0) return Language.RU;
        return LanguageService.getLanguage(chatId);
    }

    protected int getLangId() {
        return getLanguage().getId();
    }

    public void editMessageWithKeyboard(String text, long chatId, int messageId, InlineKeyboardMarkup replyKeyboard) throws TelegramApiException {
        EditMessageText new_message = new EditMessageText();
        new_message.setChatId(String.valueOf(chatId));
        new_message.setMessageId(messageId);
        new_message.setText(text);
        new_message.setReplyMarkup(replyKeyboard);
        new_message.setParseMode("html");
        try {
            bot.execute(new_message);
        } catch (TelegramApiException e) {
            if (e.toString().contains("Bad Request: can't parse entities")) {
                new_message.setParseMode(null);
                bot.execute(new_message);
            } else e.printStackTrace();
        }

    }

    public void editMessage(String text, long chatId, int messageId) throws TelegramApiException {
        EditMessageText new_message = new EditMessageText();
        new_message.setChatId(String.valueOf(chatId));
        new_message.setMessageId(messageId);
        new_message.setText(text);
        try {
            bot.execute(new_message);
        } catch (TelegramApiException e) {
            if (e.toString().contains("Bad Request: can't parse entities")) {
                new_message.setParseMode(null);
                bot.execute(new_message);
            }
            e.printStackTrace();
        }
    }

    protected int sendMessage(long messageId) throws TelegramApiException {
        return sendMessage(messageId, chatId);
    }

    protected int sendMessage(long messageId, long chatId) throws TelegramApiException {
        return sendMessage(messageId, chatId, null);
    }

    protected int sendMessage(long messageId, long chatId, Contact contact) throws TelegramApiException {
        return sendMessage(messageId, chatId, contact, null);
    }

    protected int sendMessage(long messageId, long chatId, Contact contact, String photo) throws TelegramApiException {
        lastSentMessageID = botUtils.sendMessage(messageId, chatId, contact, photo);
        return lastSentMessageID;
    }

    protected int sendMessage(String text) throws TelegramApiException {
        return sendMessage(text, chatId);
    }

    protected int sendMessage(String text, long chatId) throws TelegramApiException {
        return sendMessage(text, chatId, null);
    }

    protected int sendMessage(String text, long chatId, Contact contact) throws TelegramApiException {
        lastSentMessageID = botUtils.sendMessage(text, chatId);
        if (contact != null) {
            botUtils.sendContact(chatId, contact);
        }
        return lastSentMessageID;
    }

    protected void deleteMessage() {
        deleteMessage(chatId, lastSentMessageID);
    }

    protected void deleteMessage(int messageId) {
        if (messageId != 0)
            deleteMessage(chatId, messageId);
    }

    protected void deleteMessage(long chatId, int messageId) {
        if (messageId != 0)
            botUtils.deleteMessage(chatId, messageId);
    }

    protected String getText(int messageIdFromBD) {
        return messageRepo.findByIdAndLanguageId(messageIdFromBD, getLanguage().getId()).getName();
    }

    protected Optional<String> getTextOptional(int messageIdFromDb) {
        return messageRepo.getName(messageIdFromDb, getLanguage().getId());
    }

    public void clear() {
        update = null;
        bot = null;
    }

    protected boolean isButton(int buttonId) {
        Button button = buttonRepo.findByIdAndLangId(buttonId, getLanguage().getId());
        return updateMessageText.equals(button.getName());
    }

    public boolean isInitNormal(Update update, DefaultAbsSender bot) {
        if (botUtils == null) botUtils = new BotUtil(bot);
        this.update = update;
        this.bot = bot;
        chatId = UpdateUtil.getChatId(update);
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            updateMessage = callbackQuery.getMessage();
            updateMessageText = callbackQuery.getData();
            updateMessageId = updateMessage.getMessageId();
            editableTextOfMessage = callbackQuery.getMessage().getText();
        } else if (update.hasMessage()) {
            updateMessage = update.getMessage();
            updateMessageId = updateMessage.getMessageId();
            if (updateMessage.hasText()) updateMessageText = updateMessage.getText();
            if (updateMessage.hasPhoto()) {
                int size = update.getMessage().getPhoto().size();
                updateMessagePhoto = update.getMessage().getPhoto().get(size - 1).getFileId();
            } else {
                updateMessagePhoto = null;
            }
            if (updateMessage.hasDocument()){
                updateMessageDocument = updateMessage.getDocument().getFileId();
            }
            if (updateMessage.hasVideo()) {
                updateMessageVideo = updateMessage.getVideo().getFileId();
            }
        }
        if (hasContact()) updateMessagePhone = update.getMessage().getContact().getPhoneNumber();
//        if (markChange == null) markChange      = getText(Const.EDIT_BUTTON_ICON);
        return COMEBACK;
    }

    protected boolean isUser(long chatId) {
        int count = usersRepo.countUserByChatId(chatId);
        if (count > 0) return EXIT;
        return COMEBACK;
    }

    protected void sendMessageWithAddition() throws TelegramApiException {
        deleteMessage(updateMessageId);
        int languageId = getLanguage().getId();
        com.halykmarket.merchant.telegabot.model.standart.Message message = messageRepo.findByIdAndLanguageId((int) messageId, languageId);
        try {
            if (message.getFile() != null || message.getFileType() != null) {
                switch (message.getFileType()) {
                    case "photo":
                        SendPhoto sendPhoto = new SendPhoto();
                        sendPhoto.setPhoto(new InputFile(message.getFile()));
                        sendPhoto.setChatId(String.valueOf(chatId));
                        bot.execute(sendPhoto);
                        break;
                    case "audio":
                        SendAudio sendAudio = new SendAudio();
                        sendAudio.setAudio(new InputFile(message.getFile()));
                        sendAudio.setChatId(String.valueOf(chatId));
                        bot.execute(sendAudio);
                        break;
                    case "video":
                        SendVideo sendVideo = new SendVideo();
                        sendVideo.setVideo(new InputFile(message.getFile()));
                        sendVideo.setChatId(String.valueOf(chatId));
                        bot.execute(sendVideo);
                        break;
                    case "document":
                        SendDocument sendDocument = new SendDocument();
                        sendDocument.setDocument(new InputFile(message.getFile()));
                        sendDocument.setChatId(String.valueOf(chatId));
                        bot.execute(sendDocument);
                        break;

                }
            }
            sendMessage(messageId, chatId, null, message.getPhoto());
        } catch (TelegramApiException e) {
            log.error("Exception by send file for message " + messageId, e);
        }
    }

    protected boolean isAdmin() {
        int count = adminRepo.countByChatId(chatId);
        if (count > 0) return EXIT;
        return COMEBACK;
    }

//    protected boolean isEmployee() {
//        int count = employeeRepo.countByChatId(chatId);
//        if (count > 0) return EXIT;
//        return COMEBACK;
//    }

    protected boolean isAdmin(Long chat) {
        int count = adminRepo.countByChatId(chat);
        if (count > 0) return EXIT;
        return COMEBACK;
    }

//    protected boolean isEmployee(Long chat) {
//        int count = employeeRepo.countByChatId(chat);
//        if (count > 0) return EXIT;
//        return COMEBACK;
//    }

    protected boolean isButtonExist(String name) {

        return buttonRepo.countByNameAndLangId(name, getLangId()) > 0;
    }

    protected boolean isAdmin(long chatId) {
        int count = adminRepo.countByChatId(chatId);
        if (count > 0) return EXIT;
        return COMEBACK;
    }

    protected String getLinkForUser(long chatId, String userName) {
        return String.format("<a href = \"tg://user?id=%s\">%s</a>", chatId, userName);
    }

    protected int toDeleteMessage(int messageDeleteId) {
        SetDeleteMessages.addKeyboard(chatId, messageDeleteId);
        return messageDeleteId;
    }

    protected int toDeleteKeyboard(int messageDeleteId) {
        SetDeleteMessages.addKeyboard(chatId, messageDeleteId);
        return messageDeleteId;
    }

    protected int sendMessageWithKeyboard(int messageId, ReplyKeyboard keyboard) throws TelegramApiException {
        return sendMessageWithKeyboard(getText(messageId), keyboard);
    }

    protected int sendMessageWithKeyboard(String text, int keyboardId) throws TelegramApiException {
        return sendMessageWithKeyboard(text, keyboardMarkUpService.select(keyboardId));
    }

    protected int sendMessageWithKeyboard(String text, ReplyKeyboard keyboard) throws TelegramApiException {
        lastSentMessageID = sendMessageWithKeyboard(text, keyboard, chatId);
        return lastSentMessageID;
    }

    protected int sendMessageWithKeyboard(String text, ReplyKeyboard keyboard, long chatId) throws TelegramApiException {
        return botUtils.sendMessageWithKeyboard(text, keyboard, chatId);
    }

    protected boolean isExist(String buttonName) {
        return buttonRepo.countByNameAndLangId(buttonName, getLanguage().getId()) > 0;
    }

    protected void delete(int updateMessageId, int deleteMesId) {
        deleteMessage(updateMessageId);
        deleteMessage(deleteMesId);
        deleteMessage(lastSentMessageID);
    }

    protected void delete(int updateMessageId) {
        deleteMessage(updateMessageId);
        deleteMessage(lastSentMessageID);
    }

    protected String uploadFile(String fileId) {
        Objects.requireNonNull(fileId);
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        try {
            org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFile);
            return file.getFilePath();
        } catch (TelegramApiException e) {
            throw new IllegalStateException(e);
        }
    }

    protected boolean hasContact() {
        return update.hasMessage() && update.getMessage().getContact() != null;
    }

    protected boolean isRegistered() {
        return usersRepo.countUserByChatId(chatId) > 0;
    }

    protected boolean hasCallbackQuery() {
        return update.hasCallbackQuery();
    }

    protected boolean hasPhoto() {
        return update.hasMessage() && update.getMessage().hasPhoto();
    }

    protected boolean hasDocument() {
        return update.hasMessage() && update.getMessage().hasDocument();
    }

    protected boolean hasAudio() {
        return update.hasMessage() && update.getMessage().getAudio() != null;
    }

    protected boolean hasVideo() {
        return update.hasMessage() && update.getMessage().getVideo() != null;
    }

    protected boolean hasMessageText() {
        return update.hasMessage() && update.getMessage().hasText();
    }
}
