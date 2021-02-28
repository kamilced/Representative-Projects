package pl.edu.mimuw.weather.event;

import java.awt.image.BufferedImage;

public class AreaForecastEvent extends ForecastEvent {

    private final AreaType area;
    private final float temperature;
    private final float pressure;
    private final float humidity;
    private final float windSpeed;
    private final float windDeg;
    private final String cloudiness;
    private final String icon;
    private final BufferedImage bufferedImage;


    public AreaForecastEvent(final AreaType area, final float temperature, final float pressure,
                             final float humidity, final float windSpeed, final float windDeg,
                             final String cloudiness, final String icon, final BufferedImage bufferedImage) {
        this.area = area;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.windDeg = windDeg;
        this.cloudiness = cloudiness;
        this.icon = icon;
        this.bufferedImage = bufferedImage;
    }


    public AreaType getArea() {
        return this.area;
    }

    public float getTemperature() {
        return this.temperature;
    }

    public float getPressure() {
        return this.pressure;
    }

    public float getHumidity() { return this.humidity; }

    public float getWindSpeed() {
        return this.windSpeed;
    }

    public float getWindDeg() {
        return this.windDeg;
    }

    public String getCloudiness() {
        return cloudiness;
    }

    public String getIcon() {
        return icon;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }


    @java.lang.Override
    public java.lang.String toString() {
        //System.out.println(getTemperature() + "   " + getPressure());
        return "AreaForecastEvent(area=" + this.getArea() + ", temperature=" + this.getTemperature() +
                ", pressure=" + this.getPressure() + ", humidity=" + this.getHumidity() + ", windSpead=" +
                this.getWindSpeed() + ", windDeg=" + this.getWindDeg() +  ", cloudiness=" + this.getCloudiness() +
                ", icon=" + getIcon() + ")";
    }
}
