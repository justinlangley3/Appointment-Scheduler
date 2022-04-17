/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)CustomersTab.java   2021.05.21 at 07:19:39 EDT
 * Version: 1.0.0
 */



package Controller.Tabs;

import java.net.URL;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import java.util.Collection;
import java.util.ResourceBundle;

import javafx.application.Platform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javafx.util.StringConverter;

import Model.Address;
import Model.Customer;

import Model.DAO.Impl.AddressDAOImpl;
import Model.DAO.Impl.CustomerDAOImpl;

import Model.Instance.DataHolder;

import Util.Controls.Dialog;
import Util.Controls.Validate;

//~--- classes ----------------------------------------------------------------

public class CustomersTab
        implements Initializable
{
    private final AddressDAOImpl           addressDAO;
    private final CustomerDAOImpl          customerDAO;
    private final ObservableList<Address>  addresses;
    private final ObservableList<Customer> customers;
    private Address                        addressSelection;
    @FXML
    private Button                         save, cancel, delete;
    @FXML
    private ComboBox<Address>              customerAddress;
    private Customer                       customerSelection;
    @FXML
    private Label                          validation;
    @FXML
    private RadioButton                    newRadio, editRadio;
    @FXML
    private Slider                         customerActive;
    @FXML
    private TableColumn<Address, Integer>  customerIdColumn, customerAddressIdColumn;
    @FXML
    private TableColumn<Address, String>   customerNameColumn;
    @FXML
    private TableView<Customer>            customersTable;
    @FXML
    private TextField                      customerId, customerAddressId, customerName, customerCreated,
                                           customerCreatedBy, customerUpdated, customerUpdatedBy;
    private byte active = 0b0;

    //~--- constant enums -----------------------------------------------------

    private enum FormState { ADDRESS_NOT_SELECTED, NAME_IS_NULL, NAME_EXCEEDS_LENGTH, OK }

    private enum ViewState { NEW, EDIT, ERROR }

    //~--- constructors -------------------------------------------------------

    public CustomersTab()
    {
        this.customerDAO = new CustomerDAOImpl();
        this.addressDAO  = new AddressDAOImpl();

        if (customerDAO.doesTableHaveData())
        {
            this.customers = FXCollections.observableArrayList(customerDAO.readAll());
        }
        else
        {
            this.customers = FXCollections.observableArrayList();
        }

        if (addressDAO.doesTableHaveData())
        {
            this.addresses = FXCollections.observableArrayList(addressDAO.readAll());
        }
        else
        {
            this.addresses = FXCollections.observableArrayList();
        }
    }

    //~--- methods ------------------------------------------------------------

    public static Address findAddressById(Collection<Address> listAddresses, Integer addressId)
    {

        // mutate the address list into a stream
        // then given a known addressId, filter it to find the matching address, or null if no match is found
        return listAddresses.stream()
                            .filter(address -> addressId.equals(address.getAddressId()))
                            .findFirst()
                            .orElse(null);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {

        // Slider change listening
        customerActive.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldVal, Number newVal) -> {
                if (newVal.byteValue() > 0)
                {
                    this.active = 0b1;
                }
                else
                {
                    this.active = 0b0;
                }
            });
        configureButtons();
        configureRadios();
        configureTableView();
        configureComboBox();
        refreshData();
        setTableView();
        setComboBox();
        setFields();
    }

    @FXML
    private void configureButtons()
    {
        save.setOnAction(
            (e) -> {
                switch (this.getViewState()) {
                  case EDIT :
                      if (isFormValid() == FormState.OK)
                      {
                          this.customerSelection.setAddressId(Integer.valueOf(this.customerAddressId.getText()));
                          this.customerSelection.setCustomerName(this.customerName.getText());
                          this.customerSelection.setActive(this.active);
                          customerDAO.update(customerSelection);

                          int idx = customers.indexOf(customerSelection);

                          if (idx > -1)
                          {
                              customers.set(idx, customerSelection);
                          }
                          else
                          {
                              customers.add(customerSelection);
                          }

                          refreshTableView();
                      }

                      break;

                  case NEW :
                      if (isFormValid() == FormState.OK)
                      {
                          Customer newCustomer = new Customer(this.customerName.getText(),
                                                              Integer.valueOf(this.customerAddressId.getText()),
                                                              this.customerCreatedBy.getText(),
                                                              this.customerUpdatedBy.getText());

                          customerDAO.create(newCustomer);
                          refreshData();
                          refreshTableView();
                      }

                      break;
                }
            });
        cancel.setOnAction(
            (e) -> {
                switch (this.getViewState()) {
                  case EDIT :
                      if (customerSelection != null)
                      {
                          customerAddress.getSelectionModel()
                                         .select(findAddressById(this.addresses, customerSelection.getAddressId()));
                          Platform.runLater(
                              () -> {
                                  customerName.setText(this.customerSelection.getCustomerName());
                              });
                      }
                      else
                      {
                          Platform.runLater(
                              () -> {
                                  customerName.setText(null);
                              });
                          customerAddress.getSelectionModel()
                                         .selectFirst();
                      }

                      break;

                  case NEW :
                      customerAddress.getSelectionModel()
                                     .selectFirst();
                      Platform.runLater(
                          () -> {
                              customerName.setText(null);
                          });

                      break;
                }
            });
        delete.setOnAction(
            (e) -> {
                if (!this.customersTable.getSelectionModel()
                                        .isEmpty())
                {
                    if (Dialog.confirmationDialog("Press 'OK' to delete the selected customer",
                                                  "Are you sure?",
                                                  "Confirm Deletion"))
                    {
                        try {
                            if (!customerDAO.delete(this.customerSelection.getCustomerId()))
                            {
                                throw new SQLIntegrityConstraintViolationException();
                            }
                        } catch (SQLException ex) {
                            Alert error = Dialog.errorDialog("Customer is still associated to other database entries.",
                                                             "Object could not be deleted:",
                                                             "SQLIntegrityConstraintViolation");

                            error.show();
                        } finally {
                            refreshData();
                            refreshTableView();
                        }
                    }
                }
            });
    }

    @FXML
    private void configureComboBox()
    {
        this.customerAddress.setFocusTraversable(true);
        this.customerAddress.setConverter(new StringConverter<Address>()
                                          {
                                              @Override
                                              public String toString(Address obj)
                                              {
                                                  return obj.getAddressId()
                                                            .toString() + ", " + obj.getAddress1() + ", "
                                                                        + obj.getZip();
                                              }
                                              @Override
                                              public Address fromString(String string)
                                              {
                                                  return null;
                                              }
                                          });

        // Set cell factories
        this.customerAddress.setCellFactory(
            (ListView<Address> l) -> {
                ListCell<Address> cell = new ListCell<Address>()
                {
                    @Override
                    protected void updateItem(Address item, boolean empty)
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

                        super.updateItem((Address) item, empty);
                    }
                };

                return cell;
            });

        // set enter key action for comboboxes when highlighted
        // this is for better program accessibility
        this.customerAddress.setOnKeyPressed(
            (KeyEvent key) -> {
                if (key.getCode().equals(KeyCode.ENTER)
                    || (key.getCharacter().getBytes() [0] == '\n')
                    || (key.getCharacter().getBytes() [0] == '\r'))
                {
                    Platform.runLater(
                        () -> {
                            customerAddress.show();
                        });
                }
            });

        // set event handlers
        this.customerAddress.setOnAction(
            (ActionEvent event) -> {
                addressSelection = customerAddress.getSelectionModel()
                                                  .getSelectedItem();
            });
        this.customerAddress.getSelectionModel()
                            .selectedItemProperty()
                            .addListener(new AddressComboBoxListener());
    }

    private void configureRadios()
    {
        newRadio.setOnAction(
            (e) -> {
                setFields();
            });
        editRadio.setOnAction(
            (e) -> {
                if (customerSelection == null) {}
                else
                {
                    customersTable.getSelectionModel()
                                  .select(customersTable.getItems()
                                                        .stream()
                                                        .filter(customer -> customer.getCustomerId()
                                                                                    .equals(
                                                                                    customerSelection.getCustomerId()))
                                                        .findFirst()
                                                        .orElse(null));
                    setFields();
                }
            });
    }

    private void configureTableView()
    {
        customersTable.setEditable(false);
        customersTable.setFocusTraversable(true);
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerAddressIdColumn.setCellValueFactory(new PropertyValueFactory<>("addressId"));
        customersTable.setOnKeyPressed(
            (KeyEvent key) -> {
                if (key.getCode().equals(KeyCode.ENTER)
                    || (key.getCharacter().getBytes() [0] == '\n')
                    || (key.getCharacter().getBytes() [0] == '\r'))
                {
                    this.customerAddress.requestFocus();
                    this.customerAddress.show();
                }
            });
        customersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null)
                {
                    customerSelection = newVal;

                    setFields();
                }
            });
    }

    @FXML
    private void refreshData()
    {
        if (customerDAO.doesTableHaveData())
        {
            Platform.runLater(
                () -> {
                    this.customers.setAll(FXCollections.observableArrayList(customerDAO.readAll()));
                });
        }
        else
        {
            Platform.runLater(
                () -> {
                    this.customers.setAll(FXCollections.observableArrayList((Customer) null));
                });
        }

        if (addressDAO.doesTableHaveData())
        {
            Platform.runLater(
                () -> {
                    this.addresses.setAll(FXCollections.observableArrayList(addressDAO.readAll()));
                });
        }
        else
        {
            Platform.runLater(
                () -> {
                    this.addresses.setAll(FXCollections.observableArrayList((Address) null));
                });
        }
    }

    @FXML
    private void refreshTableView()
    {
        Platform.runLater(
            () -> {
                setTableView();
                this.customersTable.refresh();
            });
    }

    //~--- get methods --------------------------------------------------------

    private ViewState getViewState()
    {
        if (newRadio.isSelected())
        {
            return ViewState.NEW;
        }
        else if (editRadio.isSelected())
        {
            return ViewState.EDIT;
        }
        else
        {
            return ViewState.ERROR;
        }
    }

    @FXML
    private FormState isFormValid()
    {
        if (this.customerAddress.getSelectionModel()
                                .getSelectedItem() == null)
        {
            Platform.runLater(
                () -> {
                    Validate.CustomersTab.addressNotSelected(this.customerAddress, this.validation);
                });

            return FormState.ADDRESS_NOT_SELECTED;
        }

        if (this.customerName.getText()
                             .isEmpty())
        {
            Platform.runLater(
                () -> {
                    Validate.CustomersTab.emptyField(customerName, validation);
                });

            return FormState.NAME_IS_NULL;
        }

        if (this.customerName.getText()
                             .length() > 70)
        {
            Platform.runLater(
                () -> {
                    Validate.CustomersTab.lengthExceeds(customerName, validation);
                });

            return FormState.NAME_EXCEEDS_LENGTH;
        }
        else
        {
            Platform.runLater(
                () -> {
                    Validate.CustomersTab.success(customerName, customerAddress, validation);
                });
        }

        return FormState.OK;
    }

    //~--- set methods --------------------------------------------------------

    @FXML
    private void setComboBox()
    {
        switch (this.getViewState()) {
          case EDIT :
              Platform.runLater(
                  () -> {
                      this.customerAddress.setItems(this.addresses);
                      this.customerAddress.getSelectionModel()
                                          .select(findAddressById(this.addresses,
                                                                  this.customerSelection.getAddressId()));
                  });

              break;

          case NEW :
              Platform.runLater(
                  () -> {
                      this.customerAddress.setItems(this.addresses);
                      this.customerAddress.getSelectionModel()
                                          .selectFirst();
                  });

              break;
        }
    }

    @FXML
    private void setFields()
    {
        switch (this.getViewState()) {
          case EDIT :
              Platform.runLater(
                  () -> {
                      this.customerActive.setDisable(false);

                      if (customerSelection != null)
                      {
                          this.customerId.setText(customerSelection.getCustomerId()
                                                                   .toString());
                          this.customerAddress.getSelectionModel()
                                              .select(findAddressById(addresses, customerSelection.getAddressId()));
                          this.customerAddressId.setText(customerSelection.getAddressId()
                                                                          .toString());
                          this.customerName.setText(customerSelection.getCustomerName());
                          this.customerActive.setValue(customerSelection.getActive()
                                                                        .doubleValue());
                          this.customerCreated.setText(customerSelection.getCreated());
                          this.customerCreatedBy.setText(customerSelection.getCreatedBy());
                          this.customerUpdated.setText(customerSelection.getUpdated());
                          this.customerUpdatedBy.setText(customerSelection.getUpdatedBy());
                      }
                      else
                      {
                          // dialog, informing user to select a customer from the list
                      }
                  });

              break;

          case NEW :
              Platform.runLater(
                  () -> {
                      this.customerId.setText(null);
                      this.customerAddress.getSelectionModel()
                                          .selectFirst();
                      this.customerName.setText(null);
                      this.customerActive.setValue(1.0);
                      this.customerCreated.setText(null);
                      this.customerCreatedBy.setText(DataHolder.getInstance()
                                                               .getUser()
                                                               .getUsername());
                      this.customerUpdated.setText(null);
                      this.customerUpdatedBy.setText(DataHolder.getInstance()
                                                               .getUser()
                                                               .getUsername());
                      this.customerActive.setDisable(true);
                  });

              break;
        }
    }

    @FXML
    private void setTableView()
    {
        Platform.runLater(
            () -> {
                if (customers != null)
                {
                    customersTable.setItems(FXCollections.observableArrayList(customers));
                }
            });
    }

    //~--- inner classes ------------------------------------------------------

    private class AddressComboBoxListener
            implements ChangeListener<Address>
    {
        public AddressComboBoxListener() {}

        //~--- methods --------------------------------------------------------

        @Override
        public void changed(ObservableValue<? extends Address> observable, Address oldValue, Address newValue)
        {
            Platform.runLater(
                () -> {
                    Validate.CustomersTab.clearErrors(customerName, customerAddress, validation);
                    validation.setText(null);
                });

            if (newValue == null)
            {
                Platform.runLater(
                    () -> {
                        customerAddressId.setText(null);
                    });

                return;
            }
            else
            {
                addressSelection = newValue;

                Platform.runLater(
                    () -> {
                        customerAddressId.setText(newValue.getAddressId()
                                                          .toString());
                    });
            }
        }
    }
}

/*
 * @(#)CustomersTab.java 2021.05.21 at 07:19:39 EDT
 * EOF
 */
