package pl.edu.mimuw.weather.controller;

import static pl.edu.mimuw.weather.event.EventStream.binding;
import static pl.edu.mimuw.weather.event.EventStream.onEvent;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import pl.edu.mimuw.weather.control.*;
import pl.edu.mimuw.weather.event.*;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.JavaFxObservable;
import rx.schedulers.JavaFxScheduler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static pl.edu.mimuw.weather.event.EventStream.eventStream;
import static pl.edu.mimuw.weather.event.EventStream.joinStream;
import pl.edu.mimuw.weather.event.SettingsRequestEvent;




public class WeatherAppController {
    private static final int ERROR_MSG_MAX_LENGTH = 300;
    private static final int ERROR_MSG_DURATION = 30;

    @FXML
    private ForecastValueControl temperatureControl;

    @FXML
    private ForecastValueExtendedControl pressureControl;

    @FXML
    private ForecastValueExtendedControl humidityControl;

    @FXML
    private ForecastValueExtendedControl windSpeedControl;

    @FXML
    private ForecastValueExtendedControl windDegControl;

    @FXML
    private ForecastValueExtendedControl2 cloudinessControl;

    @FXML
    private ForecastValueControl iconControl;

    @FXML
    private ForecastImageControl forecastImageControl;

    @FXML
    private ForecastValueExtendedControl PM25Control;

    @FXML
    private ForecastValueExtendedControl PM10Control;

    @FXML
    private Node errorIcon;

    @FXML
    private Node workingIcon;

    @FXML
    private Button refreshButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button changeSourceButton;

    @FXML
    private void initialize() {
        initializeStatus();

        initalizeRefreshHandler();
        initializeSettingsHandler();
        initializeChangeSourceHandler();

        initializeTooltips();

    }

    public Observable<RawForecastEvent> getCityTemperature() {
        return getWeatherStream(AreaType.CITY, AreaForecastEvent::getTemperature);
    }

    public Observable<RawForecastEvent> getCityPressure() {
        return getWeatherStream(AreaType.CITY, AreaForecastEvent::getPressure);
    }
/*tutaj coś*/
    public Observable<RawForecastEvent> getCityHumidity() {
        return getWeatherStream(AreaType.CITY, AreaForecastEvent::getHumidity);
    }

    public Observable<RawForecastEvent> getCityWindSpeed() {
        return getWeatherStream(AreaType.CITY, AreaForecastEvent::getWindSpeed);
    }

    public Observable<RawForecastEvent> getCityWindDeg() {
        return getWeatherStream(AreaType.CITY, AreaForecastEvent::getWindDeg);
    }

    public Observable<RawForecastEvent2> getCityCloudiness() {
        return getWeatherStream2(AreaType.CITY, AreaForecastEvent::getCloudiness);
    }

    public Observable<RawForecastEvent> getCityPM10() {
        return getPollutionStream(PollutionType.PM10, AreaPollutionEvent::getPollutionValue);
    }

    public Observable<RawForecastEvent> getCityPM25() {
        return getPollutionStream(PollutionType.PM25, AreaPollutionEvent::getPollutionValue);
    }

    public Observable<RawForecastImageEvent> getForecastImage() {
        return getForecastImageStream(AreaForecastEvent::getBufferedImage);
    }


    private void initalizeRefreshHandler() {
        joinStream(JavaFxObservable.actionEventsOf(refreshButton).map(e -> new RefreshRequestEvent()));
    }

    private void initializeSettingsHandler() {
        joinStream(JavaFxObservable.actionEventsOf(settingsButton).map(e -> new SettingsRequestEvent()));
    }

    private void initializeChangeSourceHandler() {
        joinStream(JavaFxObservable.actionEventsOf(changeSourceButton).map(e -> new ChangeSourceRequestEvent()));
    }

