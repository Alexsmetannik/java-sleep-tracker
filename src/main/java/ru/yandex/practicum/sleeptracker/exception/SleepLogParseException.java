package ru.yandex.practicum.sleeptracker.exception;

public class SleepLogParseException extends RuntimeException {
    public SleepLogParseException(String message) {
        super(message);
    }

    public SleepLogParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
