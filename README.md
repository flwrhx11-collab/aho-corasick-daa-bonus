# Aho-Corasick Algorithm — DAA Bonus Task

## 1. Project Overview

This project implements the Aho-Corasick string matching algorithm in Java and compares its performance with the Knuth-Morris-Pratt (KMP) algorithm.

The goal is to find multiple patterns in a text efficiently and evaluate the performance of both algorithms using different pattern counts and text lengths.

## 2. Technologies

- Java 21
- IntelliJ IDEA
- Git and GitHub
- System.nanoTime() for performance measurements

## 3. Project Structure

```text
src/
  AhoCorasick.java
  TrieNode.java
  KMP.java
  Benchmark.java
  TestCases.java
  Main.java

README.md
benchmark_results.csv
.gitignore
```

## 4. Aho-Corasick Implementation

The implementation includes:

- Trie construction for multiple patterns
- Failure link construction using BFS
- Output links for matched patterns
- Searching for all pattern occurrences
- Reporting pattern IDs and match positions

### Trie Construction

All patterns are inserted into a trie. Each node represents a prefix, and each edge represents a character.

Common prefixes are shared between patterns.

### Failure Links

Failure links indicate where the algorithm should continue when the current character does not match an outgoing trie edge.

The links are constructed using breadth-first search (BFS).

### Output Links

Output links connect nodes to other nodes representing matched patterns along their failure-link paths.

They allow the algorithm to report overlapping and nested pattern matches.

### Searching

The algorithm processes the input text from left to right, updating its current state and reporting matched patterns.

## 5. Correctness Testing

The implementation is tested using:

1. Classic Aho-Corasick example
2. Overlapping matches
3. Duplicate patterns
4. No matching patterns
5. Empty text
6. Unicode characters
7. Empty pattern collection
8. Correct match positions

Both algorithms are checked against expected match counts.

## 6. Experimental Setup

### Number of Patterns

- 10
- 100
- 1,000

### Text Lengths

- 1,000 characters
- 10,000 characters
- 100,000 characters

Each combination is tested using both algorithms.

### Methodology

- Test data is generated using a fixed random seed.
- Text contains characters from the alphabet a, b, c, d.
- Pattern lengths range from 5 to 12 characters.
- Half of the generated patterns are sampled from the text.
- Both algorithms receive identical inputs.
- Each experiment is repeated five times.
- JVM warm-up runs are performed before measurements.
- Execution time is recorded using System.nanoTime().
- The average of five runs is calculated.
- Results are saved in benchmark_results.csv.

The measured time includes pattern preprocessing and the counting of matches.

The benchmark measures complete search operations, rather than the time required to create a list of all match positions.


## 7. Experimental Results

The table below shows the average execution time of both algorithms. All values are measured in milliseconds.

| Patterns | Text Length | Aho-Corasick (ms) | KMP (ms) |
|---|---|---|---|
| 10 | 1,000 | 0.432 | 1.336 |
| 10 | 10,000 | 0.802 | 1.824 |
| 10 | 100,000 | 2.722 | 10.112 |
| 100 | 1,000 | 0.854 | 0.995 |
| 100 | 10,000 | 1.053 | 10.016 |
| 100 | 100,000 | 3.079 | 96.094 |
| 1,000 | 1,000 | 1.626 | 8.239 |
| 1,000 | 10,000 | 2.102 | 90.965 |
| 1,000 | 100,000 | 6.724 | 970.342 |

The individual execution times and averages in nanoseconds are stored in `benchmark_results.csv`.


## 8. Complexity Analysis

Let:

- n = length of the input text
- k = number of patterns
- L = total length of all patterns
- d = maximum pattern length
- z = total number of matches

### Aho-Corasick

Trie construction:
O(L) expected time using hash-based transitions.

Failure and output link construction:
O(L * d) conservative worst-case upper bound for this implementation, because failure-link construction may traverse several fallback links for an edge.

Search:
O(n + z) expected time, assuming constant-time hash map operations.

Space:
O(L) for trie nodes, transitions, pattern information, and failure/output links.

Storing all occurrences requires an additional O(z) space.

### KMP

For one pattern:

- Preprocessing: O(m)
- Searching: O(n)
- Additional space: O(m)

For k patterns:

- Total time: O(k * n + L)
- Additional space: O(d) when processing patterns sequentially, excluding stored input patterns.

## 9. Performance Comparison

### Advantages of Aho-Corasick

- Searches multiple patterns in one scan of the text.
- Shares common prefixes in the trie.
- Supports overlapping and nested matches.
- Suitable for searching many keywords.

### Limitations of Aho-Corasick

- Requires additional memory for the trie.
- Failure link construction adds preprocessing work.
- Implementation is more complex than KMP.

### Advantages of KMP

- Efficient for searching a single pattern.
- Requires relatively little additional memory.
- Uses a simple prefix table.

### Limitations of KMP

- Each pattern requires a separate scan of the text.
- Performance may decrease as the number of patterns grows.


## 10. Discussion of Results

The experimental results show that Aho-Corasick outperformed
KMP in all nine tested configurations.

For 10 patterns and 100,000 characters, Aho-Corasick
required 2.722 ms, while KMP required 10.112 ms.

For 100 patterns and 100,000 characters, Aho-Corasick
required 3.079 ms, compared with 96.094 ms for KMP.

For 1,000 patterns and 100,000 characters, Aho-Corasick
required 6.724 ms, while KMP required 970.342 ms.

In the largest test, Aho-Corasick was approximately
144 times faster than KMP.

The results demonstrate that Aho-Corasick is particularly
effective when searching for many patterns simultaneously.

KMP processes every pattern separately, so its execution
time increases significantly as the number of patterns grows.

Aho-Corasick uses a trie and failure links to search for
multiple patterns during a single scan of the text.

These results are consistent with the theoretical complexity
analysis. However, the exact running times also depend on
the Java Virtual Machine, hardware, test data, and
measurement conditions.

## 11. How to Run

Open the project in IntelliJ IDEA.

Requirements:
- JDK 21 or newer

Run Main.java.

The program will:

1. Run correctness tests.
2. Execute all benchmark combinations.
3. Print average execution times.
4. Save the measurements to benchmark_results.csv.

Alternatively, from the project root:

```bash
javac -d out src/*.java
java -cp out Main
```


## 12. Conclusion

The experiments showed that Aho-Corasick was faster than running KMP separately for each pattern in all nine tested configurations.

The performance difference became larger as the number of patterns increased. In the largest experiment, with 1,000 patterns and a text of 100,000 characters, Aho-Corasick took 6.724 ms, while KMP took 970.342 ms.

Aho-Corasick is more suitable for searching many patterns simultaneously because it processes the text in a single scan. KMP remains useful for searching individual patterns and requires less additional memory.

The results are consistent with the theoretical complexity analysis, although execution times can vary depending on hardware and JVM behavior.
