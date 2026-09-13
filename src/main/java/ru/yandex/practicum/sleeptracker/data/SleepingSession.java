package ru.yandex.practicum.sleeptracker.data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static ru.yandex.practicum.sleeptracker.data.Constants.NIGHT_END;

public class SleepingSession {
    private final LocalDateTime sleepStart;
    private final LocalDateTime sleepEnd;
    private final SleepQuality quality;

    public SleepingSession(LocalDateTime sleepStart, LocalDateTime sleepEnd, SleepQuality quality) {
        this.sleepStart = sleepStart;
        this.sleepEnd = sleepEnd;
        this.quality = quality;
    }

    public LocalDateTime getSleepStart() {
        return sleepStart;
    }

    public LocalDateTime getSleepEnd() {
        return sleepEnd;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public Duration getDuration() {
        return Duration.between(sleepStart, sleepEnd);
    }

    public long getDurationOnMinutes() {
        return getDuration().toMinutes();
    }

    @Override
    public String toString() {
        return "Сессия сна {" + sleepStart + " -> " + sleepEnd + ", " + quality + '}';
    }


    public boolean overlapsNight(LocalDate night) {
        LocalDateTime nightStart = night.atStartOfDay();
        LocalDateTime nightEnd = night.atTime(NIGHT_END);
        return sleepStart.isBefore(nightEnd) && sleepEnd.isAfter(nightStart);
    }

    public boolean isNightSleep() {
        LocalDateTime nightStart = sleepStart.toLocalDate().atStartOfDay();
        LocalDateTime nightEnd = nightStart.toLocalDate().atTime(NIGHT_END);
        if (sleepStart.isBefore(nightEnd) && sleepEnd.isAfter(nightStart)) {
            return true;
        }
        return !sleepStart.toLocalDate().equals(sleepEnd.toLocalDate());
    }
}
