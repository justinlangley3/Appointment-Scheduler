/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)AddressBookTab.java   2021.05.21 at 07:40:37 EDT
 * Version: 1.0.0
 */



package Controller.Tabs;

import java.net.URL;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import java.util.ResourceBundle;

import javafx.application.Platform;

import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javafx.stage.Modality;
import javafx.stage.Stage;

import Model.Address;

import Model.DAO.Impl.AddressDAOImpl;

import Model.Instance.DataHolder;

import Util.Controls.Dialog;
import Util.Controls.Window.WindowType;

import static Util.Controls.Window.showNewWindow;

//~--- classes ----------------------------------------------------------------

public class AddressBookTab
        implements Initializable
{
    private final AddressDAOImpl          addressDAO;
    protected Address                     addressSelection;
    @FXML
    private Button                        newAddress, editAddress, deleteAddress;
    @FXML
    private TableColumn<Address, Integer> addressId, addressCityId;
    @FXML
    private TableColumn<Address, String>  address1, address2, addressPostalCode, addressPhone, addressCreated,
                                          addressCreatedBy, addressUpdated, addressUpdatedBy;
    @FXML
    private TableView<Address> addressTable;

    //~--- constructors -------------------------------------------------------

    public AddressBookTab()
    {
        this.addressDAO = new AddressDAOImpl();
    }

    //~--- methods ------------------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        setButtons();
        configureTableView();
        refreshData();
    }

    @FXML
    private void configureTableView()
    {
        addressTable.setFocusTraversable(true);

        // handle setting cells to hold data from address objects' fields
        // this is tied to their fields defined in their model class
        addressId.setCellValueFactory(new PropertyValueFactory<>("addressId"));
        address1.setCellValueFactory(new PropertyValueFactory<>("address1"));
        address2.setCellValueFactory(new PropertyValueFactory<>("address2"));
        addressCityId.setCellValueFactory(new PropertyValueFactory<>("cityId"));
        addressPostalCode.setCellValueFactory(new PropertyValueFactory<>("zip"));
        addressPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressCreated.setCellValueFactory(new PropertyValueFactory<>("created"));
        addressCreatedBy.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        addressUpdated.setCellValueFactory(new PropertyValueFactory<>("updated"));
        addressUpdatedBy.setCellValueFactory(new PropertyValueFactory<>("updatedBy"));

        // disable sorting
        addressId.setSortable(false);
        address1.setSortable(false);
        address2.setSortable(false);
        addressCityId.setSortable(false);
        addressPostalCode.setSortable(false);
        addressPhone.setSortable(false);
        addressCreated.setSortable(false);
        addressCreatedBy.setSortable(false);
        addressUpdated.setSortable(false);
        addressUpdatedBy.setSortable(false);

        // set selection listener
        addressTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null)
                {
                    addressSelection = addressTable.getSelectionModel()
                                                   .getSelectedItem();
                }
                else
                {
                    addressSelection = null;
                }
            });
        addressTable.setOnKeyPressed(
            (KeyEvent key) -> {
                if (key.getCode().equals(KeyCode.ENTER)
                    || (key.getCharacter().getBytes() [0] == '\n')
                    || (key.getCharacter().getBytes() [0] == '\r'))
                {
                    editAddress();
                }
            });
        addressTable.setEditable(false);
    }

    @FXML
    private void deleteAddress()
    {
        if (Dialog.confirmationDialog("Press 'OK' to delete selected address", "Are you sure?", "Confirm Deletion"))
        {
            try {
                if (!addressDAO.delete(this.getSelection()
                                           .getAddressId()))
                {
                    throw new SQLIntegrityConstraintViolationException();
                }
            } catch (SQLException ex) {
                Alert error = Dialog.errorDialog("Address is still associated to other database entries.",
                                                 "Object could not be deleted:",
                                                 "SQLIntegrityConstraintViolation");

                error.show();
            } finally {
                refreshData();
            }
        }
    }

    @FXML
    private void editAddress()
    {
        if (addressTable.getItems().isEmpty() || (addressTable.getSelectionModel().getSelectedItem() == null))
        {
            Dialog.infoDialog("A selection must be made in order to make an edit.",
                              "No selection was made",
                              "Invalid Selection");

            return;
        }
        else
        {
            addressSelection = addressTable.getSelectionModel()
                                           .getSelectedItem();
        }

        Stage stage = showNewWindow("Appointment Scheduler — View/Edit Address",
                                    WindowType.ADDRESS,
                                    Modality.APPLICATION_MODAL,
                                    false);

        stage.setOnCloseRequest(
            (e) -> {
                refreshData();
                addressTable.getSelectionModel()
                            .selectFirst();
            });
        stage.setOnHidden(
            (e) -> {
                if (DataHolder.getInstance()
                              .getAddress() == null)
                {
                    addressSelection = null;
                }
                else
                {
                    addressTable.getSelectionModel()
                                .select(addressTable.getItems()
                                                    .stream()
                                                    .filter(address -> address.getAddressId()
                                                                              .equals(DataHolder.getInstance()
                                                                                                .getAddress()
                                                                                                .getAddressId()))
                                                    .findFirst()
                                                    .orElse(null));
                }

                refreshData();
            });
        Platform.runLater(
            () -> {
                stage.showAndWait();
            });
    }

    @FXML
    private void newAddress()
    {

        // see Util.Controls.Window.java for information on this method call
        Stage stage = showNewWindow("Appointment Scheduler — New Address",
                                    WindowType.ADDRESS,
                                    Modality.APPLICATION_MODAL,
                                    false);

        stage.setOnCloseRequest(
            (e) -> {
                refreshData();
                addressTable.getSelectionModel()
                            .selectFirst();
            });
        stage.setOnHidden(
            (e) -> {
                if (DataHolder.getInstance()
                              .getAddress() == null)
                {
                    addressSelection = null;
                }
                else
                {
                    addressTable.getSelectionModel()
                                .select(addressTable.getItems()
                                                    .stream()
                                                    .filter(address -> address.getAddressId()
                                                                              .equals(DataHolder.getInstance()
                                                                                                .getAddress()
                                                                                                .getAddressId()))
                                                    .findFirst()
                                                    .orElse(null));
                }

                refreshData();
            });
        Platform.runLater(
            () -> {
                stage.showAndWait();
            });
    }

    @FXML
    private void populateTableView()
    {
        if (addressDAO.doesTableHaveData())
        {
            Platform.runLater(
                () -> {
                    addressTable.setItems(FXCollections.observableArrayList(addressDAO.readAll()));
                });
        }
        else
        {
            Platform.runLater(
                () -> {
                    addressTable.setItems(FXCollections.observableArrayList());
                });
        }
    }

    @FXML
    private void refreshData()
    {
        populateTableView();

        if (addressTable.getItems()
                        .isEmpty())
        {
            return;
        }

        if (addressSelection == null)
        {
            Platform.runLater(
                () -> {
                    addressTable.getSelectionModel()
                                .selectFirst();
                });
        }
        else
        {
            Platform.runLater(
                () -> {
                    addressTable.getSelectionModel()
                                .select(addressSelection);
                });
        }
    }

    //~--- get methods --------------------------------------------------------

    private Address getSelection()
    {
        return this.addressSelection;
    }

    //~--- set methods --------------------------------------------------------

    @FXML
    private void setButtons()
    {
        this.newAddress.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                DataHolder.getInstance()
                          .setAddress(null);
                newAddress();
            });
        this.editAddress.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                DataHolder.getInstance()
                          .setAddress(addressSelection);
                editAddress();
            });
        this.deleteAddress.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                deleteAddress();
                refreshData();
            });
    }
}

/*
 * @(#)AddressBookTab.java 2021.05.21 at 07:40:37 EDT
 * EOF
 */
