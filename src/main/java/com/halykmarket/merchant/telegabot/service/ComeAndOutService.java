package com.halykmarket.merchant.telegabot.service;

import com.halykmarket.merchant.telegabot.model.standart.ComeTime;
import com.halykmarket.merchant.telegabot.model.standart.OutTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ComeAndOutService {
    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFCellStyle style = workbook.createCellStyle();
    private Sheet sheet;
    private String path = "/home/meruert/Desktop/projects";

    public void sendPollService(long chatId, DefaultAbsSender bot, List<ComeTime> comeTimes, List<OutTime> outTimes) {
        try {
            sendCompReport(chatId, bot, comeTimes, outTimes);
        } catch (Exception e) {
            log.error("Can't create/send report", e);
            try {
                bot.execute(new SendMessage(String.valueOf(chatId), "Ошибка при создании отчета"));
            } catch (TelegramApiException ex) {
                log.error("Can't send message", ex);
            }
        }
    }

    private void sendCompReport(long chatId, DefaultAbsSender bot, List<ComeTime> comeTimes, List<OutTime> outTimes) throws TelegramApiException, IOException {
        sheet = workbook.createSheet();
        sheet = workbook.getSheetAt(0);

        if (comeTimes == null || comeTimes.size() == 0) {
            bot.execute(new SendMessage(String.valueOf(chatId), "За выбранный период отсутствует отчет"));
            return;
        }

        Row row1 = sheet.createRow(0);
        Row row2 = sheet.createRow(1);
        Row row3 = sheet.createRow(2);

        for (int i = 0; i < 7; i++) {
            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            Cell cell = row1.createCell(0);
            cell.setCellValue("СЛУЖЕБНАЯ ЗАПИСКА");
            cell.setCellStyle(centerStyle);
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        for (int i = 0; i < 7; i++) {

            CellStyle left = workbook.createCellStyle();
            left.setAlignment(HorizontalAlignment.LEFT);

            Cell cell = row2.createCell(0);
            cell.setCellValue("Кому: Директору Halyk Market Карюкину И.С.\n" +
                    "От кого: Проектного менеджера Управления аналитики и администрирования Halyk Market Акаева Е. \n" +
                    "Дата:  \n" +
                    "Тема: О соблюдении трудовой дисциплины сотрудниками");
            cell.setCellStyle(left);
        }
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

        for (int i = 0; i < 7; i++) {

            CellStyle left = workbook.createCellStyle();
            left.setAlignment(HorizontalAlignment.LEFT);

            Cell cell = row3.createCell(0);
            cell.setCellValue("Представляю Вам результаты мониторинга соблюдения трудовой дисциплины сотрудниками команды Мерчант за период \n" +
                    "Ниже приведена сводная таблица с временем выхода на работу и ухода домой каждого сотрудника отдела:");
            cell.setCellStyle(left);
        }
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

        int rowIndex = 3;

        Map<Integer, Integer> map = new HashMap<>();

        sheet.createRow(rowIndex);

        sheet.getRow(rowIndex).createCell(0).setCellValue("ФИО сотрудника");
        sheet.getRow(rowIndex).getCell(0).setCellStyle(setStyle());

        sheet.getRow(rowIndex).createCell(1).setCellValue("Должность сотрудника");
        sheet.getRow(rowIndex).getCell(1).setCellStyle(setStyle());

        sheet.getRow(rowIndex).createCell(2).setCellValue("График работы по ТД");
        sheet.getRow(rowIndex).getCell(2).setCellStyle(setStyle());

        sheet.getRow(rowIndex).createCell(3).setCellValue("Дата");
        sheet.getRow(rowIndex).getCell(3).setCellStyle(setStyle());

        sheet.getRow(rowIndex).createCell(4).setCellValue("   Время прихода   ");
        sheet.getRow(rowIndex).getCell(4).setCellStyle(setStyle());

        sheet.getRow(rowIndex).createCell(5).setCellValue("   Время ухода   ");
        sheet.getRow(rowIndex).getCell(5).setCellStyle(setStyle());

        sheet.getRow(rowIndex).createCell(6).setCellValue("   Комментарии   ");
        sheet.getRow(rowIndex).getCell(6).setCellStyle(setStyle());

        int cellIndex = 5;

        for (ComeTime comeTime : comeTimes) {
            map.put(comeTime.getId(), cellIndex);
            cellIndex++;
        }
        rowIndex++;

        for (ComeTime comeTime : comeTimes) {
            sheet.createRow(rowIndex);

            sheet.getRow(rowIndex).createCell(0).setCellValue(comeTime.getUser().getFullName());
            sheet.getRow(rowIndex).getCell(0).setCellStyle(setStyle());

            sheet.getRow(rowIndex).createCell(1).setCellValue(comeTime.getUser().getPosition());
            sheet.getRow(rowIndex).getCell(1).setCellStyle(setStyle());

            sheet.getRow(rowIndex).createCell(2).setCellValue(comeTime.getUser().getWorkSchedule());
            sheet.getRow(rowIndex).getCell(2).setCellStyle(setStyle());

            sheet.getRow(rowIndex).createCell(3).setCellValue(java.sql.Date.valueOf(comeTime.getCreatedDate()).toString());
            sheet.getRow(rowIndex).getCell(3).setCellStyle(setStyle());

            sheet.getRow(rowIndex).createCell(4).setCellValue("   " + comeTime.getCreatedTime().toString() + "   " );
            sheet.getRow(rowIndex).getCell(4).setCellStyle(setStyle());

            for (OutTime outTime: outTimes) {
                if (outTime.getUser().getId() == comeTime.getUser().getId() && outTime.getCreatedDate().isEqual(comeTime.getCreatedDate())){
                    sheet.getRow(rowIndex).createCell(5).setCellValue("   "  + outTime.getCreatedTime().toString() + "   " );
                    sheet.getRow(rowIndex).getCell(5).setCellStyle(setStyle());
                    break;
                }
            }
            sheet.getRow(rowIndex).createCell(6).setCellValue("          ");
            sheet.getRow(rowIndex).getCell(6).setCellStyle(setStyle());

            rowIndex++;
        }

        for (int i = 0; i < comeTimes.size() + 3; i++)
            sheet.autoSizeColumn(i);

        sendFile(chatId, bot);
    }

    private XSSFCellStyle setStyle() {
        BorderStyle tittle = BorderStyle.NONE;

        XSSFCellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        styleTitle.setBorderTop(tittle);
        styleTitle.setBorderBottom(tittle);
        styleTitle.setBorderRight(tittle);
        styleTitle.setBorderLeft(tittle);
        return styleTitle;
    }

    private void sendFile(long chatId, DefaultAbsSender bot) throws IOException, TelegramApiException {
        String fileName = "Отчет.xlsx";
        path += fileName;
        File dir = new File("/home/meruert/Desktop/projects");
        if (!dir.exists()) {
            boolean mkdir = dir.mkdir();
            if (!mkdir) {
                log.error("Cannot create dir with path:" + dir.getPath());
            }
        }
        try (FileOutputStream stream = new FileOutputStream(path)) {
            workbook.write(stream);
        } catch (IOException e) {
            log.error("Can't send File error: ", e);
        }
        sendFile(chatId, bot, fileName, path);
    }

    private void sendFile(long chatId, DefaultAbsSender bot, String fileName, String path) throws IOException, TelegramApiException {
        File file = new File(path);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            bot.execute(SendDocument.builder().chatId(String.valueOf(chatId)).document(new InputFile(fileInputStream, fileName)).build());
        }
        file.delete();
    }

}
