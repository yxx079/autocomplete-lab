package edu.course.autocomplete.engine;

import edu.course.autocomplete.model.Term;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class TrieAutocompleteContractTest {
    private static final List<Term> TERMS = List.of(
            new Term("app", 15),
            new Term("apple", 50),
            new Term("application", 30),
            new Term("apply", 30),
            new Term("banana", 80),
            new Term("你好", 20),
            new Term("你好世界", 40));

    private record EngineCase(String name, Function<List<Term>, AutocompleteEngine> factory) {
    }

    private static Stream<EngineCase> engines() {
        return Stream.of(
                new EngineCase("trie", TrieAutocomplete::new));
    }

    @TestFactory
    Stream<DynamicTest> allEnginesReturnWeightedPrefixMatches() {
        return engines().map(testCase -> DynamicTest.dynamicTest(testCase.name(), () -> {
            AutocompleteEngine engine = testCase.factory().apply(TERMS);
            assertEquals(List.of("apple", "application", "apply", "app"),
                    engine.allMatches("app", 10).stream().map(Term::query).toList());
            assertEquals(4, engine.numberOfMatches("app"));
            assertEquals(List.of("apple", "application"),
                    engine.allMatches("app", 2).stream().map(Term::query).toList());
            assertEquals(List.of("你好世界", "你好"),
                    engine.allMatches("你好", 10).stream().map(Term::query).toList());
            assertEquals(List.of(), engine.allMatches("missing", 10));
            assertThrows(IllegalArgumentException.class, () -> engine.allMatches(null, 10));
            assertThrows(IllegalArgumentException.class, () -> engine.allMatches("a", -1));
        }));
    }
}
