/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)CustomerDAOImpl.java   2021.05.20 at 10:53:53 EDT
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

import Model.Customer;

import Model.DAO.DAO;

import Util.DB.Database;

import Util.Date.Time;
import Util.Date.Time.FormatPattern;

//~--- classes ----------------------------------------------------------------

/*
Methods:
  create(Customer customer),    returns Boolean
  read(Integer customerId),     returns Customer
  readAll(),                    returns ArrayList<Customer>
  update(Customer customer),    returns Boolean
  delete(Integer customerId),   returns Boolean
*/

public class CustomerDAOImpl
        implements DAO
{
    private ResultSet rs = null;

    //~--- constructors -------------------------------------------------------

    public CustomerDAOImpl() {}

    //~--- methods ------------------------------------------------------------

    @Override
    public boolean create(Object obj)
    {
        Customer customer = (Customer) obj;

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call createCustomer(?, ?, ?, ?, ?, ?, ?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setString(1, customer.getCustomerName());
            cs.setInt(2, customer.getAddressId());
            cs.setString(3, customer.getCreatedBy());
            cs.setString(4, customer.getUpdatedBy());
            cs.registerOutParameter(5, java.sql.Types.INTEGER);
            cs.registerOutParameter(6, java.sql.Types.TIMESTAMP);
            cs.registerOutParameter(7, java.sql.Types.TIMESTAMP);
            cs.execute();

            // check first OUT parameter
            cs.getInt(5);

            // check if OUT parameter was null
            if (cs.wasNull())
            {
                // this should never happen . . .
                throw new SQLException();
            }
            else
            {
                // update our object from our OUT parameters
                customer.setCustomerId(cs.getInt(5));

                Instant timestamp = cs.getTimestamp(6)
                                      .toInstant();

                customer.setCreated(new Time(Time.FormatPattern.TIMESTAMP).ofInstant(timestamp)
                                                                          .toString());

                timestamp = cs.getTimestamp(7)
                              .toInstant();

                customer.setUpdated(new Time(Time.FormatPattern.TIMESTAMP).ofInstant(timestamp)
                                                                          .toString());

                // create successful
                System.out.println("1 row(s) inserted.");
                System.out.println("Customer object: ID=" + customer.getCustomerId() + ", create successful.");
                System.out.print("Object updated with DB data\nID=" + customer.getCustomerId() + " | Created="
                                 + customer.getCreated() + " | Updated=" + customer.getUpdated() + "\n\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(CustomerDAOImpl.class.getName())
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
    public boolean delete(Integer customerId)
    {

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call deleteCustomer(?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setInt(1, customerId);

            // execute and store rows affected
            int deleted = cs.executeUpdate();

            // check rows affected
            if (deleted > 0)
            {
                // delete successful
                System.out.println(deleted + " row(s) dropped.");
                System.out.println("Customer object: ID=" + customerId + ", delete successful.\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(CustomerDAOImpl.class.getName())
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
            String SQL = "{call doesCustomerTableHaveData()}";

            cs = conn.prepareCall(SQL);
            rs = cs.executeQuery();

            if (rs.next() == true)
            {
                // read data from ResultSet rs into a City object
                int result = rs.getInt(1);

                if (result > 0)
                {
                    System.out.println("customerTableHasData()=TRUE\n");

                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDAOImpl.class.getName())
                  .log(Level.SEVERE, null, ex);
        }

        System.out.println("customerTableHasData()=FALSE\n");

        return false;
    }

    @Override
    public Customer read(Integer customerId)
    {

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call readCustomer(?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setInt(1, customerId);

            // execute the query
            rs = cs.executeQuery();

            // position cursor on first row (there can only be one row)
            // if valid data exists, read data from ResultSet rs
            if (rs.next() == true)
            {
                // read data from ResultSet rs into a Customer object
                Customer temp = new Customer(rs.getInt(1),
                                             rs.getString(2),
                                             rs.getInt(3),
                                             rs.getByte(4),
                                             new Time(rs.getTimestamp(5).toInstant(),
                                                      FormatPattern.LOCAL_DATETIME).toString(),
                                             rs.getString(6),
                                             new Time(rs.getTimestamp(7).toInstant(),
                                                      FormatPattern.LOCAL_DATETIME).toString(),
                                             rs.getString(8));

                // read successful
                System.out.println(rs.getRow() + " row(s) read.");
                System.out.println("Customer object: ID=" + customerId + ", read successfully.\n");

                // return Customer object
                return temp;
            }
        } catch (SQLException e) {
            Logger.getLogger(CustomerDAOImpl.class.getName())
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
    public ArrayList<Customer> readAll()
    {

        // declarations
        Connection          conn      = Database.getInstance()
                                                .getConnection();
        CallableStatement   cs        = null;
        ArrayList<Customer> customers = new ArrayList<>();

        try {

            // SQL call string
            String SQL = "{call readAllCustomers()}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            // execute the query
            rs = cs.executeQuery();

            // store how many rows are returned
            int rows = 0;

            // build customers ArrayList from ResultSet rs
            while (rs.next()) {

                // read data from ResultSet rs into a Customer object
                Customer temp = new Customer(rs.getInt(1),
                                             rs.getString(2),
                                             rs.getInt(3),
                                             rs.getByte(4),
                                             new Time(rs.getTimestamp(5).toInstant(),
                                                      FormatPattern.LOCAL_DATETIME).toString(),
                                             rs.getString(6),
                                             new Time(rs.getTimestamp(7).toInstant(),
                                                      FormatPattern.LOCAL_DATETIME).toString(),
                                             rs.getString(8));

                // add Customer object to customers ArrayList
                customers.add(temp);

                rows++;
            }

            // check row count
            if (rows > 0)
            {
                // read successful
                System.out.println(rows + " row(s) read.");
                System.out.println("ArrayList of size: " + customers.size() + " created, read successfully.\n");

                // return customers ArrayList
                return customers;
            }
        } catch (SQLException e) {
            Logger.getLogger(CustomerDAOImpl.class.getName())
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
    public boolean update(Object obj)
    {
        Customer customer = (Customer) obj;

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call updateCustomer(?,?,?,?,?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setInt(1, customer.getCustomerId());
            cs.setString(2, customer.getCustomerName());
            cs.setInt(3, customer.getAddressId());
            cs.setByte(4, customer.getActive());
            cs.setString(5, customer.getUpdatedBy());

            // execute and store rows affected
            int updated = cs.executeUpdate();

            // check rows affected
            if (updated > 0)
            {
                // update successful
                System.out.println(updated + " row(s) affected.");
                System.out.println("Customer object: ID=" + customer.getCustomerId() + ", update successful.\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(CustomerDAOImpl.class.getName())
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
 * @(#)CustomerDAOImpl.java 2021.05.20 at 10:53:53 EDT
 * EOF
 */
