/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Appointment.java   2021.05.19 at 06:09:43 EDT
 * Version: 1.0.0
 */



package Model;

//~--- classes ----------------------------------------------------------------

public class Appointment
{
    private Integer appointmentId;
    private Integer customerId;
    private Integer userId;
    private String  category;
    private String  contact;
    private String  created;
    private String  createdBy;
    private String  end;
    private String  location;
    private String  start;
    private String  summary;
    private String  title;
    private String  updated;
    private String  updatedBy;
    private String  url;

    //~--- constructors -------------------------------------------------------

    public Appointment() {}

    public Appointment(Integer customerId, Integer userId, String title, String summary, String location,
                       String contact, String url, String category, String start, String end, String createdBy,
                       String updatedBy)
    {
        this.customerId = customerId;
        this.userId     = userId;
        this.title      = title;
        this.summary    = summary;
        this.location   = location;
        this.contact    = contact;
        this.url        = url;
        this.category   = category;
        this.start      = start;
        this.end        = end;
        this.createdBy  = createdBy;
        this.updatedBy  = updatedBy;
    }

    public Appointment(Integer appointmentId, Integer customerId, Integer userId, String title, String summary,
                       String location, String contact, String url, String category, String start, String end,
                       String created, String createdBy, String updated, String updatedBy)
    {
        this.appointmentId = appointmentId;
        this.customerId    = customerId;
        this.userId        = userId;
        this.title         = title;
        this.summary       = summary;
        this.location      = location;
        this.contact       = contact;
        this.url           = url;
        this.category      = category;
        this.start         = start;
        this.end           = end;
        this.created       = created;
        this.createdBy     = createdBy;
        this.updated       = updated;
        this.updatedBy     = updatedBy;
    }

    //~--- get methods --------------------------------------------------------

    public Integer getAppointmentId()
    {
        return this.appointmentId;
    }

    public String getCategory()
    {
        return this.category;
    }

    public String getContact()
    {
        return this.contact;
    }

    public String getCreated()
    {
        return this.created;
    }

    public String getCreatedBy()
    {
        return this.createdBy;
    }

    public Integer getCustomerId()
    {
        return this.customerId;
    }

    public String getEnd()
    {
        return this.end;
    }

    public String getLocation()
    {
        return this.location;
    }

    public String getStart()
    {
        return this.start;
    }

    public String getSummary()
    {
        return this.summary;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getUpdated()
    {
        return this.updated;
    }

    public String getUpdatedBy()
    {
        return this.updatedBy;
    }

    public String getUrl()
    {
        return this.url;
    }

    public Integer getUserId()
    {
        return this.userId;
    }

    //~--- set methods --------------------------------------------------------

    public void setAppointmentId(Integer appointmentId)
    {
        this.appointmentId = appointmentId;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public void setContact(String phone)
    {
        this.contact = phone;
    }

    public void setCreated(String timestamp)
    {
        this.created = timestamp;
    }

    public void setCreatedBy(String name)
    {
        this.createdBy = name;
    }

    public void setCustomerId(Integer customer)
    {
        this.customerId = customer;
    }

    public void setEnd(String end)
    {
        this.end = end;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public void setStart(String start)
    {
        this.start = start;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setUpdated(String timestamp)
    {
        this.updated = timestamp;
    }

    public void setUpdatedBy(String name)
    {
        this.updatedBy = name;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }
}

/*
 * @(#)Appointment.java 2021.05.19 at 06:09:43 EDT
 * EOF
 */
