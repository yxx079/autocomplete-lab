package edu.course.autocomplete.app;

import edu.course.autocomplete.search.AutocompleteEngine;
import edu.course.autocomplete.search.LinearAutocomplete;
import edu.course.autocomplete.search.BinarySearchAutocomplete;

import edu.course.autocomplete.model.Term;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** 在同一个位置登记引擎名称和创建方法，供 GUI 与命令行共用。 */
public final class EngineFactory {
    private static final Map<String, Function<Collection<Term>, AutocompleteEngine>> ENGINES;

    static {
        Map<String, Function<Collection<Term>, AutocompleteEngine>> engines = new LinkedHashMap<>();
        engines.put("linear", LinearAutocomplete::new);
        engines.put("binary", BinarySearchAutocomplete::new);
        // 新算法实现 AutocompleteEngine 后，在这里登记名称和构造方法即可。
        ENGINES = Collections.unmodifiableMap(engines);
    }

    private EngineFactory() {
    }

    public static List<String> supportedEngineNames() {
        return List.copyOf(ENGINES.keySet());
    }

    public static AutocompleteEngine create(String engineName, Collection<Term> terms) {
        Function<Collection<Term>, AutocompleteEngine> constructor = ENGINES.get(engineName);
        if (constructor == null) {
            throw new IllegalArgumentException("支持的检索实现：" + supportedEngineNames()
                    + "，收到：" + engineName);
        }
        return constructor.apply(terms);
    }
}
