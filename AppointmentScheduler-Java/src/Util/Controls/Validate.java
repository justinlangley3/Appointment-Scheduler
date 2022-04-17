/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Validate.java   2021.05.21 at 06:06:32 EDT
 * Version: 1.0.0
 */



package Util.Controls;

import java.io.File;
import java.io.IOException;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import Model.City;

import Model.DAO.Impl.AppointmentDAOImpl;

import Util.Date.Time;

import Util.File.IO.IOHandler;

import Util.Locale.Locale;

//~--- classes ----------------------------------------------------------------

public class Validate
{
    public static void setErrorFields(ArrayList<TextField> tf)
    {

        // programatically set all TextField borders to red
        for (int i = 0 ; i < tf.size() ; i++) {
            Validate.setErrorBorder(tf.get(i));
        }
    }

    public static void setValidFields(ArrayList<TextField> tf)
    {

        // programatically set all TextField borders to red
        for (int i = 0 ; i < tf.size() ; i++) {
            setValidBorder(tf.get(i));
        }
    }

    private static void setErrorBorder(Control c)
    {
        Platform.runLater(
            () -> {
                c.setBorder(new Border(new BorderStroke(Color.web("#d12f24"),
                                                        BorderStrokeStyle.SOLID,
                                                        new CornerRadii(7),
                                                        BorderWidths.DEFAULT)));
            });
    }

    private static void setErrorContextMessage(Label context, String text)
    {
        Platform.runLater(
            () -> {
                context.setTextFill(Color.web("#d12f24"));
                context.setText(text);
            });
    }

    private static void setNullBorder(Control c)
    {
        Platform.runLater(
            () -> {
                c.setBorder(Border.EMPTY);
            });
    }

    private static void setValidBorder(Control c)
    {
        Platform.runLater(
            () -> {
                c.setBorder(new Border(new BorderStroke(Color.web("#24d461"),
                                                        BorderStrokeStyle.SOLID,
                                                        new CornerRadii(7),
                                                        BorderWidths.DEFAULT)));
            });
    }

    private static void setValidContextMessage(Label context, String text)
    {
        Platform.runLater(
            () -> {
                context.setTextFill(Color.web("#24d461"));
                context.setText(text);
            });
    }

    //~--- inner classes ------------------------------------------------------

    /*
     * Validation controls for the "(Edit/New) Address" views
     */

    public static class Address
    {
        public static void cityNotSelected(ComboBox<City> city, Label validation)
        {
            setErrorBorder(city);
            setErrorContextMessage(validation, "City must be selected.");
        }

        public static void clearErrors(TextField address1, TextField address2, ComboBox city, TextField postalCode,
                                       TextField phone, Label validation)
        {
            setNullBorder(address1);
            setNullBorder(address2);
            Validate.setNullBorder(city);
            setNullBorder(postalCode);
            setNullBorder(phone);
            setErrorContextMessage(validation, null);
        }

        public static void emptyFields(TextField address1, ComboBox city, TextField postalCode, TextField phone,
                                       Label validation)
        {
            if (address1.getText()
                        .isEmpty())
            {
                Validate.setErrorBorder(address1);
            }

            if (city.getSelectionModel().isEmpty() || (city.getSelectionModel().getSelectedItem() == null))
            {
                setErrorBorder(city);
            }

            if (postalCode.getText()
                          .isEmpty())
            {
                Validate.setErrorBorder(postalCode);
            }

            if (phone.getText()
                     .isEmpty())
            {
                Validate.setErrorBorder(phone);
            }

            setErrorContextMessage(validation, "Highlighted fields are required.");
        }

        public static void lengthExceeds(TextField address1, TextField address2, TextField postalCode, TextField phone,
                                         Label validation)
        {
            Validate.setErrorBorder(address1);
            Validate.setErrorBorder(address2);
            Validate.setErrorBorder(postalCode);
            Validate.setErrorBorder(phone);
            setErrorContextMessage(validation,
                                   "Max Lengths:\nAddress fields - 64 Characters\nPostal code - 32 Characters\nPhone - 32 Characters");
        }

