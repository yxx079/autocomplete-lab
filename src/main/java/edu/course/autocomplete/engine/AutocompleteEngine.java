package edu.course.autocomplete.engine;

import edu.course.autocomplete.model.Term;

import java.util.List;

/**
 * 定义所有自动补全算法共同遵守的查询合同。
 *
 * <p>GUI 和命令行程序只依赖这个接口，不需要知道底层使用线性扫描、二分查找
 * 等实现。各个实现必须对相同输入给出含义一致的结果。
 */
public interface AutocompleteEngine {
    /**
     * 返回以 prefix 开头、按照项目排名规则排列的结果。
     *
     * @param prefix 查询前缀，不能为 null
     * @param limit 最多返回多少条，必须大于或等于 0
     * @return 至多 limit 条结果；没有匹配时返回空列表，不返回 null
     */
    List<Term> allMatches(String prefix, int limit);

    /**
     * 返回全部匹配数量，不受 allMatches 的 limit 限制。
     *
     * @param prefix 查询前缀，不能为 null
     * @return 全部匹配数量
     */
    long numberOfMatches(String prefix);

    /** 返回当前实现的简短名称，例如 linear 或 binary。 */
    String name();
}
