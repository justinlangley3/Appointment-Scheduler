/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)CalendarTab.java   2021.05.21 at 03:02:54 EDT
 * Version: 1.0.0
 */



package Controller.Tabs;

import java.net.URL;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.application.Platform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import javafx.stage.Modality;
import javafx.stage.Stage;

import Model.Appointment;

import Model.DAO.Impl.AppointmentDAOImpl;

import Model.Dummy.Month;
import Model.Dummy.Week;

import Model.Instance.DataHolder;

import Util.Controls.Dialog;
import Util.Controls.Window;

import Util.Date.Gregorian;
import Util.Date.Time;

//~--- classes ----------------------------------------------------------------

public class CalendarTab
        implements Initializable
{
    private final AppointmentDAOImpl          appointmentDAO;
    private final Gregorian                   calendar;
    private final ObservableList<Appointment> appointments;
    String                                    selectedDate;
    private Appointment                       selectedAppointment;
    @FXML
    private Button                            nextMonth, prevMonth, nextYear, prevYear, newAppointment, editAppointment,
                                              deleteAppointment;
    @FXML
    private Label                             position;
    @FXML
    private ListView<Appointment>             appointmentListView;
    private Month                             month;
    @FXML
    private TableColumn<Week, String>         sun, mon, tue, wed, thu, fri, sat;
    @FXML
    private TableView<Week>                   calendarTable;

    //~--- constructors -------------------------------------------------------

    // private TableView calendar;
    public CalendarTab()
    {
        appointmentDAO = new AppointmentDAOImpl();
        selectedDate   = null;
        calendar       = new Gregorian();
        appointments   = FXCollections.observableArrayList();
    }

    //~--- methods ------------------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        selectedDate = new Time().setFormatPattern(Time.FormatPattern.LOCAL_DATE)
                                 .toString();

        // This lambda expression causes the GUI to update more immediately
        // Additionally, it keeps errors from throwing if FXML items aren't set, yet
        configureButtons();
        configureAppointmentListView();
        configureCalendarTable();
        populateCalendarTable(calendar);
        updatePosition();
        populateListView();
        addCalendarTableListener();
    }

    @FXML
    private void addCalendarTableListener()
    {
        calendarTable.getFocusModel().focusedCellProperty().addListener((ObservableValue<? extends TablePosition> observable, TablePosition oldPos, TablePosition pos) -> {
                int    row           = pos.getRow();
                int    column        = pos.getColumn();
                String selectedValue = "";

                // check that position is not equal to -1, or we'll get a nullpointer exception
                if ((pos.getRow() != -1) && (pos.getColumn() != -1))
                {
                    // capture the user's selection
                    selectedValue = calendarTable.getItems()
                                                 .get(row)
                                                 .get(column);

                    // check if the selection contains data
                    if ((selectedValue != null) && (!selectedValue.isEmpty()))
                    {
                        // get the date string with the calendar object
                        selectedDate = calendar.getDatetime()
                                               .setDay(Integer.valueOf(selectedValue))
                                               .update()
                                               .toString();

                        refreshData();
                    }
                    else
                    {
                        selectedDate = "";

                        refreshData();
                    }
                }
            });
    }

    @FXML
    private void configureAppointmentListView()
    {

        // set a value factory that handles displaying apopointments
        // using a lambda makes it easier to set what we need without having to subclass
        appointmentListView.setCellFactory(
            (ListView<Appointment> c) -> {
                ListCell<Appointment> cell = new ListCell<Appointment>()
                {
                    @Override
                    protected void updateItem(Appointment item, boolean empty)
                    {
                        if (empty || (item == null))
                        {
                            Platform.runLater(
                                () -> {
                                    setText(null);
                                });
                        }
                        else
                        {
                            Platform.runLater(
                                () -> {
                                    setText(item.getAppointmentId() + ", " + item.getTitle());
                                });
                        }

                        super.updateItem((Appointment) item, empty);
                    }
                };

                return cell;
            });

        // attach a listener for selection events
        appointmentListView.getSelectionModel()
                           .selectedItemProperty()
                           .addListener(new AppointmentListChangeListener());
    }

    @FXML
    private void configureButtons()
    {

        // Here are examples of valid uses of lambda expressions
        // This is ideal compared to associating action events to controls in the FXML file
        // We can rename any of the functions called inside of these lambdas without needing to edit FXML
        nextMonth.addEventHandler(MouseEvent.MOUSE_CLICKED,
            e -> {
                calendar.getDatetime()
                        .nextMonth();
                Platform.runLater(
                    () -> {
                        populateCalendarTable(calendar);
                        appointmentListView.setItems(null);
                        updatePosition();
                    });
            });
        prevMonth.addEventHandler(MouseEvent.MOUSE_CLICKED,
            e -> {
                calendar.getDatetime()
                        .prevMonth();
                Platform.runLater(
                    () -> {
                        populateCalendarTable(calendar);
                        appointmentListView.setItems(null);
                        updatePosition();
                    });
            });
        nextYear.addEventHandler(MouseEvent.MOUSE_CLICKED,
            e -> {
                calendar.getDatetime()
                        .nextYear();
                Platform.runLater(
                    () -> {
                        populateCalendarTable(calendar);
                        updatePosition();
                    });
            });
        prevYear.addEventHandler(MouseEvent.MOUSE_CLICKED,
            e -> {
                calendar.getDatetime()
                        .prevYear();
                Platform.runLater(
                    () -> {
                        populateCalendarTable(calendar);
                        appointmentListView.setItems(null);
                        updatePosition();
                    });
            });
        editAppointment.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                DataHolder.getInstance()
                          .setAppointment(selectedAppointment);
                editAppointment();
            });
        newAppointment.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                DataHolder.getInstance()
                          .setAppointment(null);
                newAppointment();
            });
        deleteAppointment.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                deleteAppointment();
            });
    }

    @FXML
    private void configureCalendarTable()
    {
        calendarTable.setEditable(false);
        calendarTable.setFocusTraversable(true);
        sun.setCellValueFactory(new PropertyValueFactory<>("sunday"));
        mon.setCellValueFactory(new PropertyValueFactory<>("monday"));
        tue.setCellValueFactory(new PropertyValueFactory<>("tuesday"));
        wed.setCellValueFactory(new PropertyValueFactory<>("wednesday"));
        thu.setCellValueFactory(new PropertyValueFactory<>("thursday"));
        fri.setCellValueFactory(new PropertyValueFactory<>("friday"));
        sat.setCellValueFactory(new PropertyValueFactory<>("saturday"));
        sun.setSortable(false);
        mon.setSortable(false);
        tue.setSortable(false);
        wed.setSortable(false);
        thu.setSortable(false);
        fri.setSortable(false);
        sat.setSortable(false);
        sun.setResizable(false);
        mon.setResizable(false);
        tue.setResizable(false);
        wed.setResizable(false);
        thu.setResizable(false);
        fri.setResizable(false);
        sat.setResizable(false);

        // column text styling properties
        sun.setStyle("-fx-alignment: TOP-CENTER; -fx-padding: 0 0 0 2px; -fx-font-size: 1.25em;");
        mon.setStyle("-fx-alignment: TOP-CENTER; -fx-padding: 0 0 0 2px; -fx-font-size: 1.25em;");
        tue.setStyle("-fx-alignment: TOP-CENTER; -fx-padding: 0 0 0 2px; -fx-font-size: 1.25em;");
        wed.setStyle("-fx-alignment: TOP-CENTER; -fx-padding: 0 0 0 2px; -fx-font-size: 1.25em;");
        thu.setStyle("-fx-alignment: TOP-CENTER; -fx-padding: 0 0 0 2px; -fx-font-size: 1.25em;");
        fri.setStyle("-fx-alignment: TOP-CENTER; -fx-padding: 0 0 0 2px; -fx-font-size: 1.25em;");
        sat.setStyle("-fx-alignment: TOP-CENTER; -fx-padding: 0 0 0 2px; -fx-font-size: 1.25em;");
    }

    @FXML
    private void deleteAppointment()
    {
        if (Dialog.confirmationDialog("Press 'OK' to delete the appointment.", "Are you sure?", "Confirm Deletion"))
        {
            try {
                if (!appointmentDAO.delete(selectedAppointment.getAppointmentId()))
                {
                    throw new SQLException();
                }
            } catch (SQLException ex) {
                Alert error = Dialog.errorDialog("Appointment failed to delete:\n" + ex.getMessage(),
                                                 "Object could not be deleted",
                                                 ex.getClass()
                                                   .getSimpleName() + "::" + ex.getSQLState());

                error.show();
            } finally {
                populateListView();
                appointmentListView.getSelectionModel()
                                   .selectFirst();
            }
        }
    }

    @FXML
    private void editAppointment()
    {
        if (selectedAppointment == null)
        {
            return;
        }
        else
        {
            Stage appointmentStage = Window.showNewWindow("Appointment Scheduler — Edit Appointment",
                                                          Window.WindowType.APPOINTMENT,
                                                          Modality.APPLICATION_MODAL,
                                                          false);

            appointmentStage.setOnCloseRequest(
                (e) -> {
                    selectedDate = new Time().setFormatPattern(Time.FormatPattern.LOCAL_DATE)
                                             .toString();

                    refreshData();
                });
            appointmentStage.setOnHidden(
                (e) -> {
                    if (DataHolder.getInstance()
                                  .getAppointment() == null)
                    {
                        selectedDate = new Time().setFormatPattern(Time.FormatPattern.LOCAL_DATE)
                                                 .toString();
                    }
                    else
                    {
                        selectedDate = new Time(DataHolder.getInstance().getAppointment().getStart()).setFormatPattern(
                            Time.FormatPattern.LOCAL_DATE)
                                                                                                     .toString();
                    }

                    refreshData();
                });
            Platform.runLater(
                () -> {
                    appointmentStage.showAndWait();
                });
        }
    }

    @FXML
    private void newAppointment()
    {
        Stage appointmentStage = Window.showNewWindow("Appointment Scheduler — New Appointment",
                                                      Window.WindowType.APPOINTMENT,
                                                      Modality.APPLICATION_MODAL,
                                                      false);

        appointmentStage.setOnCloseRequest(
            (e) -> {
                selectedDate = new Time().setFormatPattern(Time.FormatPattern.LOCAL_DATE)
                                         .toString();

                refreshData();
            });
        appointmentStage.setOnHidden(
            (e) -> {
                if (DataHolder.getInstance()
                              .getAppointment() == null)
                {
                    selectedDate = new Time().setFormatPattern(Time.FormatPattern.LOCAL_DATE)
                                             .toString();
                }
                else
                {
                    selectedDate = new Time(DataHolder.getInstance().getAppointment().getStart()).setFormatPattern(
                        Time.FormatPattern.LOCAL_DATE)
                                                                                                 .toString();
                }

                refreshData();
            });
        Platform.runLater(
            () -> {
                appointmentStage.showAndWait();
            });
    }

    @FXML
    private void populateCalendarTable(Gregorian calendar)
    {
        HashMap<Integer, Integer> days          = new HashMap<>();
        Integer[][]               rows          = new Integer [6][7];
        int                       daysInCurrent = calendar.getDaysInMonth(calendar.getDatetime()
                                                                                  .getYear(),
                                                                          calendar.getDatetime()
                                                                                  .getMonth());

        // set the day to the 1st, so we can fill our hashmap
        calendar.getDatetime()
                .setDay(1);

        // fill our hashmap with days of month as keys, and their day of week values (0-6)
        for (int i = 1 ; i <= daysInCurrent ; i++) {
            days.put(i, calendar.dayOfWeek());
            calendar.setDatetime(calendar.getDatetime()
                                         .setDay(i + 1));
        }

        // for keeping track of our current row
        int j = 0;

        // populate our array of table rows
        for (int i = 1 ; i <= daysInCurrent ; i++) {
            rows [j][days.get(i)] = i;

            if (days.get(i) == 6)
            {
                j++;
            }
        }

        // backfill days in the prior month to the beginning of the current one
        for (int i = 0 ; i < days.get(1) ; i++) {

            // to show these days we can replace with the commented line here
            // rows [0][i] = daysInPrev - days.get(1) + i + 1;
            rows [0][i] = 0;
        }

        // fill remaining values with days in the next month
        for (int i = daysInCurrent + days.get(1) + 1 ; i <= 42 ; i++) {

            /*
             * - Start at the row (j), where we left off.
             *      (j was not changed or cleared, since we used it last)
             *
             * - Figure out the appropriate column with modular arithmetic
             *      i.e. We don't have the DOW calculations for these dates in
             *           the next month. However, we do know that they will
             *           continue in order from where we left off.
             *           (7 - ((42 - i) % 7) - 1) increments through the columns
             *
             *           The math breaks down like this:
             *           ((42 - i) % 7) results in days past the beginning of
             *           the week, the reverse of what we're going for. So we
             *           rotate by subtracting it from 7 to get the days since
             *           the start of the week, then we subtract 1 so that we
             *           map to values ranging from 0 (Sun) -> 6 (Sat).
             *
             * - Assign the values using a little more modular arithmetic
             *       i.e. (i % (current + days.get(1)))
             *       i = current value of the 42 possible that our 2D array holds
             *       We have to mod i against the number of days in the current
             *       month, with the starting shift added in ...
             *       (# of days past Sunday the first occured).
             */

            // to show these days we can replace with the commented line here
            // rows [j][7 - ((42 - i) % 7) - 1] = i % (daysInCurrent + days.get(1));
            rows [j][7 - ((42 - i) % 7) - 1] = 0;

            // check if the current column accessed is a saturday
            if (((7 - ((42 - i) % 7) - 1) == 6))
            {
                /*
                 * Note: We don't have to check if incrementing j will go out of
                 * bounds. The outer loop handles that by only allowing us to
                 * assign values up to the maximum our array stores, 42, the
                 * answer to the ultimate question of life, the universe, and
                 * everything.
                 */

                // shift to the next row
                j++;
            }
        }

        month = new Month(rows);

        Platform.runLater(
            () -> {
                calendarTable.setItems(FXCollections.observableArrayList(month.getWeeks()));
            });
    }

    @FXML
    private void populateListView()
    {
        if (!selectedDate.isEmpty())
        {
            if (appointmentDAO.doesTableHaveDataForDate(selectedDate))
            {
                ArrayList<Appointment> data = appointmentDAO.readAppointmentFromDate(selectedDate);

                if ((data == null) || data.isEmpty())
                {
                    Platform.runLater(
                        () -> {
                            appointments.setAll((Appointment) null);
                            appointmentListView.setItems(null);
                            appointmentListView.refresh();
                        });
                }
                else
                {
                    Platform.runLater(
                        () -> {
                            appointments.setAll(FXCollections.observableArrayList(data));
                            appointmentListView.setItems(FXCollections.observableArrayList(appointments));
                            appointmentListView.getSelectionModel()
                                               .selectFirst();
                        });
                }
            }
            else
            {
                Platform.runLater(
                    () -> {
                        appointmentListView.getItems()
                                           .clear();
                        appointmentListView.refresh();
                    });
            }
        }
    }

    @FXML
    private void refreshData()
    {
        if (!((selectedDate.isEmpty()) || (selectedDate == null)))
        {
            calendar.setDate(selectedDate);
            populateCalendarTable(calendar);
            updatePosition();
            populateListView();

            if (!appointmentListView.getItems()
                                    .isEmpty())
            {
                if (DataHolder.getInstance()
                              .getAppointment() == null)
                {
                    Platform.runLater(
                        () -> {
                            appointmentListView.getSelectionModel()
                                               .selectFirst();
                        });
                }
                else
                {
                    Platform.runLater(
                        () -> {
                            appointmentListView.getSelectionModel()
                                               .select(appointmentListView.getItems()
                                                                          .stream()
                                                                          .filter(
                                                                          appointment -> appointment.getAppointmentId()
                                                                                                    .equals(
                                                                                                    DataHolder.getInstance()
                                                                                                              .getAppointment()
                                                                                                              .getAppointmentId()))
                                                                          .findFirst()
                                                                          .orElse(null));
                        });
                }
            }
        }
        else
        {
            Platform.runLater(
                () -> {
                    appointmentListView.getItems()
                                       .clear();
                    appointmentListView.refresh();
                });
        }
    }

    @FXML
    private void updatePosition()
    {
        Platform.runLater(
            () -> {
                String label = "";

                switch (calendar.getDatetime()
                                .getMonth()) {
                  case 1 :
                      label = "January " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 2 :
                      label = "February " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 3 :
                      label = "March " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 4 :
                      label = "April " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 5 :
                      label = "May " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 6 :
                      label = "June " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 7 :
                      label = "July " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 8 :
                      label = "August " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 9 :
                      label = "September " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 10 :
                      label = "October " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 11 :
                      label = "November " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 12 :
                      label = "December " + Integer.toString(calendar.getDatetime().getYear());

                      break;
                }

                position.setText(label);
            });
    }

    //~--- inner classes ------------------------------------------------------

    private class AppointmentListChangeListener
            implements ChangeListener<Appointment>
    {
        public AppointmentListChangeListener() {}

        //~--- methods --------------------------------------------------------

        @Override
        public void changed(ObservableValue<? extends Appointment> observable, Appointment oldVal, Appointment newVal)
        {
            if (newVal != null)
            {
                selectedAppointment = appointmentListView.getSelectionModel()
                                                         .getSelectedItem();
            }
            else
            {
                selectedAppointment = null;
            }
        }
    }
}

/*
 * @(#)CalendarTab.java 2021.05.21 at 03:02:54 EDT
 * EOF
 */
