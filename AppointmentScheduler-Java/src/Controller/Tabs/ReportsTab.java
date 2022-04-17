/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)ReportsTab.java   2021.05.19 at 06:14:11 EDT
 * Version: 1.0.0
 */



package Controller.Tabs;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.concurrent.Task;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import Model.DAO.Impl.AppointmentDAOImpl;

import Model.Instance.DataHolder;

import Util.Controls.Validate;

import Util.Date.Gregorian;
import Util.Date.Time;

import Util.File.IO.IOHandler;

//~--- classes ----------------------------------------------------------------

//import Model.DAO.Impl.ReportDAOImpl;
public class ReportsTab
        implements Initializable
{
    private final AppointmentDAOImpl appointmentDAO;
    ExecutorService                  inputService;
    FileChooser                      fileChooser;
    Stage                            fileChooserStage;
    Task<Integer>                    getYearInput = new Task<Integer>()
    {
        @Override
        public Integer call() throws Exception
        {
            while (year.focusedProperty()
                       .get()) {}

            return Integer.valueOf(year.getText());
        }
    };
    private ArrayList<String[]> report;
    @FXML
    private Button              openFileChooser;
    @FXML
    private ChoiceBox<String>   reportChoiceBox;
    @FXML
    private ComboBox<String>    weekComboBox;
    private File                saveFile;
    @FXML
    private Label               validation, yearLabel, weekLabel;
    @FXML
    private TextField           year, destination;

    //~--- constructors -------------------------------------------------------

    public ReportsTab()
    {
        appointmentDAO = new AppointmentDAOImpl();
    }

    //~--- methods ------------------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        fileChooser      = new FileChooser();
        fileChooserStage = new Stage();
        inputService     = Executors.newSingleThreadExecutor();

        configureButtons();
        configureControls();
        configureFileChooser();
    }

    @FXML
    private void configureButtons()
    {
        openFileChooser.setOnAction(
            ActionEvent -> {
                String date    = new Time().setFormatPattern(Time.FormatPattern.LOCAL_DATE)
                                           .toString();
                File   fileDir = new File(System.getProperty("user.home") + "/Documents/AppointmentScheduler/");
                String fileName;

                if (reportChoiceBox.getSelectionModel()
                                   .isEmpty())
                {
                    Validate.ReportsTab.reportSelectionNotMade(reportChoiceBox, validation);

                    return;
                }

                switch (reportChoiceBox.getValue()) {
                  case "Appointments By Week" :
                      fileName = "WeeklyReport_" + date;

                      fileChooser.setInitialDirectory(new File(fileDir.getAbsolutePath()));
                      fileChooser.setInitialFileName(fileName);

                      break;

                  case "Current Month By Type" :
                      fileName = "MonthlyReport_" + date;

                      fileChooser.setInitialDirectory(new File(fileDir.getAbsolutePath()));
                      fileChooser.setInitialFileName(fileName);

                      break;

                  case "User Schedule" :
                      fileName = DataHolder.getInstance()
                                           .getUser()
                                           .getUsername() + "_Schedule_" + date;

                      fileChooser.setInitialDirectory(new File(fileDir.getAbsolutePath()));
                      fileChooser.setInitialFileName(fileName);

                      break;
                }

                saveFile = fileChooser.showSaveDialog(fileChooserStage);

                Platform.runLater(
                    () -> {
                        destination.setText(saveFile.getAbsoluteFile()
                                                    .toString());
                    });
                generate();
            });
    }

    @FXML
    private void configureControls()
    {
        year.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == false)
                {
                    if (year.getText()
                            .isEmpty())
                    {
                        return;
                    }

                    updateWeekComboBox(Integer.valueOf(year.getText()));
                }
                else
                {
                    weekComboBox.getItems()
                                .clear();
                }
            });
        reportChoiceBox.setItems(FXCollections.observableArrayList("Appointments By Week",
                                                                   "Current Month By Type",
                                                                   "User Schedule"));
        reportChoiceBox.setOnAction(
            (ActionEvent event) -> {
                setAppointmentsByWeekFieldsVisible(false);

                if (reportChoiceBox.getSelectionModel()
                                   .getSelectedItem()
                                   .equals("Appointments By Week"))
                {
                    setAppointmentsByWeekFieldsVisible(true);
                }
            });
    }

    @FXML
    private void configureFileChooser()
    {
        fileChooser.getExtensionFilters()
                   .add(new FileChooser.ExtensionFilter("Comma Separated Value Files", "*.csv"));
    }

    @FXML
    private void generate()
    {
        String header =
            "Appointment ID,Customer ID,User ID,Title,Details,Location,Contact,Type,URL,Begin,End,Created,Created By,Updated,Updated By";

        try {
            if (reportChoiceBox.getSelectionModel()
                               .isEmpty())
            {
                Validate.ReportsTab.reportSelectionNotMade(reportChoiceBox, validation);

                return;
            }

            switch (reportChoiceBox.getValue()) {
              case "Appointments By Week" :
                  if (Validate.ReportsTab.reportSelectionNotMade(reportChoiceBox, validation)
                          || Validate.ReportsTab.weekNotSet(weekComboBox, validation)
                          || Validate.ReportsTab.yearNotSet(year, validation)
                          || Validate.ReportsTab.pathNotSet(destination, validation)
                          || Validate.ReportsTab.pathInvalid(destination, saveFile, validation)) {}

                  getAppointmentsByWeekReport(Integer.valueOf(weekComboBox.getValue()),
                                              Integer.valueOf(year.getText()));
                  IOHandler.newCSV(saveFile, header, report);
                  Validate.ReportsTab.success(validation);

                  break;

              case "Current Month By Type" :
                  if (Validate.ReportsTab.reportSelectionNotMade(reportChoiceBox, validation)
                          || Validate.ReportsTab.pathNotSet(destination, validation)
                          || Validate.ReportsTab.pathInvalid(destination, saveFile, validation)) {}

                  getMonthlyAppointmentsByTypeReport();
                  IOHandler.newCSV(saveFile, header, report);
                  Validate.ReportsTab.success(validation);

                  break;

              case "User Schedule" :
                  if (Validate.ReportsTab.reportSelectionNotMade(reportChoiceBox, validation)
                          || Validate.ReportsTab.pathNotSet(destination, validation)
                          || Validate.ReportsTab.pathInvalid(destination, saveFile, validation)) {}

                  getAppointmentsCurrentUserReport();
                  IOHandler.newCSV(saveFile, header, report);
                  Validate.ReportsTab.success(validation);

                  break;
            }
        } catch (IOException ex) {
            Validate.ReportsTab.failure(validation);
            Logger.getLogger(ReportsTab.class.getName())
                  .log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void updateWeekComboBox(int year)
    {
        Gregorian              cal   = new Gregorian();
        int                    weeks = cal.isLongYear(year)
                                       ? 54
                                       : 53;
        ObservableList<String> items = FXCollections.observableArrayList();

        for (int i = 1 ; i < weeks ; i++) {
            items.add(String.valueOf(i));
        }

        Platform.runLater(
            () -> {
                weekComboBox.setItems(FXCollections.observableArrayList(items));
                weekComboBox.getSelectionModel()
                            .selectFirst();
            });
    }

    //~--- get methods --------------------------------------------------------

    private void getAppointmentsByWeekReport(int week, int year)
    {
        report = appointmentDAO.readByWeek(week, year);

        System.out.println(Arrays.deepToString(report.toArray()));
    }

    private void getAppointmentsCurrentUserReport()
    {
        report = appointmentDAO.readMonthlyByType();

        System.out.println(Arrays.deepToString(report.toArray()));
    }

    // this report will sort all appointments by type for the current month
    private void getMonthlyAppointmentsByTypeReport()
    {
        report = appointmentDAO.readMonthlyByType();

        System.out.println(Arrays.deepToString(report.toArray()));
    }

    //~--- set methods --------------------------------------------------------

    @FXML
    private void setAppointmentsByWeekFieldsVisible(Boolean visible)
    {
        Platform.runLater(
            () -> {

            // set controls to be empty before being displayed to the user
                if (visible)
                {
                    year.clear();
                    weekComboBox.getSelectionModel()
                                .clearSelection();
                }

                year.setVisible(visible);
                weekComboBox.setVisible(visible);
                weekLabel.setVisible(visible);
                yearLabel.setVisible(visible);
            });
    }
}

/*
 * @(#)ReportsTab.java 2021.05.19 at 06:14:11 EDT
 * EOF
 */
