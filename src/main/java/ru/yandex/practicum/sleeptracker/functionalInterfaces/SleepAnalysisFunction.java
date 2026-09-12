package ru.yandex.practicum.sleeptracker.functionalInterfaces;

import ru.yandex.practicum.sleeptracker.data.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.data.SleepingSession;

import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface SleepAnalysisFunction extends Function<List<SleepingSession>, SleepAnalysisResult<?>> {
}
