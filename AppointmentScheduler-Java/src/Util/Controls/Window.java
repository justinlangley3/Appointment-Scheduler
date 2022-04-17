/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Window.java   2021.05.19 at 06:10:31 EDT
 * Version: 1.0.0
 */



package Util.Controls;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import Controller.AddressController;
import Controller.AppointmentController;
import Controller.LoginController;
import Controller.MainController;
import Controller.RegisterController;

import Util.Locale.Locale;

//~--- classes ----------------------------------------------------------------

/*
* Class containing static methods for Window (stage) handling
* Overloaded method showNewWindow mitigates code clutter in Controller classes
*/

public class Window
{
    private static final String ADDRESS_DIRECTORY     = "/View/Address.fxml";
    private static final String APPOINTMENT_DIRECTORY = "/View/Appointment.fxml";
    private static final String ICON_PATH             = "/View/icon.png";
    private static final String LOGIN_DIRECTORY       = "/View/Login.fxml";
    private static final String MAIN_DIRECTORY        = "/View/Main.fxml";
    private static final String REGISTER_DIRECTORY    = "/View/Register.fxml";

    //~--- constant enums -----------------------------------------------------

    public static enum ViewState { NEW, EDIT; }

    public static enum WindowType { MAIN, LOGIN, REGISTER, APPOINTMENT, ADDRESS }

    //~--- methods ------------------------------------------------------------

    // easy method for closing a window on a MouseEvent (i.e. clicking a button)
    // can be called by detecting a certain action event and passing it here
    public static void closeWindow(MouseEvent e)
    {
        final Node  source = (Node) e.getSource();
        final Stage stage  = (Stage) source.getScene()
                                           .getWindow();

        stage.close();
    }

    public static void showLogin(Stage stage)
    {
        try {

            // Load entry view
            FXMLLoader loader = new FXMLLoader(Window.class.getResource(LOGIN_DIRECTORY));
            Parent     root   = loader.load();

            stage.setScene(new Scene(root));

            // Set controller
            LoginController loginController = loader.getController();

            // Pass this stage to the controller
            loginController.setStage(stage);
            stage.initStyle(StageStyle.UNIFIED);

            // Set current stage properties
            stage.setResizable(false);
            stage.getIcons()
                 .add(new Image(ICON_PATH));
            stage.setTitle(Locale.get()
                                 .val("Login.Stage.title"));

            // Center on screen and show
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(Window.class.getName())
                  .log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param title sets the Window title
     * @param w
     * @param m
     * @param isResizable boolean to set whether the window is resizable or not
     */
    public static Stage showNewWindow(String title, WindowType w, Modality m, boolean isResizable)
    {
        FXMLLoader loader;

        switch (w) {
          case MAIN :

              // attempt to show the main window
              try {

                  // get resources
                  loader = new FXMLLoader(Window.class.getResource(Window.MAIN_DIRECTORY));

                  Parent         root           = (Parent) loader.load();
                  MainController mainController = new MainController();

                  // set the controller
                  loader.setController(mainController);

                  // create a new stage
                  Stage stage = new Stage();

                  stage.centerOnScreen();

                  // set the stage properties
                  stage.initModality(m);
                  stage.setResizable(isResizable);
                  stage.getIcons()
                       .add(new Image(Window.ICON_PATH));
                  stage.setTitle(title);
                  stage.setScene(new Scene(root));

                  return stage;
              } catch (IOException e) {
                  Logger logger = Logger.getLogger(Window.class.getName());

                  logger.log(Level.SEVERE, "Failed to create new Window.\n", e);
              }

              break;

          case REGISTER :

              // attempt to show the register user window
              try {

                  // get resources
                  loader = new FXMLLoader(Window.class.getResource(REGISTER_DIRECTORY));

                  Parent root = (Parent) loader.load();

                  // get the controller
                  RegisterController registerController = new RegisterController();

                  loader.setController(registerController);

                  // create a new stage
                  Stage stage = new Stage();

                  registerController.setStage(stage);
                  stage.centerOnScreen();

                  // set the stage properties
                  stage.initModality(m);
                  stage.setResizable(isResizable);
                  stage.getIcons()
                       .add(new Image(ICON_PATH));
                  stage.setTitle(title);
                  stage.setScene(new Scene(root));

                  return stage;
              } catch (IOException e) {
                  Logger logger = Logger.getLogger(Window.class.getName());

                  logger.log(Level.SEVERE, "Failed to create new Window.\n", e);
              }

              break;

          case APPOINTMENT :

              // attempt to show the appointment window
              try {

                  // get resources
                  loader = new FXMLLoader(Window.class.getResource(APPOINTMENT_DIRECTORY));

                  Parent root = (Parent) loader.load();

                  //
                  AppointmentController appointmentController = new AppointmentController();

                  // set any necessary variables
                  // set controller
                  loader.setController(appointmentController);

                  // create a new stage
                  Stage stage = new Stage();

                  stage.centerOnScreen();

                  // set the stage properties
                  stage.initModality(m);
                  stage.setResizable(isResizable);
                  stage.getIcons()
                       .add(new Image(ICON_PATH));
                  stage.setTitle(title);
                  stage.setScene(new Scene(root));

                  return stage;
              } catch (IOException e) {
                  Logger logger = Logger.getLogger(Window.class.getName());

                  logger.log(Level.SEVERE, "Failed to create new Window.\n", e);
              }

              break;

          case ADDRESS :

              // attempt to show the address window
              try {

                  // get resources
                  loader = new FXMLLoader(Window.class.getResource(ADDRESS_DIRECTORY));

                  Parent root = (Parent) loader.load();

                  // get the controller
                  AddressController addressController = new AddressController();

                  loader.setController(addressController);

                  // create a new stage
                  Stage stage = new Stage();

                  stage.centerOnScreen();

                  // set the stage properties
                  stage.initModality(m);
                  stage.setResizable(isResizable);
                  stage.getIcons()
                       .add(new Image(ICON_PATH));
                  stage.setTitle(title);
                  stage.setScene(new Scene(root));

                  return stage;
              } catch (IOException e) {
                  Logger logger = Logger.getLogger(Window.class.getName());

                  logger.log(Level.SEVERE, "Failed to create new Window.\n", e);
              }

              break;
        }

        return null;
    }
}

/*
 * @(#)Window.java 2021.05.19 at 06:10:31 EDT
 * EOF
 */
