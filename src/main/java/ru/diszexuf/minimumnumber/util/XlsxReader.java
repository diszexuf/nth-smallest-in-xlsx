package ru.diszexuf.minimumnumber.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public final class XlsxReader {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    /**
     * Считывает первый столбец xlsx-файла и возвращает массив целых чисел
     *
     * @param filePath абсолютный путь к файлу
     * @return массив чисел из первого столбца
     */
    public static int[] readFirstColumn(String filePath) {
        File file = validateFilePath(filePath);
        return readIntegersFromFile(file);
    }

    /**
     * Валидация пути к файлу.
     */
    private static File validateFilePath(String filePath) {
        File file = getFile(filePath);

        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл не найден: " + filePath);
        }

        if (file.length() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                    "Файл слишком большой (максимум 10MB)");
        }

        if (!file.canRead()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Невозможно прочитать файл: " + filePath);
        }

        return file;
    }

    private static File getFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Путь к файлу не указан");
        }

        Path path;
        try {
            path = Path.of(filePath).normalize().toAbsolutePath();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный путь к файлу");
        }

        if (path.toString().contains("..")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Path Traversal запрещён");
        }

        return path.toFile();
    }

    /**
     * Читает целые числа из xlsx-файла
     */
    private static int[] readIntegersFromFile(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "В файле нет листов");
            }

            return extractIntegersFromSheet(sheet);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Файл повреждён или не является XLSX", e);
        }
    }

    /**
     * Извлекает целые числа из первого столбца листа
     */
    private static int[] extractIntegersFromSheet(Sheet sheet) {
        int count = 0;
        for (Row row : sheet) {
            if (isValidInteger(row.getCell(0))) {
                count++;
            }
        }

        if (count == 0) {
            return new int[0];
        }

        int[] result = new int[count];
        int index = 0;

        for (Row row : sheet) {
            Cell cell = row.getCell(0);
            if (isValidInteger(cell)) {
                result[index++] = extractInteger(cell);
            }
        }

        return result;
    }

    /**
     * Проверяет, является ли ячейка валидным целым числом
     */
    private static boolean isValidInteger(Cell cell) {
        if (cell == null) {
            return false;
        }

        // Пропускаем даты (в Excel хранятся как числа)
        if (cell.getCellType() != CellType.NUMERIC || DateUtil.isCellDateFormatted(cell)) {
            return false;
        }

        double value = cell.getNumericCellValue();

        return value == Math.floor(value)
                && value >= Integer.MIN_VALUE
                && value <= Integer.MAX_VALUE;
    }

    /**
     * Извлекает целое число из ячейки
     */
    private static int extractInteger(Cell cell) {
        double value = cell.getNumericCellValue();

        if (value != Math.floor(value)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Дробное число недопустимо: " + value);
        }

        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Число за пределами int: " + value);
        }

        return (int) value;
    }
}
