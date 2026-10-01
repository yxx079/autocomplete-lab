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
        Term[] matches = Arrays.copyOfRange(sortedTerms, range[0], range[1] + 1);
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

    private int[] matchingRange(String prefix) {
        /*
         * TODO：返回前缀匹配范围的闭区间 {first, last}。
         *
         * 已知条件：
         * - sortedTerms 已在构造器中按照 Term 的自然顺序排序。
         * - Term.byPrefixOrder(prefix.length()) 只比较前 prefix.length() 个位置。
         *
         * 建议步骤：
         * 1. 创建前缀 Comparator。
         * 2. 创建 query 为 prefix 的临时 Term；weight 不参与前缀比较。
         * 3. 调用 BinarySearchDeluxe 找到 first。
         * 4. first 为 -1 时直接返回 {-1, -1}。
         * 5. 再找到 last，返回 {first, last}。
         *
         * 检索阶段要求保持 O(log n)，不要修改方法签名。
         */
        throw new UnsupportedOperationException("TODO: matchingRange");
    }

    @Override
    public String name() {
        return "binary";
    }
}
