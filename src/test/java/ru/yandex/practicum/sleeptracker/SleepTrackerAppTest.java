package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.data.SleepQuality;
import ru.yandex.practicum.sleeptracker.data.SleepingSession;
import ru.yandex.practicum.sleeptracker.exception.SleepLogParseException;
import ru.yandex.practicum.sleeptracker.functions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.sleeptracker.data.Chronotype.*;
import static ru.yandex.practicum.sleeptracker.data.Constants.FORMATTER;
import static ru.yandex.practicum.sleeptracker.data.SleepQuality.*;

public class SleepTrackerAppTest {
    private SleepingSession session(String start, String end, SleepQuality q) {
        return new SleepingSession(LocalDateTime.parse(start, FORMATTER), LocalDateTime.parse(end, FORMATTER), q);
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
                session("01.10.25 23:00", "02.10.25 07:00", SleepQuality.GOOD),
                session("02.10.25 23:00", "03.10.25 07:00", SleepQuality.GOOD),
                session("04.10.25 23:00", "05.10.25 07:00", SleepQuality.GOOD));
        assertEquals(1L, new SleeplessNightsFunction().apply(sessionList).getValue());
    }

    @Test
    void sleeplessNightsFirstSessionAfterMidnightBeforeMidday() {
        List<SleepingSession> sessionList = List.of(
                session("05.10.25 00:10", "05.10.25 06:20", GOOD),
                session("05.10.25 23:00", "06.10.25 06:20", GOOD));
        assertEquals(0L, new SleeplessNightsFunction().apply(sessionList).getValue());
    }

    @Test
    void sleeplessNightsLongLogAcrossMonthBoundary() {
        List<SleepingSession> sessionList = new ArrayList<>();
        LocalDate date = LocalDate.of(2025, 10, 1);
        LocalDate end = LocalDate.of(2025, 11, 5);
        while (!date.isAfter(end)) {
            if (!date.equals(LocalDate.of(2025, 10, 15))) {
                LocalDateTime start = date.atTime(23, 0);
                LocalDateTime finish = date.plusDays(1).atTime(7, 0);
                sessionList.add(new SleepingSession(start, finish, GOOD));
            }
            date = date.plusDays(1);
        }
        assertEquals(1L, new SleeplessNightsFunction().apply(sessionList).getValue());
    }

    @Test
    void chronotypeTieReturnsPigeon() {
        List<SleepingSession> sessionList = List.of(
                session("01.10.25 21:00", "02.10.25 06:00", GOOD),
                session("02.10.25 23:30", "03.10.25 10:00", GOOD));
        assertEquals(PIGEON, new ChronotypeFunction().apply(sessionList).getValue());
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

    @Test
    void parserEmptyFileReturnsEmptyList() throws IOException {
        Path tmp = Files.createTempFile("sleep", ".txt");
        try {
            assertEquals(List.of(), new SleepLogParser().parse(tmp));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void parserSkipsBlankLines() throws IOException {
        Path tmp = Files.createTempFile("sleep", ".txt");
        Files.writeString(tmp, "\n\n01.10.25 23:00;02.10.25 07:00;GOOD\n\n");
        try {
            assertEquals(1, new SleepLogParser().parse(tmp).size());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void parserBrokenLineThrowsWithLineNumber() throws IOException {
        Path tmp = Files.createTempFile("sleep", ".txt");
        Files.writeString(tmp, "01.10.25 23:00;02.10.25 07:00;GOOD\nбитая строка\n");
        try {
            SleepLogParseException ex = assertThrows(SleepLogParseException.class, () -> new SleepLogParser().parse(tmp));
            assertTrue(ex.getMessage().contains("Строка 2"));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}