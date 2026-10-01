package edu.course.autocomplete.benchmark;

import edu.course.autocomplete.search.AutocompleteEngine;
import edu.course.autocomplete.search.LinearAutocomplete;
import edu.course.autocomplete.model.Term;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BenchmarkRunnerTest {
    @TempDir
    Path tempDir;

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
