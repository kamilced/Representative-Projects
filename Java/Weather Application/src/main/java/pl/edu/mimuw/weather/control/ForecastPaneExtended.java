package pl.edu.mimuw.weather.control;

import javafx.beans.DefaultProperty;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Iterator;

@DefaultProperty("forecasts")
public class ForecastPaneExtended extends StackPane {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ForecastPaneExtended.class);

    private static final String FXML_TEMPLATE = "/fxml/forecast-pane-extended.fxml";

    @FXML
    private GridPane containerGrid;

    private ObservableList<Node> forecasts = FXCollections.observableArrayList();

    public ObservableList<Node> getForecasts() {
        return forecasts;
    }

    public ForecastPaneExtended() throws IOException {
        final javafx.fxml.FXMLLoader loader = new FXMLLoader(ForecastPane.class.getResource(FXML_TEMPLATE));
        loader.setController(this);
        getChildren().add(loader.load());

        forecasts.addListener((Observable o) -> {
            int size = forecasts.size();
            int constraintsSize = containerGrid.getRowConstraints().size() * containerGrid.getColumnConstraints().size();
            if (size > constraintsSize) {
                log.error("Too many forecasts for ForecastPaneExtended!");
                return;
            }

            Iterator<Node> it = containerGrid.getChildren().iterator();
            while (it.hasNext()) {
                if (GridPane.getColumnIndex(it.next()) < 3) {
                    it.remove();
                }
            }

            containerGrid.getChildren().addAll(forecasts);

            int i = 0, j = 0;
            for (Node n : forecasts) {
                GridPane.setColumnIndex(n, j);
                GridPane.setRowIndex(n, i);
                GridPane.setRowSpan(n, 1);
                GridPane.setHalignment(n, HPos.CENTER);

                i++;
                if (i == 2) {
                    j++;
                    i = 0;
                }
            }

            if (size < constraintsSize) {
                GridPane.setRowSpan(forecasts.get(size - 1), constraintsSize - i + 1);
            }
        });
    }
}
