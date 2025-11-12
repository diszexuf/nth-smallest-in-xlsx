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

import java.nio.file.Path;

public final class XlsxReader {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * Считывает первый столбец xlsx-файла и возвращает массив целых чисел
     *
     * @param filePath абсолютный путь к файлу
     * @return массив чисел из первого столбца
     */
    public static int[] readFirstColumn(String filePath) {
        Path path = validateAndNormalizePath(filePath);
        File file = path.toFile();

        validateFileExists(file, filePath);
        validateFileSize(file);
        validateFileReadable(file, filePath);

        return readNumericIntegersFromFirstColumn(file);
    }

    private static Path validateAndNormalizePath(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Путь к файлу не указан");
        }

        Path path = Path.of(filePath).normalize();

        if (!path.isAbsolute()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Требуется абсолютный путь");
        }
        if (path.toString().contains("..")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Path Traversal запрещён");
        }

        return path;
    }

    private static void validateFileExists(File file, String originalPath) {
        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл не найден: " + originalPath);
        }
    }

    private static void validateFileSize(File file) {
        if (file.length() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Файл слишком большой (>10MB)");
        }
    }

    private static void validateFileReadable(File file, String originalPath) {
        if (!file.canRead()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Невозможно прочитать файл: " + originalPath);
        }
    }

    private static int[] readNumericIntegersFromFirstColumn(File file) {
        List<Integer> numbers = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = getFirstSheet(workbook);
            extractIntegersFromColumn(sheet, numbers);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Файл повреждён или не является XLSX", e);
        }

        return numbers.stream().mapToInt(Integer::intValue).toArray();
    }

    private static Sheet getFirstSheet(Workbook workbook) {
        if (workbook.getNumberOfSheets() == 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "В файле нет листов");
        }
        return workbook.getSheetAt(0);
    }

    private static void extractIntegersFromColumn(Sheet sheet, List<Integer> numbers) {
        for (Row row : sheet) {
            Cell cell = row.getCell(0);
            if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                double value = cell.getNumericCellValue();

                if (value != Math.floor(value)) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                            "Дробное число недопустимо: " + value);
                }
                if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                            "Число за пределами int: " + value);
                }

                numbers.add((int) value);
            }
        }
    }
}
