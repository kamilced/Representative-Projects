package pl.edu.mimuw.weather.event;

public class AreaPollutionEvent extends ForecastEvent{
    PollutionType pollutionType;
    private final float value;

    public AreaPollutionEvent (PollutionType pollutionType, final float value) {
        this.pollutionType = pollutionType;
        this.value = value;
    }

    public PollutionType getPollutionType() {
        return this.pollutionType;
    }

    public float getPollutionValue() {
        return this.value;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "AreaPollutionEvent(PollutionType=" + this.getPollutionType() +
                ", value= " + this.getPollutionValue() + ")";
    }
}
