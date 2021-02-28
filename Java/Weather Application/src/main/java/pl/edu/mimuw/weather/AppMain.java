package pl.edu.mimuw.weather;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import pl.edu.mimuw.weather.control.TooltipProlongHelper;
import pl.edu.mimuw.weather.event.ChangeSourceRequestEvent;
import pl.edu.mimuw.weather.event.ForecastEvent;
import pl.edu.mimuw.weather.event.SettingsRequestEvent;
import pl.edu.mimuw.weather.event.WeatherEvent;
import pl.edu.mimuw.weather.network.*;
import rx.Observable;
import rx.Subscription;
import rx.observables.JavaFxObservable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static pl.edu.mimuw.weather.event.EventStream.eventStream;
import static pl.edu.mimuw.weather.event.EventStream.joinStream;


public class AppMain extends Application {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AppMain.class);

    private static final String FXML_MAIN_FORM_TEMPLATE = "/fxml/weather-main.fxml";
    private static final String FXML_CLOSE_DIALOG_TEMPLATE = "/fxml/close-dialog.fxml";
    private static final String FXML_SETTINGS_DIALOG_TEMPLATE = "/fxml/settings-dialog.fxml";

    private static final String FONT_CSS = "/css/jfoenix-fonts.css";
    private static final String MATERIAL_CSS = "/css/jfoenix-design.css";
    private static final String JFX_CSS = "/css/jfx.css";

    private static final String[] LOGOS = { "/icons/weather-icon.png" };

    /**
     * ****Borrowed from XChangeApp.****
     * Dialog responsible for exiting the App.
     */
    private class DialogControllerBase {
        @FXML
        JFXDialog dialog;

        @FXML
        Button acceptButton;

        @FXML
        Button cancelButton;

        void initialize() {
            JavaFxObservable.actionEventsOf(cancelButton).subscribe(ignore -> {
                dialog.close();
            });
        }

        void show(StackPane pane) {
            dialog.show(pane);
        }

    }

    /**
     * Exiting App controller.
     */
    private class CloseDialogController extends DialogControllerBase {
        @FXML
        void initialize() {
            super.initialize();

            JavaFxObservable.actionEventsOf(acceptButton).subscribe(ignore -> {
                log.info("Exiting...");
                AppMain.this.mainStage.close(); // This should terminate the
                    // application
                System.exit(0); // Just for sure
            });
        }
    }

    /**
     * Counter of changes, needed to display data from a proper source.
     */
    private int amountOfChanges = 0;
    /**
     * Needed in changing source process.
     */
    private int intervalOfRefresh = 300;

    /**
     * ***Borrowed from XchangeApp and slightly modified***
     * Refresh interval settings.
     */
    private class SettingsDialogController extends DialogControllerBase {
        @FXML
        JFXTextField intervalField;

        @FXML
        Pane content;

        @FXML
        void initialize() {
            super.initialize();

            intervalField.textProperty().addListener((control, newValue, oldValue) -> intervalField.validate());
            acceptButton.disableProperty().bind(intervalField.getValidators().get(0).hasErrorsProperty());


            JavaFxObservable.actionEventsOf(acceptButton).subscribe(ignore -> {
                try {
                    intervalOfRefresh = Integer.parseInt(intervalField.getText());

                    AppMain.this.sourceStreams.stream().forEach(Subscription::unsubscribe);
                    AppMain.this.sourceStreams.clear();

                    AppMain.this.setupDataSources(intervalOfRefresh, amountOfChanges % 2);
                } finally {
                    dialog.close();
                }
            });

        }

    }


    private DialogControllerBase closeDialogController;
    private DialogControllerBase settingsDialogController;
    private Stage mainStage;

    private List<Subscription> sourceStreams = new LinkedList<>();

    /**
     * Starting App.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("Starting Weather App...");

        mainStage = primaryStage;

        setupTooltipDuration();

        setupDataSources(null, amountOfChanges % 2);

        setupEventHandler();

        Parent pane = FXMLLoader.load(AppMain.class.getResource(FXML_MAIN_FORM_TEMPLATE));

        JFXDecorator decorator = new JFXDecorator(mainStage, pane, false, false, true);
        ObservableList<Node> buttonsList = ((Pane) decorator.getChildren().get(0)).getChildren();
        buttonsList.get(buttonsList.size() - 1).getStyleClass().add("close-button");

        decorator.setOnCloseButtonAction(this::onClose);

        Scene scene = new Scene(decorator);
        scene.setFill(null);

        scene.getStylesheets().addAll(AppMain.class.getResource(FONT_CSS).toExternalForm(),
                AppMain.class.getResource(MATERIAL_CSS).toExternalForm(),
                AppMain.class.getResource(JFX_CSS).toExternalForm());

        mainStage.setScene(scene);

        mainStage.setWidth(700);
        mainStage.setHeight(500);
        mainStage.setResizable(false);

        addLogo();

        mainStage.show();

        log.info("Application's up and running!");
    }

    /**
     * Add App logo.
     */
    private void addLogo() {
        for (String logoPath : LOGOS) {
            mainStage.getIcons().add(new Image(AppMain.class.getResourceAsStream(logoPath)));
        }
    }

    /**
     * On closing process.
     */
    private void onClose() {
        log.info("onClose");

        if (closeDialogController == null) {
            closeDialogController = new CloseDialogController();
            createDialog(closeDialogController, FXML_CLOSE_DIALOG_TEMPLATE);
        }

        closeDialogController.show(getMainPane());
    }

    /**
     * When interval settings are changed.
     */
    private void onSettingsRequested() {
        log.info("onSettingsRequested");

        if (settingsDialogController == null) {
            settingsDialogController = new SettingsDialogController();
            createDialog(settingsDialogController, FXML_SETTINGS_DIALOG_TEMPLATE);
        }

        settingsDialogController.show(getMainPane());
    }

    /**
     * When change of source is requested.
     */
    private void onChangeSourceRequested() {
        log.info("onChangeSourceRequested");
        amountOfChanges++;
        AppMain.this.sourceStreams.stream().forEach(Subscription::unsubscribe);
        AppMain.this.sourceStreams.clear();
        setupDataSources(intervalOfRefresh, amountOfChanges % 2);
    }

    /**
     * Main Pane.
     */
    private StackPane getMainPane() {
        return (StackPane) mainStage.getScene().getRoot().lookup("#main");
    }

    /**
     * Dialog Creation.
     * @param dialogController      – dialog Controller;
     * @param fxmlPath              – path to fxml info.
     */
    private void createDialog(Object dialogController, String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(AppMain.class.getResource(fxmlPath));
        loader.setController(dialogController);
        try {
            loader.load();
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Data Sources are setup depending on refresh interval and number of source.
     * @param pollInterval          – refresh interval;
     * @param sourceNumber          – number 0 is OpenWeather Source, number 1 is meteowaw.pl source.
     */
    private void setupDataSources(Integer pollInterval, int sourceNumber) {
        ForecastDataSource[][] sources = { {new CityForecastDataSource(), new CityPollutionPM25DataSource(),
        new CityPollutionPM10DataSource()}, { new CityForecastDataSecondSource(), new CityPollutionPM25DataSource(),
                new CityPollutionPM10DataSource() } };
        for (ForecastDataSource source : sources[sourceNumber]) {
            sourceStreams.add(joinStream(source.dataSourceStream(pollInterval)));
        }
    }

    /**
     * Deal with different types of events, change of source, change of refresh interval etc.
     */
    private void setupEventHandler() {
        Observable<WeatherEvent> events = eventStream().events();

        events.ofType(ForecastEvent.class).subscribe(log::info);

        events.ofType(SettingsRequestEvent.class).subscribe(e -> onSettingsRequested());

        events.ofType(ChangeSourceRequestEvent.class).subscribe(e -> onChangeSourceRequested());
    }


    private static void setupExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(
                (t, e) -> log.error("Uncaught exception in thread \'" + t.getName() + "\'", e));
    }

    private static void setupTooltipDuration() {
        TooltipProlongHelper.setTooltipDuration(Duration.millis(500), Duration.minutes(10), Duration.millis(500));
    }

    /**
     * Text setups.
     */
    private static void setupTextRendering() {
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.lcdtext", "true");
    }

    public static void main(String[] args) {

        setupExceptionHandler();

        setupTextRendering();

        Platform.setImplicitExit(true);

        Application.launch(AppMain.class, args);
    }
}
