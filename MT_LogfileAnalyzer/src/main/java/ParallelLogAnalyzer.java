import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ParallelLogAnalyzer {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Path logDir = Paths.get("./");
        List<Path> logFiles = Files.list(logDir)
                .filter(path -> path.toString().endsWith(".log"))
                .toList();

        long startSequential = System.currentTimeMillis();
        Map<String, Integer> sequentialResults = analyzeSequentially(logFiles);
        long endSequential = System.currentTimeMillis();
        System.out.println("Sequentielle Analyse: " + sequentialResults + " Dauer: " + (endSequential - startSequential) + "ms");

        long startParallel = System.currentTimeMillis();
        Map<String, Integer> parallelResults = analyzeInParallel(logFiles);
        long endParallel = System.currentTimeMillis();
        System.out.println("Parallele Analyse: " + parallelResults + " Dauer: " + (endParallel - startParallel) + "ms");
    }

    private static Map<String, Integer> analyzeSequentially(List<Path> logFiles) throws IOException {
        Map<String, Integer> totalLogCounts = new HashMap<>();
        for (Path file : logFiles) {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                for (String level : new String[]{"TRACE", "DEBUG", "INFO", "WARN", "ERROR"}) {
                    if (line.contains(level)) {
                        totalLogCounts.put(level, totalLogCounts.getOrDefault(level, 0) + 1);
                        break;
                    }
                }
            }
        }
        return totalLogCounts;
    }

    private static Map<String, Integer> analyzeInParallel(List<Path> logFiles) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Map<String, Integer>>> futures = logFiles.stream()
                .map(file -> executor.submit(new LogAnalyzerTask(file)))
                .toList();

        Map<String, Integer> totalLogCounts = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> result = future.get();
            for (Map.Entry<String, Integer> entry : result.entrySet()) {
                totalLogCounts.put(entry.getKey(), totalLogCounts.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        executor.shutdown();
        return totalLogCounts;
    }
}