        public static void success(TextField address1, TextField address2, ComboBox city, TextField postalCode,
                                   TextField phone, Label validation)
        {
            setValidBorder(address1);
            setValidBorder(address2);
            Validate.setValidBorder(city);
            setValidBorder(postalCode);
            setValidBorder(phone);
            setValidContextMessage(validation, "Success!");
        }
    }


    /*
     * Validation controls for the "(Edit/New) Appointment" views
     */

    public static class Appointment
    {
        public static void clear(TextField title, TextArea details, TextField url, TextField location, ChoiceBox type,
                                 DatePicker start, DatePicker end, Label validation) {}

        public static Boolean customerSelectionIsValid(ComboBox customerComboBox, Label validation)
        {
            if (customerComboBox.getSelectionModel()
                                .getSelectedItem() == null)
            {
                Validate.setErrorContextMessage(validation, "Customer selection must be made");
                Validate.setErrorBorder(customerComboBox);

                return false;
            }
            else
            {
                Validate.setValidBorder(customerComboBox);
            }

            return true;
        }

        public static Boolean dateSelectionIsValid(DatePicker start, Label validation)
        {
            if (start.getValue() == null)
            {
                Validate.setErrorContextMessage(validation, "A start date must be selected.");
                Validate.setErrorBorder(start);

                return false;
            }
            else
            {
                Validate.setValidBorder(start);
            }

            return true;
        }

        public static Boolean fieldLengthsAreValid(TextField title, TextArea details, TextField url,
                                                   TextField location, Label validation)
        {
            if (title.getText()
                     .length() > 255)
            {
                Validate.setErrorContextMessage(validation, "Title field exceeds 255 characters.");
                Validate.setErrorBorder(title);

                return false;
            }
            else
            {
                Validate.setValidBorder(title);
            }

            if (details.getText()
                       .length() > 65535)
            {
                Validate.setErrorContextMessage(validation, "Details field exceeds 65,535 characters.");
                Validate.setErrorBorder(details);

                return false;
            }
            else
            {
                Validate.setValidBorder(details);
            }

            if (url.getText()
                   .length() > 255)
            {
                Validate.setErrorContextMessage(validation, "URL field exceeds 255 characters.");
                Validate.setErrorBorder(url);

                return false;
            }
            else
            {
                Validate.setValidBorder(url);
            }

            if (location.getText()
                        .length() > 65535)
            {
                Validate.setErrorContextMessage(validation, "Location field exceeds 65,535 characters.");
                Validate.setErrorBorder(location);

                return false;
            }
            else
            {
                Validate.setValidBorder(location);
            }

            return true;
        }

        public static Boolean fieldsHaveData(TextField title, TextArea details, TextField url, TextField location,
                                             Label validation)
        {
            if (title.getText()
                     .isEmpty())
            {
                Validate.setErrorContextMessage(validation, "Title field is empty");
                Validate.setErrorBorder(title);

                return false;
            }
            else
            {
                Validate.setValidBorder(title);
            }

            if (details.getText()
                       .isEmpty())
            {
                Validate.setErrorContextMessage(validation, "Details field is empty");
                Validate.setErrorBorder(details);

                return false;
            }
            else
            {
                Validate.setValidBorder(details);
            }

            if (url.getText()
                   .isEmpty())
            {
                Validate.setErrorContextMessage(validation, "URL field is empty");
                Validate.setErrorBorder(url);

                return false;
            }
            else
            {
                Validate.setValidBorder(url);
            }

            if (location.getText()
                        .isEmpty())
            {
                Validate.setErrorContextMessage(validation, "Location field is empty");
                Validate.setErrorBorder(location);

                return false;
            }
            else
            {
                Validate.setValidBorder(location);
            }

            return true;
        }

