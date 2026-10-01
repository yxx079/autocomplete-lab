package edu.course.autocomplete.engine;

import edu.course.autocomplete.model.Term;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** 对每次查询扫描全部词条的线性检索实现。 */
public final class LinearAutocomplete implements AutocompleteEngine {
    private final List<Term> terms;

    public LinearAutocomplete(Collection<Term> terms) {
        this.terms = validatedCopy(terms);
    }

    @Override
    public List<Term> allMatches(String prefix, int limit) {
        validateQuery(prefix, limit);
        if (limit == 0) {
            return List.of();
        }
        List<Term> matches = new ArrayList<>();
        for (Term term : terms) {
            if (term.query().startsWith(prefix)) {
                matches.add(term);
            }
        }
        matches.sort(TermRanking.BY_WEIGHT_THEN_QUERY);
        return List.copyOf(matches.subList(0, Math.min(limit, matches.size())));
    }

    @Override
    public long numberOfMatches(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix must not be null");
        }
        long count = 0;
        for (Term term : terms) {
            if (term.query().startsWith(prefix)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String name() {
        return "linear";
    }

    static List<Term> validatedCopy(Collection<Term> source) {
        if (source == null) {
            throw new IllegalArgumentException("terms must not be null");
        }
        List<Term> copy = new ArrayList<>(source.size());
        for (Term term : source) {
            if (term == null) {
                throw new IllegalArgumentException("terms must not contain null");
            }
            copy.add(term);
        }
        return List.copyOf(copy);
    }

    static void validateQuery(String prefix, int limit) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix must not be null");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be non-negative");
        }
    }
}
