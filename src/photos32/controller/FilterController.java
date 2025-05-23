package photos32.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for the filter pop up window.
 * Displays a UI for the user to enter filter criteria.
 * Connected to FilterWindow.fxml.
 */
public class FilterController {

    @FXML private ComboBox<String> tagName1, tagName2, logicalOperator;
    @FXML private TextField tagValue1, tagValue2;
    @FXML private VBox additionalTagContainer;
    @FXML private DatePicker startDate, endDate;
    @FXML private Button applyButton, cancelButton;

    private UserHomeController parentController;
    private Stage stage;
    private FilterCriteria existingCriteria;

    /**
     * Initializes the controller by setting up the logical operator dropdown and action handlers
     * for the cancel and apply buttons.
     * <p>
     * This method is called when the controller is loaded, and it configures the UI controls accordingly.
     */
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
     * Sets up the controller with references to the parent controller, available tag names, 
     * the current stage, and existing filter criteria (if any).
     * <p>
     * This method populates the tag name dropdowns, initializes focus listeners, and loads any existing 
     * filter criteria into the UI controls.
     *
     * @param parentController the parent controller to pass the filter criteria back to
     * @param tagNames         a list of tag names to populate the dropdowns
     * @param stage            the current stage (window) for the UI
     * @param existingCriteria the existing filter criteria to load into the UI (can be null)
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
     * Toggles the visibility of the second tag input field based on the selected logical operator.
     * <p>
     * If a valid logical operator ("AND" or "OR") is selected, the second tag input field will be displayed.
     * If no operator is selected, the second tag field will be hidden.
     *
     * @param operator the selected logical operator (either "AND" or "OR")
     */
    private void toggleSecondTag(String operator) {
        boolean show = operator != null && !operator.isEmpty();
        additionalTagContainer.setManaged(show);
        additionalTagContainer.setVisible(show);
    }

    /**
     * Loads existing filter criteria (if available) into the UI controls.
     * <p>
     * If there are existing tag filters, their values will be set in the dropdowns and text fields. 
     * Additionally, any date filters will be populated.
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
    private void onTagValueEntered() {
        // Move focus away from the text field 
        applyButton.requestFocus();
    }

    /**
     * Sets up focus listeners for the tag value fields, so that when the user finishes editing
     * and the field loses focus, the value is committed.
     */
    private void setupFocusListeners() {
        addFocusListener(tagValue1);
        addFocusListener(tagValue2);
    }

    /**
     * Adds a focus listener to a text field, which commits the text when the field loses focus.
     *
     * @param field the text field to add the focus listener to
     */
    private void addFocusListener(TextField field) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) field.setText(field.getText().trim());
        });
    }

    /**
     * Applies the filter criteria based on the user's selections and closes the filter window.
     * <p>
     * This method collects the filter criteria (tags and dates), creates a FilterCriteria object, 
     * and passes it back to the parent controller. It then closes the current filter window.
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
     * Inner class to hold filter criteria.
     */
    public static class FilterCriteria {
        private List<TagFilter> tagFilters = new ArrayList<>();
        private String logicalOperator;
        private LocalDate startDate;
        private LocalDate endDate;

        /**
         * Adds a new tag filter with the specified name and value to the list of filters.
         *
         * @param name  the tag name to filter by
         * @param value the tag value to filter by
         */
        public void addTagFilter(String name, String value) {
            tagFilters.add(new TagFilter(name, value));
        }

        /**
         * Returns the list of tag filters.
         *
         * @return the list of {@code TagFilter} objects
         */
        public List<TagFilter> getTagFilters() {
            return tagFilters;
        }

        /**
         * Returns the logical operator used to combine tag filters (e.g., "AND" or "OR").
         *
         * @return the logical operator as a string
         */
        public String getLogicalOperator() {
            return logicalOperator;
        }

        /**
         * Sets the logical operator used to combine tag filters.
         *
         * @param logicalOperator the logical operator to set ("AND" or "OR")
         */
        public void setLogicalOperator(String logicalOperator) {
            this.logicalOperator = logicalOperator;
        }

        /**
         * Returns the start date for the date range filter.
         *
         * @return the start date, or {@code null} if not set
         */
        public LocalDate getStartDate() {
            return startDate;
        }

        /**
         * Sets the start date for the date range filter.
         *
         * @param startDate the start date to set
         */
        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        /**
         * Returns the end date for the date range filter.
         *
         * @return the end date, or {@code null} if not set
         */
        public LocalDate getEndDate() {
            return endDate;
        }

        /**
         * Sets the end date for the date range filter.
         *
         * @param endDate the end date to set
         */
        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        /**
         * Determines whether any filters have been set (either tag filters or date range).
         *
         * @return {@code true} if any filters are present; {@code false} otherwise
         */
        public boolean hasFilters() {
            return !tagFilters.isEmpty() || startDate != null || endDate != null;
        }
    }

    /**
     * Inner class to represent a tag name-value pair filter.
     */
    public static class TagFilter {
        private String name;
        private String value;

        /**
         * Constructs a new TagFilter with the specified name and value.
         *
         * @param name  the name of the tag
         * @param value the value of the tag
         */
        public TagFilter(String name, String value) {
            this.name = name;
            this.value = value;
        }

        /**
         * Returns the name of the tag filter.
         *
         * @return the tag name
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the value of the tag filter.
         *
         * @return the tag value
         */
        public String getValue() {
            return value;
        }
    }
}