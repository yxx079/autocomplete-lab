package edu.course.autocomplete.search;

import java.util.Comparator;

/** 使用二分查找定位相等元素第一次和最后一次出现的位置。 */
public final class BinarySearchDeluxe {
    private BinarySearchDeluxe() {
    }

    public static <Key> int firstIndexOf(Key[] values, Key key, Comparator<Key> comparator) {
        validate(values, key, comparator);
        /*
         * TODO：返回与 key 比较结果为 0 的第一个元素下标。
         *
         * 建议思路：
         * 1. 使用闭区间 [low, high] 表示尚未排除的范围。
         * 2. 准备 result，初始值为 -1，用来保存目前找到的候选下标。
         * 3. 找到相等元素时不能立即返回，因为左侧可能还有相等元素。
         * 4. 保存 middle 后继续搜索左半区。
         *
         * 边界要求：空数组或不存在 key 时返回 -1。
         * 复杂度要求：O(log n)，不能从头到尾扫描数组，也不要修改方法签名。
         */
        throw new UnsupportedOperationException("TODO: firstIndexOf");
    }

    public static <Key> int lastIndexOf(Key[] values, Key key, Comparator<Key> comparator) {
        validate(values, key, comparator);
        /*
         * TODO：返回与 key 比较结果为 0 的最后一个元素下标。
         *
         * 结构与 firstIndexOf 相同，但找到相等元素后应继续搜索右半区，
         * 确认是否存在位置更靠后的相等元素。
         *
         * 边界要求：空数组或不存在 key 时返回 -1。
         * 复杂度要求：O(log n)，不能从头到尾扫描数组，也不要修改方法签名。
         */
        throw new UnsupportedOperationException("TODO: lastIndexOf");
    }

    private static <Key> void validate(Key[] values, Key key, Comparator<Key> comparator) {
        if (values == null || key == null || comparator == null) {
            throw new IllegalArgumentException("array, key, and comparator must not be null");
        }
    }
}
