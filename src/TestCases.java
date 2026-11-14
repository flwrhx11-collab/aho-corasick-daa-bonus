import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestCases {
    private static void check(String name, String text, List<String> patterns, long expected) {
        AhoCorasick ac = new AhoCorasick();
        for (String pattern : patterns) ac.addPattern(pattern);
        ac.build();

        long acCount = ac.countMatches(text);
        long kmpCount = KMP.countMatches(text, patterns);
        int listed = ac.findAll(text).size();

        if (acCount != expected || kmpCount != expected || listed != expected) {
            throw new AssertionError(name + ": expected " + expected
                    + ", AC=" + acCount + ", KMP=" + kmpCount + ", listed=" + listed);
        }
        System.out.println("PASS: " + name);
    }

    public static void run() {
        check("classic", "ushers", List.of("he", "she", "his", "hers"), 3);
        check("overlapping", "aaa", List.of("a", "aa"), 5);
        check("duplicate patterns", "ababa", List.of("aba", "aba"), 4);
        check("no match", "xyz", List.of("abc", "def"), 0);
        check("empty text", "", List.of("abc"), 0);
        check("unicode", "банана", List.of("ана"), 2);
        check("no patterns", "hello", List.of(), 0);

        AhoCorasick ac = new AhoCorasick();
        ac.addPattern("he");
        ac.addPattern("she");
        ac.addPattern("his");
        ac.addPattern("hers");

        Set<String> actual = new HashSet<>();
        for (AhoCorasick.Match m : ac.findAll("ushers")) {
            actual.add(m.patternId() + ":" + m.start() + ":" + m.endExclusive());
        }

        Set<String> expected = Set.of("1:1:4", "0:2:4", "3:2:6");
        if (!actual.equals(expected))
            throw new AssertionError("Wrong positions: " + actual);

        System.out.println("PASS: match positions");
        System.out.println("All correctness tests passed.\n");
    }
}
