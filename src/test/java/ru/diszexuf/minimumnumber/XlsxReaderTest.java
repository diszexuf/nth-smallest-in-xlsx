package ru.diszexuf.minimumnumber;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.diszexuf.minimumnumber.util.XlsxReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class XlsxReaderTest {

    @TempDir
    File tempDir;

    @Test
    void shouldReadNumbersFromFirstColumn() throws IOException {
        File file = createTestXlsx(-1, 0, -15, 34, 4, -15, 90, -12);
        int[] result = XlsxReader.readFirstColumn(file.getAbsolutePath());
        assertThat(result).containsExactly(-1, 0, -15, 34, 4, -15, 90, -12);
    }

    @Test
    void shouldReturnEmptyForEmptyFile() throws IOException {
        File file = createTestXlsx();
        int[] result = XlsxReader.readFirstColumn(file.getAbsolutePath());
        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowNotFoundForMissingFile() {
        assertThatThrownBy(() -> XlsxReader.readFirstColumn("/no/such/file.xlsx"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    private File createTestXlsx(int... numbers) throws IOException {
        File file = new File(tempDir, "test.xlsx");
        try (XSSFWorkbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(file)) {

            XSSFSheet sheet = wb.createSheet();
            for (int i = 0; i < numbers.length; i++) {
                sheet.createRow(i).createCell(0).setCellValue(numbers[i]);
            }

            wb.write(fos);
        }

        return file;
    }
}
