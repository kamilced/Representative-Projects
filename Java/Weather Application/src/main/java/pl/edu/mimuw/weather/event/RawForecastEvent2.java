package pl.edu.mimuw.weather.event;

import java.time.LocalDateTime;

public class RawForecastEvent2 {

    /**
     * Difference between this and RawForecastEvent is only in type of value.
     */

    private final LocalDateTime timestamp;
    private final String value;


    public RawForecastEvent2(final LocalDateTime timestamp, final String value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getValue() {
        return this.value;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "RawForecastEvent2(timestamp=" + this.getTimestamp() + ", value=" + this.getValue() + ")";
    }
}
