package ru.diszexuf.minimumnumber.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class XlsxReader {

    /**
     * Считывает первый столбец xlsx-файла и возвращает массив целых чисел
     * @param filePath абсолютный путь к файлу
     * @return массив чисел из первого столбца
     */
    public static int[] readFirstColumn(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл не найден: " + filePath);
        }
        if (!file.canRead()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Невозможно прочитать файл: " + filePath);
        }

        List<Integer> list = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    double value = cell.getNumericCellValue();
                    if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                                "Число за пределами int: " + value);
                    }
                    list.add((int) value);
                }
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Файл поврежден или не является .xlsx", e);
        }

        return list.stream().mapToInt(Integer::intValue).toArray();
    }
}
