package edu.course.autocomplete.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

class BinarySearchDeluxeTest {
    private static final Comparator<String> NATURAL = Comparator.naturalOrder();

    /** 数组只有一个名称：找到的位置是0；查询另一个名称时返回-1。 */
    @Test
    void checksOneCityName() {
        String[] values = {"Kyoto"};
        assertEquals(0, BinarySearchDeluxe.firstIndexOf(values, "Kyoto", NATURAL));
        assertEquals(0, BinarySearchDeluxe.lastIndexOf(values, "Kyoto", NATURAL));
        assertEquals(-1, BinarySearchDeluxe.firstIndexOf(values, "Oslo", NATURAL));
        assertEquals(-1, BinarySearchDeluxe.lastIndexOf(values, "Oslo", NATURAL));
    }

    /** 三个名称相同：第一次在0，最后一次在2，不能只返回中间位置。 */
    @Test
    void checksAllEqualCityNames() {
        String[] values = {"Oslo", "Oslo", "Oslo"};
        assertEquals(0, BinarySearchDeluxe.firstIndexOf(values, "Oslo", NATURAL));
        assertEquals(2, BinarySearchDeluxe.lastIndexOf(values, "Oslo", NATURAL));
    }

    @Test
    void findsFirstAndLastOccurrence() {
        String[] values = {"a", "b", "b", "b", "c"};
        assertEquals(1, BinarySearchDeluxe.firstIndexOf(values, "b", NATURAL));
        assertEquals(3, BinarySearchDeluxe.lastIndexOf(values, "b", NATURAL));
    }

    @Test
    void returnsMinusOneWhenKeyIsAbsentOrArrayIsEmpty() {
        assertEquals(-1, BinarySearchDeluxe.firstIndexOf(new String[0], "b", NATURAL));
        assertEquals(-1, BinarySearchDeluxe.lastIndexOf(new String[] {"a", "c"}, "b", NATURAL));
    }

    @Test
    void rejectsNullArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> BinarySearchDeluxe.firstIndexOf(null, "a", NATURAL));
        assertThrows(IllegalArgumentException.class,
                () -> BinarySearchDeluxe.firstIndexOf(new String[] {"a"}, null, NATURAL));
        assertThrows(IllegalArgumentException.class,
                () -> BinarySearchDeluxe.lastIndexOf(new String[] {"a"}, "a", null));
    }
}
