package ru.diszexuf.minimumnumber.util;

public final class QuickSelect {

    /**
     * Поиск n-го наименьшего элемента в массиве без полной сортировки.
     *
     * @param arr массив целых чисел
     * @param n порядковый номер
     * @return n-ое наименьшее число
     */
    public static int findNthSmallest(int[] arr, int n) {
        if (arr == null || arr.length == 0) throw new IllegalArgumentException("Array is empty");
        if (n < 1 || n > arr.length) throw new IllegalArgumentException("Invalid n");

        int left = 0, right = arr.length - 1;
        int target = n - 1;

        while (true) {
            if (left == right) return arr[left];

            int pivotIdx = partition(arr, left, right);

            if (target == pivotIdx) return arr[target];
            if (target < pivotIdx) right = pivotIdx - 1;
            else left = pivotIdx + 1;
        }
    }

    /**
    * Разбиение массива вокруг опорного элемента
     *
     * @param arr массив целых чисел
     * @param left левая граница подмассива
     * @param right правая граница подмассива
     * @return индекс опорного элемента
    */
    private static int partition(int[] arr, int left, int right) {
        int mid = left + (right - left) / 2;

        if (arr[mid] < arr[left]) swap(arr, left, mid);
        if (arr[right] < arr[left]) swap(arr, left, right);
        if (arr[right] < arr[mid]) swap(arr, mid, right);
        swap(arr, mid, right);

        int pivot = arr[right];
        int i = left;

        for (int j = left; j < right; j++) {
            if (arr[j] < pivot) {
                swap(arr, i++, j);
            }
        }
        swap(arr, i, right);
        return i;
    }

    /**
     * Смена элементов местами
     *
     * @param arr массив целых чисел
     * @param i индекс первого элемента для смены
     * @param j индекс второго элемента для смены
     */
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

}
