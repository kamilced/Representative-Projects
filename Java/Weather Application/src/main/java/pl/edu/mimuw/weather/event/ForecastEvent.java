package pl.edu.mimuw.weather.event;

import java.time.LocalDateTime;

public class ForecastEvent extends WeatherEvent {
    private final LocalDateTime timestamp;

    public ForecastEvent() {
        timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "ForecastEvent(timestamp=" + this.getTimestamp() + ")";
    }
}
