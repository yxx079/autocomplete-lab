package edu.course.lesson06.support;

import java.nio.file.Files;
import java.nio.file.Path;

/** 定位当前项目已有的真实词库。此类已提供，课堂无需实现文件搜索。 */
public final class DatasetPaths {
    private DatasetPaths() {}
    public static Path cities() {
        Path directory = Path.of("").toAbsolutePath();
        while (directory != null) {
            Path direct = directory.resolve("data/benchmark/cities.txt");
            if (Files.isRegularFile(direct)) return direct;
            Path workspace = directory.resolve("Autocomplete_教学项目/data/benchmark/cities.txt");
            if (Files.isRegularFile(workspace)) return workspace;
            directory = directory.getParent();
        }
        throw new IllegalStateException("找不到 cities.txt。请在 autocomplete-lab 项目目录运行，并保留 data/benchmark/cities.txt。");
    }
}