        public static Boolean timeSelectionIsValid(DatePicker date, TextField start, TextField end, Label validation)
        {
            AppointmentDAOImpl appointmentDAO = new AppointmentDAOImpl();
            LocalDateTime      businessOpen   = new Time(date.valueProperty().get().toString()).setHours(13)
                                                                                               .setMinutes(0)
                                                                                               .setSeconds(0)
                                                                                               .setNano(0)
                                                                                               .update()
                                                                                               .toLocal();
            LocalDateTime businessClose = new Time(date.valueProperty().get().toString()).setHours(21)
                                                                                         .setMinutes(0)
                                                                                         .setSeconds(0)
                                                                                         .setNano(0)
                                                                                         .update()
                                                                                         .toLocal();
            String startDatestring = date.getValue()
                                         .toString() + " " + start.getText() + ":00";
            String endDatestring   = date.getValue()
                                         .toString() + " " + end.getText() + ":00";

            // we go through a range of checks, returning early if an issue is found
            if (start.getText()
                     .isEmpty())
            {
                Platform.runLater(
                    () -> {
                        Validate.setErrorBorder(start);
                        Validate.setErrorContextMessage(validation, "Begin time is required.");
                    });

                return false;
            }

            if (end.getText()
                   .isEmpty())
            {
                Platform.runLater(
                    () -> {
                        Validate.setErrorBorder(end);
                        Validate.setErrorContextMessage(validation, "End time is required.");
                    });

                return false;
            }

            // we compare our strings with regex for 24-hr time with mandatory leading zero for 1 digit hours
            if ((!start.getText().matches("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$"))
                    || (!end.getText().matches("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")))
            {
                Platform.runLater(
                    () -> {
                        Validate.setErrorBorder(start);
                        Validate.setErrorBorder(end);
                        Validate.setErrorContextMessage(validation, "Times must be in 24-hr format: [00-23]:[00-59]");
                    });

                return false;
            }

            LocalDateTime startDatetime = date.valueProperty()
                                              .get()
                                              .atTime(Integer.valueOf(start.getText()
                                                                           .split(":") [0]),
                                                      Integer.valueOf(start.getText()
                                                                           .split(":") [1]));
            LocalDateTime endDatetime = date.valueProperty()
                                            .get()
                                            .atTime(Integer.valueOf(end.getText()
                                                                       .split(":") [0]),
                                                    Integer.valueOf(end.getText()
                                                                       .split(":") [1]));

            if (endDatetime.isBefore(startDatetime))
            {
                Platform.runLater(
                    () -> {
                        Validate.setErrorBorder(start);
                        Validate.setErrorBorder(end);
                        Validate.setErrorContextMessage(validation, "Begin time must precede end time.");
                    });

                return false;
            }

            if (!startDatetime.plusMinutes(14)
                              .isBefore(endDatetime))
            {
                Platform.runLater(
                    () -> {
                        Validate.setErrorBorder(start);
                        Validate.setErrorBorder(end);
                        Validate.setErrorContextMessage(validation, "Appointment duration must be 15 minutes minimum.");
                    });

                return false;
            }

            if (startDatetime.isBefore(businessOpen)
                    || endDatetime.isBefore(businessOpen)
                    || startDatetime.isAfter(businessClose)
                    || endDatetime.isAfter(businessClose))
            {
                Platform.runLater(
                    () -> {
                        Validate.setErrorBorder(start);
                        Validate.setErrorContextMessage(validation,
                                                        "Appointment must be within business hours: "
                                                        + businessOpen.getHour() + ":00-" + businessClose.getHour()
                                                        + ":00");
                    });

                return false;
            }

            if ((!Arrays.asList(0, 15, 30, 45).contains(startDatetime.getMinute()))
                    || (!Arrays.asList(0, 15, 30, 45).contains(startDatetime.getMinute())))
            {
                Platform.runLater(
                    () -> {
                        Validate.setErrorBorder(start);
                        Validate.setErrorBorder(end);
                        Validate.setErrorContextMessage(validation,
                                                        "Appointment times must be in 15 minute increments.");
                    });

                return false;
            }

            // now that times have been check that they contain valid data,
            // we must check with the database to see if the timeslot has been filled
            // use a stream to check for a match
            if (appointmentDAO.doesTableHaveDataForDate(start.getText()))
            {
                ArrayList<Model.Appointment> appointments = appointmentDAO.readAppointmentFromDate(startDatestring);

                if ((appointments.stream()
                                 .filter(
                                     appointment -> new Time(startDatestring).getLocalDateTime().isEqual(
                                         new Time(appointment.getStart()).getLocalDateTime())
                                                    || new Time(endDatestring).getLocalDateTime().isEqual(
                                                        new Time(appointment.getEnd()).getLocalDateTime())
                                                    || (new Time(startDatestring).getLocalDateTime().isAfter(
                                                        new Time(
                                                            appointment.getStart()).getLocalDateTime()) && new Time(
                                                                startDatestring).getLocalDateTime().isBefore(
                                                                new Time(appointment.getEnd()).getLocalDateTime()))
                                                    || (new Time(endDatestring).getLocalDateTime().isAfter(
                                                        new Time(
                                                            appointment.getStart()).getLocalDateTime()) && new Time(
                                                                endDatestring).getLocalDateTime().isBefore(
                                                                new Time(appointment.getEnd()).getLocalDateTime())))
                                 .findFirst()
                                 .orElse(null) != null))
                {
                    Platform.runLater(
                        () -> {
                            Validate.setErrorBorder(start);
                            Validate.setErrorBorder(end);
                            Validate.setErrorContextMessage(validation,
                                                            "An appointment already exists for this time slot.");
                        });

                    return false;
                }
            }

            Platform.runLater(
                () -> {
                    Validate.setValidBorder(start);
                    Validate.setValidBorder(end);
                });

            return true;
        }

