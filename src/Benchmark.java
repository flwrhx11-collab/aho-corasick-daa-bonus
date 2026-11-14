import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Benchmark {
    private static final int[] PATTERN_COUNTS = {10, 100, 1000};
    private static final int[] TEXT_LENGTHS = {1000, 10000, 100000};
    private static final int REPEATS = 5;
    private static volatile long blackhole;

    private static String randomText(Random random, int length) {
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            text.append((char) ('a' + random.nextInt(4)));
        }
        return text.toString();
    }

    private static List<String> makePatterns(Random random, String text, int count) {
        List<String> patterns = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int length = 5 + random.nextInt(8);
            if (i % 2 == 0) {
                int start = random.nextInt(text.length() - length + 1);
                patterns.add(text.substring(start, start + length));
            } else {
                patterns.add(randomText(random, length));
            }
        }
        return patterns;
    }

    private static long runAho(String text, List<String> patterns) {
        AhoCorasick ac = new AhoCorasick();
        for (String p : patterns) ac.addPattern(p);
        ac.build();
        return ac.countMatches(text);
    }

    private static long runKmp(String text, List<String> patterns) {
        return KMP.countMatches(text, patterns);
    }

    private static String row(int count, int length, String name, long[] times) {
        double average = 0;
        StringBuilder row = new StringBuilder(count + "," + length + "," + name);
        for (long time : times) {
            row.append(',').append(time);
            average += time / (double) REPEATS;
        }
        row.append(',').append(String.format(Locale.US, "%.2f", average));
        System.out.printf(Locale.US, "%4d patterns, %6d chars, %-12s avg %.3f ms%n",
                count, length, name, average / 1_000_000);
        return row.toString();
    }

    public static void run() throws IOException {
        Path resultFile = Path.of("benchmark_results.csv");
        try (BufferedWriter out = Files.newBufferedWriter(resultFile)) {
            out.write("patterns,text_length,algorithm,run1_ns,run2_ns,run3_ns,run4_ns,run5_ns,average_ns");
            out.newLine();

            for (int count : PATTERN_COUNTS) {
                for (int length : TEXT_LENGTHS) {
                    Random random = new Random(2026L + count * 100000L + length);
                    String text = randomText(random, length);
                    List<String> patterns = makePatterns(random, text, count);

                    long expected = runAho(text, patterns);
                    if (expected != runKmp(text, patterns)) {
                        throw new AssertionError("Aho-Corasick and KMP counts differ");
                    }

                    // JVM warm-up runs are not included in the measurements.
                    for (int w = 0; w < 2; w++) {
                        blackhole = runAho(text, patterns);
                        blackhole = runKmp(text, patterns);
                    }

                    long[] ahoTimes = new long[REPEATS];
                    long[] kmpTimes = new long[REPEATS];
                    for (int r = 0; r < REPEATS; r++) {
                        long start = System.nanoTime();
                        blackhole = runAho(text, patterns);
                        ahoTimes[r] = System.nanoTime() - start;

                        start = System.nanoTime();
                        blackhole = runKmp(text, patterns);
                        kmpTimes[r] = System.nanoTime() - start;
                    }

                    out.write(row(count, length, "Aho-Corasick", ahoTimes));
                    out.newLine();
                    out.write(row(count, length, "KMP", kmpTimes));
                    out.newLine();
                    out.flush();
                }
            }
        }
        System.out.println("Results saved to " + resultFile.toAbsolutePath());
    }
}

