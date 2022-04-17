/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Country.java   2021.05.19 at 06:09:53 EDT
 * Version: 1.0.0
 */



package Model;

//~--- classes ----------------------------------------------------------------

public class Country
{
    private Integer countryId;
    private String  countryName;
    private String  created;
    private String  createdBy;
    private String  updated;
    private String  updatedBy;

    //~--- constructors -------------------------------------------------------

    public Country() {}

    public Country(String countryName, String createdBy, String updatedBy)
    {
        this.countryName = countryName;
        this.createdBy   = createdBy;
        this.updatedBy   = updatedBy;
    }

    public Country(Integer countryId, String countryName, String created, String createdBy, String updated,
                   String updatedBy)
    {
        this.countryId   = countryId;
        this.countryName = countryName;
        this.created     = created;
        this.createdBy   = createdBy;
        this.updated     = updated;
        this.updatedBy   = updatedBy;
    }

    //~--- methods ------------------------------------------------------------

    public String toString()
    {
        return this.getCountryName();
    }

    //~--- get methods --------------------------------------------------------

    public Integer getCountryId()
    {
        return this.countryId;
    }

    public String getCountryName()
    {
        return this.countryName;
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

    public void setCountryId(Integer countryId)
    {
        this.countryId = countryId;
    }

    public void setCountryName(String countryName)
    {
        this.countryName = countryName;
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
 * @(#)Country.java 2021.05.19 at 06:09:53 EDT
 * EOF
 */
