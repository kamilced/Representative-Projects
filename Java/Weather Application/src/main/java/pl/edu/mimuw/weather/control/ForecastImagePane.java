package pl.edu.mimuw.weather.control;

import javafx.beans.DefaultProperty;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
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

/***
 * Class responsible for displaying forecast image.
 */
@DefaultProperty("images")
public class ForecastImagePane extends StackPane {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ForecastPane.class);

    private static final String FXML_TEMPLATE = "/fxml/forecast-image-pane.fxml";

    private ObservableList<Node> images = FXCollections.observableArrayList();


    @FXML
    private GridPane containerGrid;


    public ObservableList<Node> getImages() {
        return images;
    }

    public ForecastImagePane() throws IOException {
        final javafx.fxml.FXMLLoader loader = new FXMLLoader(ForecastImagePane.class.getResource(FXML_TEMPLATE));
        loader.setController(this);
        getChildren().add(loader.load());

        images.addListener((Observable o) -> {
            int size = images.size();
            int constraintsSize = containerGrid.getRowConstraints().size();
            if (size > constraintsSize) {
                log.error("Too many images for ForecastImagePane!");
                return;
            }

            Iterator<Node> it = containerGrid.getChildren().iterator();
            while (it.hasNext()) {
                if (GridPane.getColumnIndex(it.next()) == 1) {
                    it.remove();
                }
            }

            containerGrid.getChildren().addAll(images);

            int i = 0;
            for (Node n : images) {
                GridPane.setColumnIndex(n, 1);
                GridPane.setRowIndex(n, i);
                GridPane.setRowSpan(n, 1);
                GridPane.setHalignment(n, HPos.CENTER);
                i++;
            }
        });
    }
}