        public static Boolean typeSelectionIsValid(ChoiceBox cb, Label validation)
        {
            if (cb.getSelectionModel()
                  .getSelectedItem() == null)
            {
                Validate.setErrorContextMessage(validation, "Appointment type must be selected.");
                Validate.setErrorBorder(cb);

                return false;
            }
            else
            {
                Validate.setValidBorder(cb);
            }

            return true;
        }

        public static void valid(Label validation)
        {
            Validate.setValidContextMessage(validation, "Input valid!");
        }
    }


    /*
     * Validation controls for the "Cities" view
     */

    public static class CitiesTab
    {
        public static void clear(ComboBox country, TextField cityName, Label validation)
        {
            validation.setText(null);
            setNullBorder(country);
            setNullBorder(cityName);
        }

        public static Boolean countrySelectionPresent(ComboBox country, Label validation)
        {
            if (country.getItems().isEmpty() || (country.getSelectionModel().getSelectedItem() == null))
            {
                setErrorContextMessage(validation, "An associated country must be selected.");
                setErrorBorder(country);

                return false;
            }

            setValidBorder(country);

            return true;
        }

        public static Boolean notEmpty(TextField cityName, Label validation)
        {
            if (cityName.getText()
                        .isEmpty())
            {
                setErrorContextMessage(validation, "Highlighted fields must be filled.");
                setErrorBorder(cityName);

                return false;
            }

            setValidBorder(cityName);

            return true;
        }

        public static void success(Label validation)
        {
            setValidContextMessage(validation, "Success!");
        }

        public static Boolean validLength(TextField cityName, Label validation)
        {
            if (cityName.getText()
                        .length() > 255)
            {
                setErrorContextMessage(validation, "City name must not exceed 255 characters.");
                setErrorBorder(cityName);

                return false;
            }

            setValidBorder(cityName);

            return true;
        }
    }


    /*
     * Validation controls for the "Countries" view
     */

    public static class CountriesTab
    {
        public static void clear(TextField countryName, Label validation)
        {
            validation.setText(null);
            setNullBorder(countryName);
        }

        public static void countryNotSelected(TextField countryId, TextField countryName, TextField created,
                                              TextField createdBy, TextField updated, TextField updatedBy,
                                              Label validation)
        {
            setErrorContextMessage(validation, "Please re-select a country before editing.");
            setErrorBorder(countryId);
            setErrorBorder(countryName);
            setErrorBorder(created);
            setErrorBorder(createdBy);
            setErrorBorder(updated);
            setErrorBorder(updatedBy);
        }

