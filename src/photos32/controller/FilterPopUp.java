// FilterPopup.java
package photos32.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterPopUp {
    
    private Stage stage;
    private ComboBox<String> tagNameComboBox;
    private TextField tagValueField;
    private VBox tagContainer;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Map<String, List<String>> selectedTags = new HashMap<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean filterApplied = false;
    
    // Replace with your actual tag names
    private List<String> availableTagNames = new ArrayList<>(List.of("Location", "Person", "Event"));
    
    public FilterPopUp(Window owner) {
        stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        stage.setTitle("Filter Photos");
        stage.setMinWidth(400);
        stage.setMinHeight(400);
        
        createUI();
    }
    
    private void createUI() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        
        // Tags section title
        Label tagsLabel = new Label("Filter by Tags");
        tagsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Tag container for dynamically adding tag filters
        tagContainer = new VBox(10);
        addTagFilterRow();
        
        // Add tag button
        Button addTagButton = new Button("Add Another Tag");
        addTagButton.setOnAction(e -> addTagFilterRow());
        
        // Date section title
        Label dateLabel = new Label("Filter by Date");
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Date picker section
        GridPane dateGrid = new GridPane();
        dateGrid.setHgap(10);
        dateGrid.setVgap(10);
        
        dateGrid.add(new Label("Start Date:"), 0, 0);
        startDatePicker = new DatePicker();
        dateGrid.add(startDatePicker, 1, 0);
        
        dateGrid.add(new Label("End Date:"), 0, 1);
        endDatePicker = new DatePicker();
        dateGrid.add(endDatePicker, 1, 1);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        Button okButton = new Button("Apply Filters");
        Button cancelButton = new Button("Cancel");
        
        okButton.setOnAction(e -> {
            saveFilters();
            stage.close();
        });
        
        cancelButton.setOnAction(e -> stage.close());
        
        buttonBox.getChildren().addAll(okButton, cancelButton);
        
        // Add all to root
        root.getChildren().addAll(tagsLabel, tagContainer, addTagButton, 
                                  new Separator(), dateLabel, dateGrid, 
                                  new Separator(), buttonBox);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }
    
    private void addTagFilterRow() {
        HBox tagRow = new HBox(10);
        
        // Tag name dropdown
        tagNameComboBox = new ComboBox<>();
        tagNameComboBox.setPromptText("Select tag");
        ObservableList<String> tagOptions = FXCollections.observableArrayList(availableTagNames);
        tagNameComboBox.setItems(tagOptions);
        
        // Tag value field
        tagValueField = new TextField();
        tagValueField.setPromptText("Enter tag value");
        
        // Add button to add this tag-value pair
        Button addValueButton = new Button("+");
        addValueButton.setOnAction(e -> {
            String tagName = tagNameComboBox.getValue();
            String tagValue = tagValueField.getText().trim();
            
            if (tagName != null && !tagName.isEmpty() && !tagValue.isEmpty()) {
                selectedTags.computeIfAbsent(tagName, k -> new ArrayList<>()).add(tagValue);
                tagValueField.clear();
                updateTagDisplay(tagRow, tagName);
            }
        });
        
        // Remove row button
        Button removeButton = new Button("×");
        removeButton.setStyle("-fx-font-weight: bold;");
        removeButton.setOnAction(e -> tagContainer.getChildren().remove(tagRow));
        
        tagRow.getChildren().addAll(tagNameComboBox, tagValueField, addValueButton, removeButton);
        tagContainer.getChildren().add(tagRow);
    }
    
    private void updateTagDisplay(HBox tagRow, String tagName) {
        // Clear existing tag displays in this row
        tagRow.getChildren().removeIf(node -> node instanceof Label);
        
        // Add new tag display after combobox and textfield
        List<String> values = selectedTags.get(tagName);
        if (values != null && !values.isEmpty()) {
            Label tagDisplay = new Label(tagName + ": " + String.join(", ", values));
            tagDisplay.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 3 8; -fx-background-radius: 4;");
            tagRow.getChildren().add(2, tagDisplay);
        }
    }
    
    private void saveFilters() {
        // Save date range
        startDate = startDatePicker.getValue();
        endDate = endDatePicker.getValue();
        
        // Set filter applied flag
        filterApplied = !selectedTags.isEmpty() || startDate != null || endDate != null;
    }
    
    public void show() {
        stage.showAndWait();
    }
    
    public Map<String, List<String>> getSelectedTags() {
        return selectedTags;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public boolean isFilterApplied() {
        return filterApplied;
    }
    
    public void clearFilters() {
        selectedTags.clear();
        startDate = null;
        endDate = null;
        filterApplied = false;
    }
}