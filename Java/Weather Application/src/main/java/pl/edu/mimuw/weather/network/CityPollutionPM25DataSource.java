package pl.edu.mimuw.weather.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.reactivex.netty.RxNetty;
import pl.edu.mimuw.weather.event.AreaPollutionEvent;
import pl.edu.mimuw.weather.event.PollutionType;
import rx.Observable;

public class CityPollutionPM25DataSource extends ForecastDataSource {
    private static final String URL_STATION = "http://api.gios.gov.pl/pjp-api/rest/data/getData/";
    private static final String POLLUTION_ID = "3585";

    /**
     * Extends method from ForecastDataSource.
     * Request a stream of data which include information about PM 2.5 pollution in Warsaw.
     */
    @Override
    protected Observable<AreaPollutionEvent> makeRequest() {
        String url = URL_STATION + POLLUTION_ID;

        return RxNetty.createHttpRequest(JsonHelper.withJsonHeader(prepareHttpGETRequest(url)))
                .compose(this::unpackResponse).map(JsonHelper::asJsonObject).map(jsonObject -> {
                    JsonArray valuesJsonArray = jsonObject.getAsJsonObject().get("values").getAsJsonArray();
                    float pollution = valuesJsonArray.get(1).getAsJsonObject().get("value").getAsFloat();

                    return new AreaPollutionEvent(PollutionType.PM25, pollution);

                });
    }
}
