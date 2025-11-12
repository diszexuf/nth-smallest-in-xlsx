package ru.diszexuf.minimumnumber.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.diszexuf.minimumnumber.model.NthMinimumRequest;
import ru.diszexuf.minimumnumber.util.QuickSelect;
import ru.diszexuf.minimumnumber.util.XlsxReader;

@Service
@Slf4j
public class NthMinimumServiceImpl implements NthMinimumService {

    public int findNthMinimum(NthMinimumRequest request) {
        String filePath = request.getFilePath().replace("\\", "/");
        int n = request.getN();

        int[] numbers = XlsxReader.readFirstColumn(filePath);

        if (numbers.length == 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No numeric data in first column");
        }
        if (n > numbers.length) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "N must not exceed total numbers: " + numbers.length);
        }

        return QuickSelect.findNthSmallest(numbers, n);
    }
}
