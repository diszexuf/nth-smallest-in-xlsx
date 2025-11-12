package ru.diszexuf.minimumnumber;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.diszexuf.minimumnumber.model.NthMinimumRequest;
import ru.diszexuf.minimumnumber.service.NthMinimumServiceImpl;
import ru.diszexuf.minimumnumber.util.QuickSelect;
import ru.diszexuf.minimumnumber.util.XlsxReader;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class NthMinimumServiceTest {

    @InjectMocks
    NthMinimumServiceImpl service;

    @Test
    void shouldFindNthMinimum() {
        var request = new NthMinimumRequest();
        request.setFilePath("/test.xlsx");
        request.setN(5);

        try (MockedStatic<XlsxReader> xlsx = mockStatic(XlsxReader.class);
             MockedStatic<QuickSelect> qs = mockStatic(QuickSelect.class)) {

            xlsx.when(() -> XlsxReader.readFirstColumn("/test.xlsx"))
                    .thenReturn(new int[]{12, 9, -34, 12, -1, 0, 3, -4, 5});
            qs.when(() -> QuickSelect.findNthSmallest(any(), eq(5)))
                    .thenReturn(3);

            int result = service.findNthMinimum(request);

            assertThat(result).isEqualTo(3);
        }
    }

}
