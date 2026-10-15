package edu.course.autocomplete.search;

import edu.course.autocomplete.comparison.WeightThenQueryComparator;

import edu.course.autocomplete.model.Term;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/** 预先排序词条，通过两次二分查找定位前缀区间，再对匹配结果排序。 */
public final class BinarySearchAutocomplete implements AutocompleteEngine {
    private final Term[] sortedTerms;

    public BinarySearchAutocomplete(Collection<Term> terms) {
        List<Term> copy = LinearAutocomplete.validatedCopy(terms);
        sortedTerms = copy.toArray(new Term[0]);
        // 按名称排序，保证相同前缀的词条连续。
        Arrays.sort(sortedTerms);
    }

    @Override
    public List<Term> allMatches(String prefix, int limit) {
        LinearAutocomplete.validateQuery(prefix, limit);
        if (limit == 0) {
            return List.of();
        }
        int[] range = matchingRange(prefix);
        if (range[0] < 0) {
            return List.of();
        }
        // 匹配范围包含右端，copyOfRange 的右端不包含。
        Term[] matches = Arrays.copyOfRange(sortedTerms, range[0], range[1] + 1);
        // 仅对匹配结果排名，保留检索数组的顺序。
        Arrays.sort(matches, WeightThenQueryComparator.INSTANCE);
        return List.copyOf(Arrays.asList(matches).subList(0, Math.min(limit, matches.length)));
    }

    @Override
    public long numberOfMatches(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix must not be null");
        }
        int[] range = matchingRange(prefix);
        return range[0] < 0 ? 0 : range[1] - range[0] + 1L;
    }

    /** 返回包含左右端点的匹配范围；无匹配时返回 {-1, -1}。 */
    private int[] matchingRange(String prefix) {
        Comparator<Term> comparator = Term.byPrefixOrder(prefix.length());
        // 权重不参与前缀比较。
        Term key = new Term(prefix, 0);
        int first = BinarySearchDeluxe.firstIndexOf(sortedTerms, key, comparator);
        if (first == -1) {
            return new int[] {-1, -1};
        }
        int last = BinarySearchDeluxe.lastIndexOf(sortedTerms, key, comparator);
        return new int[] {first, last};
    }

    @Override
    public String name() {
        return "binary";
    }
}
