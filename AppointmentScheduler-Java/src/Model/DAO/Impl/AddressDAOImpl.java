/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)AddressDAOImpl.java   2021.05.20 at 10:55:59 EDT
 * Version: 1.0.0
 */



package Model.DAO.Impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.Instant;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.Address;

import Model.DAO.DAO;

import Util.DB.Database;

import Util.Date.Time;
import Util.Date.Time.FormatPattern;

//~--- classes ----------------------------------------------------------------

/*
Methods:
  create(Address address),      returns Boolean
  read(Integer addressId),      returns Address
  readAll(),                    returns ArrayList<Address>
  update(Address Address),      returns Boolean
  delete(Integer addressId),    returns Boolean
*/

public class AddressDAOImpl
        implements DAO
{
    private ResultSet rs = null;

    //~--- constructors -------------------------------------------------------

    public AddressDAOImpl() {}

    //~--- methods ------------------------------------------------------------

    @Override
    public boolean create(Object addressObj)
    {
        Address address = (Address) addressObj;

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call createAddress(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

            // prepare the stored procedure
            cs = conn.prepareCall(SQL);

            cs.setString(1, address.getAddress1());
            cs.setString(2, address.getAddress2());
            cs.setInt(3, address.getCityId());
            cs.setString(4, address.getZip());
            cs.setString(5, address.getPhone());
            cs.setString(6, address.getCreatedBy());
            cs.setString(7, address.getUpdatedBy());
            cs.registerOutParameter(8, java.sql.Types.INTEGER);
            cs.registerOutParameter(9, java.sql.Types.TIMESTAMP);
            cs.registerOutParameter(10, java.sql.Types.TIMESTAMP);
            cs.execute();

            // check first OUT parameter
            cs.getInt(8);

            // check if OUT parameter was null
            if (cs.wasNull())
            {
                // this should never happen . . .
                throw new SQLException();
            }
            else
            {
                // addressId from its out parameter
                address.setAddressId(cs.getInt(8));

                // get the "created" timestamp from its OUT parameter as an instant
                Instant timestamp = cs.getTimestamp(9)
                                      .toInstant();

                // updated the objects "created" field with the timestamp using method chaining
                address.setCreated(new Time(FormatPattern.TIMESTAMP).ofInstant(timestamp)
                                                                    .toString());

                // updated the timestamp to match the "updated" OUT parameter
                timestamp = cs.getTimestamp(10)
                              .toInstant();

                // update the objects "updated" field with the updated timestamp
                address.setUpdated(new Time(FormatPattern.TIMESTAMP).ofInstant(timestamp)
                                                                    .toString());

                // create success
                System.out.println("1 row(s) inserted.");
                System.out.println("Address object: ID=" + address.getAddressId() + ", create successful.");
                System.out.print("Object updated with DB data\nID=" + address.getAddressId() + " | Created="
                                 + address.getCreated() + " | Updated=" + address.getUpdated() + "\n\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(AddressDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {

            // close the connection upon returning
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        // create failed
        System.out.println("0 row(s) inserted.");

        return false;
    }

    @Override
    public boolean delete(Integer addressId)
    {

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call deleteAddress(?)}";

            // prepare the stored procedure
            cs = conn.prepareCall(SQL);

            cs.setInt(1, addressId);

            // execute and store rows affected
            int deleted = cs.executeUpdate();

            // check rows affected
            if (deleted > 0)
            {
                // delete successful
                System.out.println(deleted + " row(s) dropped.");
                System.out.println("Address Object: ID=" + addressId + ", delete successful.\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(AddressDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {

            // close the connection upon returning
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        // delete failed
        System.out.println("0 row(s) dropped.");

        return false;
    }

    @Override
    public boolean doesTableHaveData()
    {
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {
            String SQL = "{call doesAddressTableHaveData()}";

            cs = conn.prepareCall(SQL);
            rs = cs.executeQuery();

            if (rs.next() == true)
            {
                // read data from ResultSet rs into a City object
                int result = rs.getInt(1);

                if (result > 0)
                {
                    System.out.println("addressTableHasData()=TRUE\n");

                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddressDAOImpl.class.getName())
                  .log(Level.SEVERE, null, ex);
        }

        System.out.println("addressTableHasData()=FALSE\n");

        return false;
    }

    @Override
    public Address read(Integer addressId)
    {

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call readAddress(?)}";

            // prepare the stored procedure
            cs = conn.prepareCall(SQL);

            cs.setInt(1, addressId);

            // execute the query
            rs = cs.executeQuery();

            // position cursor on first row (there can only be one row)
            // if valid data exists, read data from ResultSet rs
            if (rs.next() == true)
            {
                // read data from ResultSet rs into an Address object
                Address temp = new Address(rs.getInt(1),
                                           rs.getString(2),
                                           rs.getString(3),
                                           rs.getInt(4),
                                           rs.getString(5),
                                           rs.getString(6),

                // set a time object with DB values (it autoconverts timezone)
                new Time(FormatPattern.LOCAL_DATETIME).ofInstant(rs.getTimestamp(7).toInstant()).toString(),
                                           rs.getString(8),
                                           new Time(FormatPattern.LOCAL_DATETIME).ofInstant(
                                               rs.getTimestamp(9).toInstant()).toString(),
                                           rs.getString(10));

                // read successful
                System.out.println(rs.getRow() + " row(s) read.");
                System.out.println("Address Object: ID=" + temp.getAddressId() + ", read successfully\n");

                // return the Address object
                return temp;
            }
        } catch (SQLException e) {
            Logger.getLogger(AddressDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {

            // close the connection upon returning
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        // read failed
        System.out.println("0 row(s) read.");

        return null;
    }

    public ArrayList<Address> readAll()
    {

        // declarations
        CallableStatement  cs        = null;
        Connection         conn      = Database.getInstance()
                                               .getConnection();
        ArrayList<Address> addresses = new ArrayList<>();

        try {

            // SQL call string
            String SQL = "{call readAllAddresses()}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            // execute the query
            rs = cs.executeQuery();

            // store how many rows are returned
            int rows = 0;

            // build addresses ArrayList from ResultSet rs
            while (rs.next()) {
                System.out.println(rs.getString(7));
                System.out.println(rs.getString(9));

                // read the current row into an Address object
                Address temp = new Address(rs.getInt(1),
                                           rs.getString(2),
                                           rs.getString(3),
                                           rs.getInt(4),
                                           rs.getString(5),
                                           rs.getString(6),
                                           new Time(FormatPattern.LOCAL_DATETIME).ofInstant(
                                               rs.getTimestamp(7).toInstant()).toString(),
                                           rs.getString(8),
                                           new Time(FormatPattern.LOCAL_DATETIME).ofInstant(
                                               rs.getTimestamp(9).toInstant()).toString(),
                                           rs.getString(10));

                // add Address object to addresses ArrayList
                addresses.add(temp);

                rows++;
            }

            // check row count returned from query, cursor is now positioned at the last row
            if (rows > 0)
            {
                // read successful
                System.out.println(rows + " row(s) read.");
                System.out.println("ArrayList of size: " + addresses.size() + " created, read successfully.\n");

                // return the ArrayList
                return addresses;
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            Logger.getLogger(AddressDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {

            // close the connection upon returning
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        // read failed
        System.out.println("0 row(s) read.");

        return null;
    }

    @Override
    public boolean update(Object addressObj)
    {
        Address addr = (Address) addressObj;

        // declarations
        CallableStatement cs   = null;
        Connection        conn = Database.getInstance()
                                         .getConnection();

        try {

            // SQL call string
            String SQL = "{call updateAddress(?, ?, ?, ?, ?, ?, ?)}";

            // prepare the stored procedure
            cs = conn.prepareCall(SQL);

            cs.setInt(1, addr.getAddressId());
            cs.setString(2, addr.getAddress1());
            cs.setString(3, addr.getAddress2());
            cs.setInt(4, addr.getCityId());
            cs.setString(5, addr.getZip());
            cs.setString(6, addr.getPhone());
            cs.setString(7, addr.getUpdatedBy());

            // update and store rows affected
            int updated = cs.executeUpdate();

            if (updated != 0)
            {
                // update successful
                System.out.println(updated + " row(s) affected.");
                System.out.println("Address object: ID=" + addr.getAddressId() + ", update successful.\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(AddressDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {

            // close the connection upon returning
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        // update failed
        System.out.println("0 row(s) affected.");

        return false;
    }
}

/*
 * @(#)AddressDAOImpl.java 2021.05.20 at 10:55:59 EDT
 * EOF
 */
