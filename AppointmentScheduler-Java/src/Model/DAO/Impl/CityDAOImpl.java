/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)CityDAOImpl.java   2021.05.20 at 10:55:59 EDT
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

import Model.City;

import Model.DAO.DAO;

import Util.DB.Database;

import Util.Date.Time;
import Util.Date.Time.FormatPattern;

//~--- classes ----------------------------------------------------------------

/*
Methods:
  create(City city),        returns Boolean
  read(Integer cityId),     returns City
  readAll(),                returns ArrayList<City>
  update(City city),        returns Boolean
  delete(Integer cityId),   returns Boolean
*/

public class CityDAOImpl
        implements DAO
{
    private ResultSet rs = null;

    //~--- constructors -------------------------------------------------------

    public CityDAOImpl() {}

    //~--- methods ------------------------------------------------------------

    @Override
    public boolean create(Object obj)
    {
        City city = (City) obj;

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call createCity(?, ?, ?, ?, ?, ?, ?)}";

            // prepare the stored procedure
            cs = conn.prepareCall(SQL);

            cs.setString(1, city.getCityName());
            cs.setInt(2, city.getCountryId());
            cs.setString(3, city.getCreatedBy());
            cs.setString(4, city.getUpdatedBy());
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
                city.setCityId(cs.getInt(5));

                Instant timestamp = cs.getTimestamp(6)
                                      .toInstant();

                city.setCreated(new Time(FormatPattern.TIMESTAMP).ofInstant(timestamp)
                                                                 .toString());

                timestamp = cs.getTimestamp(7)
                              .toInstant();

                city.setUpdated(new Time(FormatPattern.TIMESTAMP).ofInstant(timestamp)
                                                                 .toString());

                // create success
                System.out.println("1 row(s) inserted.");
                System.out.println("City object: ID=" + city.getCityId() + ", create successful.");
                System.out.print("Object updated with DB data\nID=" + city.getCityId() + " | Created="
                                 + city.getCreated() + " | Updated=" + city.getUpdated() + "\n\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(CityDAOImpl.class.getName())
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
    public boolean delete(Integer cityId)
    {

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call deleteCity(?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setInt(1, cityId);

            // execute and store rows affected
            int deleted = cs.executeUpdate();

            if (deleted > 0)
            {
                // delete successful
                System.out.println(deleted + " row(s) dropped.");
                System.out.println("City object: ID=" + cityId + ", delete successful.\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(CityDAOImpl.class.getName())
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

    public boolean doesTableHaveData()
    {
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {
            String SQL = "{call doesCityTableHaveData()}";

            cs = conn.prepareCall(SQL);
            rs = cs.executeQuery();

            if (rs.next() == true)
            {
                // read data from ResultSet rs into a City object
                int result = rs.getInt(1);

                if (result > 0)
                {
                    System.out.println("cityTableHasData()=TRUE\n");

                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CityDAOImpl.class.getName())
                  .log(Level.SEVERE, null, ex);
        }

        System.out.println("cityTableHasData()=FALSE\n");

        return false;
    }

    @Override
    public City read(Integer cityId)
    {

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call readCity(?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setInt(1, cityId);

            // execute the query
            rs = cs.executeQuery();

            // position cursor on first row (there can only be one row)
            // if valid data exists, read data from ResultSet rs
            if (rs.next() == true)
            {
                // read data from ResultSet rs into a City object
                City temp = new City(rs.getInt(1),
                                     rs.getString(2),
                                     rs.getInt(3),
                                     new Time(rs.getTimestamp(4).toInstant(), FormatPattern.LOCAL_DATETIME).toString(),
                                     rs.getString(5),
                                     new Time(rs.getTimestamp(6).toInstant(), FormatPattern.LOCAL_DATETIME).toString(),
                                     rs.getString(7));

                // read successful
                System.out.println(rs.getRow() + " row(s) read.");
                System.out.println("City object: ID=" + cityId + ", read successfully.\n");

                // return City object
                return temp;
            }
        } catch (SQLException e) {
            Logger.getLogger(CityDAOImpl.class.getName())
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
    public ArrayList<City> readAll()
    {

        // declarations
        Connection        conn   = Database.getInstance()
                                           .getConnection();
        CallableStatement cs     = null;
        ArrayList<City>   cities = new ArrayList<>();

        try {

            // SQL call string
            String SQL = "{call readAllCities()}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            // exceute the query
            rs = cs.executeQuery();

            // store how many rows are returned
            int rows = 0;

            // build cities ArrayList from ResultSet rs
            while (rs.next()) {

                // read data from ResultSet rs into a City object
                City temp = new City(rs.getInt(1),
                                     rs.getString(2),
                                     rs.getInt(3),
                                     new Time(rs.getTimestamp(4).toInstant(), FormatPattern.LOCAL_DATETIME).toString(),
                                     rs.getString(5),
                                     new Time(rs.getTimestamp(6).toInstant(), FormatPattern.LOCAL_DATETIME).toString(),
                                     rs.getString(7));

                // add City object to cities ArrayList
                cities.add(temp);

                rows++;
            }

            // check row count returned from query, cursor is now positioned at the last row
            if (rows > 0)
            {
                // read successful
                System.out.println(rows + " row(s) read.");
                System.out.println("ArrayList of size: " + cities.size() + " created, read successfully.\n");

                // return the ArrayList
                return cities;
            }
        } catch (SQLException e) {
            Logger.getLogger(CityDAOImpl.class.getName())
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
        City city = (City) obj;

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {

            // SQL call string
            String SQL = "{call updateCity(?, ?, ?, ?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setInt(1, city.getCityId());
            cs.setString(2, city.getCityName());
            cs.setInt(3, city.getCountryId());
            cs.setString(4, city.getUpdatedBy());

            // execute and store rows affected
            int updated = cs.executeUpdate();

            if (updated > 0)
            {
                // update successful
                System.out.println(updated + " row(s) affected.");
                System.out.println("City object: ID=" + city.getCityId() + ", update successful.\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(CityDAOImpl.class.getName())
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
 * @(#)CityDAOImpl.java 2021.05.20 at 10:55:59 EDT
 * EOF
 */
