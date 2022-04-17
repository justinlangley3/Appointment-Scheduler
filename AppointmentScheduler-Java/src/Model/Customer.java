/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Customer.java   2021.05.19 at 06:09:57 EDT
 * Version: 1.0.0
 */



package Model;

//~--- classes ----------------------------------------------------------------

public class Customer
{
    private Byte    active;
    private Integer addressId;
    private Integer customerId;
    private String  created;
    private String  createdBy;
    private String  customerName;
    private String  updated;
    private String  updatedBy;

    //~--- constructors -------------------------------------------------------

    public Customer() {}

    public Customer(String customerName, Integer addressId, String createdBy, String updatedBy)
    {
        this.customerName = customerName;
        this.addressId    = addressId;
        this.createdBy    = createdBy;
        this.updatedBy    = updatedBy;
    }

    public Customer(Integer customerId, String customerName, Integer addressId, Byte active, String created,
                    String createdBy, String updated, String updatedBy)
    {
        this.customerId   = customerId;
        this.customerName = customerName;
        this.addressId    = addressId;
        this.active       = active;
        this.created      = created;
        this.createdBy    = createdBy;
        this.updated      = updated;
        this.updatedBy    = updatedBy;
    }

    //~--- methods ------------------------------------------------------------

    @Override
    public String toString()
    {
        return this.getCustomerId() + ", " + this.getCustomerName();
    }

    //~--- get methods --------------------------------------------------------

    public Byte getActive()
    {
        return this.active;
    }

    public Integer getAddressId()
    {
        return this.addressId;
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

    public String getCustomerName()
    {
        return this.customerName;
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

    public void setActive(Byte active)
    {
        this.active = active;
    }

    public void setAddressId(Integer addressId)
    {
        this.addressId = addressId;
    }

    public void setCreated(String timestamp)
    {
        this.created = timestamp;
    }

    public void setCreatedBy(String name)
    {
        this.createdBy = name;
    }

    public void setCustomerId(Integer customerId)
    {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
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
 * @(#)Customer.java 2021.05.19 at 06:09:57 EDT
 * EOF
 */