        public static void empty(TextField countryName, Label validation)
        {
            setErrorContextMessage(validation, "Country name is required.");
            setErrorBorder(countryName);
        }

        public static void length(TextField countryName, Label validation)
        {
            setErrorContextMessage(validation, "Country names cannot be longer than 64 chars.");
            setErrorBorder(countryName);
        }

        public static void success(TextField countryName, Label validation)
        {
            setValidContextMessage(validation, "Success!");
            setValidBorder(countryName);
        }
    }


    public static class CustomersTab
    {
        public static void addressNotSelected(ComboBox address, Label validation)
        {
            setErrorBorder(address);
            setErrorContextMessage(validation, "An address must be selected.");
        }

        public static void clearErrors(TextField customerName, ComboBox address, Label validation)
        {
            setNullBorder(address);
            setNullBorder(customerName);
            setErrorContextMessage(validation, null);
        }

        public static void emptyField(TextField customerName, Label validation)
        {
            setErrorBorder(customerName);
            setErrorContextMessage(validation, "Customer name is required.");
        }

        public static void lengthExceeds(TextField customerName, Label validation)
        {
            setErrorBorder(customerName);
            setErrorContextMessage(validation, "Customer name must be < 70 characters.");
        }

        public static void success(TextField customerName, ComboBox address, Label validation)
        {
            setValidBorder(address);
            setValidBorder(customerName);
            setValidContextMessage(validation, "Success!");
        }
    }


    /*
     * Validation controls for the "Login" view
     */

    public static class Login
    {
        public static void bothLengthExceeds(TextField user, PasswordField password, Label validation)
        {
            try {
                setErrorContextMessage(validation, Locale.get()
                                                         .val("Login.Label.validation.FieldsExceed"));
            } catch (IOException ex) {
                Logger.getLogger(Validate.class.getName())
                      .log(Level.SEVERE, null, ex);
            }

            setErrorBorder(user);
            setErrorBorder(password);
        }

        public static void clearContext(TextField user, PasswordField password, Label validation)
        {
            setErrorContextMessage(validation, null);
            setNullBorder(user);
            setNullBorder(password);
        }

        public static void credentialsNotFound(TextField user, PasswordField password, Label validation)
        {
            try {
                setErrorContextMessage(validation, Locale.get()
                                                         .val("Login.Label.validation.CredentialsNotFound"));
            } catch (IOException ex) {
                Logger.getLogger(Validate.class.getName())
                      .log(Level.SEVERE, null, ex);
            }

            setNullBorder(user);
            setNullBorder(password);
        }

        public static void failure(TextField user, PasswordField password, Label validation)
        {
            try {
                setErrorContextMessage(validation, Locale.get()
                                                         .val("Login.Label.validation.Failure"));
            } catch (IOException ex) {
                Logger.getLogger(Validate.class.getName())
                      .log(Level.SEVERE, null, ex);
            }

            setNullBorder(user);
            setNullBorder(password);
        }

        public static void fieldsEmpty(TextField user, PasswordField password, Label validation)
        {
            try {
                setErrorContextMessage(validation, Locale.get()
                                                         .val("Login.Label.validation.Empty"));
            } catch (IOException ex) {
                Logger.getLogger(Validate.class.getName())
                      .log(Level.SEVERE, null, ex);
            }

            setErrorBorder(user);
            setErrorBorder(password);
        }

        public static void hashFail(TextField user, PasswordField password, Label validation)
        {
            try {
                setErrorContextMessage(validation, Locale.get()
                                                         .val("Login.Label.validation.HashFailure"));
            } catch (IOException ex) {
                Logger.getLogger(Validate.class.getName())
                      .log(Level.SEVERE, null, ex);
            }

            setErrorBorder(password);

            // username already passed if password authentication failed
            setValidBorder(user);
        }

