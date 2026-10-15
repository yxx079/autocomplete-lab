package edu.course.lesson06.practice;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LabelPracticeTest {
    @Test void truncatesOnlyLongText() { assertEquals("Shanghai", LabelPractice.firstPart("Shanghai, China", 8)); }
    @Test void keepsShortText() { assertEquals("Rome", LabelPractice.firstPart("Rome", 8)); }
    @Test void keepsEqualLength() { assertEquals("Oslo", LabelPractice.firstPart("Oslo", 4)); }
    @Test void acceptsEmptyText() { assertEquals("", LabelPractice.firstPart("", 8)); }
    @Test void zeroLimitReturnsEmpty() { assertEquals("", LabelPractice.firstPart("Shanghai", 0)); }
    @Test void displayDoesNotReplaceOriginal() {
        String original = "Shanghai, China";
        LabelPractice.firstPart(original, 8);
        assertEquals("Shanghai, China", original);
    }
}
