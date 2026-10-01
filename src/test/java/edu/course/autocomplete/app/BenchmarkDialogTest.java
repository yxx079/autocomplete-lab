package edu.course.autocomplete.app;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BenchmarkDialogTest {
    @TempDir Path tempDir;

    @Test
    void findsModuleRootInsteadOfParentWorkspace() throws Exception {
        Files.createFile(tempDir.resolve("pom.xml"));
        Path module = Files.createDirectories(tempDir.resolve("module"));
        Files.createFile(module.resolve("pom.xml"));
        Path classes = Files.createDirectories(module.resolve("target/classes"));
        assertEquals(module, BenchmarkDialog.findProjectRoot(classes));
    }

    @Test
    void returnsNullWhenThereIsNoProject() {
        assertNull(BenchmarkDialog.findProjectRoot(tempDir));
    }
}
