package ru.diszexuf.minimumnumber;

import org.junit.jupiter.api.Test;
import ru.diszexuf.minimumnumber.util.QuickSelect;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class QuickSelectTest {

    @Test
    void shouldReturnThirdSmallest() {
        int[] arr = {12, 9, -34, 12, -1, 0, 3, -4, 5};
        int res = QuickSelect.findNthSmallest(arr, 5);
        assertThat(res).isEqualTo(3);
    }

    @Test
    void shouldThrowExceptionForInvalidN() {
        int arr[] = {1, 2, 3, 4};

        assertThatThrownBy(() -> QuickSelect.findNthSmallest(arr, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> QuickSelect.findNthSmallest(arr, 10)).isInstanceOf(IllegalArgumentException.class);
    }
}
