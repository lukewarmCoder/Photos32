package photos32.controller;

import photos32.model.Counter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CounterController {
    private Counter counter = new Counter();  // Link to Model

    @FXML private Label countLabel;  // Link to View
    @FXML private Button incrementButton;

    @FXML
    private void handleIncrement() {
        counter.increment();  // Update Model
        countLabel.setText(String.valueOf(counter.getCount()));  // Update View
    }
}