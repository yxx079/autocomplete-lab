package edu.course.autocomplete.data;

import edu.course.autocomplete.model.Term;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** 读取“记录数 + 权重制表符查询文本”格式的 Autocomplete 数据文件。 */
public final class DatasetLoader {
    private DatasetLoader() {
    }

    public static List<Term> load(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String countLine = reader.readLine();
            if (countLine == null) {
                throw new IllegalArgumentException("dataset is empty");
            }
            int expected = parseCount(countLine, path);
            List<Term> terms = new ArrayList<>(expected);
            for (int lineNumber = 2; lineNumber <= expected + 1; lineNumber++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IllegalArgumentException(
                            "expected " + expected + " records but found " + terms.size());
                }
                terms.add(parseTerm(line, lineNumber));
            }
            String trailing;
            while ((trailing = reader.readLine()) != null) {
                if (!trailing.isBlank()) {
                    throw new IllegalArgumentException("dataset contains more than " + expected + " records");
                }
            }
            return List.copyOf(terms);
        }
    }

    private static int parseCount(String line, Path path) {
        try {
            int count = Integer.parseInt(line.strip());
            if (count < 0) {
                throw new IllegalArgumentException("negative record count in " + path);
            }
            return count;
        }
        catch (NumberFormatException exception) {
            throw new IllegalArgumentException("invalid record count in " + path, exception);
        }
    }

    private static Term parseTerm(String line, int lineNumber) {
        int tab = line.indexOf('\t');
        if (tab < 0) {
            throw new IllegalArgumentException("line " + lineNumber + " has no tab separator");
        }
        String weightText = line.substring(0, tab).strip();
        String query = line.substring(tab + 1);
        try {
            return new Term(query, Long.parseLong(weightText));
        }
        catch (NumberFormatException exception) {
            throw new IllegalArgumentException("line " + lineNumber + " has an invalid weight", exception);
        }
    }
}
