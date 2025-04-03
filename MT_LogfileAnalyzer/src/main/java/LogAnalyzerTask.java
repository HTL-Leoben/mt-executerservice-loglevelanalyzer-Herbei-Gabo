import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

class LogAnalyzerTask implements Callable<Map<String, Integer>> {
    private final Path filePath;

    public LogAnalyzerTask(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Map<String, Integer> call() throws Exception {
        Map<String, Integer> logLevelCount = new HashMap<>();
        List<String> lines = Files.readAllLines(filePath);
        for (String line : lines) {
            for (String level : new String[]{"TRACE", "DEBUG", "INFO", "WARN", "ERROR"}) {
                if (line.contains(level)) {
                    logLevelCount.put(level, logLevelCount.getOrDefault(level, 0) + 1);
                    break;
                }
            }
        }
        return logLevelCount;
    }
}