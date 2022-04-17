/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)CountriesTab.java   2021.05.21 at 07:21:43 EDT
 * Version: 1.0.0
 */



package Controller.Tabs;

import java.net.URL;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import Model.Country;

import Model.DAO.Impl.CountryDAOImpl;

import Model.Instance.DataHolder;

import Util.Controls.Dialog;
import Util.Controls.Validate;

//~--- classes ----------------------------------------------------------------

public class CountriesTab
        implements Initializable
{
    private final CountryDAOImpl CountryDAO;
    @FXML
    private Button               editSave, deleteCountry, editCancel, newCreate, newCancel;
    private Country              selection;
    @FXML
    private Label                editValidation, newValidation;
    @FXML
    private Label                statusListView;
    @FXML
    private ListView<Country>    listCountries;
    @FXML
    private TextField            editCountryId, editCountryName, editCountryCreated, editCountryCreatedBy,
                                 editCountryUpdated, editCountryUpdatedBy;
    @FXML
    private TextField newCountryId, newCountryName, newCountryCreated, newCountryCreatedBy, newCountryUpdated,
                      newCountryUpdatedBy;

    //~--- constructors -------------------------------------------------------

    public CountriesTab()
    {
        this.CountryDAO = new CountryDAOImpl();
    }

    //~--- methods ------------------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        newCountryCreatedBy.setText(DataHolder.getInstance()
                                              .getUser()
                                              .getUsername());
        newCountryUpdatedBy.setText(DataHolder.getInstance()
                                              .getUser()
                                              .getUsername());
        configureCountryListView();
        updateCountryListView();
        updateFields(listCountries.getSelectionModel()
                                  .getSelectedItem());
        setButtons();
    }

    @FXML
    private void configureCountryListView()
    {
        listCountries.setCellFactory(
            (ListView<Country> c) -> {
                ListCell<Country> cell = new ListCell<Country>()
                {
                    @Override
                    protected void updateItem(Country item, boolean empty)
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
                                    setText(item.getCountryName());
                                });
                        }

                        super.updateItem((Country) item, empty);
                    }
                };

                return cell;
            });
    }

    @FXML
    private void delete()
    {
        if (Dialog.confirmationDialog("Press 'OK' to delete selected country.", "Are you sure?", "Confirm Deletion"))
        {
            try {
                if (!CountryDAO.delete(selection.getCountryId()))
                {
                    throw new SQLIntegrityConstraintViolationException("Key constraint check failed.");
                }
            } catch (SQLException ex) {
                Alert error = Dialog.errorDialog("Country is still associated to other database entries.",
                                                 "Object could not be deleted:",
                                                 "SQLIntegrityConstraintViolation");

                error.show();
            } finally {
                updateCountryListView();
            }
        }
    }

    @FXML
    private void updateCountryListView()
    {
        ArrayList<Country> countries = CountryDAO.readAll();

        if ((countries == null) || countries.isEmpty())
        {
            return;
        }
        else
        {
            listCountries.setItems(null);
            Platform.runLater(
                () -> {
                    listCountries.setItems(FXCollections.observableArrayList(countries));
                    listCountries.getSelectionModel()
                                 .selectedItemProperty()
                                 .addListener(new CountryListChangeListener());
                    listCountries.getSelectionModel()
                                 .selectFirst();
                    listCountries.getFocusModel()
                                 .focus(0);
                    listCountries.refresh();
                });
        }
    }

    @FXML
    private void updateFields(Country selected)
    {
        if (selected != null)
        {
            Platform.runLater(
                () -> {
                    editCountryId.setText(selected.getCountryId()
                                                  .toString());
                    editCountryName.setText(selected.getCountryName());
                    editCountryCreated.setText(selected.getCreated());
                    editCountryCreatedBy.setText(selected.getCreatedBy());
                    editCountryUpdated.setText(selected.getUpdated());
                    editCountryUpdatedBy.setText(selected.getUpdatedBy());
                });
        }
    }

    //~--- set methods --------------------------------------------------------

    @FXML
    private void setButtons()
    {

        // set handler for edit country save button
        editSave.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                Country edit = listCountries.getSelectionModel()
                                            .getSelectedItem();

                if (editCountryId.getText()
                                 .isEmpty())
                {
                    Platform.runLater(
                        () -> {
                            Validate.CountriesTab.countryNotSelected(editCountryId,
                                                                     editCountryName,
                                                                     editCountryCreated,
                                                                     editCountryCreatedBy,
                                                                     editCountryUpdated,
                                                                     editCountryUpdatedBy,
                                                                     editValidation);
                        });

                    return;
                }

                if (editCountryName.getText()
                                   .isEmpty())
                {
                    Platform.runLater(
                        () -> {
                            Validate.CountriesTab.empty(editCountryName, editValidation);
                        });

                    return;
                }

                if (editCountryName.getText()
                                   .length() > 64)
                {
                    Platform.runLater(
                        () -> {
                            Validate.CountriesTab.length(editCountryName, editValidation);
                        });
                }
                else
                {
                    Platform.runLater(
                        () -> {
                            Validate.CountriesTab.success(editCountryName, editValidation);
                            edit.setCountryName(editCountryName.getText());
                            edit.setUpdatedBy(DataHolder.getInstance()
                                                        .getUser()
                                                        .getUsername());
                        });
                    CountryDAO.update(edit);
                    updateCountryListView();
                }
            });

        // set handler for edit country cancel button
        editCancel.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                Platform.runLater(
                    () -> {
                        listCountries.getSelectionModel()
                                     .selectFirst();
                        editValidation.setText(null);
                    });
            });

        // set handler for delete country button
        deleteCountry.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                delete();
            });

        // set handler for new country cancel button
        newCancel.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                Platform.runLater(
                    () -> {
                        newCountryCreatedBy.setPromptText(null);
                        newCountryUpdatedBy.setPromptText(null);
                        newCountryCreatedBy.setText(DataHolder.getInstance()
                                                              .getUser()
                                                              .getUsername());
                        newCountryUpdatedBy.setText(DataHolder.getInstance()
                                                              .getUser()
                                                              .getUsername());
                        newCountryName.clear();
                        Validate.CountriesTab.clear(newCountryName, newValidation);
                    });
            });

        // set handler for new country create button
        newCreate.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (e) -> {
                Platform.runLater(
                    () -> {
                        newCountryCreatedBy.setPromptText(null);
                        newCountryUpdatedBy.setPromptText(null);
                        newCountryCreatedBy.setText(DataHolder.getInstance()
                                                              .getUser()
                                                              .getUsername());
                        newCountryUpdatedBy.setText(DataHolder.getInstance()
                                                              .getUser()
                                                              .getUsername());
                    });

                if (newCountryName.getText()
                                  .isEmpty())
                {
                    Platform.runLater(
                        () -> {
                            Validate.CountriesTab.empty(newCountryName, newValidation);
                        });

                    return;
                }

                if (newCountryName.getText()
                                  .length() > 64)
                {
                    Platform.runLater(
                        () -> {
                            Validate.CountriesTab.length(newCountryName, newValidation);
                        });
                }
                else
                {
                    Country newCountry = new Country(newCountryName.getText(),
                                                     DataHolder.getInstance().getUser().getUsername(),
                                                     DataHolder.getInstance().getUser().getUsername());

                    Platform.runLater(
                        () -> {
                            Validate.CountriesTab.success(newCountryName, newValidation);
                        });
                    CountryDAO.create(newCountry);
                    updateCountryListView();
                }
            });
    }

    //~--- inner classes ------------------------------------------------------

    private class CountryListChangeListener
            implements ChangeListener<Country>
    {
        public CountryListChangeListener() {}

        //~--- methods --------------------------------------------------------

        @Override
        public void changed(ObservableValue<? extends Country> observable, Country oldValue, Country newValue)
        {
            Validate.CountriesTab.clear(editCountryName, editValidation);
            editValidation.setText(null);

            if (newValue == null)
            {
                Platform.runLater(
                    () -> {
                        listCountries.refresh();
                    });

                selection = null;
            }
            else
            {
                selection = newValue;

                updateFields(selection);
            }
        }
    }
}

/*
 * @(#)CountriesTab.java 2021.05.21 at 07:21:43 EDT
 * EOF
 */
