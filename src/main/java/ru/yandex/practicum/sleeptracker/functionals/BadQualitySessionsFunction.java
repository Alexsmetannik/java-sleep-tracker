package ru.yandex.practicum.sleeptracker.functionals;

import ru.yandex.practicum.sleeptracker.data.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.data.SleepingSession;

import java.util.List;

import static ru.yandex.practicum.sleeptracker.data.SleepQuality.BAD;

public class BadQualitySessionsFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long badCount = sessions.stream()
                .filter(s -> s.getQuality() == BAD)
                .count();
        return new SleepAnalysisResult<>("Количество сессий с плохим качеством сна", badCount);
    }
}
