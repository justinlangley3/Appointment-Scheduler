/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)WeeklyTab.java   2021.05.21 at 07:20:37 EDT
 * Version: 1.0.0
 */



package Controller.Tabs;

import java.net.URL;

import java.sql.SQLException;

import java.util.ArrayList;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import javafx.stage.Modality;
import javafx.stage.Stage;

import Model.Appointment;

import Model.DAO.Impl.AppointmentDAOImpl;

import Model.Dummy.Week;

import Model.Instance.DataHolder;

import Util.Controls.Dialog;
import Util.Controls.Window;

import Util.Date.Gregorian;
import Util.Date.Time;

//~--- classes ----------------------------------------------------------------

public class WeeklyTab
        implements Initializable
{
    private final AppointmentDAOImpl          appointmentDAO;
    private final Gregorian                   calendar;
    private final ObservableList<Appointment> appointments;
    private Appointment                       selectedAppointment;
    @FXML
    private Button                            prevWeek, prevYear, nextWeek, nextYear, newAppointment, editAppointment,
                                              deleteAppointment;
    @FXML
    private Label                             position;
    @FXML
    private ListView<Appointment>             appointmentListView;
    private String                            selectedDate;
    @FXML
    private TableColumn<Week, String>         sun, mon, tue, wed, thu, fri, sat;
    @FXML
    private TableView<Week>                   weekTableView;
    @FXML
    private TextArea                          summary;
    @FXML
    private TextField                         appointmentId, customerId, userId, appointmentTitle, location, url,
                                              category, contact, begin, end, created, createdBy, updated, updatedBy;

    //~--- constructors -------------------------------------------------------

    public WeeklyTab()
    {
        appointmentDAO = new AppointmentDAOImpl();
        calendar       = new Gregorian();
        selectedDate   = null;
        appointments   = FXCollections.observableArrayList();

        calendar.setStartOfWeek(Gregorian.WeekStarts.DEFAULT);
    }

    //~--- methods ------------------------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        selectedDate = new Time().setFormatPattern(Time.FormatPattern.LOCAL_DATE)
                                 .update()
                                 .toString();

        configureButtons();
        configureAppointmentListView();
        configureWeekTableView();
        populateWeekTableView(calendar);
        updatePosition();
        populateAppointmentListView();
        addWeekTableViewListener();
    }

    @FXML
    private void addWeekTableViewListener()
    {
        weekTableView.getFocusModel().focusedCellProperty().addListener((ObservableValue<? extends TablePosition> observable, TablePosition oldPos, TablePosition pos) -> {
                int    row           = pos.getRow();
                int    column        = pos.getColumn();
                String selectedValue = "";

                // check that position is not equal to -1, or we'll get a nullpointer exception
                if ((pos.getRow() != -1) && (pos.getColumn() != -1))
                {
                    // capture the user's selection
                    selectedValue = weekTableView.getItems()
                                                 .get(row)
                                                 .get(column);

                    // check if the selection contains data
                    if ((selectedValue != null) && (!selectedValue.isEmpty()))
                    {
                        // get the date string with the calendar object
                        selectedDate = calendar.getDatetime()
                                               .setFormatPattern(Time.FormatPattern.LOCAL_DATE)
                                               .setDay(Integer.valueOf(selectedValue))
                                               .update()
                                               .toString();

                        refreshData();
                    }
                    else
                    {
                        selectedDate = "";
                    }
                }
            });
    }

    @FXML
    private void clearAppointmentFields()
    {
        Platform.runLater(
            () -> {
                this.appointmentId.setText("");
                this.customerId.setText("");
                this.userId.setText("");
                this.appointmentTitle.setText("");
                this.summary.setText("");
                this.location.setText("");
                this.category.setText("");
                this.contact.setText("");
                this.begin.setText("");
                this.end.setText("");
                this.created.setText("");
                this.createdBy.setText("");
                this.updated.setText("");
                this.updatedBy.setText("");
            });
    }

    @FXML
    private void configureAppointmentListView()
    {

        // set a value factory that handles displaying apopointments
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
                           .addListener(new WeeklyTab.AppointmentListChangeListener());
    }

    @FXML
    private void configureButtons()
    {
        prevYear.addEventHandler(MouseEvent.MOUSE_CLICKED,
            e -> {
                calendar.getDatetime()
                        .prevYear();
                appointmentListView.setItems(null);
                populateWeekTableView(calendar.update());
                updatePosition();
            });
        prevWeek.addEventHandler(MouseEvent.MOUSE_CLICKED,
            e -> {
                if ((calendar.getDatetime().getWeek() == 1)
                    && calendar.isLongYear(calendar.getDatetime().getYear() - 1))
                {
                    calendar.getDatetime()
                            .setYear(calendar.getDatetime()
                                             .getYear() - 1)
                            .setMonth(12)
                            .setDay(31)
                            .setWeek(53)
                            .update();
                }
                else
                {
                    calendar.getDatetime()
                            .prevWeek();
                }

                appointmentListView.setItems(null);
                populateWeekTableView(calendar.update());
                updatePosition();
            });
        nextYear.addEventHandler(MouseEvent.MOUSE_CLICKED,
            e -> {
                calendar.getDatetime()
                        .nextYear();
                appointmentListView.setItems(null);
                populateWeekTableView(calendar.update());
                updatePosition();
            });
        nextWeek.addEventHandler(MouseEvent.MOUSE_CLICKED,
            e -> {
                if (calendar.getDatetime()
                            .getWeek() == 53)
                {
                    calendar.getDatetime()
                            .setYear(calendar.getDatetime()
                                             .getYear() + 1)
                            .setMonth(1)
                            .setDay(7)
                            .setWeek(1)
                            .update();
                }
                else
                {
                    calendar.getDatetime()
                            .nextWeek();
                }

                appointmentListView.setItems(null);
                populateWeekTableView(calendar.update());
                updatePosition();
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
    private void configureWeekTableView()
    {
        weekTableView.setEditable(false);
        weekTableView.setFocusTraversable(true);
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
        Platform.runLater(
            () -> {
                if (Dialog.confirmationDialog("Press 'OK' to delete selected appointment.",
                                              "Are you sure?",
                                              "Confirm Deletion"))
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
                        populateAppointmentListView();
                        appointmentListView.getSelectionModel()
                                           .selectFirst();
                    }
                }
            });
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

                return;
            });
        appointmentStage.setOnHidden(
            (e) -> {
                if (DataHolder.getInstance()
                              .getAppointment() == null)
                {
                    selectedDate = new Time().setFormatPattern(Time.FormatPattern.LOCAL_DATE)
                                             .toString();

                    refreshData();

                    return;
                }

                selectedDate = new Time(DataHolder.getInstance().getAppointment().getStart()).setFormatPattern(
                    Time.FormatPattern.LOCAL_DATE)
                                                                                             .toString();

                refreshData();
            });
        Platform.runLater(
            () -> {
                appointmentStage.showAndWait();
            });
    }

    @FXML
    private void populateAppointmentFields(Appointment selected)
    {
        Platform.runLater(
            () -> {
                this.appointmentId.setText(selected.getAppointmentId()
                                                   .toString());
                this.customerId.setText(selected.getCustomerId()
                                                .toString());
                this.userId.setText(selected.getUserId()
                                            .toString());
                this.appointmentTitle.setText(selected.getTitle());
                this.summary.setText(selected.getSummary());
                this.location.setText(selected.getLocation());
                this.url.setText(selected.getUrl());
                this.category.setText(selected.getCategory());
                this.contact.setText(selected.getContact());
                this.begin.setText(selected.getStart());
                this.end.setText(selected.getEnd());
                this.created.setText(selected.getCreated());
                this.createdBy.setText(selected.getCreatedBy());
                this.updated.setText(selected.getUpdated());
                this.updatedBy.setText(selected.getUpdatedBy());
            });
    }

    @FXML
    private void populateAppointmentListView()
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
    private void populateWeekTableView(Gregorian cal)
    {
        String[] data = null;

        // dirty fix to make the week set appropriately due to the fact we use an aligned week of year
        if ((7 - calendar.dayOfWeek()) < calendar.getStartOfYear())
        {
            data = cal.getWeek(cal.getDatetime()
                                  .getYear(), cal.getDatetime()
                                                 .getWeek() - 1);
        }
        else
        {
            data = cal.getWeek(cal.getDatetime()
                                  .getYear(), cal.getDatetime()
                                                 .getWeek());
        }

        Integer[][] rows = new Integer [1][7];

        for (int i = 0 ; i < 1 ; i++) {
            for (int j = 0 ; j < data.length ; j++) {
                Time parsed = new Time(data [j]);

                rows [i][j] = parsed.getDay();
            }

            java.lang.System.gc();
        }

        Week week = new Week(rows [0]);

        calendar.setDate(data [4]);
        Platform.runLater(
            () -> {
                weekTableView.getItems()
                             .setAll(FXCollections.observableArrayList(week));
            });
    }

    @FXML
    private void refreshData()
    {
        if (!((selectedDate.isEmpty()) || (selectedDate == null)))
        {
            calendar.setDate(selectedDate);
            populateWeekTableView(calendar);
            updatePosition();
            populateAppointmentListView();

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
                String label = "Week " + calendar.getDatetime().getWeek() + ", ";

                switch (calendar.getDatetime()
                                .getMonth()) {
                  case 1 :
                      label = label + "January " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 2 :
                      label = label + "February " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 3 :
                      label = label + "March " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 4 :
                      label = label + "April " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 5 :
                      label = label + "May " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 6 :
                      label = label + "June " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 7 :
                      label = label + "July " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 8 :
                      label = label + "August " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 9 :
                      label = label + "September " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 10 :
                      label = label + "October " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 11 :
                      label = label + "November " + Integer.toString(calendar.getDatetime().getYear());

                      break;

                  case 12 :
                      label = label + "December " + Integer.toString(calendar.getDatetime().getYear());

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
                clearAppointmentFields();

                selectedAppointment = appointmentListView.getSelectionModel()
                                                         .getSelectedItem();

                populateAppointmentFields(selectedAppointment);
            }
            else
            {
                clearAppointmentFields();

                selectedAppointment = null;
            }
        }
    }
}

/*
 * @(#)WeeklyTab.java 2021.05.21 at 07:20:37 EDT
 * EOF
 */
