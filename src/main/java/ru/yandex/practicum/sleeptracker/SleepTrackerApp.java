package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.data.SleepingSession;
import ru.yandex.practicum.sleeptracker.functionalInterfaces.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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
        if (args.length == 0) {
            System.err.println("Необходимо указать путь к файлу лога сна");
            return;
        }

        Path path = Path.of(args[0]);
        SleepTrackerApp app = new SleepTrackerApp();
        SleepLogParser parser = new SleepLogParser();

        try {
            List<SleepingSession> sessions = parser.parse(path);
            System.out.println("Всего сессий сна: " + sessions.size());

            app.getFunctionsList().stream()
                    .map(f -> f.apply(sessions))
                    .forEach(result -> System.out.println(
                            result.getDescription() + ": " + result.getValue()));
        } catch (IOException e) {
            System.err.println("Не удалось прочитать файл: " + e.getMessage());
        }
    }

    public List<SleepAnalysisFunction> getFunctionsList() {
        return functions;
    }
}