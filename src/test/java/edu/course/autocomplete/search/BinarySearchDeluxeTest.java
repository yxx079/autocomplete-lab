package edu.course.autocomplete.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

class BinarySearchDeluxeTest {
    private static final Comparator<String> NATURAL = Comparator.naturalOrder();

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
