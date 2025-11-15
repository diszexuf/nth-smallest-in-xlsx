package ru.diszexuf.minimumnumber.util;

public final class QuickSelect {

    /**
     * Поиск N-го наименьшего элемента в массиве
     *
     * @param arr массив целых чисел
     * @param n   порядковый номер (1 = минимальный элемент)
     * @return N-ое наименьшее число
     */
    public static int findNthSmallest(int[] arr, int n) {
        if (n < 1 || n > arr.length) {
            throw new IllegalArgumentException("Invalid n: " + n);
        }

        int left = 0;
        int right = arr.length - 1;
        int target = n - 1;

        while (left <= right) {
            int[] bounds = partition3Way(arr, left, right);
            int ltEnd = bounds[0];
            int gtStart = bounds[1];

            if (target >= ltEnd && target < gtStart) return arr[target];

            if (target < ltEnd) right = ltEnd - 1;
            else left = gtStart;

        }

        return arr[target];
    }

    /**
     * Трехчастное разбиение массива:
     * [left...ltEnd) - элементы < pivot
     * [ltEnd...gtStart) - элементы == pivot
     * [gtStart...right] - элементы > pivot
     *
     * @param arr   массив целых чисел
     * @param left  левая граница подмассива
     * @param right правая граница подмассива
     * @return [ltEnd, gtStart] - границы средней части
     */
    private static int[] partition3Way(int[] arr, int left, int right) {
        int pivot = medianOfThree(arr, left, right);

        int lt = left;
        int i = left;
        int gt = right;

        while (i <= gt) {
            if (arr[i] < pivot) {
                swap(arr, lt++, i++);
            } else if (arr[i] > pivot) {
                swap(arr, i, gt--);
            } else {
                i++;
            }
        }

        return new int[]{lt, gt + 1};
    }

    /**
     * Выбор опорного элемента методом "медиана трех"
     *
     * @param arr   массив целых чисел
     * @param left  левая граница
     * @param right правая граница
     * @return значение медианы трех элементов
     */
    private static int medianOfThree(int[] arr, int left, int right) {
        int mid = left + (right - left) / 2;

        if (arr[mid] < arr[left]) swap(arr, left, mid);
        if (arr[right] < arr[left]) swap(arr, left, right);
        if (arr[right] < arr[mid]) swap(arr, mid, right);

        return arr[mid];
    }

    /**
     * Обмен местами элементов
     *
     * @param arr массив целых чисел
     * @param i   индекс первого элемента
     * @param j   индекс второго элемента
     */
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}