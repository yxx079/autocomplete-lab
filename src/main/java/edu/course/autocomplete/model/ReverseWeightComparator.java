package edu.course.autocomplete.model;

import java.util.Comparator;

/** 按照权重从高到低比较两个 Term。 */
public final class ReverseWeightComparator implements Comparator<Term> {
    @Override
    public int compare(Term left, Term right) {
        return Long.compare(right.weight(), left.weight());
    }
}
