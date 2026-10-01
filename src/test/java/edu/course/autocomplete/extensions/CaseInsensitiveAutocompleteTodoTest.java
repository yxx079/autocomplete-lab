package edu.course.autocomplete.extensions;

import edu.course.autocomplete.search.AutocompleteEngine;
import edu.course.autocomplete.search.BinarySearchAutocomplete;
import edu.course.autocomplete.search.LinearAutocomplete;
import edu.course.autocomplete.search.TrieAutocomplete;
import edu.course.autocomplete.model.Term;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/** TODO 作业：让所有前缀检索引擎忽略英文大小写，但保留结果的原始文本。 */
class CaseInsensitiveAutocompleteTodoTest {
    private static final List<Term> TERMS = List.of(
            new Term("Microsoft", 30),
            new Term("microchip", 20),
            new Term("MICA", 10),
            new Term("Apple", 100));

    private record EngineCase(String name, Function<List<Term>, AutocompleteEngine> factory) {
    }

    private static Stream<EngineCase> engines() {
        return Stream.of(
                new EngineCase("linear", LinearAutocomplete::new),
                new EngineCase("binary", BinarySearchAutocomplete::new),
                new EngineCase("trie", TrieAutocomplete::new));
    }

    @TestFactory
    Stream<DynamicTest> allEnginesMatchMixedCaseAsciiPrefix() {
        return engines().map(testCase -> DynamicTest.dynamicTest(testCase.name(), () -> {
            AutocompleteEngine engine = testCase.factory().apply(TERMS);

            assertEquals(3, engine.numberOfMatches("mI"));
            assertEquals(List.of("Microsoft", "microchip", "MICA"),
                    engine.allMatches("mI", 10).stream().map(Term::query).toList());
        }));
    }
}
