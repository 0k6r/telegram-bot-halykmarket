package com.halykmarket.merchant.telegabot.command.impl;

import com.halykmarket.merchant.telegabot.model.standart.Admin;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.halykmarket.merchant.telegabot.command.Command;
import com.halykmarket.merchant.telegabot.model.standart.User;
import com.halykmarket.merchant.telegabot.util.Const;

import java.sql.SQLException;
import java.util.List;

@Slf4j
public class id006_EditAdmin extends Command {

    private int mess;
    private static String delete;
    private static String deleteIcon;
    private static String showIcon;
    private StringBuilder text;
    private List<Admin> allAdmins;

    @Override
    public boolean execute() throws TelegramApiException, SQLException {
        deleteMessage(updateMessageId);
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        if (deleteIcon == null) {
            deleteIcon = getText(Const.ICON_CROSS);
            showIcon = getText(Const.ICON_LOUPE);
            delete = getText(Const.DELETE_BUTTON_SLASH);
        }
        if (mess != 0) {
            deleteMessage(mess);
        }
        if (updateMessageText.contains(delete)) {
            if (allAdmins.size() > 1) {
                int numberAdminList = Integer.parseInt(updateMessageText.replaceAll("[^0-9]",""));
                adminRepo.delete(allAdmins.get(numberAdminList));
            }
        }
        sendEditorAdmin();
        return COMEBACK;
    }


    private void sendEditorAdmin() throws SQLException, TelegramApiException {
        deleteMessage(updateMessageId);
        try {
            getText(true);
            mess = sendMessage(String.format(getText(Const.ADMIN_SHOW_LIST), text.toString()));
        } catch (TelegramApiException e) {
            getText(false);
            mess = sendMessage(String.format(getText(Const.ADMIN_SHOW_LIST), text.toString()));
        }
        toDeleteMessage(mess);
    }

    private String getInfoByUser(User user) {
        return String.format("%s %s %s", user.getFullName(), user.getPhone(), user.getChatId());
    }

    private void getText(boolean withLink) throws SQLException {
        text = new StringBuilder();
        allAdmins = adminRepo.findAll();
        int count = 0;
        for (Admin admin : allAdmins) {
            try {
                User user = usersRepo.getByChatId(admin.getChatId());
                //User user = usersDao.getUserById(admin);
                if (allAdmins.size() == 1) {
                    if (withLink) {
                        text.append(getLinkForUser(admin.getChatId(), user.getUsername())).append(space).append(next);
                    } else {
                        text.append(getInfoByUser(user)).append(space).append(next);
                    }
                    text.append(getText(Const.WARNING_INFO_ABOUT_ADMIN)).append(next);
                    count++;
                } else {
                    if (withLink) {
                        text.append(delete).append(count).append(deleteIcon).append(" - ").append(showIcon).append(getLinkForUser(user.getChatId(), user.getUsername())).append(space).append(next);
                    } else {
                        text.append(delete).append(count).append(deleteIcon).append(" - ").append(getInfoByUser(user)).append(space).append(next);
                    }
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
