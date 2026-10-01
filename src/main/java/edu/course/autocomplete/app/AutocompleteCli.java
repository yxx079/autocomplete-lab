package edu.course.autocomplete.app;

import edu.course.autocomplete.data.DatasetLoader;
import edu.course.autocomplete.engine.AutocompleteEngine;
import edu.course.autocomplete.engine.EngineFactory;
import edu.course.autocomplete.model.Term;

import java.nio.file.Path;
import java.util.List;

/** 根据数据文件、检索实现、前缀和数量限制执行命令行查询。 */
public final class AutocompleteCli {
    private AutocompleteCli() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3 || args.length > 4) {
            System.err.println("Usage: AutocompleteCli <dataset> <engine> <prefix> [limit]; engines="
                    + EngineFactory.supportedEngineNames());
            System.exit(2);
        }
        List<Term> terms = DatasetLoader.load(Path.of(args[0]));
        AutocompleteEngine engine = EngineFactory.create(args[1], terms);
        int limit = args.length == 4 ? Integer.parseInt(args[3]) : 10;
        System.out.printf("engine=%s records=%d matches=%d%n",
                engine.name(), terms.size(), engine.numberOfMatches(args[2]));
        List<Term> matches = engine.allMatches(args[2], limit);
        for (Term term : matches) {
            System.out.println(term);
        }
    }
}
