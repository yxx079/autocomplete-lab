package edu.course.autocomplete.benchmark;

import edu.course.autocomplete.search.AutocompleteEngine;
import edu.course.autocomplete.search.LinearAutocomplete;
import edu.course.autocomplete.model.Term;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BenchmarkRunnerTest {
    @TempDir
    Path tempDir;

    @Test
    void timesCountAndResultsForEveryWarmupAndMeasuredQuery() {
        CountingEngine a = new CountingEngine("a", 2, List.of(new Term("apple", 3)));
        CountingEngine b = new CountingEngine("b", 2, List.of(new Term("apple", 3)));
        var report = BenchmarkRunner.compare(List.of("a", "b"),
                name -> name.equals("a") ? a : b, "app", 10, 2, 5);
        assertEquals(Boolean.TRUE, report.resultsConsistent());
        for (CountingEngine engine : List.of(a, b)) {
            assertEquals(7, engine.countCalls);
            assertEquals(7, engine.resultCalls);
        }
        for (var row : report.measurements()) {
            assertEquals(2L, row.matches());
            assertTrue(row.medianNanos() >= 0);
            assertTrue(row.p95Nanos() >= row.medianNanos());
        }
    }

    @Test
    void detectsDifferentResultWeightsEvenWhenMatchCountsAgree() {
        var report = BenchmarkRunner.compare(List.of("a", "b"), name ->
                new CountingEngine(name, 1, List.of(new Term("apple", name.equals("a") ? 3 : 4))),
                "app", 10, 0, 2);
        assertEquals(Boolean.FALSE, report.resultsConsistent());
    }

    @Test
    void reportsTodoSeparatelyAndExportsTheSnapshotWithBlankFailedTimings() throws Exception {
        var report = BenchmarkRunner.compare(List.of("linear", "binary"), name -> {
            if (name.equals("binary")) throw new UnsupportedOperationException("TODO: matchingRange");
            return new CountingEngine(name, 1, List.of(new Term("app", 3)));
        }, "a,\"b", 10, 1, 3);
        assertEquals(2, report.measurements().size());
        assertTrue(report.measurements().get(0).succeeded());
        assertEquals("TODO: matchingRange", report.measurements().get(1).error());
        assertEquals(null, report.measurements().get(1).medianNanos());
        assertEquals(null, report.resultsConsistent());
        Path output = tempDir.resolve("report.csv");
        BenchmarkRunner.export(report, tempDir.resolve("data/benchmark/actors.txt").toAbsolutePath(), 2875183, output);
        List<String> lines = Files.readAllLines(output);
        assertEquals(3, lines.size());
        assertTrue(lines.get(1).startsWith("actors.txt,2875183,\"a,\"\"b\",linear,10,1,3,1,"));
        assertTrue(lines.get(1).contains(report.measurements().get(0).medianNanos().toString()));
        assertTrue(lines.get(2).endsWith(",,,,,error,TODO: matchingRange"));
    }

    @Test
    void rejectsInvalidRoundsAndUsesCountsWhenCheckingAgreement() {
        assertThrows(IllegalArgumentException.class, () -> BenchmarkRunner.compare(
                List.of("a"), name -> new CountingEngine(name, 0, List.of()), "app", 10, 0, 0));
        var report = BenchmarkRunner.compare(List.of("a", "b"), name ->
                new CountingEngine(name, name.equals("a") ? 1 : 2, List.of(new Term("app", 3))),
                "app", 10, 0, 1);
        assertEquals(Boolean.FALSE, report.resultsConsistent());
    }

    private static final class CountingEngine implements AutocompleteEngine {
        final String name;
        final long count;
        final List<Term> terms;
        int countCalls;
        int resultCalls;
        CountingEngine(String name, long count, List<Term> terms) {
            this.name = name; this.count = count; this.terms = terms;
        }
        @Override public String name() { return name; }
        @Override public long numberOfMatches(String prefix) { countCalls++; return count; }
        @Override public List<Term> allMatches(String prefix, int limit) { resultCalls++; return terms; }
    }

    @Test
    void writesOneCsvRowPerPrefix() throws Exception {
        AutocompleteEngine engine = new LinearAutocomplete(List.of(
                new Term("app", 1), new Term("apple", 2), new Term("banana", 3)));
        Path output = tempDir.resolve("benchmark.csv");

        BenchmarkRunner.run(engine, List.of("app", "ban"), 2, 3, output);

        List<String> lines = Files.readAllLines(output);
        assertEquals(3, lines.size());
        assertEquals("engine,prefix,warmup_rounds,measure_rounds,matches,median_ns,p95_ns", lines.get(0));
        assertTrue(lines.get(1).startsWith("linear,app,2,3,2,"));
    }
}