    private void initializeStatus() {
        Observable<WeatherEvent> events = eventStream().eventsInFx();

        // Basically, we keep track of the difference between issued requests
        // and completed requests
        // If this difference is > 0 we display the spinning icon...
        workingIcon.visibleProperty()
                .bind(binding(events.ofType(NetworkRequestIssuedEvent.class).map(e -> 1) // Every
                        // issued
                        // request
                        // contributes
                        // +1
                        .mergeWith(events.ofType(NetworkRequestFinishedEvent.class).map(e -> -1) // Every
                                // completed
                                // request
                                // contributes
                                // -1
                                .delay(2, TimeUnit.SECONDS, JavaFxScheduler.getInstance())) // We delay
                        // completion
                        // events for 2
                        // seconds so
                        // that the
                        // spinning icon
                        // is always
                        // displayed for
                        // at least 2
                        // seconds and
                        // it does not
                        // blink
                        .scan(0, (x, y) -> x + y).map(v -> v > 0))

                );

        /*
         * This should show the error icon when an error event arrives and hides
         * the icon after 30 seconds unless another error arrives
         */
        Observable<ErrorEvent> errors = events.ofType(ErrorEvent.class);
        errorIcon.visibleProperty()
                .bind(onEvent(errors, true).andOn(
                        errors.throttleWithTimeout(ERROR_MSG_DURATION, TimeUnit.SECONDS, JavaFxScheduler.getInstance()),
                        false).toBinding());
    }

    /**
     * *****Additional feature from XChagneApp*****
     */
    private void initializeTooltips() {
        Tooltip.install(workingIcon, new Tooltip("Fetching data..."));

        Tooltip errorTooltip = new Tooltip();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        eventStream().eventsInFx().ofType(ErrorEvent.class).subscribe(e -> {
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            e.getCause().printStackTrace(new PrintStream(ostream));
            String details = new String(ostream.toByteArray());
            if (details.length() > ERROR_MSG_MAX_LENGTH) {
                details = details.substring(0, ERROR_MSG_MAX_LENGTH) + "\u2026"; // Add
                // ellipsis
                // (...)
                // at
                // the
                // end
            }

            errorTooltip.setText(MessageFormat.format("An error has occurred ({0}):\n{1}",
                    e.getTimestamp().format(formatter), details));
        });
        Tooltip.install(errorIcon, errorTooltip);

          ForecastValueControl[] forecastControls = { temperatureControl };
        for (ForecastValueControl control : forecastControls) {
            Tooltip tooltipPopup = new Tooltip();
            ForecastTooltip tooltipContent = new ForecastTooltip(control.getSource(), control.getTitle());

            tooltipPopup.setGraphic(tooltipContent);

            Tooltip.install(control, tooltipPopup);
        }

        ForecastValueExtendedControl[] forecastControls2 = { pressureControl, humidityControl, windSpeedControl,
        windDegControl, PM25Control, PM10Control/*
        , cloudinessControl, iconControl*/};

        for (ForecastValueExtendedControl control : forecastControls2) {
            Tooltip tooltipPopup = new Tooltip();
            ForecastTooltip tooltipContent = new ForecastTooltip(control.getSource(), control.getTitle());

            tooltipPopup.setGraphic(tooltipContent);

            Tooltip.install(control, tooltipPopup);
        }
    }

    /**
     * Different Streams support.
     */
    private Observable<RawForecastEvent> getWeatherStream(AreaType areaType,
                                                          Func1<AreaForecastEvent, Float> extractor) {
        return eventStream().eventsInFx().ofType(AreaForecastEvent.class)
                .filter(e -> e.getArea() == areaType)
                .map(e -> new RawForecastEvent(e.getTimestamp(), extractor.call(e)));
    }

    private Observable<RawForecastEvent2> getWeatherStream2(AreaType areaType,
                                                          Func1<AreaForecastEvent, String> extractor) {
        return eventStream().eventsInFx().ofType(AreaForecastEvent.class)
                .filter(e -> e.getArea() == areaType)
                .map(e -> new RawForecastEvent2(e.getTimestamp(), extractor.call(e)));
    }

    private Observable<RawForecastEvent> getPollutionStream(PollutionType pollutionType,
                                                            Func1<AreaPollutionEvent, Float> extractor) {
        return eventStream().eventsInFx().ofType(AreaPollutionEvent.class)
                .filter(e -> e.getPollutionType() == pollutionType)
                .map(e -> new RawForecastEvent(e.getTimestamp(), extractor.call(e)));
    }

    private Observable<RawForecastImageEvent> getForecastImageStream(Func1<AreaForecastEvent, BufferedImage> extractor) {
        return eventStream().eventsInFx().ofType(AreaForecastEvent.class)
                .map(e -> new RawForecastImageEvent(e.getTimestamp(), extractor.call(e)));
    }
}
