package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.data.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.data.SleepingSession;

import java.util.Comparator;
import java.util.List;

public class MaxDurationFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long max = sessions.stream()
                .map(SleepingSession::getDurationOnMinutes)
                .max(Comparator.naturalOrder())
                .orElse(0L);
        return new SleepAnalysisResult<>("Максимальная продолжительность сессии (в минутах)", max);
    }
}
