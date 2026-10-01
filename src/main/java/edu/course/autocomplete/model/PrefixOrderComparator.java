package edu.course.autocomplete.model;

import java.util.Comparator;

/** 最多比较查询文本前 prefixLength 个 UTF-16 代码单元。 */
public final class PrefixOrderComparator implements Comparator<Term> {
    private final int prefixLength;

    public PrefixOrderComparator(int prefixLength) {
        if (prefixLength < 0) {
            throw new IllegalArgumentException("prefix length must be non-negative");
        }
        this.prefixLength = prefixLength;
    }

    @Override
    public int compare(Term left, Term right) {
        String leftQuery = left.query();
        String rightQuery = right.query();
        int sharedLimit = Math.min(
                prefixLength,
                Math.min(leftQuery.length(), rightQuery.length()));

        for (int i = 0; i < sharedLimit; i++) {
            int comparison = Character.compare(leftQuery.charAt(i), rightQuery.charAt(i));
            if (comparison != 0) {
                return comparison;
            }
        }

        int leftLength = Math.min(leftQuery.length(), prefixLength);
        int rightLength = Math.min(rightQuery.length(), prefixLength);
        return Integer.compare(leftLength, rightLength);
    }
}
