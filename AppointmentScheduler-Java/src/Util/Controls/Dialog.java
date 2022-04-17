/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Dialog.java   2021.05.20 at 10:48:44 EDT
 * Version: 1.0.0
 */



package Util.Controls;

import java.util.Optional;

import javafx.application.Platform;

import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import javafx.stage.Modality;

//~--- classes ----------------------------------------------------------------

public final class Dialog
{
    public Dialog() {}

    //~--- methods ------------------------------------------------------------

    /**
     * Asks the user to confirm a choice and returns their selection.
     * @param content String
     * @param header String
     * @param title String
     * @return ButtonType, an enum representing the button pressed
     */
    @FXML
    public static Boolean confirmationDialog(String content, String header, String title)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();

        dialogPane.getStylesheets()
                  .add("View/style.css");
        dialogPane.getStyleClass()
                  .add("dialog");

        Optional<ButtonType> choice = alert.showAndWait();

        if ((choice.isPresent()) && (choice.get() == (ButtonType.OK)))
        {
            return true;
        }

        return false;
    }

    /**
     * Displays an error dialog and returns the alert itself so exception
     * handling can be allowed to take place gracefully.
     * @param content String
     * @param header String
     * @param title String
     * @return Alert
     */
    @FXML
    public static Alert errorDialog(String content, String header, String title)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.initModality(Modality.NONE);

        DialogPane dialogPane = alert.getDialogPane();

        dialogPane.getStylesheets()
                  .add("View/style.css");
        dialogPane.getStyleClass()
                  .add("dialog");
        dialogPane.setMinSize(300, 150);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        return alert;
    }

    /**
     * Display information to the user, returns void
     * @param content String
     * @param header String
     * @param title String
     */
    @FXML
    public static void infoDialog(String content, String header, String title)
    {
        Alert      alert      = new Alert(Alert.AlertType.INFORMATION);
        DialogPane dialogPane = alert.getDialogPane();

        dialogPane.getStylesheets()
                  .add("View/style.css");
        dialogPane.getStyleClass()
                  .add("dialog");
        Platform.runLater(
            () -> {
                alert.setTitle(title);
                alert.setHeaderText(header);
                alert.setContentText(content);
                alert.show();
            });
    }
}

/*
 * @(#)Dialog.java 2021.05.20 at 10:48:44 EDT
 * EOF
 */
