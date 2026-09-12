package ru.yandex.practicum.sleeptracker.data;

import java.time.Duration;
import java.time.LocalDateTime;

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

}
