package com.halykmarket.merchant.telegabot.command.impl;

import com.halykmarket.merchant.telegabot.command.Command;
import com.halykmarket.merchant.telegabot.enums.WaitingType;
import com.halykmarket.merchant.telegabot.exceptions.UserNotFoundException;
import com.halykmarket.merchant.telegabot.model.standart.ComeTime;
import com.halykmarket.merchant.telegabot.model.standart.OutTime;
import com.halykmarket.merchant.telegabot.model.standart.User;
import com.halykmarket.merchant.telegabot.service.ComeAndOutService;
import com.halykmarket.merchant.telegabot.util.DateKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class id001_ShowInfo extends Command {
    private final ComeAndOutService comeAndOutService = new ComeAndOutService();
    private final ComeTime comeTime = new ComeTime();
    private final OutTime outTime = new OutTime();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    private DateKeyboard dateKeyboard;

    @Override
    public boolean execute() throws TelegramApiException {
        User user = usersRepo.findFirstByChatId(chatId)
                .orElseThrow(() -> new UserNotFoundException("User with chatId: " + chatId +
                        " not found"));
        deleteMessage(updateMessageId);
        switch (waitingType) {
            case START:
                if (!isRegistered()) {
                    super.sendMessage("Вы не зарегистрированы, зарегистрируйтесь пожалуйста нажав на кнопку -> /start");
                }
                if (isButton(15)) {
                    var comeTimeList = comeTimeRepo.findComeTimeInDateRangeAndByUserId(LocalDate.now(),
                            LocalDate.now(), user.getId());
                    if (comeTimeList.isEmpty()) {
                        var comeTime = new ComeTime();
                        comeTime.setUser(user);
                        comeTime.setCreatedTime(LocalTime.parse(LocalTime.now().format(formatter)));
                        comeTimeRepo.save(comeTime);
                        super.sendMessage("Вы зарегистрировали свое время прихода: " + comeTime.getCreatedTime());
                    } else {
                        for (ComeTime ct : comeTimeList) {
                            if (ct.getUser().getId() == user.getId()) {
                                sendMessage("Вы же уже регистрировали свое время прихода");
                                break;
                            }
                        }
                    }
                } else if (isButton(16)) {
                    List<OutTime> outTimes = outTimeRepo.findComeTimeInDateRangeAndByUserId(LocalDate.now(), LocalDate.now(), user.getId());
                    if (outTimes.isEmpty()) {
                        OutTime outTime = new OutTime();
                        outTime.setUser(user);
                        outTime.setCreatedTime(LocalTime.parse(LocalTime.now().format(formatter)));
                        outTimeRepo.save(outTime);
                        sendMessage("Вы зарегистрировали свое время ухода: " + outTime.getCreatedTime());
                    } else {
                        for (OutTime ct : outTimes) {
                            if (ct.getUser().getId() == user.getId()) {
                                sendMessage("Вы же уже регистрировали свое время ухода");
                                break;
                            }
                        }
                    }
                } else if (isButton(17)) {
                    dateKeyboard = new DateKeyboard();
                    sendStartDate();
                    waitingType = WaitingType.WRITE_COME_DATE;
                } else if (isButton(18)) {
                    dateKeyboard = new DateKeyboard();
                    sendStartDate();
                    waitingType = WaitingType.WRITE_OUT_DATE;
                } else if (isButton(19)) {
                    LocalDate startDate = LocalDate.now().minusDays(7);
                    LocalDate endDate = LocalDate.now().minusDays(2);
                    List<ComeTime> comeTimeList = comeTimeRepo.findComeTimeInDateRangeOrderByDateAsc(startDate, endDate);
                    List<OutTime> outTimes = outTimeRepo.findComeTimeInDateRangeOrderByDateAsc(startDate, endDate);
                    System.out.println(comeTimeList);
                    comeAndOutService.sendPollService(chatId, bot, comeTimeList, outTimes);
                } else return COMEBACK;
                return COMEBACK;
            case WRITE_COME_DATE:
                try {
                    deleteMessage(updateMessageId);
                    List<ComeTime> comeTimeList = comeTimeRepo.findAll();
                    for (ComeTime c : comeTimeList) {
                        if (c.getCreatedDate().equals(dateKeyboard.getDateDate(updateMessageText).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
                            sendMessage("В этот день вы уже отмечались. Ваше время прихода: " + c.getCreatedDate() + " " + c.getCreatedTime());
                            return COMEBACK;
                        }
                    }
                    if (dateKeyboard.getDateDate(updateMessageText).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(LocalDate.now())) {
                        sendMessage("Время будущей даты нельзя кастомно ввести, попробуйте еще раз");
                        sendStartDate();
                        waitingType = WaitingType.WRITE_COME_DATE;
                        return COMEBACK;
                    }
                    if (dateKeyboard.isNext(updateMessageText)) {
                        sendStartDate();
                        return COMEBACK;
                    }
                    comeTime.setUser(user);
                    comeTime.setCreatedDate(dateKeyboard.getDateDate(updateMessageText).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    sendMessage("Напишите во сколько вы пришли? (в формате HH:mm)");
                    waitingType = WaitingType.WRITE_COME_TIME;
                } catch (Exception e) {
                    sendMessage("Вы не правильно ввели дату, попробуйте еще раз");
                    System.out.println("Произошла ошибка. Класс ошибки: " + e.getClass().getName());
                    waitingType = WaitingType.WRITE_COME_DATE;
                }
                return COMEBACK;
            case WRITE_COME_TIME:
                deleteMessage(updateMessageId);
                try {
                    if (update.hasMessage() &&
                            update.getMessage().hasText() &&
                            update.getMessage().getText().length() <= 50) {
                        String text = update.getMessage().getText();
                        LocalTime parsedTime = LocalTime.parse(text, formatter);
                        comeTime.setCreatedTime(parsedTime);
                        comeTimeRepo.save(comeTime);
                        sendMessage("Вы успешно зарегистрировали свое время прихода вручную: " + comeTime.getCreatedDate() + " - " + comeTime.getCreatedTime());
                        return COMEBACK;
                    }
                } catch (Exception e) {
                    giveErrorTimeMessage(e);
                    waitingType = WaitingType.WRITE_COME_TIME;
                    return COMEBACK;
                }
            case WRITE_OUT_DATE:
                try {
                    deleteMessage(updateMessageId);
                    List<OutTime> outTimeList = outTimeRepo.findAll();
                    for (OutTime o : outTimeList) {
                        if (o.getCreatedDate().isEqual(dateKeyboard.getDateDate(updateMessageText).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
                            sendMessage("В этот день вы уже отмечались. Ваше время ухода: " + o.getCreatedDate() + " " + o.getCreatedTime());
                            return COMEBACK;
                        }
                    }
                    if (dateKeyboard.getDateDate(updateMessageText).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(LocalDate.now())) {
                        sendMessage("Время будущей даты нельзя кастомно ввести, попробуйте еще раз");
                        sendStartDate();
                        waitingType = WaitingType.WRITE_OUT_DATE;
                        return COMEBACK;
                    }
                    if (dateKeyboard.isNext(updateMessageText)) {
                        sendStartDate();
                        return COMEBACK;
                    }
                    outTime.setUser(user);
                    outTime.setCreatedDate(dateKeyboard.getDateDate(updateMessageText).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    sendMessage("Напишите во сколько вы ушли? (в формате HH:mm)");
                    waitingType = WaitingType.WRITE_OUT_TIME;
                } catch (Exception e) {
                    sendMessage("Вы не правильно ввели дату, попробуйте еще раз");
                    System.out.println("Произошла ошибка. Класс ошибки: " + e.getClass().getName());
                    waitingType = WaitingType.WRITE_COME_DATE;
                }
                return COMEBACK;
            case WRITE_OUT_TIME:
                deleteMessage(updateMessageId);
                try {
                    if (update.hasMessage() &&
                            update.getMessage().hasText() &&
                            update.getMessage().getText().length() <= 50) {
                        System.out.println(update.getMessage().getText());
                        String text = update.getMessage().getText();
                        LocalTime parsedTime = LocalTime.parse(text, formatter);
                        outTime.setCreatedTime(parsedTime);
                        outTimeRepo.save(outTime);
                        sendMessage("Вы успешно зарегистрировали свое время ухода вручную: " + outTime.getCreatedDate() + " - " + outTime.getCreatedTime());
                        return COMEBACK;
                    }
                } catch (Exception e) {
                    giveErrorTimeMessage(e);
                    waitingType = WaitingType.WRITE_COME_TIME;
                    return COMEBACK;
                }
        }
        return EXIT;
    }

    private void giveErrorTimeMessage(Exception e) throws TelegramApiException {
        sendMessage("Вы не правильно ввели формат времени, попробуйте еще раз в формате HH:mm.");
        System.out.println("Произошла ошибка. Класс ошибки: " + e.getClass().getName());
    }

    private void sendStartDate() throws TelegramApiException {
        toDeleteKeyboard(sendMessageWithKeyboard("Выберите дату", dateKeyboard.getCalendarKeyboard()));
    }

}