        public static void passwordFieldEmpty(TextField user, PasswordField password, Label validation)
        {
            try {
                setErrorContextMessage(validation, Locale.get()
                                                         .val("Login.Label.validation.PasswordEmpty"));
            } catch (IOException ex) {
                Logger.getLogger(Validate.class.getName())
                      .log(Level.SEVERE, null, ex);
            }

            setErrorBorder(password);

            // Unknown at this point if the user entered a valid username
            setNullBorder(user);
        }

        public static void passwordLengthExceeds(TextField user, PasswordField password, Label validation)
        {
            try {
                setErrorContextMessage(validation, Locale.get()
                                                         .val("Login.Label.validation.PasswordExceeds"));
            } catch (IOException ex) {
                Logger.getLogger(Validate.class.getName())
                      .log(Level.SEVERE, null, ex);
            }

            setNullBorder(user);
            setErrorBorder(password);
        }

        public static void success(TextField user, PasswordField password, Label validation)
        {
            try {
                setValidContextMessage(validation, Locale.get()
                                                         .val("Login.Label.validation.Success"));
            } catch (IOException ex) {
                Logger.getLogger(Validate.class.getName())
                      .log(Level.SEVERE, null, ex);
            }

            Validate.setValidBorder(user);
            Validate.setValidBorder(password);
        }

        public static void usernameFail(TextField user, PasswordField password, Label validation)
        {
            try {
                setErrorContextMessage(validation, Locale.get()
                                                         .val("Login.Label.validation.UsernameFailure"));
            } catch (IOException ex) {
                Logger.getLogger(Validate.class.getName())
                      .log(Level.SEVERE, null, ex);
            }

            setErrorBorder(user);

            // The user field is invalid. Therefore, we cannot validate password.
            // Hence, we will not give the user a context clue on this field
            setNullBorder(password);
        }

        public static void usernameFieldEmpty(TextField user, PasswordField password, Label validation)
        {
            try {
                setErrorContextMessage(validation, Locale.get()
                                                         .val("Login.Label.validation.UsernameEmpty"));
            } catch (IOException ex) {
                Logger.getLogger(Validate.class.getName())
                      .log(Level.SEVERE, null, ex);
            }

            setErrorBorder(user);

            // Without a username we have no idea if the password will authenticate
            // We will not provide a context clue on this field
            setNullBorder(password);
        }

        public static void usernameLengthExceeds(TextField user, PasswordField password, Label validation)
        {
            try {
                setErrorContextMessage(validation, Locale.get()
                                                         .val("Login.Label.validation.UsernameExceeds"));
            } catch (IOException ex) {
                Logger.getLogger(Validate.class.getName())
                      .log(Level.SEVERE, null, ex);
            }

            setErrorBorder(user);
            setNullBorder(password);
        }
    }


    /*
     * Validation controls for the "Register" view
     */

    public static class Register
    {
        public static void empty(TextField user, PasswordField password, PasswordField confirm, Label validation)
        {
            setErrorContextMessage(validation, "Please complete all fields.");
            setErrorBorder(user);
            setErrorBorder(password);
            setErrorBorder(confirm);
        }

        public static void mismatch(TextField user, PasswordField password, PasswordField confirm, Label validation)
        {
            setErrorContextMessage(validation, "Password does not match.");
            setErrorBorder(password);
            setErrorBorder(confirm);
            setValidBorder(user);
        }

        public static void passwordEmpty(TextField user, PasswordField password, PasswordField confirm,
                                         Label validation)
        {
            setErrorContextMessage(validation, "Password is required.");
            setErrorBorder(password);
            setErrorBorder(confirm);
            setNullBorder(user);
        }

        public static void requirements(TextField user, PasswordField password, PasswordField confirm, Label validation)
        {
            setErrorContextMessage(validation,
                                   "Requirements:\nAt least 8 characters\nAt least one uppercase\nAt least one number\nAt least one symbol: ! @ # $ % & * /");
            setErrorBorder(password);
            setErrorBorder(confirm);
            setNullBorder(user);
        }

