import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class AhoCorasick {
    public record Match(int patternId, int start, int endExclusive) {}

    private final TrieNode root = new TrieNode();
    private final List<String> patterns = new ArrayList<>();
    private boolean built = false;

    public int addPattern(String pattern) {
        if (built) throw new IllegalStateException("Add patterns before build()");
        if (pattern == null || pattern.isEmpty())
            throw new IllegalArgumentException("Pattern must not be empty");

        int id = patterns.size();
        patterns.add(pattern);
        TrieNode node = root;
        for (char c : pattern.toCharArray()) {
            node = node.children.computeIfAbsent(c, key -> new TrieNode());
        }
        node.patternIds.add(id);
        return id;
    }

    public void build() {
        if (built) return;
        root.failure = root;
        Queue<TrieNode> queue = new ArrayDeque<>();
        for (TrieNode child : root.children.values()) {
            child.failure = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            TrieNode current = queue.remove();
            for (var entry : current.children.entrySet()) {
                char c = entry.getKey();
                TrieNode child = entry.getValue();
                TrieNode fallback = current.failure;

                while (fallback != root && !fallback.children.containsKey(c)) {
                    fallback = fallback.failure;
                }
                child.failure = fallback.children.getOrDefault(c, root);
                if (child.failure == child) child.failure = root;

                TrieNode fail = child.failure;
                child.outputLink = !fail.patternIds.isEmpty() ? fail : fail.outputLink;
                queue.add(child);
            }
        }
        built = true;
    }

    public List<Match> findAll(String text) {
        if (text == null) throw new IllegalArgumentException("Text is null");
        build();
        List<Match> result = new ArrayList<>();
        TrieNode state = root;

        for (int i = 0; i < text.length(); i++) {
            state = nextState(state, text.charAt(i));
            TrieNode node = state;
            while (node != null && node != root) {
                for (int id : node.patternIds) {
                    result.add(new Match(id, i + 1 - patterns.get(id).length(), i + 1));
                }
                node = node.outputLink;
            }
        }
        return result;
    }

    public long countMatches(String text) {
        if (text == null) throw new IllegalArgumentException("Text is null");
        build();
        long count = 0;
        TrieNode state = root;
        for (int i = 0; i < text.length(); i++) {
            state = nextState(state, text.charAt(i));
            TrieNode node = state;
            while (node != null && node != root) {
                count += node.patternIds.size();
                node = node.outputLink;
            }
        }
        return count;
    }

    private TrieNode nextState(TrieNode state, char c) {
        while (state != root && !state.children.containsKey(c)) {
            state = state.failure;
        }
        return state.children.getOrDefault(c, root);
    }
}

