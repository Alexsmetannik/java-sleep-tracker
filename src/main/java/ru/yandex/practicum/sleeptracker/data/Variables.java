package ru.yandex.practicum.sleeptracker.data;

import java.time.LocalTime;

public class Variables {
    public static final LocalTime MIDDAY = LocalTime.of(12, 0);
    public static final LocalTime NIGHT_START = LocalTime.MIDNIGHT;
    public static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    public static final LocalTime OWL_SLEEP_AFTER = LocalTime.of(23, 0);
    public static final LocalTime OWL_WAKE_AFTER = LocalTime.of(9, 0);
    public static final LocalTime LARK_SLEEP_BEFORE = LocalTime.of(22, 0);
    public static final LocalTime LARK_WAKE_BEFORE = LocalTime.of(7, 0);
}
