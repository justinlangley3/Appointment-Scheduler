/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Address.java   2021.05.19 at 06:09:38 EDT
 * Version: 1.0.0
 */



package Model;

import java.util.Arrays;
import java.util.List;

//~--- classes ----------------------------------------------------------------

public class Address
{
    private Integer addressId;
    private Integer cityId;
    private String  address1;
    private String  address2;
    private String  created;
    private String  createdBy;
    private String  phone;
    private String  updated;
    private String  updatedBy;
    private String  zip;

    //~--- constructors -------------------------------------------------------

    public Address() {}

    public Address(String address1, String address2, Integer cityId, String zip, String phone, String createdBy,
                   String updatedBy)
    {
        this.address1  = address1;
        this.address2  = address2;
        this.cityId    = cityId;
        this.zip       = zip;
        this.phone     = phone;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Address(Integer addressId, String address1, String address2, Integer cityId, String zip, String phone,
                   String created, String createdBy, String updated, String updatedBy)
    {
        this.addressId = addressId;
        this.address1  = address1;
        this.address2  = address2;
        this.cityId    = cityId;
        this.zip       = zip;
        this.phone     = phone;
        this.created   = created;
        this.createdBy = createdBy;
        this.updated   = updated;
        this.updatedBy = updatedBy;
    }

    //~--- methods ------------------------------------------------------------

    public String toString()
    {
        return this.getAddressId()
                   .toString() + ", " + this.getAddress1() + ", " + this.getZip();
    }

    //~--- get methods --------------------------------------------------------

    public String getAddress1()
    {
        return this.address1;
    }

    public String getAddress2()
    {
        return this.address2;
    }

    public Integer getAddressId()
    {
        return this.addressId;
    }

    public Integer getCityId()
    {
        return this.cityId;
    }

    public String getCreated()
    {
        return this.created;
    }

    public String getCreatedBy()
    {
        return this.createdBy;
    }

    public static List<String> getFieldNames(List<String> newList)
    {
        newList = Arrays.asList("addressId",
                                "address1",
                                "address2",
                                "cityId",
                                "zip",
                                "phone",
                                "created",
                                "createdBy",
                                "updated",
                                "updatedBy");

        return newList;
    }

    public static String getFieldNames(String newString)
    {
        newString = "addressId,address1,address2,cityId,zip,phone,created,createdBy,updated,updatedBy";

        return newString;
    }

    public String getPhone()
    {
        return this.phone;
    }

    public String getUpdated()
    {
        return this.updated;
    }

    public String getUpdatedBy()
    {
        return this.updatedBy;
    }

    public String getZip()
    {
        return this.zip;
    }

    //~--- set methods --------------------------------------------------------

    public void setAddress1(String address1)
    {
        this.address1 = address1;
    }

    public void setAddress2(String address2)
    {
        this.address2 = address2;
    }

    public void setAddressId(Integer addressId)
    {
        this.addressId = addressId;
    }

    public void setCityId(Integer cityId)
    {
        this.cityId = cityId;
    }

    public void setCreated(String timestamp)
    {
        this.created = timestamp;
    }

    public void setCreatedBy(String name)
    {
        this.createdBy = name;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setUpdated(String timestamp)
    {
        this.updated = timestamp;
    }

    public void setUpdatedBy(String name)
    {
        this.updatedBy = name;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }
}

/*
 * @(#)Address.java 2021.05.19 at 06:09:38 EDT
 * EOF
 */
