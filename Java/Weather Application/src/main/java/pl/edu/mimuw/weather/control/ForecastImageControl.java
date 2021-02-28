package pl.edu.mimuw.weather.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.weathericons.WeatherIcons;
import pl.edu.mimuw.weather.event.RawForecastEvent;
import pl.edu.mimuw.weather.event.RawForecastImageEvent;
import rx.Observable;

/**
 * Control of forecast image displaying.
 */
public class ForecastImageControl extends Pane {

    private FontIcon noDataIcon = new FontIcon(WeatherIcons.NA);

    private HBox innerContainer;

    private ImageView imageControl;

    private ObjectProperty<Observable<RawForecastImageEvent>> sourceProperty = new SimpleObjectProperty<>();

    private StringProperty titleProperty = new SimpleStringProperty("-");

    public Observable<RawForecastImageEvent> getSource() {
        return sourceProperty.get();
    }

    public void setSource(Observable<RawForecastImageEvent> source) {
        source.subscribe(e -> {
            if (innerContainer == null) {
                createContentControls();
            }

            if (e.getValue() != null) {
                imageControl.setImage(SwingFXUtils.toFXImage(e.getValue(), null));
            }
        });


        sourceProperty.set(source);
    }

    /**
     * @return Title of image
     */
    public String getTitle() {
        return titleProperty.get();
    }

    public void setTitle(String title) {
        titleProperty.set(title);
    }

    /**
     * When there is no image to display.
     */
    public ForecastImageControl() {
        noDataIcon.getStyleClass().add("no-data");
        getChildren().add(noDataIcon);
    }

    /**
     * Basic control of image area.
     */
    private void createContentControls() {
        getChildren().remove(noDataIcon);

        imageControl = new ImageView();
        imageControl.setSmooth(true);
        imageControl.setFitWidth(200);
        imageControl.setFitHeight(200);

        innerContainer = new HBox();
        innerContainer.getStyleClass().add("forecast-icon-container2");
        innerContainer.getChildren().add(imageControl);

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
