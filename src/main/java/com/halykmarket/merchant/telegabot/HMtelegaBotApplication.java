package com.halykmarket.merchant.telegabot;

import com.halykmarket.merchant.telegabot.configuration.Bot;
import com.halykmarket.merchant.telegabot.util.NotificationJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@Slf4j
public class HMtelegaBotApplication implements CommandLineRunner {

    public static void main(String[] args)  {
        try {
            // Запуск планировщика
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            // Определите задачу, которая будет выполняться каждый день, кроме субботы и воскресенья
            JobDetail job = JobBuilder.newJob(NotificationJob.class)
                    .withIdentity("notificationJob", "group1")
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("notificationTrigger", "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 10 ? * MON-FRI")) // Каждый день в 10:00 утра, пн-пт
                    .build();

            // Запустите задачу
            scheduler.scheduleJob(job, trigger);

            // Создайте и запустите отдельный поток для Spring Boot приложения
            Thread springBootThread = new Thread(() -> {
                SpringApplication.run(HMtelegaBotApplication.class, args);
            });
            springBootThread.start();

            // Дождитесь, пока поток Spring Boot приложения не завершит свою работу
            springBootThread.join();
        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("ApiContextInitializer.InitNormal()");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        try {
            telegramBotsApi.registerBot(bot);
            log.info("Bot was registered: " + bot.getBotUsername());
        } catch (TelegramApiRequestException e) {
            log.error("Error in main class", e);
        }
    }

}
