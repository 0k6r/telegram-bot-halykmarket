package com.halykmarket.merchant.telegabot.command.impl;

import com.halykmarket.merchant.telegabot.exceptions.MessageNotFoundException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.halykmarket.merchant.telegabot.command.Command;
import com.halykmarket.merchant.telegabot.model.standart.Message;
import com.halykmarket.merchant.telegabot.util.Const;

@Component
public class id005_ShowAdminInfo extends Command {

    @Override
    public boolean execute() throws TelegramApiException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        deleteMessage(updateMessageId);
        Message message = messageRepo.findByIdAndLanguageId((int)messageId, getLanguage().getId())
                .orElseThrow(() -> new MessageNotFoundException("Message with id: " + messageId +
                        " and languageId: " + getLanguage().getId() + " not found"));
        sendMessage(messageId, chatId, null, message.getPhoto());
        if (message.getFile() != null) {
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
        return EXIT;
    }
}
