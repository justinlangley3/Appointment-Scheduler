/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)MainController.java   2021.05.21 at 12:02:43 EDT
 * Version: 1.0.0
 */



package Controller;

import java.io.IOException;

import java.net.URL;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.Event;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

import Model.Appointment;

import Model.DAO.Impl.AppointmentDAOImpl;

import Model.Instance.DataHolder;

import Util.Controls.Dialog;

import Util.Date.Time;

//~--- classes ----------------------------------------------------------------

public class MainController
        implements Initializable
{
    private final AppointmentDAOImpl appointmentDAO;
    @FXML
    private Menu                     file, edit, view;
    @FXML
    private MenuBar                  applicationMenu;
    @FXML
    private MenuItem                 logout, exit, newAddress, newAppointment, addresses, calendar, cities, countries,
                                     reports, weekly, about;
    @FXML
    private Tab addressBookTab, calendarTab, citiesTab, countriesTab, customersTab, reportsTab, weeklyTab;

    //~--- constructors -------------------------------------------------------

    /*
     *  ---------------------------- View Embedding ----------------------------
     *  Notes:
     *      - This is a method for embedding nested views. This approach makes
     *        use of nested controllers.
     *
     *      - The DataHolder Singleton class defined in the package
     *        "Model.Instance" makes calling this method easier. There are
     *        getter methods defined for URL objects of each nested FXML file
     *        of this application. Any of these method calls of the DataHolder
     *        class may be passed as a parameter to this method here.
     *
     *      - After passing in a URL this method can then embed the correct
     *        content on a per tab basis.
     *
     *      - Calls to this method should look similar to this:
     *          embedView(DataHolder.getInstance().getSomeURL())
     *
     *      - To make all of this work an "<fx:include>" has been set on each
     *        AnchorPane for the FXML file containing the embedded content for
     *        its respective AnchorPane parent. These includes are found in the
     *        Main.fxml file for this MainController class as we are performing
     *        the embeds here.
     *
     *      - Making use of the Platform.runLater() built-in allows for setting
     *        view content while other activities occur in nested controller
     *        logic such as database communication. After these activities
     *        complete the views will update with any changes that occured
     *        since the time of setting the content. This is primarily to
     *        reduce the noticeable "hang" that occurs within the application.
     */

    public MainController()
    {
        appointmentDAO = new AppointmentDAOImpl();
    }

    //~--- methods ------------------------------------------------------------

    @FXML
    public void checkUpcomingAppointments()
    {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        if (appointmentDAO.doesTableHaveDataForDate(new Time().setFormatPattern(Time.FormatPattern.LOCAL_DATE)
                                                              .toString()))
        {
            appointments.setAll(
                FXCollections.observableArrayList(
                    appointmentDAO.readAppointmentFromDate(new Time().setFormatPattern(Time.FormatPattern.LOCAL_DATE)
                                                                     .toString())));
        }

        if (!appointments.isEmpty())
        {
            for (int i = 0 ; i < appointments.size() ; i++) {
                Boolean appointmentExists =
                    new Time(appointments.get(i).getStart()).getLocalDateTime().isBefore(LocalDateTime.now().plus(15L,
                                                                                                                  ChronoUnit.MINUTES))
                    && new Time(appointments.get(i).getStart()).getLocalDateTime().isAfter(LocalDateTime.now());

                if (appointmentExists)
                {
                    String content = "Details:\r\n" + "AppointmentId:\t" + appointments.get(i).getAppointmentId()
                                     + "\nTitle:\t\t\t\t" + appointments.get(i).getTitle() + "\nDetails:\t\t\t"
                                     + appointments.get(i).getSummary() + "\nStart time:\t\t"
                                     + appointments.get(i).getStart() + "\nEnd Time:\t\t\t"
                                     + appointments.get(i).getEnd() + "\nContact:\t\t\t"
                                     + appointments.get(i).getContact() + "\r\n";
                    String header = "There is an appointment within the next 15 minutes.";
                    String title  = "Upcoming Appointment";

                    Dialog.infoDialog(content, header, title);

                    return;
                }
            }
        }
    }

    @FXML
    public void embedView(URL location) throws IOException
    {

        // new FXMLLoader for every call to this method
        final FXMLLoader loader = new FXMLLoader(location);

        // document root
        final AnchorPane tabAnchor = loader.load();

        // ---------------------- Embed Address Book View ---------------------
        if (location == DataHolder.getInstance().getAddressBookTabURL())
        {
            Platform.runLater(
                () -> {
                    addressBookTab.setContent(tabAnchor);
                });
        }

        // ------------------------ Embed Calendar View -----------------------
        if (location == (DataHolder.getInstance().getCalendarTabURL()))
        {
            Platform.runLater(
                () -> {
                    calendarTab.setContent(tabAnchor);
                });
        }

        // ------------------------ Embed Cities View -------------------------
        if (location == (DataHolder.getInstance().getCitiesTabURL()))
        {
            Platform.runLater(
                () -> {
                    citiesTab.setContent(tabAnchor);
                });
        }

        // ----------------------- Embed Countries View -----------------------
        if (location == (DataHolder.getInstance().getCountriesTabURL()))
        {
            Platform.runLater(
                () -> {
                    countriesTab.setContent(tabAnchor);
                });
        }

        // ----------------------- Embed Customers View -----------------------
        if (location == (DataHolder.getInstance().getCustomersTabURL()))
        {
            Platform.runLater(
                () -> {
                    customersTab.setContent(tabAnchor);
                });
        }

        // ------------------------ Embed Reports View ------------------------
        if (location == (DataHolder.getInstance().getReportsTabURL()))
        {
            Platform.runLater(
                () -> {
                    reportsTab.setContent(tabAnchor);
                });
        }

        // ------------------------ Embed Weekly View -------------------------
        if (location == (DataHolder.getInstance().getWeeklyTabURL()))
        {
            Platform.runLater(
                () -> {
                    weeklyTab.setContent(tabAnchor);
                });
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {

        /*
         * ------------------------- Tab Event Handling ------------------------
         * Notes:
         *  - The purpose of theses lambda expressions is so that we can
         *    programatically modify the tabs' behavior as if they were buttons.
         *
         *  - Additionally, the logic for the tabs was made into it's own
         *    parameterized method — embedView(URL location)
         *    It's functionally the same as an EventHandler.
         *
         *  - We need only change the underlying method call when refactoring,
         *    and changes will automatically reflect here.
         */

        /*
         *  ------------------------ Address Book Tab --------------------------
         */

        addressBookTab.setOnSelectionChanged(
            (Event e) -> {
                if (addressBookTab.isSelected())
                {
                    try {
                        embedView(DataHolder.getInstance()
                                            .getAddressBookTabURL());
                    } catch (IOException ex) {
                        Logger.getLogger(MainController.class.getName())
                              .log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    addressBookTab.setContent(null);
                }
            });

        /*
         *  ---------------------------- Cities Tab ----------------------------
         */

        citiesTab.setOnSelectionChanged(
            (Event e) -> {
                if (citiesTab.isSelected())
                {
                    try {
                        embedView(DataHolder.getInstance()
                                            .getCitiesTabURL());
                    } catch (IOException ex) {
                        Logger.getLogger(MainController.class.getName())
                              .log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    citiesTab.setContent(null);
                }
            });

        /*
         *  --------------------------- Countries Tab --------------------------
         */

        countriesTab.setOnSelectionChanged(
            (Event e) -> {
                if (countriesTab.isSelected())
                {
                    try {
                        embedView(DataHolder.getInstance()
                                            .getCountriesTabURL());
                    } catch (IOException ex) {
                        Logger.getLogger(MainController.class.getName())
                              .log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    countriesTab.setContent(null);
                }
            });

        /*
         *  --------------------------- Calendar Tab ---------------------------
         */

        calendarTab.setOnSelectionChanged(
            (Event e) -> {
                if (calendarTab.isSelected())
                {
                    try {
                        embedView(DataHolder.getInstance()
                                            .getCalendarTabURL());
                    } catch (IOException ex) {
                        Logger.getLogger(MainController.class.getName())
                              .log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    calendarTab.setContent(null);
                }
            });

        /*
         *  --------------------------- Customers Tab --------------------------
         */

        customersTab.setOnSelectionChanged(
            (Event e) -> {
                if (customersTab.isSelected())
                {
                    try {
                        embedView(DataHolder.getInstance()
                                            .getCustomersTabURL());
                    } catch (IOException ex) {
                        Logger.getLogger(MainController.class.getName())
                              .log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    customersTab.setContent(null);
                }
            });

        /*
         *  ---------------------------- Reports Tab ---------------------------
         */

        reportsTab.setOnSelectionChanged(
            (Event e) -> {
                if (reportsTab.isSelected())
                {
                    try {
                        embedView(DataHolder.getInstance()
                                            .getReportsTabURL());
                    } catch (IOException ex) {
                        Logger.getLogger(MainController.class.getName())
                              .log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    reportsTab.setContent(null);
                }
            });

        /*
         *  ---------------------------- Weekly Tab ----------------------------
         */

        weeklyTab.setOnSelectionChanged(
            (Event e) -> {
                if (weeklyTab.isSelected())
                {
                    try {
                        embedView(DataHolder.getInstance()
                                            .getWeeklyTabURL());
                    } catch (IOException ex) {
                        Logger.getLogger(MainController.class.getName())
                              .log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    weeklyTab.setContent(null);
                }
            });
        Platform.runLater(
            () -> {
                checkUpcomingAppointments();
            });
    }
}

/*
 * @(#)MainController.java 2021.05.21 at 12:02:43 EDT
 * EOF
 */
