package edu.course.autocomplete.compatibility;

import edu.course.autocomplete.search.BinarySearchAutocomplete;

import edu.course.autocomplete.model.Term;

import java.util.Arrays;
import java.util.List;

/** 提供与 Princeton Autocomplete 接口一致的数组形式 API。 */
public final class Autocomplete {
    private final BinarySearchAutocomplete engine;

    public Autocomplete(Term[] terms) {
        if (terms == null) {
            throw new IllegalArgumentException("terms must not be null");
        }
        engine = new BinarySearchAutocomplete(Arrays.asList(terms.clone()));
    }

    public Term[] allMatches(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix must not be null");
        }
        List<Term> matches = engine.allMatches(prefix, Integer.MAX_VALUE);
        return matches.toArray(new Term[0]);
    }

    public int numberOfMatches(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix must not be null");
        }
        return Math.toIntExact(engine.numberOfMatches(prefix));
    }
}
