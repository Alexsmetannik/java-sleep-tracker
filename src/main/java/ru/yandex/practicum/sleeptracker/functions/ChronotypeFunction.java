package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.data.Chronotype;
import ru.yandex.practicum.sleeptracker.data.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.data.SleepingSession;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.yandex.practicum.sleeptracker.data.Chronotype.*;
import static ru.yandex.practicum.sleeptracker.data.Constants.*;

public class ChronotypeFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Chronotype> apply(List<SleepingSession> sessions) {
        Map<Chronotype, Long> counts = sessions.stream()
                .filter(SleepingSession::isNightSleep)
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

    private Chronotype getChronotype(SleepingSession session) {
        LocalTime sleepTime = toNightScale(session.getSleepStart().toLocalTime());
        LocalTime wakeTime = toNightScale(session.getSleepEnd().toLocalTime());
        if (sleepTime.isAfter(OWL_SLEEP_AFTER) && wakeTime.isAfter(OWL_WAKE_AFTER)) {
            return OWL;
        }
        if (sleepTime.isBefore(LARK_SLEEP_BEFORE) && wakeTime.isBefore(LARK_WAKE_BEFORE)) {
            return LARK;
        }
        return PIGEON;
    }

    private LocalTime toNightScale(LocalTime time) {
        return time.isBefore(MIDDAY) ? time.plusHours(24) : time;
    }
}
