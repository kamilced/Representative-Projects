package pl.edu.mimuw.weather.network;

import io.reactivex.netty.RxNetty;
import javafx.scene.shape.PathElement;
import pl.edu.mimuw.weather.event.AreaForecastEvent;
import pl.edu.mimuw.weather.event.AreaType;
import rx.Observable;
import rx.exceptions.Exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CityForecastDataSecondSource extends ForecastDataSource{
    private static final String URL = "http://www.meteo.waw.pl/";

    private class CityForecastNotFoundException extends Exception {
        private static final long serialVersionUID = 1L;
    };

    /**
     * Some patterns from html source of meteo.waw.pl to get weather data.
     */
    private static final Pattern TEMPERATURE_PATTERN = Pattern.compile("<span class=\"msr_short_text\">temperatura <span id=\"PARAM_0_TA\">(\\d*)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern HUMIDITY_PATTERN = Pattern.compile("<span class=\"msr_short_text\">wilgotność <span id=\"PARAM_0_RH\">(\\d*)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern PRESSURE_PATTERN = Pattern.compile("<span class=\"msr_short_text\">ciśnienie <span id=\"PARAM_0_PR\">(\\d*)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern WIND_SPEED_PATTERN = Pattern.compile("<span class=\"msr_short_text\">wiatr <span id=\"PARAM_0_WV\">(\\d*.\\d*)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern WIND_DEG_PATTERN = Pattern.compile("<div class=\"plotbox_title\">kierunek: <strong id=\"PARAM_WD\">(\\d*)",
            Pattern.CASE_INSENSITIVE);

    @Override
    protected Observable<AreaForecastEvent> makeRequest() {


        return RxNetty.createHttpRequest(prepareHttpGETRequest(URL)).compose(this::unpackResponse).map(htmlSource -> {
            Matcher temp = TEMPERATURE_PATTERN.matcher(htmlSource);
            Matcher hum = HUMIDITY_PATTERN.matcher(htmlSource);
            Matcher press = PRESSURE_PATTERN.matcher(htmlSource);
            Matcher wind_s = WIND_SPEED_PATTERN.matcher(htmlSource);
            Matcher wind_d = WIND_DEG_PATTERN.matcher(htmlSource);

            if (temp.find() && hum.find() && press.find() && wind_s.find() && wind_d.find()) {
                String wind_tmp = wind_s.group(1).trim();
                wind_tmp = wind_tmp.replace(",", ".");

                return new AreaForecastEvent(AreaType.CITY, Float.parseFloat(temp.group(1).trim()),
                        Float.parseFloat(press.group(1).trim()), Float.parseFloat(hum.group(1).trim()),
                        Float.valueOf(wind_tmp).floatValue(), Float.parseFloat(wind_d.group(1).trim()),
                        "N/A", null, null);
            }
            throw Exceptions.propagate(new CityForecastNotFoundException());
        });
    }
}
