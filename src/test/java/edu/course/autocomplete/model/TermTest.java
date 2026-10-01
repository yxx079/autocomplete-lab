package edu.course.autocomplete.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TermTest {
    @Test
    void constructorRejectsNullQueryAndNegativeWeight() {
        assertThrows(IllegalArgumentException.class, () -> new Term(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new Term("java", -1));
    }

    @Test
    void accessorsExposeTheImmutableValue() {
        Term term = new Term("java compiler", 42);
        assertEquals("java compiler", term.query());
        assertEquals(42, term.weight());
        assertEquals("          42\tjava compiler", term.toString());
    }

    static Stream<Arguments> naturalOrderCases() {
        return Stream.of(
                Arguments.of("apple", "banana", -1),
                Arguments.of("banana", "apple", 1),
                Arguments.of("same", "same", 0),
                Arguments.of("你", "我", -1));
    }

    @ParameterizedTest
    @MethodSource("naturalOrderCases")
    void naturalOrderUsesTheQuery(String left, String right, int expectedSign) {
        int actual = new Term(left, 1).compareTo(new Term(right, 2));
        assertEquals(expectedSign, Integer.signum(actual));
    }

    @Test
    void reverseWeightComparatorProducesNegativePositiveAndZero() {
        Comparator<Term> comparator = Term.byReverseWeightOrder();
        assertTrue(comparator.compare(new Term("heavy", 20), new Term("light", 10)) < 0);
        assertTrue(comparator.compare(new Term("light", 10), new Term("heavy", 20)) > 0);
        assertEquals(0, comparator.compare(new Term("a", 10), new Term("b", 10)));
    }

    @Test
    void prefixComparatorUsesAtMostRCharacters() {
        Comparator<Term> firstTwo = Term.byPrefixOrder(2);
        assertEquals(0, firstTwo.compare(new Term("application", 1), new Term("apple", 2)));
        assertTrue(firstTwo.compare(new Term("ant", 1), new Term("bat", 1)) < 0);
        assertTrue(firstTwo.compare(new Term("cat", 1), new Term("bat", 1)) > 0);
        assertEquals(0, Term.byPrefixOrder(0).compare(new Term("a", 1), new Term("z", 1)));
    }

    @Test
    void prefixComparatorRejectsNegativeLength() {
        assertThrows(IllegalArgumentException.class, () -> Term.byPrefixOrder(-1));
    }
}
