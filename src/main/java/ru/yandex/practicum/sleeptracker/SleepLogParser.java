package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.data.SleepQuality;
import ru.yandex.practicum.sleeptracker.data.SleepingSession;
import ru.yandex.practicum.sleeptracker.exception.SleepLogParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ru.yandex.practicum.sleeptracker.data.Constants.FORMATTER;

public class SleepLogParser {

    public List<SleepingSession> parse(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            List<String> nonBlank = lines
                    .filter(line -> !line.isBlank())
                    .toList();

            return IntStream.range(0, nonBlank.size())
                    .mapToObj(i -> parseLine(nonBlank.get(i), i + 1))
                    .collect(Collectors.toList());
        }
    }

    private SleepingSession parseLine(String line, int lineNumber) {
        String[] parts = line.split(";");
        if (parts.length != 3) {
            throw new SleepLogParseException(
                    "Строка " + lineNumber + ": ожидается 3 поля, разделённые ';', но было получено полей - "
                            + parts.length);
        }
        try {
            LocalDateTime start = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime end = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepQuality quality = SleepQuality.valueOf(parts[2].trim());
            return new SleepingSession(start, end, quality);
        } catch (DateTimeParseException e) {
            throw new SleepLogParseException(
                    "Строка " + lineNumber + ": неверный формат даты/времени — '" + line + "'", e);
        } catch (IllegalArgumentException e) {
            throw new SleepLogParseException(
                    "Строка " + lineNumber + ": неверный формат качества сна — '" + parts[2].trim() + "'", e);
        }
    }
}
