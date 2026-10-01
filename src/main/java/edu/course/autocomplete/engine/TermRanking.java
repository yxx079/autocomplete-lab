package edu.course.autocomplete.engine;

import edu.course.autocomplete.model.Term;

import java.util.Comparator;

final class TermRanking {
    static final Comparator<Term> BY_WEIGHT_THEN_QUERY = new WeightThenQueryComparator();

    private TermRanking() {
    }
}
