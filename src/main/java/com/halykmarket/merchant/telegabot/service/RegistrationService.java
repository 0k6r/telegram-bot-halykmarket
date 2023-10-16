package com.halykmarket.merchant.telegabot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.halykmarket.merchant.telegabot.enums.WaitingType;
import com.halykmarket.merchant.telegabot.model.standart.User;
import com.halykmarket.merchant.telegabot.util.BotUtil;
import com.halykmarket.merchant.telegabot.util.Const;
import com.halykmarket.merchant.telegabot.util.UpdateUtil;

@Service
@Slf4j
public class RegistrationService {

    private User user;
    private long chatId;
    private BotUtil botUtil;
    private WaitingType waitingType = WaitingType.START;
    private boolean COMEBACK = false;
    private boolean EXIT = true;

    public boolean isRegistration(Update update, BotUtil botUtil) throws TelegramApiException {
        if (botUtil == null || chatId == 0) {
            chatId = UpdateUtil.getChatId(update);
            this.botUtil = botUtil;
        }
        switch (waitingType) {
            case START:
                user = new User();
                user.setChatId(chatId);
                if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().length() <= 50) {
                    if (!update.getMessage().getFrom().getFirstName().isEmpty()){
                        user.setFullName(update.getMessage().getFrom().getFirstName());
                        if (update.getMessage().getFrom().getLastName() != null) {
                            user.setFullName(update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName());
                        }
                    }else {
                       log.info("Something is wrong with set users fullname ");
                       System.out.println("Something is wrong with set users fullname ");
                    }
                    user.setUsername(update.getMessage().getFrom().getUserName());
                } else {
                    wrongData();
                }
                waitingType = WaitingType.SET_PHONE_NUMBER;
                return COMEBACK;
            case SET_PHONE_NUMBER:
                if (botUtil.hasContactOwner(update)) {
                    String phone = update.getMessage().getContact().getPhoneNumber();
                    if (phone.charAt(0) == '8') {
                        phone = phone.replaceFirst("8", "+7");
                    } else if (phone.charAt(0) == '7') {
                        phone = phone.replaceFirst("7", "+7");
                    }
                    user.setPhone(phone);
                }else {
                    wrongData();
                    return COMEBACK;
                }
        }
        return EXIT;
    }

    private int wrongData() throws TelegramApiException {
        return botUtil.sendMessage(Const.WRONG_DATA_TEXT, chatId);
    }

    public User getUser() {
        return user;
    }
}
