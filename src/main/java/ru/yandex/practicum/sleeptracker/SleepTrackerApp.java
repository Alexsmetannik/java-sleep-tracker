package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.data.SleepingSession;
import ru.yandex.practicum.sleeptracker.exception.SleepLogParseException;
import ru.yandex.practicum.sleeptracker.functions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static ru.yandex.practicum.sleeptracker.data.Constants.DEFAULT_PATH_LOG_FILE;

public class SleepTrackerApp {
    private final List<SleepAnalysisFunction> functions = List.of(
            new TotalSessionsFunction(),
            new MinDurationFunction(),
            new MaxDurationFunction(),
            new AverageDurationFunction(),
            new BadQualitySessionsFunction(),
            new SleeplessNightsFunction(),
            new ChronotypeFunction()
    );

    public static void main(String[] args) {
        String pathString = (args.length > 0) ? args[0] : DEFAULT_PATH_LOG_FILE;
        Path path = Path.of(pathString);
        SleepTrackerApp app = new SleepTrackerApp();
        SleepLogParser parser = new SleepLogParser();

        try {
            List<SleepingSession> sessions = parser.parse(path);
            System.out.println("Всего сессий сна: " + sessions.size());

            app.getFunctionsList().stream()
                    .map(f -> f.apply(sessions))
                    .forEach(result -> System.out.println(
                            result.getDescription() + ": " + result.getValue()));
        } catch (SleepLogParseException e) {
            System.out.println("Ошибка формата файла лога: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл: " + e.getMessage());
        }
    }

    public List<SleepAnalysisFunction> getFunctionsList() {
        return functions;
    }
}