/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)CountryDAOImpl.java   2021.05.20 at 10:05:12 EDT
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

import Model.Country;

import Model.DAO.DAO;

import Util.DB.Database;

import Util.Date.Time;
import Util.Date.Time.FormatPattern;

//~--- classes ----------------------------------------------------------------

/*
Methods:
  create(Country country),      returns Boolean
  read(Integer countryId),      returns City
  readAll(),                    returns ArrayList<City>
  update(Country country),      returns Boolean
  delete(Integer countryId),    returns Boolean
*/

public class CountryDAOImpl
        implements DAO
{
    private ResultSet rs = null;

    //~--- constructors -------------------------------------------------------

    public CountryDAOImpl() {}

    //~--- methods ------------------------------------------------------------

    @Override
    public boolean create(Object obj)
    {
        Country country = (Country) obj;

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call createCountry(?, ?, ?, ?, ?, ?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setString(1, country.getCountryName());
            cs.setString(2, country.getCreatedBy());
            cs.setString(3, country.getUpdatedBy());
            cs.registerOutParameter(4, java.sql.Types.INTEGER);
            cs.registerOutParameter(5, java.sql.Types.TIMESTAMP);
            cs.registerOutParameter(6, java.sql.Types.TIMESTAMP);
            cs.execute();

            // check first OUT parameter
            cs.getInt(4);

            // check if OUT parameter was null
            if (cs.wasNull())
            {
                // this should never happen . . .
                throw new SQLException();
            }
            else
            {
                // update our object from our OUT parameters
                country.setCountryId(cs.getInt(4));

                Instant timestamp = cs.getTimestamp(5)
                                      .toInstant();

                country.setCreated(new Time(FormatPattern.TIMESTAMP).ofInstant(timestamp)
                                                                    .toString());

                timestamp = cs.getTimestamp(6)
                              .toInstant();

                country.setCreated(new Time(FormatPattern.TIMESTAMP).ofInstant(timestamp)
                                                                    .toString());

                // create success
                System.out.println("1 row(s) inserted.");
                System.out.println("Country object: ID=" + country.getCountryId() + ", create successful.");
                System.out.print("Object updated with DB data\nID=" + country.getCountryId() + " | Created="
                                 + country.getCreated() + " | Updated=" + country.getUpdated() + "\n\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(CountryDAOImpl.class.getName())
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
    public boolean delete(Integer countryId)
    {

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call deleteCountry(?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setInt(1, countryId);

            // execute and store rows affected
            int deleted = cs.executeUpdate();

            if (deleted > 0)
            {
                // delete successful
                System.out.println(deleted + " row(s) dropped.");
                System.out.println("Country object: ID=" + countryId + ", delete successful.\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(CountryDAOImpl.class.getName())
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
            String SQL = "{call doesCountryTableHaveData()}";

            cs = conn.prepareCall(SQL);
            rs = cs.executeQuery();

            if (rs.next() == true)
            {
                // read data from ResultSet rs into a City object
                int result = rs.getInt(1);

                if (result > 0)
                {
                    System.out.println("countryTableHasData()=TRUE\n");

                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CountryDAOImpl.class.getName())
                  .log(Level.SEVERE, null, ex);
        }

        System.out.println("countryTableHasData()=FALSE\n");

        return false;
    }

    @Override
    public Country read(Integer countryId)
    {

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call readCountry(?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setInt(1, countryId);

            // execute the query
            rs = cs.executeQuery();

            // position cursor on first row (there can only be one row)
            // if valid data exists, read data from ResultSet rs
            if (rs.next() == true)
            {
                // read data from ResultSet rs into a Country object
                Country temp = new Country(rs.getInt(1),
                                           rs.getString(2),
                                           new Time(rs.getTimestamp(3).toInstant(),
                                                    FormatPattern.LOCAL_DATETIME).toString(),
                                           rs.getString(4),
                                           new Time(rs.getTimestamp(5).toInstant(),
                                                    FormatPattern.LOCAL_DATETIME).toString(),
                                           rs.getString(6));

                // read successful
                System.out.println(rs.getRow() + " row(s) read.");
                System.out.println("Country object: ID=" + countryId + ", read successfully.\n");

                // return Country object
                return temp;
            }
        } catch (SQLException e) {
            Logger.getLogger(CountryDAOImpl.class.getName())
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
    public ArrayList<Country> readAll()
    {

        // declarations
        Connection         conn      = Database.getInstance()
                                               .getConnection();
        CallableStatement  cs        = null;
        ArrayList<Country> countries = new ArrayList<>();

        try {

            // SQL call string
            String SQL = "{call readAllCountries()}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            // execute the query
            rs = cs.executeQuery();

            // store how many rows are returned
            int rows = 0;

            // build countries ArrayList from ResultSet rs
            while (rs.next()) {

                // read data from ResultSet rs into a Country object
                Country temp = new Country(rs.getInt(1),
                                           rs.getString(2),
                                           new Time(rs.getTimestamp(3).toInstant(),
                                                    FormatPattern.LOCAL_DATETIME).toString(),
                                           rs.getString(4),
                                           new Time(rs.getTimestamp(5).toInstant(),
                                                    FormatPattern.LOCAL_DATETIME).toString(),
                                           rs.getString(6));

                // add Country object to ArrayList
                countries.add(temp);

                rows++;
            }

            // check row count returned from query, cursor is now positioned at the last row
            if (rows > 0)
            {
                // read successful
                System.out.println(rows + " row(s) read.");
                System.out.println("ArrayList of size: " + countries.size() + " created, read successfully.\n");

                // return the ArrayList
                return countries;
            }
        } catch (SQLException e) {
            Logger.getLogger(CountryDAOImpl.class.getName())
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
        Country country = (Country) obj;

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call updateCountry(?, ?, ?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setInt(1, country.getCountryId());
            cs.setString(2, country.getCountryName());
            cs.setString(3, country.getUpdatedBy());

            // execute and store rows affected
            int updated = cs.executeUpdate();

            if (updated > 0)
            {
                // update successful
                System.out.println(updated + " row(s) affected.");
                System.out.println("Country object: ID=" + country.getCountryId() + ", update successful.\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(CountryDAOImpl.class.getName())
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
 * @(#)CountryDAOImpl.java 2021.05.20 at 10:05:12 EDT
 * EOF
 */
