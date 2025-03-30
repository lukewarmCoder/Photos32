package photos32.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import photos32.model.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FilterController {

    @FXML private ComboBox<String> tagName1, tagName2, logicalOperator;
    @FXML private TextField tagValue1, tagValue2;
    @FXML private VBox additionalTagContainer;
    @FXML private DatePicker startDate, endDate;
    @FXML private Button applyButton, cancelButton;

    private UserHomeController parentController;
    private Stage stage;
    private FilterCriteria existingCriteria;

    @FXML
    public void initialize() {
        // Setup logical operator dropdown
        logicalOperator.setItems(FXCollections.observableArrayList("AND", "OR"));
        logicalOperator.valueProperty().addListener((obs, oldVal, newVal) -> toggleSecondTag(newVal));
        
        // Close the window or apply filters
        cancelButton.setOnAction(e -> stage.close());
        applyButton.setOnAction(e -> applyFilters());
    }

    /**
     * Set up the controller with reference to parent and available tag names
     */
    public void setup(UserHomeController parentController, List<String> tagNames, Stage stage, FilterCriteria existingCriteria) {
        this.parentController = parentController;
        this.stage = stage;
        this.existingCriteria = existingCriteria;
        
        // Populate tag name dropdowns
        ObservableList<String> tagNameOptions = FXCollections.observableArrayList(tagNames);
        tagName1.setItems(tagNameOptions);
        tagName2.setItems(tagNameOptions);
        
        // Add focus listeners AFTER fields are fully initialized
        setupFocusListeners();
        loadExistingCriteria();
    }

    /**
     * Show the second tag field when a logical operator has been chosen from the dropdown
     */
    private void toggleSecondTag(String operator) {
        boolean show = operator != null && !operator.isEmpty();
        additionalTagContainer.setManaged(show);
        additionalTagContainer.setVisible(show);
    }

    /**
     * Load existing filter criteria into UI controls
     */
    private void loadExistingCriteria() {
        if (existingCriteria == null) return;

        if (!existingCriteria.getTagFilters().isEmpty()) {
            TagFilter firstFilter = existingCriteria.getTagFilters().get(0);
            tagName1.setValue(firstFilter.getName());
            tagValue1.setText(firstFilter.getValue());
            
            // Second tag filter if exists
            if (existingCriteria.getTagFilters().size() > 1) {
                logicalOperator.setValue(existingCriteria.getLogicalOperator());
                TagFilter secondFilter = existingCriteria.getTagFilters().get(1);
                tagName2.setValue(secondFilter.getName());
                tagValue2.setText(secondFilter.getValue());
                toggleSecondTag(logicalOperator.getValue()); // Make sure second tag container is visible
            }
        }
        
        // Date filters
        startDate.setValue(existingCriteria.getStartDate());
        endDate.setValue(existingCriteria.getEndDate());
    }

    @FXML
    public void onTagValueEntered() {
        // Move focus away from the text field 
        applyButton.requestFocus();
    }

    /**
     * Setup focus listeners for tag value fields to commit when focus is lost
     */
    private void setupFocusListeners() {
        addFocusListener(tagValue1);
        addFocusListener(tagValue2);
    }

    private void addFocusListener(TextField field) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) field.setText(field.getText().trim());
        });
    }

    /**
     * Apply the filter criteria and close the window
     */
    private void applyFilters() {

        // Collect filter criteria
        FilterCriteria criteria = new FilterCriteria();
        
        // Tag filters
        if (tagName1.getValue() != null && !tagValue1.getText().isEmpty()) {
            criteria.addTagFilter(tagName1.getValue().trim(), tagValue1.getText());

            if (logicalOperator.getValue() != null && tagName2.getValue() != null && !tagValue2.getText().isEmpty()) {
                criteria.setLogicalOperator(logicalOperator.getValue());
                criteria.addTagFilter(tagName2.getValue().trim(), tagValue2.getText());
            }
        }
        
        // Date filters
        if (startDate.getValue() != null) criteria.setStartDate(startDate.getValue());
        if (endDate.getValue() != null) criteria.setEndDate(endDate.getValue());
        

        
        // Pass filter criteria to parent controller
        parentController.applyFilterCriteria(criteria);
        
        // Close the window
        stage.close();
    }

    /**
     * Validates the DatePicker input.
     */
    private boolean isValidDate(DatePicker datePicker) {
        String inputText = datePicker.getEditor().getText().trim();

        if (inputText.isEmpty()) {
            return true; // Allow empty dates
        }

        // Ensure the input matches MM-DD-YYYY format strictly before parsing
        if (!inputText.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return false; // Immediately reject invalid formats
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate parsedDate = LocalDate.parse(inputText, formatter);

            // Set the parsed date back to the DatePicker for consistency
            datePicker.setValue(parsedDate);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Inner class to hold filter criteria
    public static class FilterCriteria {
        private List<TagFilter> tagFilters = new ArrayList<>();
        private String logicalOperator;
        private LocalDate startDate;
        private LocalDate endDate;

        public void addTagFilter(String name, String value) {
            tagFilters.add(new TagFilter(name, value));
        }

        public List<TagFilter> getTagFilters() {
            return tagFilters;
        }

        public String getLogicalOperator() {
            return logicalOperator;
        }

        public void setLogicalOperator(String logicalOperator) {
            this.logicalOperator = logicalOperator;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public boolean hasFilters() {
            return !tagFilters.isEmpty() || startDate != null || endDate != null;
        }
    }

    /**
     * Inner class to represent a tag name-value pair filter
     */
    public static class TagFilter {
        private String name;
        private String value;

        public TagFilter(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}