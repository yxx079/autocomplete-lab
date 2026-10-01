package edu.course.autocomplete.model;

import edu.course.autocomplete.comparison.PrefixOrderComparator;
import edu.course.autocomplete.comparison.ReverseWeightComparator;

import java.util.Comparator;
import java.util.Objects;

/** 保存查询文本及其非负排序权重的不可变数据对象。 */
public final class Term implements Comparable<Term> {
    private final String query;
    private final long weight;

    public Term(String query, long weight) {
        if (query == null) {
            throw new IllegalArgumentException("query must not be null");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("weight must be non-negative");
        }
        this.query = query;
        this.weight = weight;
    }

    public String query() {
        return query;
    }

    public long weight() {
        return weight;
    }

    public static Comparator<Term> byReverseWeightOrder() {
        return new ReverseWeightComparator();
    }

    public static Comparator<Term> byPrefixOrder(int r) {
        if (r < 0) {
            throw new IllegalArgumentException("prefix length must be non-negative");
        }
        return new PrefixOrderComparator(r);
    }

    @Override
    public int compareTo(Term that) {
        Objects.requireNonNull(that, "that");
        return query.compareTo(that.query);
    }

    @Override
    public String toString() {
        return String.format("%12d\t%s", weight, query);
    }
}
