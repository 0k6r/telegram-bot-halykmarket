package com.halykmarket.merchant.telegabot.util;

import com.halykmarket.merchant.telegabot.configuration.Bot;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class NotificationJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        var bot = new Bot();
        bot.sendMessageToUser("505377121", "Дорогой товарищ-коллега, не забудь отметиться :)");
    }

}
