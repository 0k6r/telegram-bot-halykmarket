package com.halykmarket.merchant.telegabot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.SimpleDateFormat;
import java.util.*;

public class DateKeyboard {

    private static final SimpleDateFormat formatCallBack = new SimpleDateFormat("dd.MM.yyyy");
    private static final SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat formatMonth = new SimpleDateFormat("MMMMM");
    private final Calendar startDay = Calendar.getInstance(new Locale("en", "UK"));
    private final String buttonBackMonth = "◄";
    private final String buttonNextMonth = "►";
    private final String buttonBackYear = "◄◄";
    private final String buttonNextYear = "►►";
    private final String emptyValue = "-";


    public boolean isNext(String updateText) {
        if (updateText.equals(buttonNextMonth)) {
            startDay.add(Calendar.MONTH, 1);
            return true;
        }
        if (updateText.equals(buttonBackMonth)) {
            startDay.add(Calendar.MONTH, -1);
            return true;
        }
        if (updateText.equals(buttonNextYear)) {
            startDay.add(Calendar.YEAR, 1);
            return true;
        }
        if (updateText.equals(buttonBackYear)) {
            startDay.add(Calendar.YEAR, -1);
            return true;
        }
        return updateText.equals(emptyValue) ||
                updateText.equals(formatYear.format(startDay.getTime())) ||
                updateText.equals(formatMonth.format(startDay.getTime()));
    }

    public String getDate(String updateText) {
        try {
            if (updateText.length() > 2) return null;
            int day = Integer.parseInt(updateText);
            startDay.set(Calendar.DAY_OF_MONTH, day);
            return formatCallBack.format(startDay.getTime());
        } catch (NumberFormatException ignore) {
            return null;
        }
    }

    public Calendar getCalendarDate(String updateText) {
        if (getDate(updateText) == null) return null;
        return this.startDay;
    }

    public Date getDateDate(String updateText) {
        return this.getCalendarDate(updateText).getTime();
    }

    public InlineKeyboardMarkup getCalendarKeyboard() {
        var simpleDateFormat = new SimpleDateFormat("dd");
        ArrayList<String> listButtons = new ArrayList<>();
        startDay.set(Calendar.DAY_OF_MONTH, 1);
        int currentMonth = startDay.get(Calendar.MONTH);
        listButtons.add(buttonBackYear + "," + formatYear.format(startDay.getTime()) + "," + buttonNextYear);
        listButtons.add(buttonBackMonth + "," + formatMonth.format(startDay.getTime()) + "," + buttonNextMonth);
        for (int i = 0; i < 5; i++) {
            StringBuilder row = new StringBuilder();
            for (int t = 0; t < 7; t++) {
                if (startDay.get(Calendar.MONTH) != currentMonth) {
                    row.append(emptyValue).append(",");
                    continue;
                }
                row.append(simpleDateFormat.format(startDay.getTime())).append(",");
                startDay.add(Calendar.DATE, 1);
            }
            listButtons.add(row.toString());
            if (startDay.get(Calendar.MONTH) != currentMonth) break;
        }
        startDay.add(Calendar.MONTH, -1);
        return this.getInlineKeyboard(listButtons.toArray(new String[0]));
    }

    private InlineKeyboardMarkup getInlineKeyboard(String[] namesButton) {
        var keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsKeyboard = new ArrayList<>();
        String buttonIdsString;
        for (String s : namesButton) {
            buttonIdsString = s;
            var rowButton = new ArrayList<InlineKeyboardButton>();
            var buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                var button = new InlineKeyboardButton();
                button.setText(buttonId);
                button.setCallbackData(buttonId);
                rowButton.add(button);
            }
            rowsKeyboard.add(rowButton);
        }
        keyboard.setKeyboard(rowsKeyboard);
        return keyboard;
    }
}
