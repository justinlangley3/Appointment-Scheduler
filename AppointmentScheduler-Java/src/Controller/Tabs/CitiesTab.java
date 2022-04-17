/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)CitiesTab.java   2021.05.21 at 08:55:52 EDT
 * Version: 1.0.0
 */



package Controller.Tabs;

import java.net.URL;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import java.util.ResourceBundle;

import javafx.application.Platform;

import javafx.collections.FXCollections;

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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javafx.util.StringConverter;

import Model.City;
import Model.Country;

import Model.DAO.Impl.CityDAOImpl;
import Model.DAO.Impl.CountryDAOImpl;

import Model.Instance.DataHolder;

import Util.Controls.Dialog;
import Util.Controls.Validate;

//~--- classes ----------------------------------------------------------------

public class CitiesTab
        implements Initializable
{
    private final CityDAOImpl    cityDAO;
    private final CountryDAOImpl countryDAO;
    @FXML
    private Button               delete, save, cancel;
    private City                 citySelection;
    @FXML
    private ComboBox<Country>    countryComboBox;
    private Country              countrySelection;
    @FXML
    private Label                validation;
    @FXML
    private ListView<City>       citiesListView;
    @FXML
    private RadioButton          newRadio, editRadio;
    @FXML
    private TextField            cityId, countryId, cityName, created, createdBy, updated, updatedBy;

    //~--- constant enums -----------------------------------------------------

    private enum ValidationState { EMPTY_FIELDS, LENGTH_EXCEEDS, COUNTY_NOT_SELECTED }

    private enum ViewState { NEW, EDIT, ERROR }

    //~--- constructors -------------------------------------------------------

    public CitiesTab()
    {
        this.countryDAO = new CountryDAOImpl();
        this.cityDAO    = new CityDAOImpl();
    }

    //~--- methods ------------------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        configureButtons();
        configureRadios();
        configureComboBox();
        configureCityListView();
        refreshData();
        populateFields();
    }

    @FXML
    private void configureButtons()
    {
        save.setOnAction(
            (e) -> {
                switch (this.getViewState()) {
                  case EDIT :
                      if (countryComboBox.getItems().isEmpty()
                          || (countryComboBox.getSelectionModel().getSelectedItem() == null))
                      {
                          return;
                      }

                      if (Validate.CitiesTab.notEmpty(cityName, validation)
                          && Validate.CitiesTab.validLength(cityName, validation)
                          && Validate.CitiesTab.countrySelectionPresent(countryComboBox, validation))
                      {
                          this.citySelection.setCityName(cityName.getText());
                          this.citySelection.setCountryId(Integer.valueOf(countryId.getText()));
                          this.citySelection.setUpdatedBy(DataHolder.getInstance()
                                                                    .getUser()
                                                                    .getUsername());
                          cityDAO.update(this.citySelection);
                          Platform.runLater(
                              () -> {
                                  Validate.CitiesTab.success(validation);
                              });
                          refreshData();
                          populateFields();
                          citiesListView.getSelectionModel()
                                        .select(citiesListView.getItems()
                                                              .stream()
                                                              .filter(city -> city.getCityId()
                                                                                  .equals(citySelection.getCityId()))
                                                              .findFirst()
                                                              .orElse(null));
                      }

                      break;

                  case NEW :
                      if (Validate.CitiesTab.notEmpty(cityName, validation)
                          && Validate.CitiesTab.validLength(cityName, validation)
                          && Validate.CitiesTab.countrySelectionPresent(countryComboBox, validation))
                      {
                          City newCity = new City(cityName.getText(),
                                                  Integer.valueOf(countryId.getText()),
                                                  DataHolder.getInstance().getUser().getUsername(),
                                                  DataHolder.getInstance().getUser().getUsername());

                          cityDAO.create(newCity);
                          Platform.runLater(
                              () -> {
                                  Validate.CitiesTab.success(validation);
                                  cityName.clear();
                              });
                          refreshData();
                          populateFields();
                          citiesListView.getSelectionModel()
                                        .select(citiesListView.getItems()
                                                              .stream()
                                                              .filter(city -> city.getCityId()
                                                                                  .equals(newCity.getCityId()))
                                                              .findFirst()
                                                              .orElse(null));
                      }

                      break;
                }
            });
        cancel.setOnAction(
            (e) -> {
                switch (this.getViewState()) {
                  case EDIT :
                      Platform.runLater(
                          () -> {
                              validation.setText(null);

                              if (!citiesListView.getItems()
                                                 .isEmpty())
                              {
                                  citiesListView.getSelectionModel()
                                                .selectFirst();
                              }
                          });

                      break;

                  case NEW :
                      Platform.runLater(
                          () -> {
                              if (!countryComboBox.getItems()
                                                  .isEmpty())
                              {
                                  countryComboBox.getSelectionModel()
                                                 .selectFirst();
                              }

                              Validate.CitiesTab.clear(countryComboBox, cityName, validation);
                              createdBy.setPromptText(null);
                              updatedBy.setPromptText(null);
                              createdBy.setText(DataHolder.getInstance()
                                                          .getUser()
                                                          .getUsername());
                              updatedBy.setText(DataHolder.getInstance()
                                                          .getUser()
                                                          .getUsername());
                              cityName.clear();
                          });

                      break;
                }
            });
        delete.setOnAction(
            (e) -> {
                City toDelete = citiesListView.getSelectionModel()
                                              .getSelectedItem();

                if (Dialog.confirmationDialog("Press 'OK' to delete the selected city.",
                                              "Are you sure?",
                                              "Confirm Deletion"))
                {
                    try {
                        if (!cityDAO.delete(toDelete.getCityId()))
                        {
                            throw new SQLIntegrityConstraintViolationException();
                        }
                    } catch (SQLException ex) {
                        Alert error = Dialog.errorDialog("City is still associated to other database entries.",
                                                         "Object could not be deleted:",
                                                         "SQLIntegrityConstraintViolation");

                        error.show();
                    } finally {
                        refreshData();
                        populateFields();
                    }
                }
            });

        switch (this.getViewState()) {
          case EDIT :
              Platform.runLater(
                  () -> {
                      delete.setVisible(true);
                      delete.setDisable(false);
                  });

              break;

          case NEW :
              Platform.runLater(
                  () -> {
                      delete.setVisible(true);
                      delete.setDisable(false);
                  });

              break;
        }
    }

    @FXML
    private void configureCityListView()
    {
        citiesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null)
                {
                    citySelection    = newVal;
                    countrySelection = countryComboBox.getItems()
                                                      .stream()
                                                      .filter(country -> country.getCountryId()
                                                                                .equals(newVal.getCountryId()))
                                                      .findFirst()
                                                      .orElse(null);
                }
                else
                {
                    citySelection    = null;
                    countrySelection = null;
                }

                populateFields();
            });
    }

    @FXML
    private void configureComboBox()
    {
        this.countryComboBox.setFocusTraversable(true);
        citiesListView.setCellFactory(
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
        countryComboBox.setConverter(new StringConverter<Country>()
                                     {
                                         @Override
                                         public String toString(Country obj)
                                         {
                                             return obj.getCountryName();
                                         }
                                         @Override
                                         public Country fromString(String string)
                                         {
                                             return null;
                                         }
                                     });

        // Set cell factories
        countryComboBox.setCellFactory(
            (ListView<Country> l) -> {
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
                                    setText(item.toString());
                                });
                        }

                        super.updateItem((Country) item, empty);
                    }
                };

                return cell;
            });

        // set enter key action for comboboxes when highlighted
        // this is for better program accessibility
        countryComboBox.setOnKeyPressed(
            (KeyEvent key) -> {
                if (key.getCode().equals(KeyCode.ENTER)
                    || (key.getCharacter().getBytes() [0] == '\n')
                    || (key.getCharacter().getBytes() [0] == '\r'))
                {
                    Platform.runLater(
                        () -> {
                            countryComboBox.show();
                        });
                }
            });

        // set event handlers
        countryComboBox.setOnAction(
            (ActionEvent event) -> {
                countrySelection = countryComboBox.getSelectionModel()
                                                  .getSelectedItem();
            });
        countryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null)
                {
                    countrySelection = newVal;

                    Platform.runLater(
                        () -> {
                            countryId.setText(newVal.getCountryId()
                                                    .toString());
                        });
                }
                else
                {
                    countrySelection = null;
                }
            });
    }

    @FXML
    private void configureRadios()
    {
        newRadio.setOnAction(
            (e) -> {
                configureButtons();
                configureCityListView();
                refreshData();
                populateFields();
            });
        editRadio.setOnAction(
            (e) -> {
                configureButtons();
                configureCityListView();
                refreshData();
                populateFields();
            });
    }

    @FXML
    private void populateFields()
    {
        switch (this.getViewState()) {
          case EDIT :
              if (citySelection != null)
              {
                  Platform.runLater(
                      () -> {
                          cityId.setText(citySelection.getCityId()
                                                      .toString());
                          cityName.setText(citySelection.getCityName());
                          created.setText(citySelection.getCreated());
                          createdBy.setText(citySelection.getCreatedBy());
                          updated.setText(citySelection.getUpdated());
                          updatedBy.setText(citySelection.getUpdatedBy());
                          countryComboBox.getSelectionModel()
                                         .select(countryComboBox.getItems()
                                                                .stream()
                                                                .filter(country -> country.getCountryId()
                                                                                          .equals(
                                                                                          citySelection.getCountryId()))
                                                                .findFirst()
                                                                .orElse(null));
                      });
              }
              else
              {
                  Platform.runLater(
                      () -> {
                          if (!countryComboBox.getItems()
                                              .isEmpty())
                          {
                              countryComboBox.getSelectionModel()
                                             .selectFirst();
                              cityId.setText("");
                              cityName.setText("");
                              created.setText("");
                              createdBy.setText(DataHolder.getInstance()
                                                          .getUser()
                                                          .getUsername());
                              updated.setText("");
                              updatedBy.setText(DataHolder.getInstance()
                                                          .getUser()
                                                          .getUsername());
                          }
                      });
              }

              break;

          case NEW :
              Platform.runLater(
                  () -> {
                      if ((citySelection == null)) {}
                      else
                      {
                          if (!countryComboBox.getItems()
                                              .isEmpty())
                          {
                              countryComboBox.getSelectionModel()
                                             .selectFirst();
                          }

                          return;
                      }

                      cityId.setText("AUTO");
                      cityName.setText("");
                      created.setText("AUTO");
                      createdBy.setText(DataHolder.getInstance()
                                                  .getUser()
                                                  .getUsername());
                      updated.setText("AUTO");
                      updatedBy.setText(DataHolder.getInstance()
                                                  .getUser()
                                                  .getUsername());
                  });

              break;
        }
    }

    @FXML
    private void refreshData()
    {
        if (countryDAO.doesTableHaveData())
        {
            Platform.runLater(
                () -> {
                    countryComboBox.setItems(FXCollections.observableArrayList(countryDAO.readAll()));
                });
        }
        else
        {
            Platform.runLater(
                () -> {
                    countryComboBox.setItems(FXCollections.observableArrayList());
                });
        }

        if (cityDAO.doesTableHaveData())
        {
            Platform.runLater(
                () -> {
                    citiesListView.setItems(FXCollections.observableArrayList(cityDAO.readAll()));
                });
        }
        else
        {
            Platform.runLater(
                () -> {
                    citiesListView.setItems(FXCollections.observableArrayList());
                });
        }
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
}

/*
 * @(#)CitiesTab.java 2021.05.21 at 08:55:52 EDT
 * EOF
 */
