package edu.course.autocomplete.app;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class AutocompleteGuiDatasetTest {
    @TempDir Path directory;

    @Test
    void startsWithoutArgumentsAndExplicitOptionsStillWork() {
        var defaults = AutocompleteGui.parseArguments(new String[0]);
        assertEquals(Path.of("data/classroom/tiny.txt"), defaults.dataset());
        assertEquals("linear", defaults.engineName());
        assertEquals(10, defaults.limit());
        var explicit = AutocompleteGui.parseArguments(new String[] {"actors.txt", "BINARY", "20"});
        assertEquals(Path.of("actors.txt"), explicit.dataset());
        assertEquals("binary", explicit.engineName());
        assertEquals(20, explicit.limit());
        assertThrows(IllegalArgumentException.class,
                () -> AutocompleteGui.parseArguments(new String[] {"actors.txt"}));
    }

    @Test
    void menuIncludesDataFilesButExcludesNotes() throws Exception {
        Path a = write("a.txt", "1\n3\tapple\n");
        Path b = write("b.txt", "1\n4\t北京\n");
        write("notes.txt", "说明文本\n");
        assertEquals(List.of(a, b), AutocompleteGui.availableDatasets(a));
    }

    @Test
    void loadingAnotherFileUpdatesRecordsAndSupportsMixedText() throws Exception {
        var first = AutocompleteGui.loadDataset(write("a.txt", "1\n3\tapple\n"), "linear");
        var second = AutocompleteGui.loadDataset(
                write("b.txt", "2\n9\t北京 Java\n7\tJava 教程\n"), "linear");
        assertEquals(1, first.recordCount);
        assertEquals(2, second.recordCount);
        assertEquals("北京 Java", second.engine.allMatches("北京 J", 10).get(0).query());
        assertEquals(0, second.engine.numberOfMatches("java"));
        assertEquals(0, second.engine.numberOfMatches("beijing"));
        assertEquals(1, first.engine.numberOfMatches("app"));
    }

    @Test
    void malformedFileDoesNotMutatePreviousEngine() throws Exception {
        var previous = AutocompleteGui.loadDataset(write("good.txt", "1\n3\tapple\n"), "linear");
        Path bad = write("bad.txt", "2\n3\tbanana\n");
        assertThrows(IllegalArgumentException.class, () -> AutocompleteGui.loadDataset(bad, "linear"));
        assertEquals(1, previous.engine.numberOfMatches("app"));
    }

    @Test
    void menuUsesFactoryNamesAndUnknownAlgorithmIsRejected() throws Exception {
        assertEquals(List.of("linear", "binary", "trie"), EngineFactory.supportedEngineNames());
        Path data = write("data.txt", "1\n3\tapple\n");
        for (String name : EngineFactory.supportedEngineNames()) {
            assertEquals(name, AutocompleteGui.loadDataset(data, name).engine.name());
        }
        assertThrows(IllegalArgumentException.class,
                () -> AutocompleteGui.loadDataset(data, "missing"));
    }

    private Path write(String name, String content) throws Exception {
        return Files.writeString(directory.resolve(name), content);
    }
}
