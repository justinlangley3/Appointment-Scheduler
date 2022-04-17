/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)City.java   2021.05.19 at 06:09:49 EDT
 * Version: 1.0.0
 */



package Model;

//~--- classes ----------------------------------------------------------------

public class City
{
    private Integer cityId;
    private Integer countryId;
    private String  cityName;
    private String  created;
    private String  createdBy;
    private String  updated;
    private String  updatedBy;

    //~--- constructors -------------------------------------------------------

    public City() {}

    public City(String cityName, Integer countryId, String createdBy, String updatedBy)
    {
        this.cityName  = cityName;
        this.countryId = countryId;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public City(Integer cityId, String cityName, Integer countryId, String created, String createdBy, String updated,
                String updatedBy)
    {
        this.cityId    = cityId;
        this.cityName  = cityName;
        this.countryId = countryId;
        this.created   = created;
        this.createdBy = createdBy;
        this.updated   = updated;
        this.updatedBy = updatedBy;
    }

    //~--- methods ------------------------------------------------------------

    public String toString()
    {
        return this.getCityName();
    }

    //~--- get methods --------------------------------------------------------

    public Integer getCityId()
    {
        return this.cityId;
    }

    public String getCityName()
    {
        return this.cityName;
    }

    public Integer getCountryId()
    {
        return this.countryId;
    }

    public String getCreated()
    {
        return this.created;
    }

    public String getCreatedBy()
    {
        return this.createdBy;
    }

    public String getUpdated()
    {
        return this.updated;
    }

    public String getUpdatedBy()
    {
        return this.updatedBy;
    }

    //~--- set methods --------------------------------------------------------

    public void setCityId(Integer cityId)
    {
        this.cityId = cityId;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }

    public void setCountryId(Integer countryId)
    {
        this.countryId = countryId;
    }

    public void setCreated(String timestamp)
    {
        this.created = timestamp;
    }

    public void setCreatedBy(String name)
    {
        this.createdBy = name;
    }

    public void setUpdated(String timestamp)
    {
        this.updated = timestamp;
    }

    public void setUpdatedBy(String name)
    {
        this.updatedBy = name;
    }
}

/*
 * @(#)City.java 2021.05.19 at 06:09:49 EDT
 * EOF
 */
