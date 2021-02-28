package pl.edu.mimuw.weather.event;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

public class RawForecastImageEvent {
    private final LocalDateTime timestamp;
    private final BufferedImage value;

    public RawForecastImageEvent(final LocalDateTime timestamp, final BufferedImage value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public BufferedImage getValue() {
        return this.value;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "RawForecastImageEvent(timestamp=" + this.getTimestamp() + ")";
    }
}
