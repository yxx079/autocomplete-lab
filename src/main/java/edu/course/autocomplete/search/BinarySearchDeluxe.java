package edu.course.autocomplete.search;

import java.util.Comparator;

/** 使用二分查找定位相等元素第一次和最后一次出现的位置。 */
public final class BinarySearchDeluxe {
    private BinarySearchDeluxe() {
    }

    /**
     * 返回按 comparator 比较相等的第一个位置，未找到时返回 -1。
     * 数组必须已按同一比较规则排序。
     */
    public static <Key> int firstIndexOf(Key[] values, Key key, Comparator<Key> comparator) {
        validate(values, key, comparator);
        int low = 0;
        int high = values.length - 1;
        int result = -1;
        while (low <= high) {
            int middle = low + (high - low) / 2;
            int comparison = comparator.compare(values[middle], key);
            if (comparison < 0) {
                low = middle + 1;
            }
            else if (comparison > 0) {
                high = middle - 1;
            }
            else {
                // 保留当前匹配，继续向左查找首个位置。
                result = middle;
                high = middle - 1;
            }
        }
        return result;
    }

    /**
     * 返回按 comparator 比较相等的最后一个位置，未找到时返回 -1。
     * 数组必须已按同一比较规则排序。
     */
    public static <Key> int lastIndexOf(Key[] values, Key key, Comparator<Key> comparator) {
        validate(values, key, comparator);
        int low = 0;
        int high = values.length - 1;
        int result = -1;
        while (low <= high) {
            int middle = low + (high - low) / 2;
            int comparison = comparator.compare(values[middle], key);
            if (comparison < 0) {
                low = middle + 1;
            }
            else if (comparison > 0) {
                high = middle - 1;
            }
            else {
                // 保留当前匹配，继续向右查找最后一个位置。
                result = middle;
                low = middle + 1;
            }
        }
        return result;
    }

    private static <Key> void validate(Key[] values, Key key, Comparator<Key> comparator) {
        if (values == null || key == null || comparator == null) {
            throw new IllegalArgumentException("array, key, and comparator must not be null");
        }
    }
}
