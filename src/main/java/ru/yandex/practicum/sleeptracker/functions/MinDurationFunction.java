package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.data.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.data.SleepingSession;

import java.util.Comparator;
import java.util.List;

public class MinDurationFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long min;
        min = sessions.stream()
                .map(SleepingSession::getDurationOnMinutes)
                .min(Comparator.naturalOrder())
                .orElse(0L);
        return new SleepAnalysisResult<>("Минимальная продолжительность сессии (в минутах)", min);
    }
}
