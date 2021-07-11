package Utils;

import javafx.scene.control.Alert;

/**
 * Class used for generating simple alerts used throughout application
 */
public class Alerts {
    /**
     * This method pulls in needed variables to generate alerts to simplify alert usage throughout code
     * @param type alert type
     * @param title alert title
     * @param header alert header
     * @param content alert message/content
     * @param showStatus alert status
     */
    public static void GenerateAlert(String type, String title, String header, String content, String showStatus) {
        Alert alert = new Alert(Alert.AlertType.valueOf(type));
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        switch (showStatus) {
            case "Show":
                alert.show();
                break;
            case "ShowAndWait":
                alert.showAndWait();
                break;
        }
    }
}
