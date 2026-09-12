package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.data.SleepQuality;
import ru.yandex.practicum.sleeptracker.data.SleepingSession;
import ru.yandex.practicum.sleeptracker.functionals.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.sleeptracker.data.Chronotype.*;
import static ru.yandex.practicum.sleeptracker.data.SleepQuality.*;
import static ru.yandex.practicum.sleeptracker.data.Variables.FORMATTER;

public class SleepTrackerAppTest {
    private SleepingSession session(String start, String end, SleepQuality q) {
        return new SleepingSession(LocalDateTime.parse(start, FORMATTER), LocalDateTime.parse(end, FORMATTER), q);
    }

    private List<SleepingSession> getSessionList() {
        List<SleepingSession> sessionList = new ArrayList<>();
        sessionList.add(session("01.10.25 22:00", "02.10.25 07:00", GOOD));
        return sessionList;
    }

    @Test
    void totalSessionsSendEmptyListReturnsZero() {
        assertEquals(0, new TotalSessionsFunction().apply(List.of()).getValue());
    }

    @Test
    void totalSessionsSendThreeSessionsReturnsThree() {
        List<SleepingSession> sessionList = List.of(
                session("01.10.25 22:00", "02.10.25 07:00", GOOD),
                session("02.10.25 23:00", "03.10.25 07:00", NORMAL),
                session("03.10.25 23:00", "04.10.25 07:00", BAD));
        assertEquals(3, new TotalSessionsFunction().apply(sessionList).getValue());
    }

    @Test
    void minDurationReturnsSmallest() {
        List<SleepingSession> sessionList = List.of(
                session("01.10.25 22:00", "02.10.25 07:00", GOOD),
                session("02.10.25 23:00", "03.10.25 02:00", BAD));
        assertEquals(180L, new MinDurationFunction().apply(sessionList).getValue());
    }

    @Test
    void minDurationSendEmptyListReturnsZero() {
        assertEquals(0L, new MinDurationFunction().apply(List.of()).getValue());
    }

    @Test
    void maxDurationReturnsLargest() {
        List<SleepingSession> sessionList = List.of(
                session("01.10.25 22:00", "02.10.25 07:00", GOOD),
                session("02.10.25 23:00", "03.10.25 02:00", BAD));
        assertEquals(540L, new MaxDurationFunction().apply(sessionList).getValue());
    }

    @Test
    void maxDurationSendEmptyListReturnsZero() {
        assertEquals(0L, new MaxDurationFunction().apply(List.of()).getValue());
    }

    @Test
    void averageDurationReturnsAverage() {
        List<SleepingSession> sessionList = List.of(
                session("01.10.25 22:00", "02.10.25 07:00", GOOD),
                session("02.10.25 23:00", "03.10.25 02:00", BAD));
        assertEquals(360.0, new AverageDurationFunction().apply(sessionList).getValue());
    }

    @Test
    void averageDurationSendEmptyListReturnsZero() {
        assertEquals(0.0, new AverageDurationFunction().apply(List.of()).getValue());
    }

    @Test
    void badQualitySessionsReturnBadCount() {
        List<SleepingSession> sessionList = List.of(
                session("01.10.25 22:00", "02.10.25 07:00", GOOD),
                session("02.10.25 23:00", "03.10.25 02:00", BAD),
                session("03.10.25 23:00", "04.10.25 02:00", BAD));
        assertEquals(2L, new BadQualitySessionsFunction().apply(sessionList).getValue());
    }

    @Test
    void badQualitySessionsSendNoBadReturnsZero() {
        List<SleepingSession> sessionList = List.of(
                session("01.10.25 22:00", "02.10.25 07:00", GOOD),
                session("02.10.25 23:00", "03.10.25 02:00", NORMAL));
        assertEquals(0L, new BadQualitySessionsFunction().apply(sessionList).getValue());
    }

    @Test
    void sleeplessNightsSendAllNightsCoveredReturnsZero() {
        List<SleepingSession> sessionList = List.of(
                session("01.10.25 23:00", "02.10.25 07:00", GOOD),
                session("02.10.25 23:00", "03.10.25 07:00", GOOD),
                session("03.10.25 23:00", "04.10.25 07:00", GOOD));
        assertEquals(0L, new SleeplessNightsFunction().apply(sessionList).getValue());
    }

    @Test
    void sleeplessNightsReturnsOneSleepless() {
        List<SleepingSession> sessionList = List.of(
                session("01.10.25 23:00", "02.10.25 07:00", GOOD),
                session("02.10.25 23:00", "03.10.25 07:00", GOOD),
                session("04.10.25 00:30", "04.10.25 07:00", GOOD));
        assertEquals(1L, new SleeplessNightsFunction().apply(sessionList).getValue());
    }

    @Test
    void sleeplessNightsSendFirstSessionAfterNoon() {
        List<SleepingSession> sessionList = List.of(session("01.10.25 23:00", "02.10.25 07:00", GOOD));
        assertEquals(0L, new SleeplessNightsFunction().apply(sessionList).getValue());
    }

    @Test
    void sleeplessNightsSendSessionStartsBeforeMidnightAndEndsAfter() {
        assertEquals(0L, new SleeplessNightsFunction().apply(getSessionList()).getValue());
    }

    @Test
    void chronotypeReturnsOwl() {
        List<SleepingSession> sessionList = List.of(
                session("01.10.25 23:30", "02.10.25 10:00", GOOD),
                session("02.10.25 23:30", "03.10.25 10:00", GOOD),
                session("03.10.25 21:00", "04.10.25 06:00", GOOD));
        assertEquals(OWL, new ChronotypeFunction().apply(sessionList).getValue());
    }

    @Test
    void chronotypeReturnsLark() {
        List<SleepingSession> sessionList = List.of(
                session("01.10.25 21:00", "02.10.25 06:00", GOOD),
                session("02.10.25 21:30", "03.10.25 06:30", GOOD),
                session("03.10.25 23:30", "04.10.25 10:00", GOOD));
        assertEquals(LARK, new ChronotypeFunction().apply(sessionList).getValue());
    }

    @Test
    void chronotypeReturnsPigeon() {
        List<SleepingSession> sessionList = List.of(
                session("01.10.25 22:30", "02.10.25 08:00", GOOD),
                session("02.10.25 22:30", "03.10.25 08:00", GOOD),
                session("03.10.25 23:30", "04.10.25 10:00", GOOD));
        assertEquals(PIGEON, new ChronotypeFunction().apply(sessionList).getValue());
    }
}