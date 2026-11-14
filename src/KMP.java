import java.util.List;

public class KMP {
    public static long countMatches(String text, List<String> patterns) {
        long count = 0;
        for (String pattern : patterns) {
            if (pattern == null || pattern.isEmpty())
                throw new IllegalArgumentException("Pattern must not be empty");
            int[] lps = buildLps(pattern);
            int j = 0;
            for (int i = 0; i < text.length(); i++) {
                while (j > 0 && text.charAt(i) != pattern.charAt(j)) {
                    j = lps[j - 1];
                }
                if (text.charAt(i) == pattern.charAt(j)) j++;
                if (j == pattern.length()) {
                    count++;
                    j = lps[j - 1];
                }
            }
        }
        return count;
    }

    private static int[] buildLps(String pattern) {
        int[] lps = new int[pattern.length()];
        for (int i = 1, len = 0; i < pattern.length();) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else if (len != 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }
}
