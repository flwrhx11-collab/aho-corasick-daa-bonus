import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrieNode {
    final Map<Character, TrieNode> children = new HashMap<>();
    final List<Integer> patternIds = new ArrayList<>();
    TrieNode failure;
    TrieNode outputLink;
}
