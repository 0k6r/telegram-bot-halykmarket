package com.halykmarket.merchant.telegabot.command.impl;

import com.halykmarket.merchant.telegabot.model.standart.User;
import com.halykmarket.merchant.telegabot.util.Const;
import com.halykmarket.merchant.telegabot.util.NotificationJob;
import com.halykmarket.merchant.telegabot.service.RegistrationService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.halykmarket.merchant.telegabot.command.Command;

public class id002_Registration extends Command {

    private RegistrationService registrationService = new RegistrationService();

    @Override
    public boolean execute() throws TelegramApiException, SchedulerException {
        deleteMessage(updateMessageId);
        sendMessage(Const.WELCOME_EMOGI, chatId);
        sendMessage(Const.WELCOME_MESSAGE, chatId);
//        sendMessageWithKeyboard(getText(5), 3);
        if (!isRegistered()) {
            if (!registrationService.isRegistration(update, botUtils)) {
                User user = registrationService.getUser();
                usersRepo.save(user);

                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();

                JobDetail job = JobBuilder.newJob(NotificationJob.class)
                        .withIdentity("notificationJob", "group1")
                        .build();

                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity("notificationTrigger", "group1")
                        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 10 ? * MON-FRI")) // Каждый день в 10:00 утра, пн-пт
                        .build();

                scheduler.scheduleJob(job, trigger);

            } else {
                return COMEBACK;
            }
        }
        return EXIT;
    }

    private boolean isPhoneNumber(String phone) {

        if (phone.charAt(0) == '8') {
            phone = phone.replaceFirst("8", "+7");
        } else if (phone.charAt(0) == '7') {
            phone = phone.replaceFirst("7", "+7");
        }
        return phone.charAt(0) == '+' && phone.charAt(1) == '7' && phone.substring(2).length() == 10 && isLong(phone.substring(2));
    }

    protected boolean isLong(String mess) {
        try {
            Long.parseLong(mess);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
