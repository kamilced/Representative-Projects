package pl.edu.mimuw.weather.event;

import java.time.LocalDateTime;

public class RawForecastEvent {
    private final LocalDateTime timestamp;
    private final float value;

    public RawForecastEvent(final LocalDateTime timestamp, final float value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public float getValue() {
        return this.value;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "RawForecastEvent(timestamp=" + this.getTimestamp() + ", value=" + this.getValue() + ")";
    }
}
