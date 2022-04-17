/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)User.java   2021.05.19 at 06:10:07 EDT
 * Version: 1.0.0
 */



package Model;

//~--- classes ----------------------------------------------------------------

public class User
{
    private Byte    active;
    private Integer userId;
    private String  created;
    private String  createdBy;
    private String  updated;
    private String  updatedBy;
    private String  username;
    private byte[]  hash;
    private byte[]  salt;

    //~--- constructors -------------------------------------------------------

    public User() {}

    public User(String username, byte[] hash, byte[] salt, String createdBy, String updatedBy)
    {
        this.username  = username;
        this.hash      = hash;
        this.salt      = salt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public User(Integer userId, String username, byte[] hash, byte[] salt, Byte active, String created,
                String createdBy, String updated, String updatedBy)
    {
        this.userId    = userId;
        this.username  = username;
        this.hash      = hash;
        this.salt      = salt;
        this.active    = active;
        this.created   = created;
        this.createdBy = createdBy;
        this.updated   = updated;
        this.updatedBy = updatedBy;
    }

    //~--- get methods --------------------------------------------------------

    public Byte getActive()
    {
        return this.active;
    }

    public String getCreated()
    {
        return this.created;
    }

    public String getCreatedBy()
    {
        return this.createdBy;
    }

    public byte[] getPassword()
    {
        return this.hash;
    }

    public byte[] getSalt()
    {
        return this.salt;
    }

    public String getUpdated()
    {
        return this.updated;
    }

    public String getUpdatedBy()
    {
        return this.updatedBy;
    }

    public Integer getUserId()
    {
        return this.userId;
    }

    public String getUsername()
    {
        return this.username;
    }

    //~--- set methods --------------------------------------------------------

    public void setActive(Byte active)
    {
        this.active = active;
    }

    public void setCreated(String timestamp)
    {
        this.created = timestamp;
    }

    public void setCreatedBy(String name)
    {
        this.createdBy = name;
    }

    public void setPassword(byte[] hash)
    {
        this.hash = hash;
    }

    public void setSalt(byte[] salt)
    {
        this.salt = salt;
    }

    public void setUpdated(String timestamp)
    {
        this.updated = timestamp;
    }

    public void setUpdatedBy(String name)
    {
        this.updatedBy = name;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }
}

/*
 * @(#)User.java 2021.05.19 at 06:10:07 EDT
 * EOF
 */
