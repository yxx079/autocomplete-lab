package edu.course.autocomplete.benchmark;

import edu.course.autocomplete.model.Term;
import edu.course.autocomplete.search.AutocompleteEngine;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/** 与 GUI 一致地测量计数及取结果两次调用，保留结果供展示和 CSV 导出。 */
public final class BenchmarkRunner {
    private BenchmarkRunner() { }

    public record Measurement(String engine, Long matches, Long medianNanos,
                              Long p95Nanos, String error) {
        public boolean succeeded() { return error == null; }
    }

    public record Comparison(String prefix, int limit, int warmupRounds, int measureRounds,
                             List<Measurement> measurements, Boolean resultsConsistent) {
        public Comparison { measurements = List.copyOf(measurements); }
    }

    private record QueryResult(long count, List<Term> terms) { }
    private record Measured(Measurement measurement, QueryResult result) { }

    /** 工厂在后台构建每个引擎；构建成本不进入查询计时。失败的算法单独报告。 */
    public static Comparison compare(List<String> names, Function<String, AutocompleteEngine> factory,
                                     String prefix, int limit, int warmup, int rounds) {
        validate(prefix, limit, warmup, rounds);
        if (names == null || names.isEmpty() || factory == null) {
            throw new IllegalArgumentException("engines and factory must be provided");
        }
        List<Measurement> rows = new ArrayList<>();
        QueryResult reference = null;
        boolean consistent = true;
        int successes = 0;
        for (String name : names) {
            checkInterrupted();
            try {
                Measured measured = measure(factory.apply(name), prefix, limit, warmup, rounds);
                rows.add(measured.measurement());
                if (reference == null) reference = measured.result();
                else consistent &= sameResults(reference, measured.result());
                successes++;
            } catch (RuntimeException exception) {
                if (exception instanceof java.util.concurrent.CancellationException) throw exception;
                String message = exception.getMessage();
                rows.add(new Measurement(name, null, null, null,
                        message == null ? exception.getClass().getSimpleName() : message));
            }
        }
        Boolean agreement = successes == names.size() && successes >= 2 ? consistent : null;
        return new Comparison(prefix, limit, warmup, rounds, rows, agreement);
    }

    private static Measured measure(AutocompleteEngine engine, String prefix, int limit,
                                    int warmup, int rounds) {
        if (engine == null) throw new IllegalArgumentException("engine must not be null");
        for (int i = 0; i < warmup; i++) {
            checkInterrupted();
            engine.numberOfMatches(prefix);
            engine.allMatches(prefix, limit);
        }
        long[] samples = new long[rounds];
        QueryResult last = null;
        for (int i = 0; i < rounds; i++) {
            checkInterrupted();
            long start = System.nanoTime();
            long count = engine.numberOfMatches(prefix);
            List<Term> matches = engine.allMatches(prefix, limit);
            samples[i] = System.nanoTime() - start;
            last = new QueryResult(count, List.copyOf(matches));
        }
        Arrays.sort(samples);
        return new Measured(new Measurement(engine.name(), last.count(),
                percentile(samples, 0.50), percentile(samples, 0.95), null), last);
    }

    private static boolean sameResults(QueryResult left, QueryResult right) {
        if (left.count() != right.count() || left.terms().size() != right.terms().size()) return false;
        for (int i = 0; i < left.terms().size(); i++) {
            Term a = left.terms().get(i), b = right.terms().get(i);
            if (!a.query().equals(b.query()) || a.weight() != b.weight()) return false;
        }
        return true;
    }

    private static void validate(String prefix, int limit, int warmup, int rounds) {
        if (prefix == null || limit <= 0 || warmup < 0 || rounds <= 0) {
            throw new IllegalArgumentException("invalid benchmark arguments");
        }
    }

    private static void checkInterrupted() {
        if (Thread.currentThread().isInterrupted()) {
            throw new java.util.concurrent.CancellationException("性能测试已取消");
        }
    }

    /** 保留原来的批量调用入口，计时现在同样包含 numberOfMatches 与 allMatches。 */
    public static void run(AutocompleteEngine engine, List<String> prefixes,
                           int warmupRounds, int measureRounds, Path output) throws IOException {
        if (engine == null || prefixes == null || output == null) {
            throw new IllegalArgumentException("invalid benchmark arguments");
        }
        validate("", 10, warmupRounds, measureRounds);
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            writer.write("engine,prefix,warmup_rounds,measure_rounds,matches,median_ns,p95_ns");
            writer.newLine();
            for (String prefix : prefixes) {
                validate(prefix, 10, warmupRounds, measureRounds);
                Measurement row = measure(engine, prefix, 10, warmupRounds, measureRounds).measurement();
                writer.write(String.join(",", csv(row.engine()), csv(prefix),
                        Integer.toString(warmupRounds), Integer.toString(measureRounds),
                        row.matches().toString(), row.medianNanos().toString(), row.p95Nanos().toString()));
                writer.newLine();
            }
        }
    }

    /** 导出已经测完的快照，不在保存文件时重新测量或读取当前 GUI 状态。 */
    public static void export(Comparison comparison, Path dataset, int records, Path output) throws IOException {
        if (comparison == null || dataset == null || records < 0 || output == null) {
            throw new IllegalArgumentException("invalid export arguments");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            writer.write("dataset,records,prefix,engine,limit,warmup_rounds,measure_rounds,matches,median_ns,p95_ns,results_consistent,status,error");
            writer.newLine();
            for (Measurement row : comparison.measurements()) {
                writer.write(String.join(",", csv(dataset.getFileName().toString()), Integer.toString(records),
                        csv(comparison.prefix()), csv(row.engine()), Integer.toString(comparison.limit()),
                        Integer.toString(comparison.warmupRounds()), Integer.toString(comparison.measureRounds()),
                        cell(row.matches()), cell(row.medianNanos()), cell(row.p95Nanos()),
                        cell(comparison.resultsConsistent()), row.succeeded() ? "ok" : "error", csv(row.error())));
                writer.newLine();
            }
        }
    }

    private static String cell(Object value) { return value == null ? "" : value.toString(); }
    private static long percentile(long[] sorted, double percentile) {
        return sorted[Math.max(0, (int) Math.ceil(percentile * sorted.length) - 1)];
    }
    private static String csv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
