package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.data.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.data.SleepingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ru.yandex.practicum.sleeptracker.data.Constants.*;

public class SleeplessNightsFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Количество бессонных ночей", 0L);
        }

        SleepingSession first = sessions.getFirst();
        SleepingSession last = sessions.getLast();

        LocalDate firstNight = first.getSleepStart().toLocalTime().isBefore(MIDDAY)
                ? first.getSleepStart().toLocalDate()
                : first.getSleepStart().toLocalDate().plusDays(1);

        LocalDate lastNight = ((last.getSleepEnd().toLocalTime().isBefore(NIGHT_END)
                && last.getSleepEnd().toLocalTime().isAfter(NIGHT_START))
                || last.getSleepEnd().toLocalTime().isBefore(MIDDAY))
                ? last.getSleepEnd().toLocalDate()
                : last.getSleepEnd().toLocalDate().plusDays(1);

        long totalNights = ChronoUnit.DAYS.between(firstNight, lastNight) + 1;

        Set<LocalDate> nightsWithSleep = sessions.stream()
                .flatMap(s -> getListOfNightsWhenSleepSessionSpans(s).stream())
                .collect(Collectors.toSet());

        long sleepless = LongStream.range(0, totalNights)
                .mapToObj(firstNight::plusDays)
                .filter(date -> !nightsWithSleep.contains(date))
                .count();

        return new SleepAnalysisResult<>("Количество бессонных ночей", sleepless);
    }

    private List<LocalDate> getListOfNightsWhenSleepSessionSpans(SleepingSession session) {
        LocalDateTime startSession = session.getSleepStart();
        LocalDateTime endSession = session.getSleepEnd();
        LocalDate startNight = getNightDate(startSession);
        LocalDate endNight = getNightDate(endSession);

        long count = ChronoUnit.DAYS.between(startNight, endNight) + 1;
        if (count <= 0) {
            return List.of();
        }
        return LongStream.range(0, count)
                .mapToObj(startNight::plusDays)
                .filter(night -> overlapsNight(startSession, endSession, night))
                .collect(Collectors.toList());
    }

    private LocalDate getNightDate(LocalDateTime moment) {
        LocalTime time = moment.toLocalTime();
        if (time.isBefore(MIDDAY)) {
            return moment.toLocalDate();
        }
        return moment.toLocalDate().plusDays(1);
    }

    private boolean overlapsNight(LocalDateTime start, LocalDateTime end, LocalDate night) {
        LocalDateTime nightStart = night.atStartOfDay();
        LocalDateTime nightEnd = night.atTime(NIGHT_END);
        return start.isBefore(nightEnd) && end.isAfter(nightStart);
    }
}
