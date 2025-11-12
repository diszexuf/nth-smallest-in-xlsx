package ru.diszexuf.minimumnumber.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.diszexuf.minimumnumber.model.NthMinimumRequest;
import ru.diszexuf.minimumnumber.util.QuickSelect;
import ru.diszexuf.minimumnumber.util.XlsxReader;

@Service
public class NthMinimumServiceImpl implements NthMinimumService {

    public int findNthMinimum(NthMinimumRequest request) {
        int n = validateN(request.getN());
        String filePath = request.getFilePath();

        int[] numbers = XlsxReader.readFirstColumn(filePath);
        validateNumbersArray(numbers, n);

        return QuickSelect.findNthSmallest(numbers, n);
    }

    private int validateN(int n) {
        if (n < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "N должно быть не меньше 1");
        }
        return n;
    }

    private void validateNumbersArray(int[] numbers, int n) {
        if (numbers.length == 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "В первом столбце нет числовых данных");
        }
        if (n > numbers.length) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "N не должно превышать количество чисел: " + numbers.length);
        }
    }
}
