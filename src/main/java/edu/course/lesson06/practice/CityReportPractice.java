package edu.course.lesson06.practice;

import edu.course.autocomplete.io.DatasetLoader;
import edu.course.autocomplete.model.Term;
import edu.course.autocomplete.search.AutocompleteEngine;
import edu.course.autocomplete.search.LinearAutocomplete;
import edu.course.lesson06.support.DatasetPaths;
import java.util.List;

/** 将现有加载、查询和展示方法组合为一次报告；不用重写检索或排名。 */
public final class CityReportPractice {
    private CityReportPractice() {}
    public static String buildReport(AutocompleteEngine engine, String prefix, int limit, int labelLimit) {
        // TODO 1：调用 engine.numberOfMatches，取得不受显示上限影响的总数量。
        long total = 0;
        // TODO 2：调用 engine.allMatches，取得最多 limit 条结果。
        List<Term> matches = List.of();
        String report = "前缀：[" + prefix + "]\n总匹配数：" + total
                + "\n显示条数：" + matches.size() + "\n";
        // TODO 3：遍历 matches，将原始名称、权重和 firstPart 得到的简称追加到 report。
        return report;
    }
    /** 入口和数据读取已经提供。启动参数留空；先只关注 buildReport。 */
    public static void main(String[] args) throws Exception {
        List<Term> terms = DatasetLoader.load(DatasetPaths.cities());
        System.out.println("加载词条：" + terms.size());
        AutocompleteEngine engine = new LinearAutocomplete(terms);
        System.out.print(buildReport(engine, "San ", 3, 12));
    }
}
