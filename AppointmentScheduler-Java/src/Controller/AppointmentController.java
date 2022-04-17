/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)AppointmentController.java   2021.05.21 at 02:37:51 EDT
 * Version: 1.0.0
 */



package Controller;

import java.net.URL;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javafx.application.Platform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javafx.util.StringConverter;

import Model.Appointment;
import Model.Customer;

import Model.DAO.Impl.AppointmentDAOImpl;
import Model.DAO.Impl.CustomerDAOImpl;

import Model.Instance.DataHolder;

import Util.Controls.Validate;
import Util.Controls.Window;

import Util.Date.Time;

//~--- classes ----------------------------------------------------------------

public class AppointmentController
        implements Initializable
{
    private final AppointmentDAOImpl       appointmentDAO;
    private final CustomerDAOImpl          customerDAO;
    private final ObservableList<Customer> customers;
    private Appointment                    appointmentSelection;
    @FXML
    private Button                         save, cancel;
    @FXML
    private ChoiceBox<String>              category;
    @FXML
    private ComboBox<Customer>             customerComboBox;
    private Customer                       customerSelection;
    @FXML
    private DatePicker                     startDate;
    @FXML
    private Label                          validation, sceneTitle;
    @FXML
    private TextArea                       summary;
    @FXML
    private TextField                      appointmentTitle, url, location, created, createdBy, updated, updatedBy,
                                           appointmentId, userId, startTime, endTime;
    private ViewState viewState;

    //~--- constant enums -----------------------------------------------------

    private enum ViewState { NEW, EDIT }

    //~--- constructors -------------------------------------------------------

    public AppointmentController()
    {
        appointmentDAO = new AppointmentDAOImpl();
        customerDAO    = new CustomerDAOImpl();

        if (customerDAO.doesTableHaveData())
        {
            customers = FXCollections.observableArrayList(customerDAO.readAll());
        }
        else
        {
            customers = FXCollections.observableArrayList();
        }
    }

    //~--- methods ------------------------------------------------------------

    @FXML
    public void close(MouseEvent e)
    {
        Window.closeWindow(e);
    }

    @FXML
    public void configButtons()
    {
        save.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                if (save())
                {
                    Window.closeWindow(e);
                }
            });
        cancel.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                DataHolder.getInstance()
                          .setAppointment(null);
                Window.closeWindow(e);
            });
    }

    @FXML
    public void configDatePickers()
    {
        startDate.setConverter(new StringConverter<LocalDate>()
                               {
                                   String            pattern       = "yyyy-MM-dd";
                                   DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
                                   {
                                       Platform.runLater(
                                           () -> {
                                               startDate.setPromptText(pattern.toLowerCase());
                                           });
                                   }
                                   @Override
                                   public String toString(LocalDate date)
                                   {
                                       if (date != null)
                                       {
                                           return dateFormatter.format(date);
                                       }
                                       else
                                       {
                                           return "";
                                       }
                                   }
                                   @Override
                                   public LocalDate fromString(String string)
                                   {
                                       if ((string != null) &&!string.isEmpty())
                                       {
                                           return LocalDate.parse(string, dateFormatter);
                                       }
                                       else
                                       {
                                           return null;
                                       }
                                   }
                               });
    }

    public static Customer findCustomerById(Collection<Customer> listCustomers, Integer customerId)
    {
        return listCustomers.stream()
                            .filter(customer -> customerId.equals(customer.getCustomerId()))
                            .findFirst()
                            .orElse(null);
    }

    public void initViewState()
    {
        if (DataHolder.getInstance()
                      .getAppointment() == null)
        {
            this.viewState = ViewState.NEW;
        }
        else
        {
            this.viewState = ViewState.EDIT;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        Platform.runLater(
            () -> {
                initViewState();
                configButtons();
                configDatePickers();
                configCustomerComboBox();
                setFields();
                setCustomerComboBox();
            });
    }

    public Boolean save()
    {
        if (Validate.Appointment.fieldsHaveData(appointmentTitle, summary, url, location, validation)
                && Validate.Appointment.fieldLengthsAreValid(appointmentTitle, summary, url, location, validation)
                && Validate.Appointment.typeSelectionIsValid(category, validation)
                && Validate.Appointment.dateSelectionIsValid(startDate, validation)
                && Validate.Appointment.timeSelectionIsValid(startDate, startTime, endTime, validation)
                && Validate.Appointment.customerSelectionIsValid(customerComboBox, validation))
        {
            Validate.Appointment.valid(validation);

            switch (getViewState()) {
              case NEW :

                  // trickeries to get our datetimes to parse, and then properly convert to UTC for database storage
                  String newStart = startDate.getValue()
                                             .toString() + " " + startTime.getText() + ":00 "
                                                         + ZonedDateTime.now().getOffset();

                  /*
                   * The GUI controls for selecting an end date were disabled
                   * Here are several reasons why:
                   *   - an appointment naturally should not be allowed to span multiple days
                   *   - form validation is easier to implement
                   *   - special checks for DB access are not required for various things such as:
                   *      an appointment from a prior day possibly overlapping into a selected day
                   *      when alerts are implemented, we can conveniently check if there is one
                   *       within the next 15 min without worrying about the prior mentioned case
                   *   - general convenience when working with DB queries and appointments
                   */

                  // setting the end datetime to be the same as start date, but with the endTime chosen from the spinner
                  String newEnd = startDate.getValue()
                                           .toString() + " " + endTime.getText() + ":00 "
                                                       + ZonedDateTime.now().getOffset();

                  // create the object to insert
                  Appointment temp =
                      new Appointment(customerComboBox.getSelectionModel().getSelectedItem().getCustomerId(),
                                      DataHolder.getInstance().getUser().getUserId(),
                                      appointmentTitle.getText(),
                                      summary.getText().replaceAll("\r\n\t", " "),
                                      location.getText(),
                                      DataHolder.getInstance().getUser().getUsername(),
                                      url.getText(),
                                      category.getSelectionModel().getSelectedItem(),
                                      newStart,
                                      newEnd,
                                      DataHolder.getInstance().getUser().getUsername(),
                                      DataHolder.getInstance().getUser().getUsername());

                  appointmentDAO.create(temp);
                  DataHolder.getInstance()
                            .setAppointment(temp);

                  return true;

              case EDIT :

                  // dirty method of getting datetimes from a time to parse, and then properly convert to UTC for database storage
                  String editStart = startDate.getValue()
                                              .toString() + " " + startTime.getText() + ":00 "
                                                          + ZonedDateTime.now().getOffset();

                  // setting the end datetime to be the same as start date, but with the endTime chosen from the spinner
                  String editEnd = startDate.getValue()
                                            .toString() + " " + endTime.getText() + ":00 "
                                                        + ZonedDateTime.now().getOffset();;

                  appointmentSelection.setCustomerId(this.customerComboBox.getSelectionModel()
                                                                          .getSelectedItem()
                                                                          .getCustomerId());
                  appointmentSelection.setTitle(appointmentTitle.getText());
                  appointmentSelection.setSummary(summary.getText()
                                                         .replaceAll("[\\r\\n\\t]+", " "));
                  appointmentSelection.setLocation(location.getText());
                  appointmentSelection.setUrl(url.getText());
                  appointmentSelection.setCategory(category.getSelectionModel()
                                                           .getSelectedItem());
                  appointmentSelection.setStart(editStart);
                  appointmentSelection.setEnd(editEnd);
                  appointmentSelection.setUpdatedBy(DataHolder.getInstance()
                                                              .getUser()
                                                              .getUsername());
                  appointmentDAO.update(appointmentSelection);
                  DataHolder.getInstance()
                            .setAppointment(appointmentSelection);

                  return true;
            }
        }

        return false;
    }

    //~--- get methods --------------------------------------------------------

    public ViewState getViewState()
    {
        return this.viewState;
    }

    //~--- set methods --------------------------------------------------------

    @FXML
    public void setCustomerComboBox()
    {
        switch (this.viewState) {
          case NEW :
              this.customerComboBox.setItems(customers);

              break;

          case EDIT :
              this.customerComboBox.setItems(customers);
              this.customerComboBox.getSelectionModel()
                                   .select(findCustomerById(this.customers, appointmentSelection.getCustomerId()));

              break;
        }
    }

    @FXML
    public void setFields()
    {
        ObservableList<String> items   = category.getItems();
        Map<String, Integer>   typeMap = new TreeMap<>();

        typeMap.put("Consultation", 0);
        typeMap.put("Design", 1);
        typeMap.put("Deployment", 2);
        typeMap.put("Development", 3);
        typeMap.put("Maintenance", 4);
        typeMap.put("Operations", 5);
        typeMap.put("Planning", 6);
        typeMap.put("Prototyping", 7);
        typeMap.put("Security", 8);

        switch (getViewState()) {
          case NEW :
              this.viewState = ViewState.NEW;

              setSelection(DataHolder.getInstance()
                                     .getAppointment());
              DataHolder.getInstance()
                        .setAppointment(null);
              Platform.runLater(
                  () -> {
                      items.addAll(FXCollections.observableArrayList(typeMap.keySet()));
                      category.getSelectionModel()
                              .selectFirst();
                      createdBy.setText(DataHolder.getInstance()
                                                  .getUser()
                                                  .getUsername());
                      updatedBy.setText(DataHolder.getInstance()
                                                  .getUser()
                                                  .getUsername());
                  });

              break;

          // need to add way to set customer, and a way to set type
          case EDIT :
              this.viewState = ViewState.EDIT;

              setSelection(DataHolder.getInstance()
                                     .getAppointment());
              DataHolder.getInstance()
                        .setAppointment(null);

              String sd, st, et;

              sd = new Time(appointmentSelection.getStart()).setFormatPattern(Time.FormatPattern.LOCAL_DATE)
                                                            .toString();
              st = new Time(appointmentSelection.getStart()).setFormatPattern(Time.FormatPattern.TIME_ONLY)
                                                            .toString();
              et = new Time(appointmentSelection.getEnd()).setFormatPattern(Time.FormatPattern.TIME_ONLY)
                                                          .toString();

              System.out.println(sd + ", " + st + ", " + et);
              Platform.runLater(
                  () -> {
                      items.addAll(FXCollections.observableArrayList(typeMap.keySet()));
                      sceneTitle.setText("View/Edit Appointment");
                      appointmentId.setText(appointmentSelection.getAppointmentId()
                                                                .toString());
                      customerComboBox.getSelectionModel()
                                      .select(findCustomerById(customers, appointmentSelection.getCustomerId()));
                      userId.setText(appointmentSelection.getUserId()
                                                         .toString());
                      appointmentTitle.setText(appointmentSelection.getTitle());
                      summary.setText(appointmentSelection.getSummary());
                      url.setText(appointmentSelection.getUrl());
                      location.setText(appointmentSelection.getLocation());
                      category.getSelectionModel()
                              .select(typeMap.getOrDefault(appointmentSelection.getCategory(), 0));
                      startDate.valueProperty()
                               .set(LocalDate.parse(sd));
                      startTime.setText(st);
                      endTime.setText(et);
                      created.setText(appointmentSelection.getCreated());
                      updated.setText(appointmentSelection.getUpdated());
                      createdBy.setText(appointmentSelection.getCreatedBy());
                      updatedBy.setText(appointmentSelection.getUpdatedBy());
                  });

              break;
        }
    }

    public void setSelection(Appointment appointment)
    {
        this.appointmentSelection = appointment;
    }

    public void setViewState(ViewState vs)
    {
        this.viewState = vs;
    }

    //~--- methods ------------------------------------------------------------

    @FXML
    private void configCustomerComboBox()
    {
        customerComboBox.setFocusTraversable(true);
        customerComboBox.setConverter(new StringConverter<Customer>()
                                      {
                                          @Override
                                          public String toString(Customer obj)
                                          {
                                              return obj.getCustomerId()
                                                        .toString() + ", " + obj.getCustomerName();
                                          }
                                          @Override
                                          public Customer fromString(String string)
                                          {
                                              return null;
                                          }
                                      });
        customerComboBox.setCellFactory(
            (ListView<Customer> l) -> {
                ListCell<Customer> cell = new ListCell<Customer>()
                {
                    @Override
                    protected void updateItem(Customer item, boolean empty)
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
                                    setText(item.toString());
                                });
                        }

                        super.updateItem((Customer) item, empty);
                    }
                };

                return cell;
            });
        customerComboBox.setOnKeyPressed(
            (KeyEvent key) -> {
                if (key.getCode().equals(KeyCode.ENTER)
                    || (key.getCharacter().getBytes() [0] == '\n')
                    || (key.getCharacter().getBytes() [0] == '\r'))
                {
                    Platform.runLater(
                        () -> {
                            customerComboBox.show();
                        });
                }
            });
        customerComboBox.setOnAction(
            (ActionEvent e) -> {
                customerSelection = customerComboBox.getSelectionModel()
                                                    .getSelectedItem();
            });
        customerComboBox.getSelectionModel()
                        .selectedItemProperty()
                        .addListener(new CustomerComboBoxListener());
    }

    //~--- inner classes ------------------------------------------------------

    private class CustomerComboBoxListener
            implements ChangeListener<Customer>
    {
        public CustomerComboBoxListener() {}

        //~--- methods --------------------------------------------------------

        @Override
        public void changed(ObservableValue<? extends Customer> observable, Customer oldValue, Customer newValue)
        {
            Platform.runLater(
                () -> {
                    validation.setText(null);
                });

            if (newValue == null)
            {
                Platform.runLater(() -> {}
                );

                return;
            }
            else
            {
                Platform.runLater(() -> {}
                );
            }
        }
    }
}

/*
 * @(#)AppointmentController.java 2021.05.21 at 02:37:51 EDT
 * EOF
 */
