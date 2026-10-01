package edu.course.autocomplete.compatibility;

import edu.course.autocomplete.model.Term;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AutocompleteCompatibilityTest {
    @Test
    void officialApiReturnsTermArrayInDescendingWeightOrder() {
        Term[] terms = {
                new Term("auto", 10),
                new Term("automatic", 30),
                new Term("automobile", 20),
                new Term("binary", 100)
        };
        Autocomplete autocomplete = new Autocomplete(terms);

        assertArrayEquals(new String[] {"automatic", "automobile", "auto"},
                java.util.Arrays.stream(autocomplete.allMatches("auto")).map(Term::query).toArray(String[]::new));
        assertEquals(3, autocomplete.numberOfMatches("auto"));
    }

    @Test
    void officialApiDefensivelyCopiesInputAndRejectsNulls() {
        Term[] terms = {new Term("java", 10)};
        Autocomplete autocomplete = new Autocomplete(terms);
        terms[0] = new Term("changed", 20);
        assertEquals("java", autocomplete.allMatches("")[0].query());
        assertThrows(IllegalArgumentException.class, () -> new Autocomplete(null));
        assertThrows(IllegalArgumentException.class, () -> new Autocomplete(new Term[] {null}));
        assertThrows(IllegalArgumentException.class, () -> autocomplete.allMatches(null));
    }
}
