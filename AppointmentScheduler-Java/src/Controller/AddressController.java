/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)AddressController.java   2021.05.21 at 03:17:20 EDT
 * Version: 1.0.0
 */



package Controller;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.application.Platform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javafx.stage.Stage;

import javafx.util.StringConverter;

import Model.Address;
import Model.City;

import Model.DAO.Impl.AddressDAOImpl;
import Model.DAO.Impl.CityDAOImpl;

import Model.Instance.DataHolder;

import Util.Controls.Validate;
import Util.Controls.Window;

//~--- classes ----------------------------------------------------------------

public class AddressController
        implements Initializable
{
    private final AddressDAOImpl addressDAO;
    private final CityDAOImpl    cityDAO;
    private Address              addressSelection;
    @FXML
    private Button               saveButton, cancelButton;
    private City                 citySelection;
    @FXML
    private ComboBox<City>       cityComboBox;
    @FXML
    private Label                title, validation;
    private ObservableList<City> cities;
    @FXML
    private TextField            addressId, address1, address2, cityId, postalCode, phone, created, createdBy, updated,
                                 updatedBy;
    private ViewState            viewState;

    //~--- constant enums -----------------------------------------------------

    private enum ValidationState { EMPTY_FIELDS, LENGTH_EXCEEDS, CITY_NOT_SELECTED, OK }

    private enum ViewState { NEW, EDIT }

    //~--- constructors -------------------------------------------------------

    public AddressController()
    {
        this.cities     = FXCollections.observableArrayList();
        this.addressDAO = new AddressDAOImpl();
        this.cityDAO    = new CityDAOImpl();
    }

    //~--- methods ------------------------------------------------------------

    @FXML
    public void close(MouseEvent e)
    {
        Window.closeWindow(e);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        Platform.runLater(() -> {
                addressSelection = DataHolder.getInstance()
                                           .getAddress();

                DataHolder.getInstance()
                          .setAddress(null);
                configureCityComboBox();
                configureButtons();
                setFields();
            });
    }

    @FXML
    public void saveAddress()
    {
        switch (this.viewState) {
          case NEW :
              Address newAddress = new Address(address1.getText(),
                                               address2.getText(),
                                               Integer.valueOf(cityId.getText()),
                                               postalCode.getText(),
                                               phone.getText(),
                                               DataHolder.getInstance().getUser().getUsername(),
                                               DataHolder.getInstance().getUser().getUsername());

              addressDAO.create(newAddress);
              DataHolder.getInstance()
                        .setAddress(newAddress);

              break;

          case EDIT :
              Address edit = this.getAddress();

              edit.setAddress1(address1.getText());
              edit.setAddress2(address2.getText());
              edit.setCityId(Integer.valueOf(cityId.getText()));
              edit.setZip(postalCode.getText());
              edit.setPhone(phone.getText());
              edit.setUpdatedBy(DataHolder.getInstance()
                                          .getUser()
                                          .getUsername());
              addressDAO.update(edit);
              DataHolder.getInstance()
                        .setAddress(edit);

              break;
        }
    }

    @FXML
    public ValidationState validateInputs(TextField address1, TextField address2, TextField postalCode,
                                          TextField phone, ComboBox<City> cityComboBox)
    {

        // check if fields are empty
        // address2 field is not included (it isn't mandatory)
        if ((address1.getText().isEmpty()) || (postalCode.getText().isEmpty()) || (phone.getText().isEmpty()))
        {
            // invalid input (empty fields)
            return ValidationState.EMPTY_FIELDS;
        }

        // similar check to above but for the city ComboBox (it shouldn't ever be empty)
        if (cityComboBox.getSelectionModel().isEmpty() || (cityComboBox.getSelectionModel().getSelectedItem() == null))
        {
            // invalid input (city not selected)
            return ValidationState.CITY_NOT_SELECTED;
        }

        // field length checks (max lengths allowed by database table)
        if ((address1.getText().length() > 64)
                || (address2.getText().length() > 64)
                || (postalCode.getText().length() > 32)
                || (phone.getText().length() > 32))
        {
            // invalid input (field lengths exceed maximum)
            return ValidationState.LENGTH_EXCEEDS;
        }

        // all checks passed
        return ValidationState.OK;
    }

    //~--- get methods --------------------------------------------------------

    public Address getAddress()
    {
        return this.addressSelection;
    }

    @FXML
    public boolean isFormValid()
    {
        boolean isValid = false;

        // clear any errors from previous save attempts
        Validate.Address.clearErrors(address1, address2, cityComboBox, postalCode, phone, validation);

        switch (validateInputs(this.address1, this.address2, this.postalCode, this.phone, this.cityComboBox)) {
          case EMPTY_FIELDS :
              Validate.Address.emptyFields(address1, cityComboBox, postalCode, phone, validation);

              break;

          case LENGTH_EXCEEDS :
              Validate.Address.lengthExceeds(address1, address2, postalCode, phone, validation);

              break;

          case CITY_NOT_SELECTED :
              Validate.Address.cityNotSelected(cityComboBox, validation);

              break;

          case OK :
              isValid = true;

              break;
        }

        return isValid;
    }

    //~--- set methods --------------------------------------------------------

    public void setAddress(Address address)
    {
        this.addressSelection = address;
    }

    @FXML
    public void setFields()
    {

        // determining ViewState should be its own method getState() or getViewState()
        if (getAddress() == null)
        {
            viewState = ViewState.NEW;

            Platform.runLater(
                () -> {
                    title.setText("New Address");
                    addressId.setText("");
                    addressId.setPromptText("Auto");
                    address1.setText("");
                    address2.setText("");
                    postalCode.setText("");
                    phone.setText("");
                    created.setText("");
                    created.setPromptText("Auto");
                    createdBy.setText("Auto");
                    updated.setText("");
                    updated.setPromptText("Auto");
                    updatedBy.setText("Auto");
                });
        }
        else
        {
            viewState = ViewState.EDIT;

            Platform.runLater(
                () -> {
                    title.setText("View/Edit Address");
                    addressId.setText(this.getAddress()
                                          .getAddressId()
                                          .toString());
                    address1.setText(this.getAddress()
                                         .getAddress1());
                    address2.setText(this.getAddress()
                                         .getAddress2());
                    cityId.setText(this.getAddress()
                                       .getCityId()
                                       .toString());
                    cityComboBox.getSelectionModel()
                                .select(cityDAO.read(this.getAddress()
                                                         .getCityId()));
                    postalCode.setText(this.getAddress()
                                           .getZip());
                    phone.setText(this.getAddress()
                                      .getPhone());
                    created.setText(this.getAddress()
                                        .getCreated());
                    createdBy.setText(this.getAddress()
                                          .getCreatedBy());
                    updated.setText(this.getAddress()
                                        .getUpdated());
                    updatedBy.setText(this.getAddress()
                                          .getUpdatedBy());
                });
        }
    }

    //~--- methods ------------------------------------------------------------

    @FXML
    private void configureButtons()
    {
        saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                Node  source = (Node) e.getSource();
                Stage stage  = (Stage) source.getScene()
                                             .getWindow();

                if (isFormValid())
                {
                    saveAddress();
                    close(e);
                }
                else
                {
                    // save unsuccessful
                    System.out.println("Address creation unsuccessful -- check validation errors.\n");
                }
            });
        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                close(e);
            });
        saveButton.setDefaultButton(true);
        cancelButton.setCancelButton(true);
    }

    @FXML
    private void configureCityComboBox()
    {

        // fetch cities from DB as an observableArrayList
        setCities(FXCollections.observableArrayList(cityDAO.readAll()));

        // set string converter for objects contained by the ComboBox
        cityComboBox.setConverter(new StringConverter<City>()
                                  {
                                      @Override
                                      public String toString(City obj)
                                      {
                                          return obj.getCityName();
                                      }
                                      @Override
                                      public City fromString(String string)
                                      {
                                          return null;
                                      }
                                  });

        // set the ComboBox cell factory
        cityComboBox.setCellFactory(
            (ListView<City> l) -> {
                ListCell<City> cell = new ListCell<City>()
                {
                    @Override
                    protected void updateItem(City item, boolean empty)
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
                                    setText(item.getCityName());
                                });
                        }

                        super.updateItem((City) item, empty);
                    }
                };

                return cell;
            });

        // set change listener for ComboBox
        cityComboBox.getSelectionModel()
                    .selectedItemProperty()
                    .addListener(new CityComboBoxListener());

        // set event handler for ComboBox
        cityComboBox.setOnAction(
            (ActionEvent e) -> {
                citySelection = cityComboBox.getSelectionModel()
                                            .getSelectedItem();

                e.consume();
            });
        Platform.runLater(
            () -> {
                setCityComboBox();
            });
    }

    //~--- get methods --------------------------------------------------------

    private ObservableList<City> getCities()
    {
        return cities;
    }

    //~--- set methods --------------------------------------------------------

    private void setCities(ObservableList<City> cities)
    {
        this.cities = cities;
    }

    private void setCityComboBox()
    {

        // check if we have any cities to add
        if ((getCities() != null) && (!getCities().isEmpty()))
        {
            // add found cities
            Platform.runLater(
                () -> {
                    cityComboBox.setItems(getCities());
                    cityComboBox.getSelectionModel()
                                .selectFirst();
                });
        }
    }

    //~--- inner classes ------------------------------------------------------

    private class CityComboBoxListener
            implements ChangeListener<City>
    {
        public CityComboBoxListener() {}

        //~--- methods --------------------------------------------------------

        @Override
        public void changed(ObservableValue<? extends City> observable, City oldValue, City newValue)
        {
            Validate.Address.clearErrors(address1, address2, cityComboBox, postalCode, phone, validation);
            Platform.runLater(
                () -> {
                    validation.setText(null);
                });

            if ((newValue == null))
            {
                Platform.runLater(
                    () -> {
                        cityId.setText(null);
                    });

                return;
            }
            else
            {
                citySelection = newValue;

                Platform.runLater(
                    () -> {
                        cityId.setText(citySelection.getCityId()
                                                    .toString());
                    });
            }
        }
    }
}

/*
 * @(#)AddressController.java 2021.05.21 at 03:17:20 EDT
 * EOF
 */
