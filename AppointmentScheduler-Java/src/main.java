/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)main.java   2021.05.21 at 08:44:43 EDT
 * Version: 1.0.0
 */



import javafx.application.Application;

import javafx.stage.Stage;

import Util.Controls.Window;

//~--- classes ----------------------------------------------------------------

public class main
        extends Application
{
    private final String icon;

    //~--- constructors -------------------------------------------------------

    public main()
    {
        this.icon = "View/icon.png";
    }

    //~--- methods ------------------------------------------------------------

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        Window.showLogin(stage);
    }
}

/*
 * @(#)main.java 2021.05.21 at 08:44:43 EDT
 * EOF
 */
