package edu.course.autocomplete.benchmark;

import edu.course.autocomplete.search.AutocompleteEngine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 运行预热和重复测量，并将查询延迟统计写入 CSV 文件。 */
public final class BenchmarkRunner {
    private BenchmarkRunner() {
    }

    public static void run(AutocompleteEngine engine, List<String> prefixes,
                           int warmupRounds, int measureRounds, Path output) throws IOException {
        if (engine == null || prefixes == null || output == null || warmupRounds < 0 || measureRounds <= 0) {
            throw new IllegalArgumentException("invalid benchmark arguments");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            writer.write("engine,prefix,warmup_rounds,measure_rounds,matches,median_ns,p95_ns");
            writer.newLine();
            for (String prefix : prefixes) {
                for (int i = 0; i < warmupRounds; i++) {
                    engine.allMatches(prefix, 10);
                }
                List<Long> samples = new ArrayList<>(measureRounds);
                for (int i = 0; i < measureRounds; i++) {
                    long start = System.nanoTime();
                    engine.allMatches(prefix, 10);
                    samples.add(System.nanoTime() - start);
                }
                Collections.sort(samples);
                long median = percentile(samples, 0.50);
                long p95 = percentile(samples, 0.95);
                writer.write(String.join(",",
                        csv(engine.name()), csv(prefix), Integer.toString(warmupRounds),
                        Integer.toString(measureRounds), Long.toString(engine.numberOfMatches(prefix)),
                        Long.toString(median), Long.toString(p95)));
                writer.newLine();
            }
        }
    }

    private static long percentile(List<Long> sorted, double percentile) {
        int index = (int) Math.ceil(percentile * sorted.size()) - 1;
        return sorted.get(Math.max(0, Math.min(index, sorted.size() - 1)));
    }

    private static String csv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
