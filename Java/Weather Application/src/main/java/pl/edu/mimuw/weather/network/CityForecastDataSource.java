package pl.edu.mimuw.weather.network;

import com.google.gson.JsonObject;
import com.google.gson.reflect.*;
import com.google.gson.*;
import io.reactivex.netty.RxNetty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import pl.edu.mimuw.weather.AppMain;
import pl.edu.mimuw.weather.event.AreaForecastEvent;
import pl.edu.mimuw.weather.event.AreaType;
import rx.Observable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.HashMap;



public class CityForecastDataSource extends ForecastDataSource {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CityForecastDataSource.class);
    private static final String URL = "http://api.openweathermap.org/data/2.5/forecast?id=";
    private static final String APPID = "&APPID=903dd6eb1cc700a10ca1bba506857a3f";
    private static final String TEMP_SCALE = "&units=metric";
    private static final String AREA_ID = "6695624";

    /**
     * Some JSON keys to extract forecast data from stream.
     */
    private static final String OPENING_JSON_KEY = "list";
    private static final String MAIN_JSON_KEY = "main";
    private static final String TEMP_JSON_KEY = "temp";
    private static final String PRESS_JSON_KEY = "pressure";
    private static final String HUMIDITY_JSON_KEY = "humidity";
    private static final String WIND_JSON_KEY = "wind";
    private static final String WIND_SPEED_JSON_KEY = "speed";
    private static final String WIND_DEG_JSON_KEY = "deg";
    private static final String CLOUDINESS_JSON_KEY = "weather";
    private static final String DESC_JSON_KEY = "description";
    private static final String ICON_NAME_JSON_KEY = "icon";

    private static final String URL_IMAGE = "http://openweathermap.org/img/w/";
    private static final String IMG_EXTENSION = ".png";

    /**
     * Method is responsible for sending request to OpenWeatherMap.org.
     * It transforms data to JSONs Object and cut out relevant informations and transform to
     * form of AreaForecastEvent.
     * @return AreaForecastEvent.
     */
    @Override
    protected Observable<AreaForecastEvent> makeRequest() {
    String url = URL + AREA_ID + APPID + TEMP_SCALE;

        return RxNetty.createHttpRequest(JsonHelper.withJsonHeader(prepareHttpGETRequest(url)))
                .compose(this::unpackResponse).map(JsonHelper::asJsonObject).map(jsonObject -> {
                    JsonArray listJsonArray = jsonObject.getAsJsonArray(OPENING_JSON_KEY);
                    JsonObject mainJsonObject = listJsonArray.getAsJsonArray().get(0).getAsJsonObject().
                            get(MAIN_JSON_KEY).getAsJsonObject();

                    int temp = mainJsonObject.get(TEMP_JSON_KEY).getAsInt();
                    int pressure = mainJsonObject.get(PRESS_JSON_KEY).getAsInt();
                    int humidity = mainJsonObject.get(HUMIDITY_JSON_KEY).getAsInt();

                    JsonObject windJsonObject = listJsonArray.getAsJsonArray().get(0).getAsJsonObject().
                            get(WIND_JSON_KEY).getAsJsonObject();

                    float windSpeed = windJsonObject.get(WIND_SPEED_JSON_KEY).getAsFloat();
                    int windDeg = windJsonObject.get(WIND_DEG_JSON_KEY).getAsInt();

                    JsonArray weatherJsonObject = listJsonArray.getAsJsonArray().get(0).getAsJsonObject().
                            get(CLOUDINESS_JSON_KEY).getAsJsonArray();
                    String clouds = weatherJsonObject.getAsJsonArray().get(0).getAsJsonObject().
                            get(DESC_JSON_KEY).getAsString();
                    String icon = weatherJsonObject.getAsJsonArray().get(0).getAsJsonObject()
                            .get(ICON_NAME_JSON_KEY).getAsString();

                    BufferedImage bufferedImage = null;
                    try {
                        URL image_url = new URL(URL_IMAGE + icon + IMG_EXTENSION);
                        bufferedImage = ImageIO.read(image_url);

                    } catch (IOException e) {
                        log.error(e);
                    }

                    return new AreaForecastEvent(AreaType.CITY, temp, pressure, humidity, windSpeed,
                     windDeg, clouds, icon, bufferedImage);

                });
    }
}
