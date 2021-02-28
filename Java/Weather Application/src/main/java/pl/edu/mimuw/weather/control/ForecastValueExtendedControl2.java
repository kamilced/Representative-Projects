package pl.edu.mimuw.weather.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.weathericons.WeatherIcons;
import pl.edu.mimuw.weather.event.RawForecastEvent;
import pl.edu.mimuw.weather.event.RawForecastEvent2;
import rx.Observable;

/**
 * Control of lower box with data.
 * Separated from basic class 'cause of some key difference.
 */
public class ForecastValueExtendedControl2 extends Pane {

    private FontIcon noDataIcon = new FontIcon(WeatherIcons.NA);

    private HBox innerContainer;

    private Text prefixLabel;
    private Text suffixLabel;
    private Text textControl;

    private ObjectProperty<Observable<RawForecastEvent2>> sourceProperty = new SimpleObjectProperty<>();

    private StringProperty prefixProperty = new SimpleStringProperty();
    private StringProperty suffixProperty = new SimpleStringProperty();
    private StringProperty titleProperty = new SimpleStringProperty("-");

    public Observable<RawForecastEvent2> getSource() {
        return sourceProperty.get();
    }


    public void setSource(Observable<RawForecastEvent2> source) {
        source.subscribe(e -> {
            if (innerContainer == null) {
                createContentControls();
            }

            textControl.setText(e.getValue());
        });

        sourceProperty.set(source);
    }


    public String getPrefix() {
        return prefixProperty.get();
    }

    public void setPrefix(String prefix) {
        prefixProperty.set(prefix);
    }

    public String getSuffix() {
        return suffixProperty.get();
    }

    public void setSuffix(String sufix) {
        suffixProperty.set(sufix);
    }

    public String getTitle() {
        return titleProperty.get();
    }

    public void setTitle(String title) {
        titleProperty.set(title);
    }

    public ForecastValueExtendedControl2() {
        noDataIcon.getStyleClass().add("no-data2");
        getChildren().add(noDataIcon);
    }

    private void createContentControls() {
        getChildren().remove(noDataIcon);

        textControl = new Text();
        textControl.getStyleClass().add("forecast-value2");

        prefixLabel = new Text();
        prefixLabel.textProperty().bind(prefixProperty);
        prefixLabel.getStyleClass().add("helper-label2");

        suffixLabel = new Text();
        suffixLabel.textProperty().bind(suffixProperty);
        suffixLabel.getStyleClass().add("helper-label2");

        innerContainer = new HBox();
        innerContainer.getStyleClass().add("value-container2");
        innerContainer.getChildren().addAll(prefixLabel, textControl, suffixLabel);

        getChildren().add(innerContainer);


    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        if (noDataIcon.isVisible()) {
            noDataIcon.relocate((getWidth() - noDataIcon.getLayoutBounds().getWidth()) / 2,
                    (getHeight() - noDataIcon.getLayoutBounds().getHeight()) / 2);
        }

        if (innerContainer != null) {
            innerContainer.relocate((getWidth() - innerContainer.getLayoutBounds().getWidth()) / 2,
                    (getHeight() - innerContainer.getLayoutBounds().getHeight()) / 2);
        }
    }
}
