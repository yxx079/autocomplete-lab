package edu.course.autocomplete.io;

import edu.course.autocomplete.model.Term;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DatasetLoaderTest {
    @TempDir
    Path tempDir;

    @Test
    void loadsOfficialCountWeightTabQueryFormatAndUnicode() throws IOException {
        Path file = tempDir.resolve("sample.txt");
        Files.writeString(file, "3\n10\tjava\n8\tjava virtual machine\n6\t你好\n");

        List<Term> terms = DatasetLoader.load(file);

        assertEquals(List.of("java", "java virtual machine", "你好"),
                terms.stream().map(Term::query).toList());
        assertEquals(8, terms.get(1).weight());
    }

    @Test
    void rejectsRecordCountMismatch() throws IOException {
        Path file = tempDir.resolve("wrong-count.txt");
        Files.writeString(file, "2\n10\tone\n");
        assertThrows(IllegalArgumentException.class, () -> DatasetLoader.load(file));
    }

    @Test
    void rejectsMissingTabAndNegativeWeight() throws IOException {
        Path missingTab = tempDir.resolve("missing-tab.txt");
        Files.writeString(missingTab, "1\n10 one\n");
        assertThrows(IllegalArgumentException.class, () -> DatasetLoader.load(missingTab));

        Path negative = tempDir.resolve("negative.txt");
        Files.writeString(negative, "1\n-1\tone\n");
        assertThrows(IllegalArgumentException.class, () -> DatasetLoader.load(negative));
    }

    @Test
    void acceptsBlankLinesAfterTheDeclaredRecords() throws IOException {
        Path file = tempDir.resolve("trailing-blank.txt");
        Files.writeString(file, "1\n10\tone\n\n");
        assertEquals(List.of("one"), DatasetLoader.load(file).stream().map(Term::query).toList());
    }
}
