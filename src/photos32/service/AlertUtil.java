package photos32.service;

import javafx.scene.control.Alert;

public class AlertUtil {

    /**
     * Utility method to configure and display an alert dialog.
     *
     * @param alert   the {@link Alert} to display
     * @param title   the title of the alert
     * @param header  the optional header text
     * @param content the content message of the alert
     */
    public static void showAlert(Alert alert, String title, String header, String content) {
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
    }
}
