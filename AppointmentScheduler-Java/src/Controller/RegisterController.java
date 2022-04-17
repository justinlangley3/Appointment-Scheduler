/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)RegisterController.java   2021.05.19 at 06:13:33 EDT
 * Version: 1.0.0
 */



package Controller;

import java.net.URL;

import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javafx.stage.Stage;

import Model.DAO.Impl.UserDAOImpl;

import Model.User;

import Util.Controls.Validate;
import Util.Controls.Window;

import static Util.Security.Crypto.Hash.generateHash;
import static Util.Security.Crypto.Hash.generateSalt;

//~--- classes ----------------------------------------------------------------

public class RegisterController
        implements Initializable
{
    private final UserDAOImpl UserDAO;
    Stage                     stage;
    @FXML
    private Button            register, cancel;

    // executor for pooling threads
    private Executor      exec;
    @FXML
    private Label         validation;
    @FXML
    private PasswordField fieldConfirm;
    @FXML
    private PasswordField fieldPassword;
    @FXML
    private TextField     fieldUser;

    //~--- constructors -------------------------------------------------------

    public RegisterController()
    {
        this.UserDAO = new UserDAOImpl();
    }

    //~--- methods ------------------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {

        // set executor to use daemon threads
        exec = Executors.newCachedThreadPool(
            runnable -> {
                Thread t = new Thread(runnable);

                t.setDaemon(false);

                return t;
            });

        register.setOnMouseClicked(
            (MouseEvent e) -> {
                register();
                Window.closeWindow(e);
            });
        cancel.setOnMouseClicked(
            (MouseEvent e) -> {
                Window.closeWindow(e);
            });
    }

    //~--- set methods --------------------------------------------------------

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    //~--- methods ------------------------------------------------------------

    @FXML
    private void cancel()
    {
        stage.close();
    }

    @FXML
    private boolean checkUsername(String username)
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

    private boolean compareCharArray(char[] password, char[] confirm)
    {
        return Arrays.equals(password, confirm);
    }

    @FXML
    private void register()
    {

        // grab user input
        final String username = fieldUser.getText();
        final char[] password = fieldPassword.getText()
                                             .toCharArray();
        final char[] confirm  = fieldConfirm.getText()
                                            .toCharArray();

        if ((username.isEmpty()) && (password.length == 0) && (confirm.length == 0))
        {
            Validate.Register.empty(fieldUser, fieldPassword, fieldConfirm, validation);

            return;
        }

        if (username.isEmpty())
        {
            Validate.Register.usernameFieldEmpty(fieldUser, fieldPassword, fieldConfirm, validation);

            return;
        }

        if ((password.length == 0) || (confirm.length == 0))
        {
            Validate.Register.passwordEmpty(fieldUser, fieldPassword, fieldConfirm, validation);

            return;
        }

        // Note: usernames are inherently case insensitive
        if (checkUsername(username))
        {
            Validate.Register.usernameTaken(fieldUser, fieldPassword, fieldConfirm, validation);

            return;
        }

        if (!compareCharArray(password, confirm))
        {
            Validate.Register.mismatch(fieldUser, fieldPassword, fieldConfirm, validation);

            return;
        }
        else
        {
            /*
             * Build a new user ...
             * We need to generate a salt first. Then, we can hash the user's
             * salted input (accomplished with the generateHash() function).
             * Both the hash and salt will be stored in the database.
             *
             * Using a basic constructor provided by the User model, we can
             * create a user object suitable to send to the DB, where it will
             * fill in the rest of the user's properties (ID, timestamps, etc).
             */

            // hashing function from our security util
            // returns an ArrayList<byte[]> with elements {hash, salt}
            User              newUser = null;
            ArrayList<byte[]> hash    = null;

            try {
                hash = generateHash(password, generateSalt());
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(RegisterController.class.getName())
                      .log(Level.SEVERE, null, ex);
            }

            if (!hash.isEmpty() || (hash != null))
            {
                // basic user object constructor from the User model
                newUser = new User(username, hash.get(0), hash.get(1), username, username);
            }
            else
            {
                // dialog to notify user that registration failed
            }

            // submit to the database
            UserDAO.create(newUser);
        }
    }
}

/*
 * @(#)RegisterController.java 2021.05.19 at 06:13:33 EDT
 * EOF
 */
