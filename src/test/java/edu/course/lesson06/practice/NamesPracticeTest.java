package edu.course.lesson06.practice;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NamesPracticeTest {
    private String output(String[] names, int minimum) {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            NamesPractice.printLongNames(names, minimum);
        } finally { System.setOut(original); }
        return bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
    @Test void printsOnlyMatchingNames() { assertEquals("Shanghai\n", output(new String[] {"Rome", "Shanghai", "Oslo"}, 5)); }
    @Test void includesEqualLength() { assertEquals("Rome\nOslo\n", output(new String[] {"Rome", "Oslo"}, 4)); }
    @Test void noMatchPrintsNothing() { assertEquals("", output(new String[] {"Rome", "Oslo"}, 9)); }
    @Test void emptyArrayPrintsNothing() { assertEquals("", output(new String[0], 5)); }
    @Test void zeroMinimumIncludesEmptyName() { assertEquals("\nRome\n", output(new String[] {"", "Rome"}, 0)); }
    @Test void preservesInputAndOrder() {
        String[] names = {"Vienna", "Rome", "Shanghai"};
        assertEquals("Vienna\nShanghai\n", output(names, 5));
        assertArrayEquals(new String[] {"Vienna", "Rome", "Shanghai"}, names);
    }
}
