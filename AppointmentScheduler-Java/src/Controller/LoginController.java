/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)LoginController.java   2021.05.21 at 07:22:55 EDT
 * Version: 1.0.0
 */



package Controller;

import java.io.IOException;

import java.net.URL;

import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.FXCollections;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javafx.stage.Modality;
import javafx.stage.Stage;

import Model.DAO.Impl.UserDAOImpl;

import Model.User;

import Util.Controls.Validate;
import Util.Controls.Window.WindowType;

import Util.DB.Database;

import Util.Locale.Locale;

import Util.Security.Crypto;
import Util.Security.Crypto.AuthenticationException;
import Util.Security.Log;

import static Util.Controls.Window.showNewWindow;

//~--- classes ----------------------------------------------------------------

public class LoginController
        implements Initializable
{
    private final UserDAOImpl UserDAO;
    public User               currentUser;
    @FXML
    private Button            register, login, exit;
    @FXML
    private ComboBox<String>  localeComboBox;

    // executor for thread pooling
    private Executor      exec;
    @FXML
    private Label         usernameLabel, passwordLabel, validation, title;
    @FXML
    private PasswordField password;
    private Stage         stage;
    @FXML
    private TextField     username;

    //~--- constant enums -----------------------------------------------------

    private enum LoginState {
        LOGIN_OK, LOGIN_FAIL, USERNAME_EMPTY, PASSWORD_EMPTY, BOTH_EMPTY, USERNAME_LENGTH_EXCEEDS,
        PASSWORD_LENGTH_EXCEEDS, BOTH_LENGTH_EXCEEDS, CREDENTIALS_NOT_FOUND, USERNAME_FAIL, HASH_FAIL;
    }

    //~--- constructors -------------------------------------------------------

    public LoginController()
    {
        this.UserDAO     = new UserDAOImpl();
        this.currentUser = new User();
    }

    //~--- methods ------------------------------------------------------------

    // Note: usernames are inherently case insensitive
    public boolean checkUsername(String username)
    {
        boolean       exists        = false;
        Task<Boolean> userExistTask = new Task<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                return UserDAO.doesUserExist(username);
            }
        };

        userExistTask.setOnSucceeded(
            (WorkerStateEvent e) -> {
                boolean b = (Boolean) e.getSource()
                                       .getValue();
            });
        exec.execute(userExistTask);

        try {
            exists = userExistTask.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(LoginController.class.getName())
                  .log(Level.SEVERE, null, ex);
        }

        return exists;
    }

    @FXML
    public void configureLocaleComboBox()
    {
        Platform.runLater(
            () -> {
                localeComboBox.setItems(FXCollections.observableArrayList("English", "Español"));
                localeComboBox.getSelectionModel()
                              .selectedItemProperty()
                              .addListener(new LocaleComboBoxListener());
            });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        configureLocaleComboBox();
        setLocaleComboBox();
        setControls();

        // set executor to use daemon threads
        exec = Executors.newCachedThreadPool(
            runnable -> {
                Thread t = new Thread(runnable);

                t.setDaemon(false);

                return t;
            });

        register.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                register();
            });
        login.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                try {
                    login();
                } catch (NoSuchAlgorithmException ex) {

                    // Do some sort of information log dump and call Platform.exit()
                    Logger.getLogger(LoginController.class.getName())
                          .log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(LoginController.class.getName())
                          .log(Level.SEVERE, null, ex);
                }
            });
        login.setOnKeyPressed(
            (KeyEvent key) -> {
                if (key.getCode().equals(KeyCode.ENTER)
                    || (key.getCharacter().getBytes() [0] == '\n')
                    || (key.getCharacter().getBytes() [0] == '\r'))
                {
                    try {
                        login();
                    } catch (NoSuchAlgorithmException ex) {

                        // Do some sort of information log dump and call Platform.exit()
                        Logger.getLogger(LoginController.class.getName())
                              .log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {

                        // Display some dialog
                        Logger.getLogger(LoginController.class.getName())
                              .log(Level.SEVERE, null, ex);
                    }
                }
            });
        exit.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                exit();
            });
    }

    public void retrieveUser(String username)
    {
        Task<User> retrieveUserTask = new Task<User>()
        {
            @Override
            public User call() throws Exception
            {
                return UserDAO.read(username);
            }
        };

        exec.execute(retrieveUserTask);

        try {
            setUser(retrieveUserTask.get());
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(LoginController.class.getName())
                  .log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void updateStageTitleOnLocaleChange()
    {
        try {
            stage.setTitle(Locale.get()
                                 .val("Login.Stage.title"));
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName())
                  .log(Level.SEVERE, null, ex);
        }
    }

    //~--- get methods --------------------------------------------------------

    public User getUser()
    {
        return this.currentUser;
    }

    public ArrayList<byte[]> getUserHashData(String username)
    {
        ArrayList<byte[]>       hashData     = new ArrayList<>();
        Task<ArrayList<byte[]>> userDataTask = new Task<ArrayList<byte[]>>()
        {
            @Override
            public ArrayList<byte[]> call() throws Exception
            {

                // get user hash data from the DB
                return UserDAO.readHashData(username);
            }
        };

        // execute the task
        exec.execute(userDataTask);

        // retrieve data
        try {
            hashData = userDataTask.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(LoginController.class.getName())
                  .log(Level.SEVERE, null, ex);
        }

        return hashData;
    }

    //~--- set methods --------------------------------------------------------

    @FXML
    public void setControls()
    {
        try {
            title.setText(Locale.get()
                                .val("Login.Label.title"));
            usernameLabel.setText(Locale.get()
                                        .val("Fields.Label.Username"));
            passwordLabel.setText(Locale.get()
                                        .val("Fields.Label.Password"));
            login.setText(Locale.get()
                                .val("Button.login"));
            exit.setText(Locale.get()
                               .val("Button.exit"));
            register.setText(Locale.get()
                                   .val("Button.register"));
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName())
                  .log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void setLocaleComboBox()
    {
        switch (Locale.get()
                      .getLocale()) {
          case "en-US" :
              Platform.runLater(
                  () -> {
                      localeComboBox.getSelectionModel()
                                    .select(0);
                  });

              break;

          case "es-ES" :
              Platform.runLater(
                  () -> {
                      localeComboBox.getSelectionModel()
                                    .select(1);
                  });

              break;

          default :
              Platform.runLater(
                  () -> {
                      localeComboBox.getSelectionModel()
                                    .select(0);
                  });
        }
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    public void setUser(User user)
    {
        this.currentUser = user;
    }

    //~--- methods ------------------------------------------------------------

    @FXML
    private void exit()
    {
        Database.getInstance()
                .closeConnection(Database.getInstance()
                                         .getConnection());
        Platform.exit();
    }

    @FXML
    private void focus()
    {
        Platform.runLater(
            () -> {
                stage.show();
            });
    }

    @FXML
    private void hide()
    {
        Platform.runLater(
            () -> {
                stage.hide();
            });
    }

    @FXML
    private void login() throws NoSuchAlgorithmException, InterruptedException
    {
        Validate.Login.clearContext(username, password, validation);

        LoginState state = getLoginState();

        switch (state) {
          case LOGIN_OK :
              Log log = new Log();

              log.log(Level.INFO, "New login instance, User: " + currentUser.getUsername());
              Model.Instance.DataHolder.getInstance()
                                       .setUser(currentUser);
              Platform.runLater(
                  () -> {

                  // handle validation
                      Validate.Login.success(username, password, validation);

                      // Log success message to console
                      System.out.println("Login successful. User: {ID=" + currentUser.getUserId().toString() + ", "
                                         + currentUser.getUsername() + "}\n");
                  });
              showMainView();

              break;

          case USERNAME_FAIL :
              Platform.runLater(
                  () -> {

                  // handle validation
                      Validate.Login.usernameFail(username, password, validation);
                  });
              System.out.println("Username does not exist.\n");

              break;

          case HASH_FAIL :
              Platform.runLater(
                  () -> {

                  // handle validation
                      Validate.Login.hashFail(username, password, validation);
                  });
              System.out.println("Password does not match.\n");

              break;

          case BOTH_EMPTY :
              Platform.runLater(
                  () -> {

                  // handle validation
                      Validate.Login.fieldsEmpty(username, password, validation);
                  });
              System.out.println("Both fields are empty.\n");

              break;

          case USERNAME_EMPTY :
              Platform.runLater(
                  () -> {

                  // handle validation
                      Validate.Login.usernameFieldEmpty(username, password, validation);
                  });
              System.out.println("Username field is empty.\n");

              break;

          case PASSWORD_EMPTY :
              Platform.runLater(
                  () -> {

                  // handle validation
                      Validate.Login.passwordFieldEmpty(username, password, validation);
                  });
              System.out.println("Password field is empty.\n");

              break;

          case USERNAME_LENGTH_EXCEEDS :
              Platform.runLater(
                  () -> {
                      Validate.Login.usernameLengthExceeds(username, password, validation);
                  });

              break;

          case PASSWORD_LENGTH_EXCEEDS :
              Platform.runLater(
                  () -> {
                      Validate.Login.passwordLengthExceeds(username, password, validation);
                  });

              break;

          case BOTH_LENGTH_EXCEEDS :
              Platform.runLater(
                  () -> {
                      Validate.Login.bothLengthExceeds(username, password, validation);
                  });

              break;

          case CREDENTIALS_NOT_FOUND :
              Platform.runLater(
                  () -> {

                  // handle validation, dialog
                      Validate.Login.credentialsNotFound(username, password, validation);
                  });
              System.out.println("Credentials could not be verified.");
              System.out.println("Check connection and try again.\n");

              break;

          case LOGIN_FAIL :
              Platform.runLater(
                  () -> {

                  // handle validation
                      Validate.Login.failure(username, password, validation);
                  });
              System.out.println("An unknown error occured during login.\n");

              break;
        }
    }

    @FXML
    private void register()
    {
        showRegisterView();
    }

    @FXML
    private void showMainView()
    {
        hide();

        Stage main = showNewWindow("Appointment Scheduler — Overview", WindowType.MAIN, Modality.WINDOW_MODAL, true);

        main.setOnHiding(
            (e) -> {
                focus();
            });
        Platform.runLater(
            () -> {
                main.showAndWait();
            });
    }

    @FXML
    private void showRegisterView()
    {
        hide();

        Stage register = showNewWindow("Appointment Scheduler — Register New User",
                                       WindowType.REGISTER,
                                       Modality.WINDOW_MODAL,
                                       false);

        register.setOnHiding(
            (e) -> {
                focus();
            });
        Platform.runLater(
            () -> {
                register.showAndWait();
            });
    }

    //~--- get methods --------------------------------------------------------

    private LoginState getLoginState() throws AuthenticationException
    {

        /*
         * For storing hash data queried from the DB. This is locally scoped,
         * as we don't need it outside of this function. Once we authenticate
         * the user, our public User object will be populated with this data,
         * and readily available for further use throughout the program.
         */

        ArrayList<byte[]> userHashData = new ArrayList<>();

        /*
         * Outcomes of key logic checks, which are initialized to false in order
         * to prevent unauthorized access.
         */

        boolean      userExists  = false;
        boolean      hashMatches = false;
        final String username    = this.username.getText();
        final char[] password    = this.password.getText()
                                                .toCharArray();

        try {

            // null checks on form field(s)
            if (username.isEmpty() && (password.length == 0))
            {
                return LoginState.BOTH_EMPTY;
            }

            if (username.isEmpty() && (!(password.length == 0)))
            {
                return LoginState.USERNAME_EMPTY;
            }

            if ((password.length == 0) && (!username.isEmpty()))
            {
                return LoginState.PASSWORD_EMPTY;
            }

            if ((username.length() > 32) && (!(password.length > 32)))
            {
                return LoginState.USERNAME_LENGTH_EXCEEDS;
            }

            if (!(username.length() > 32) && ((password.length > 32)))
            {
                return LoginState.PASSWORD_LENGTH_EXCEEDS;
            }
            else
            {
                // null checks and length checks passed - safe to attempt retrieval
                // run our threads to retrieve DB data
                userExists   = UserDAO.doesUserExist(username);
                userHashData = UserDAO.readHashData(username);

                // attempt to set current user (we're assuming the user entered a correct username)
                retrieveUser(username);
            }

            if ((username.length() > 32) && (password.length > 32))
            {
                return LoginState.BOTH_LENGTH_EXCEEDS;
            }
            else if (!userExists)
            {
                // user does not exist
                return LoginState.USERNAME_FAIL;
            }

            // last null check to see if the DB returned data
            if (!((userHashData != null) || (userHashData.size() > 0)))
            {
                // the data was unable to be retrieved for hash comparison
                // this shouldn't happen except for DB connectivity issues
                System.out.println("Login Failed. User: {" + this.username.getText()
                                   + "}, credentials were unable to be verified.");

                return LoginState.CREDENTIALS_NOT_FOUND;
            }

            // Compare hash of the entered password against the DB entry
            try {

                // Store results in local boolean hashMatches
                hashMatches = Crypto.Hash.isHashValid(this.password.getText()
                                                                   .toCharArray(),
                                                      userHashData.get(0),
                                                      userHashData.get(1));
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(LoginController.class.getName())
                      .log(Level.SEVERE, null, ex);

                // general login failure, due to non-existent hashing algorithm
                return LoginState.LOGIN_FAIL;
            }

            // check if the password is a match
            // note: the boolean was set in the above try-catch
            if (!hashMatches)
            {
                // hash mismatch
                return LoginState.HASH_FAIL;
            }

            if (hashMatches)
            {
                System.out.println("Login authenticated.\n");

                // valid login
                return LoginState.LOGIN_OK;
            }

            // all previous checks failed, we should throw an exception
            throw new AuthenticationException("Unable to authenticate login state.");
        } catch (AuthenticationException e) {

            // catch and log the exception
            Logger.getLogger(LoginController.class.getName())
                  .log(Level.SEVERE, null, e);

            // general login failure, not applicable to any prior checks
            return LoginState.LOGIN_FAIL;
        }
    }

    //~--- inner classes ------------------------------------------------------

    private class LocaleComboBoxListener
            implements ChangeListener<String>
    {
        public LocaleComboBoxListener() {}

        //~--- methods --------------------------------------------------------

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
        {
            switch (newValue) {
              case "Español" :
                  Locale.get()
                        .setLocale("es-ES");

                  break;

              case "English" :
                  Locale.get()
                        .setLocale("en-US");

                  break;
            }

            Platform.runLater(
                () -> {
                    setControls();
                    updateStageTitleOnLocaleChange();
                });
            Validate.Login.clearContext(username, password, validation);
        }
    }
}

/*
 * @(#)LoginController.java 2021.05.21 at 07:22:55 EDT
 * EOF
 */