        public static void requirementsCase(TextField user, PasswordField password, PasswordField confirm,
                                            Label validation)
        {
            setErrorContextMessage(validation, "Password must contain at least one uppercase.");
            setErrorBorder(password);
            setErrorBorder(confirm);
            setNullBorder(user);
        }

        public static void requirementsLength(TextField user, PasswordField password, PasswordField confirm,
                                              Label validation)
        {
            setErrorContextMessage(validation, "Password must be 8-32 characters in length.");
            setErrorBorder(password);
            setErrorBorder(confirm);
            setNullBorder(user);
        }

        public static void requirementsNumber(TextField user, PasswordField password, PasswordField confirm,
                                              Label validation)
        {
            setErrorContextMessage(validation, "Password must contain at least one number.");
            setErrorBorder(password);
            setErrorBorder(confirm);
            setNullBorder(user);
        }

        public static void requirementsSymbol(TextField user, PasswordField password, PasswordField confirm,
                                              Label validation)
        {
            setErrorContextMessage(validation, "Must contain at least one special character:\n! @ # $ % & * /");
            setErrorBorder(password);
            setErrorBorder(confirm);
            setNullBorder(user);
        }

        public static void success(TextField user, PasswordField password, PasswordField confirm, Label validation)
        {
            setValidContextMessage(validation, "Success!");
            setValidBorder(user);
            setValidBorder(password);
            setValidBorder(confirm);
        }

        public static void usernameFieldEmpty(TextField user, PasswordField password, PasswordField confirm,
                                              Label validation)
        {
            setErrorContextMessage(validation, "Username field is required.");
            setErrorBorder(user);

            // We will not provide a context clue on these fields
            setNullBorder(password);
            setNullBorder(confirm);
        }

        public static void usernameTaken(TextField user, PasswordField password, PasswordField confirm,
                                         Label validation)
        {
            setErrorContextMessage(validation, "Username already taken.");
            setErrorBorder(user);
            setNullBorder(password);
            setNullBorder(confirm);
        }
    }


    public static class ReportsTab
    {
        public static void failure(Label validation)
        {
            setErrorContextMessage(validation, "Failed to create report.");
        }

        public static Boolean pathInvalid(TextField destination, File saveFile, Label validation)
        {
            if (!IOHandler.hasReadWriteAccess(saveFile))
            {
                setErrorBorder(destination);
                setErrorContextMessage(validation,
                                       "Please enter a valid path. The path cannot be a protected directory.");

                return false;
            }
            else
            {
                setValidBorder(destination);

                return true;
            }
        }

        public static Boolean pathNotSet(TextField path, Label validation)
        {
            if (path.getText()
                    .isEmpty())
            {
                setErrorContextMessage(validation, "Please choose a valid file path.");
                setErrorBorder(path);

                return false;
            }

            setValidBorder(path);

            return true;
        }

        public static Boolean reportSelectionNotMade(ChoiceBox report, Label validation)
        {
            if ((report.getSelectionModel().getSelectedItem() == "")
                    || (report.getSelectionModel().getSelectedItem() == null))
            {
                setErrorContextMessage(validation, "A report selection must be made.");
                setErrorBorder(report);

                return false;
            }

            setValidBorder(report);

            return true;
        }

        public static void success(Label validation)
        {
            setValidContextMessage(validation, "Report generated successfully!");
        }

        public static Boolean weekNotSet(ComboBox<String> week, Label validation)
        {
            if (week.getSelectionModel()
                    .getSelectedItem()
                    .isEmpty())
            {
                setErrorContextMessage(validation, "Please select a week.");
                setErrorBorder(week);

                return false;
            }

            setValidBorder(week);

            return true;
        }

        public static Boolean yearNotSet(TextField year, Label validation)
        {
            if (year.getText()
                    .isEmpty())
            {
                setErrorContextMessage(validation, "Please enter a year.");
                setErrorBorder(year);

                return false;
            }

            setValidBorder(year);

            return true;
        }
    }
}

/*
 * @(#)Validate.java 2021.05.21 at 06:06:32 EDT
 * EOF
 */
