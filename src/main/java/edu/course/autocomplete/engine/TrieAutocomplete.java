package edu.course.autocomplete.engine;

import edu.course.autocomplete.model.Term;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** 使用 Unicode 码点构建 Trie 前缀索引。 */
public final class TrieAutocomplete implements AutocompleteEngine {
    private static final class Node {
        // 下一个 Unicode 码点及其对应的子节点。
        private final Map<Integer, Node> children = new TreeMap<>();

        // 恰好在当前节点结束的 Term。相同 query 可以出现多次，因此使用 List。
        private final List<Term> terminalTerms = new ArrayList<>();

        // 当前节点整棵子树中包含的 Term 数量，用于快速完成 numberOfMatches。
        private long subtreeCount;
    }

    private final Node root = new Node();

    public TrieAutocomplete(Collection<Term> terms) {
        for (Term term : LinearAutocomplete.validatedCopy(terms)) {
            insert(term);
        }
    }

    private void insert(Term term) {
        Node node = root;
        node.subtreeCount++;
        String query = term.query();
        for (int offset = 0; offset < query.length();) {
            int codePoint = query.codePointAt(offset);
            Node child = node.children.get(codePoint);
            if (child == null) {
                child = new Node();
                node.children.put(codePoint, child);
            }
            node = child;
            node.subtreeCount++;
            offset += Character.charCount(codePoint);
        }
        node.terminalTerms.add(term);
    }

    @Override
    public List<Term> allMatches(String prefix, int limit) {
        LinearAutocomplete.validateQuery(prefix, limit);
        if (limit == 0) {
            return List.of();
        }
        Node node = find(prefix);
        if (node == null) {
            return List.of();
        }
        List<Term> matches = new ArrayList<>((int) Math.min(node.subtreeCount, Integer.MAX_VALUE));
        collect(node, matches);
        matches.sort(TermRanking.BY_WEIGHT_THEN_QUERY);
        return List.copyOf(matches.subList(0, Math.min(limit, matches.size())));
    }

    @Override
    public long numberOfMatches(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix must not be null");
        }
        Node node = find(prefix);
        return node == null ? 0 : node.subtreeCount;
    }

    private Node find(String prefix) {
        /*
         * TODO：找到 prefix 对应的 Trie 节点。
         *
         * 建议参考 insert 中已经完成的 Unicode 码点循环：
         * 1. 从 root 开始；空 prefix 应当直接得到 root。
         * 2. 使用 codePointAt(offset) 取得当前码点。
         * 3. 使用 Character.charCount(codePoint) 移动 offset。
         * 4. 路径中的某个 child 不存在时立即返回 null。
         *
         * 时间复杂度应与 prefix 的 Unicode 码点数量成正比。
         */
        throw new UnsupportedOperationException("TODO: find");
    }

    private static void collect(Node node, List<Term> output) {
        /*
         * TODO：把当前节点整棵子树中的 Term 加入 output。
         *
         * 建议步骤：
         * 1. 先加入当前节点的 terminalTerms。
         * 2. 再依次递归访问 children 中的每个子节点。
         *
         * 这里不负责排序；allMatches 会在收集完成后统一排序。
         * 不要修改 Trie，也不要修改方法签名。
         */
        throw new UnsupportedOperationException("TODO: collect");
    }

    @Override
    public String name() {
        return "trie";
    }
}
