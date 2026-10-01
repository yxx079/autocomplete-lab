package edu.course.autocomplete.comparison;

import edu.course.autocomplete.model.Term;

import java.util.Comparator;

/** 先按权重降序比较，权重相同时再按完整查询文本升序比较。 */
public final class WeightThenQueryComparator implements Comparator<Term> {
    /** 共用同一个无状态排名规则，避免另设只保存常量的包装类。 */
    public static final WeightThenQueryComparator INSTANCE = new WeightThenQueryComparator();
    @Override
    public int compare(Term left, Term right) {
        int weightComparison = Long.compare(right.weight(), left.weight());
        if (weightComparison != 0) {
            return weightComparison;
        }
        return left.query().compareTo(right.query());
    }
}
