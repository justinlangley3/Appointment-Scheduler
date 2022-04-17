# Appointment Schedular App v1.0
### Overview
This application is a prototype for appointment scheduling. It is meant to provide minimum functionality for a business with scheduling requirements for meetings, or appointments.

## Usage
Higher level views appear in the "Overview" screen as tabs.
Selecting a given tab will dynamically load an embedded view for that tab.

Object Notes:
1. Objects are linked in the database.
  a. An object at a higher level in the ERD (like an appointment) requires child objects to be created first.
  b. Key constraints disallow deleting objects at a lower level in the ERD before their parents have been deleted
  c. Dependencies are as follows:
    |Associations| Country | City | Address | Customer | Appointment | User |
    |------------|---------|------|---------|----------|-------------|------|
    |Appointment |   x     |  x   |   x     |    x     |             |  x   |
    |Customer    |   x     |  x   |   x     |          |             |      |
    |Address     |   x     |  x   |         |          |             |      |
    |City        |   x     |      |         |          |             |      |
    |Country     |         |      |         |          |             |      |
    
3. If an object does not delete:
  a. It should throw an SQLIntegrityConstraintViolationException (see known bugs).
  b. It has associated objects that need to be deleted first before its deletion can occur.

### The Calendar/Weekly Views
1. Calendar View
  a. The monthly calendar can move forward/backward by month or by year.
  b. To check appointments for a given day, the day must be selected in the calendar.
    - This runs a DB query and populates the list view containing appointments for the selected day.
    - Selecting an appointment, then clicking the "View/Edit" button will allow viewing the appointment or making changes.
2. Weekly View
  a. The weekly calendar can move forward/backward by week or by year
  b. To check appointments for a given day, the day must be selected in the calendar.
    - This runs a DB query and populates the list view containing appointments for the selected day.
    - When an appointment is selected it will populate the appointment details in the fields below the calendar.
Note: Upon creating or editing an appointment, these views will do their best to map to the month/week containing the date the appointment is set to occur. Additionally, it will refresh the list for that day.

### Reports

To use reports a report type selection must be made. If the "Appointments by Week" report is selected, then the additional fields must be filled.
At this point, a destination can be chosen through the FileChooser. Upon clicking save, a .csv file will be created at the chosen location with the chosen file name.
Each line contained in the .csv file will contain a unique appointment entry.

1. Report Types
  a. The report types included are:
    - User schedule
      * All future appointments for the current user
    - Appointments by Type (Current Month)
      * Appointments in the current month in sorted order by type
    - Appointments by Week
      * Allows selecting a week, and year, then produces all appointments for that week

### Multilingual Support

At the Login screen there is a dropdown in the bottom left allowing the user to select a language.
This feature is not fully implemented and currently only translates the Login page (and all validation messages therein) to the corresponding language.
However, most translations for the supported languages already exist in their corresponding .properties files contained within the project.

### Automatic Time Zone Conversions

You can simply use the application as is, and it will handle converting all times to/from UTC.
When appointments are created time selections are fixed between business hours of 13:00 UTC - 21:00 UTC (9am - 5pm EST) and shown in local time.

### Login Security

When a new user is created through the "Register" view, the password is salted with 32 bytes of random data, then hashed with SHA-256.
The resulting hash and password are stored in the database.
When a user logs in, the process is repeated and compared to the database entries, so that no plaintext passwords are stored.
This provides minimum acceptable application security.

### Customizable Database Configuration

The application currently uses a MySQL database, and Connector/J v8.0.22
The db.properties file can be edited to change database parameters.
Additionally, the application uses callable statements, which are independent of database implementation flavors.
The only thing requirements for utilizing a different database implementation are:
- Migrating the database and all stored procedures
- Refactoring the driver code to use a different database driver

A copy of all database configurations (including stored procedures) are contained in the \MySQL\Dump\ folder in the project directory

## Features

- [x] *A Login form that can translate log-in and error control messages into two languages*
- [x] *CRUD capabilities for customer records including name, address, phone number*
- [x] *CRUD capabilities for maintaining appointments*
- [x] *Monthly Calendar View*
- [x] *Weekly Calendar View*
- [x] **Exception controls including:**
  * [x] *Disallow scheduling appointments outside of business hours*
  * [x] *Login validation (Disallow invalid username/password combination)*
  * [x] *Disallow scheduling an appointment that spans multiple business days*
  * [x] *Disallow non-existent customer data*
  * [x] *Disallow scheduling overlapping appointments*
  * [ ] *Disallow bad customer data (i18n for invalid zip code, phone number, etc. for the selected country)*
- [x] *Alerts on login for appointments occuring within the next 15 minutes*
- [x] **Reporting**
  * [x] *Monthly appointments ordered by type*
  * [x] *User schedule (future appointments for current user in chronological order)*
  * [x] *Appointments by selected week and year*
  * [x] *.csv output to a custom location*
- [x] *Reporting of user login activity by appending to a plaintext file (located at "./security.log")*
    

## Known Bugs:
- In the weekly view, the week containing leap day does not display on leap years.