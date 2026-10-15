package edu.course.lesson06.practice;

import edu.course.autocomplete.io.DatasetLoader;
import edu.course.autocomplete.model.Term;
import edu.course.autocomplete.search.AutocompleteEngine;
import edu.course.autocomplete.search.LinearAutocomplete;
import edu.course.autocomplete.search.BinarySearchAutocomplete;
import edu.course.lesson06.support.DatasetPaths;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CityReportPracticeTest {
    private AutocompleteEngine fixture() {
        return new LinearAutocomplete(List.of(
            new Term("San Diego, California, United States", 1307402),
            new Term("San Antonio, Texas, United States", 1327407),
            new Term("San Jose, California, United States", 945942),
            new Term("Rome, Italy", 2563241)));
    }
    @Test void totalCountIsIndependentOfDisplayLimit() {
        assertEquals("前缀：[San ]\n总匹配数：3\n显示条数：1\n"
            + "San Antonio, Texas, United States | 权重：1327407 | 简称：San Antonio,\n",
            CityReportPractice.buildReport(fixture(), "San ", 1, 12));
    }
    @Test void noMatchesProducesHeaderOnly() {
        assertEquals("前缀：[NoSuchCity]\n总匹配数：0\n显示条数：0\n", CityReportPractice.buildReport(fixture(), "NoSuchCity", 3, 12));
    }
    @Test void zeroDisplayLimitKeepsTotalCount() {
        assertEquals("前缀：[San ]\n总匹配数：3\n显示条数：0\n", CityReportPractice.buildReport(fixture(), "San ", 0, 12));
    }
    @Test void prefixIsCaseSensitive() {
        assertEquals("前缀：[san ]\n总匹配数：0\n显示条数：0\n", CityReportPractice.buildReport(fixture(), "san ", 3, 12));
    }
    @Test void repeatedQueriesDoNotLeakPreviousResults() {
        AutocompleteEngine engine = fixture();
        String first = CityReportPractice.buildReport(engine, "San ", 3, 12);
        assertTrue(CityReportPractice.buildReport(engine, "Rome", 3, 12).contains("总匹配数：1\n"));
        assertEquals(first, CityReportPractice.buildReport(engine, "San ", 3, 12));
    }
    @Test void zeroLabelLimitDoesNotReplaceOriginalName() {
        String report = CityReportPractice.buildReport(fixture(), "San ", 1, 0);
        assertTrue(report.contains("San Antonio, Texas, United States | 权重：1327407 | 简称：\n"));
    }
    @Test void emptyPrefixMatchesWholeFixture() {
        assertTrue(CityReportPractice.buildReport(fixture(), "", 2, 12).startsWith("前缀：[]\n总匹配数：4\n显示条数：2\n"));
    }
    @Test void fullCityDatasetProducesExpectedThreeRows() throws Exception {
        List<Term> terms = DatasetLoader.load(DatasetPaths.cities());
        assertEquals(93827, terms.size());
        String expected = "前缀：[San ]\n总匹配数：1061\n显示条数：3\n"
            + "San Antonio, Texas, United States | 权重：1327407 | 简称：San Antonio,\n"
            + "San Diego, California, United States | 权重：1307402 | 简称：San Diego, C\n"
            + "San Jose, California, United States | 权重：945942 | 简称：San Jose, Ca\n";
        assertEquals(expected, CityReportPractice.buildReport(new LinearAutocomplete(terms), "San ", 3, 12));
        assertEquals(expected, CityReportPractice.buildReport(new BinarySearchAutocomplete(terms), "San ", 3, 12));
    }
}
