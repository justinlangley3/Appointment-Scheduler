/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)DataHolder.java   2021.05.20 at 10:47:13 EDT
 * Version: 1.0.0
 */



package Model.Instance;

import java.net.URL;

import Model.Address;
import Model.Appointment;
import Model.User;

//~--- classes ----------------------------------------------------------------

//Singleton class for availability of User and other data objects program-wide
public final class DataHolder
{
    private static final DataHolder INSTANCE = new DataHolder();

    //~--- fields -------------------------------------------------------------

    private final URL   addressBookTabURL = getClass().getResource("/View/Tabs/AddressBookTab.fxml");
    private final URL   calendarTabURL    = getClass().getResource("/View/Tabs/CalendarTab.fxml");
    private final URL   citiesTabURL      = getClass().getResource("/View/Tabs/CitiesTab.fxml");
    private final URL   countriesTabURL   = getClass().getResource("/View/Tabs/CountriesTab.fxml");
    private final URL   customersTabURL   = getClass().getResource("/View/Tabs/CustomersTab.fxml");
    private final URL   reportsTabURL     = getClass().getResource("/View/Tabs/ReportsTab.fxml");
    private final URL   weeklyTabURL      = getClass().getResource("/View/Tabs/WeeklyTab.fxml");
    private Address     address;
    private Appointment appointment;
    private User        user;

    //~--- constructors -------------------------------------------------------

    private DataHolder() {}

    //~--- get methods --------------------------------------------------------

    public Address getAddress()
    {
        return this.address;
    }

    public URL getAddressBookTabURL()
    {
        return this.addressBookTabURL;
    }

    public Appointment getAppointment()
    {
        return this.appointment;
    }

    public URL getCalendarTabURL()
    {
        return this.calendarTabURL;
    }

    public URL getCitiesTabURL()
    {
        return this.citiesTabURL;
    }

    public URL getCountriesTabURL()
    {
        return this.countriesTabURL;
    }

    public URL getCustomersTabURL()
    {
        return this.customersTabURL;
    }

    public static DataHolder getInstance()
    {
        return INSTANCE;
    }

    public URL getReportsTabURL()
    {
        return this.reportsTabURL;
    }

    public User getUser()
    {
        return this.user;
    }

    public URL getWeeklyTabURL()
    {
        return this.weeklyTabURL;
    }

    //~--- set methods --------------------------------------------------------

    public void setAddress(Address address)
    {
        this.address = address;
    }

    public void setAppointment(Appointment appointment)
    {
        this.appointment = appointment;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}

/*
 * @(#)DataHolder.java 2021.05.20 at 10:47:13 EDT
 * EOF
 */
