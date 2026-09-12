package ru.yandex.practicum.sleeptracker.functionalInterfaces;

import ru.yandex.practicum.sleeptracker.data.Chronotype;
import ru.yandex.practicum.sleeptracker.data.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.data.SleepingSession;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.yandex.practicum.sleeptracker.data.Chronotype.*;
import static ru.yandex.practicum.sleeptracker.data.Variables.*;

public class ChronotypeFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Chronotype> apply(List<SleepingSession> sessions) {
        Map<Chronotype, Long> counts = sessions.stream()
                .filter(this::isNightSleep)
                .map(this::getChronotype)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        long owl = counts.getOrDefault(OWL, 0L);
        long lark = counts.getOrDefault(LARK, 0L);
        long pigeon = counts.getOrDefault(PIGEON, 0L);

        Chronotype result;
        if (owl > lark && owl > pigeon) {
            result = OWL;
        } else if (lark > owl && lark > pigeon) {
            result = LARK;
        } else {
            result = PIGEON;
        }

        return new SleepAnalysisResult<>("Хронотип пользователя", result);
    }

    private boolean isNightSleep(SleepingSession session) {
        LocalDateTime startSession = session.getSleepStart();
        LocalDateTime endSession = session.getSleepEnd();
        LocalDateTime nightStart = startSession.toLocalDate().atStartOfDay();
        LocalDateTime nightEnd = nightStart.plusHours(6);
        if (startSession.isBefore(nightEnd) && endSession.isAfter(nightStart)) {
            return true;
        }
        return !startSession.toLocalDate().equals(endSession.toLocalDate());
    }

    private Chronotype getChronotype(SleepingSession session) {
        LocalTime sleepTime = session.getSleepStart().toLocalTime();
        LocalTime wakeTime = session.getSleepEnd().toLocalTime();
        if (sleepTime.isAfter(OWL_SLEEP_AFTER) && wakeTime.isAfter(OWL_WAKE_AFTER)) {
            return OWL;
        }
        if (sleepTime.isBefore(LARK_SLEEP_BEFORE) && wakeTime.isBefore(LARK_WAKE_BEFORE)) {
            return LARK;
        }
        return PIGEON;
    }
}
