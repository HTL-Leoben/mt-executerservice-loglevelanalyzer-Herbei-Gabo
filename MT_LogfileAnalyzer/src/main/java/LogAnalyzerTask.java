import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class LogAnalyzerTask implements Callable<Map<String, Integer>> {
    private final Path logFile;

    public LogAnalyzerTask(Path logFile) {
        this.logFile = logFile;
    }

    @Override
    public Map<String, Integer> call() {
        Map<String, Integer> logCounts = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(logFile);
            for (String line : lines) {
                for (String level : new String[]{"TRACE", "DEBUG", "INFO", "WARN", "ERROR"}) {
                    if (line.contains(level)) {
                        logCounts.put(level, logCounts.getOrDefault(level, 0) + 1);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logCounts;
    }
}
