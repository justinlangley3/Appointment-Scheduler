/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)UserDAOImpl.java   2021.05.19 at 06:09:09 EDT
 * Version: 1.0.0
 */



package Model.DAO.Impl;

import java.security.NoSuchAlgorithmException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import java.time.Instant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.DAO.DAO;

import Model.User;

import Util.DB.Database;

import Util.Date.Time;

import Util.Security.Crypto;

//~--- classes ----------------------------------------------------------------

/*
Methods:
  create(User user),                            returns Boolean
  read(String username),                        returns User
  readHashData(Integer userId),                 returns List(hash, salt)
  update(User user),                            returns Boolean
  delete(User currentUser, char[] password),    returns Boolean
*/

public class UserDAOImpl
        implements DAO
{
    private ResultSet rs = null;

    //~--- constructors -------------------------------------------------------

    public UserDAOImpl() {}

    //~--- methods ------------------------------------------------------------

    @Override
    public boolean create(Object obj)
    {
        User user = (User) obj;

        // declarations
        CallableStatement cs   = null;
        Connection        conn = Database.getInstance()
                                         .getConnection();

        // unique username check
        if (read(user.getUsername()) != null)
        {
            System.out.println("Username already in use.");

            return false;
        }

        try {

            // SQL call string
            String SQL = "{call createUser(?, ?, ?, ?, ?, ?, ?, ?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setString(1, user.getUsername());
            cs.setBytes(2, user.getPassword());
            cs.setBytes(3, user.getSalt());
            cs.setString(4, user.getCreatedBy());
            cs.setString(5, user.getUpdatedBy());
            cs.registerOutParameter(6, java.sql.Types.INTEGER);
            cs.registerOutParameter(7, java.sql.Types.TIMESTAMP);
            cs.registerOutParameter(8, java.sql.Types.TIMESTAMP);
            cs.execute();

            // check first OUT parameter
            cs.getInt(6);

            // check if OUT parameter was null
            if (cs.wasNull())
            {
                // this should never happen . . .
                throw new SQLException();
            }
            else
            {
                // update our object from our OUT parameters
                user.setUserId(cs.getInt(6));

                Instant timestamp = cs.getTimestamp(7)
                                      .toInstant();

                user.setCreated(new Time(Time.FormatPattern.TIMESTAMP).ofInstant(timestamp)
                                                                      .toString());

                timestamp = cs.getTimestamp(8)
                              .toInstant();

                user.setUpdated(new Time(Time.FormatPattern.TIMESTAMP).ofInstant(timestamp)
                                                                      .toString());

                // create success
                System.out.println("1 row(s) inserted.");
                System.out.println("User object: ID=" + user.getUserId() + ", create successful.");
                System.out.print("\nObject updated with DB data\nID=" + user.getUserId() + " | Created="
                                 + user.getCreated() + " | Updated=" + user.getUpdated() + "\n\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDAOImpl.class.getName())
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
    public boolean delete(Integer userId)
    {
        return false;
    }

    // This method works for a user deleting their own acccount information only
    public boolean delete(User currentUser, char[] password) throws SQLIntegrityConstraintViolationException
    {

        // declarations
        Connection        conn       = Database.getInstance()
                                               .getConnection();
        CallableStatement cs         = null;
        ArrayList<byte[]> data       = readHashData(currentUser.getUsername());
        Boolean           isExpected = false;

        try {

            // check password hash against expected value
            isExpected = Crypto.Hash.isHashValid(password, data.get(0), data.get(1));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserDAOImpl.class.getName())
                  .log(Level.SEVERE, null, ex);
        }

        // clear sensitive data
        data.forEach((n) -> Arrays.fill(n, (byte) 0));
        data.clear();

        // ensure user entered correct password to confirm deletion
        if (!isExpected)
        {
            System.out.println("Error. Unable to delete user, ID=" + currentUser.getUserId()
                               + " -- Password does not match.\n");

            return false;
        }
        else
        {
            try {

                // SQL call string
                String SQL = "{call deleteUser(?, ?)}";

                // prepare stored procedure
                cs = conn.prepareCall(SQL);

                cs.setInt(1, currentUser.getUserId());
                cs.setString(2, Arrays.toString(password));

                // execute and store rows affected
                int deleted = cs.executeUpdate();

                if (deleted == 0)
                {
                    throw new SQLIntegrityConstraintViolationException();
                }

                // check rows affected
                if (deleted > 0)
                {
                    // delete successful
                    System.out.println(deleted + " row(s) dropped.");
                    System.out.println("User object: ID=" + currentUser.getUserId() + ", delete successful.\n");

                    return true;
                }
            } catch (SQLException e) {
                Logger.getLogger(UserDAOImpl.class.getName())
                      .log(Level.SEVERE, null, e);
            } finally {

                // close the connection upon returning
                Database.getInstance()
                        .closeResultSet(rs);
                Database.getInstance()
                        .closeStatement(cs);
            }
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
            String SQL = "{call doesUserTableHaveData()}";

            cs = conn.prepareCall(SQL);
            rs = cs.executeQuery();

            if (rs.next() == true)
            {
                // read data from ResultSet rs into a City object
                int result = rs.getInt(1);

                if (result > 0)
                {
                    System.out.println("userTableHasData()=TRUE\n");

                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAOImpl.class.getName())
                  .log(Level.SEVERE, null, ex);
        }

        System.out.println("userTableHasData()=FALSE\n");

        return false;
    }

    public boolean doesUserExist(String username)
    {

        // declarations
        CallableStatement cs   = null;
        Connection        conn = Database.getInstance()
                                         .getConnection();

        System.out.println("Checking if user exists...");

        // This query is done on the username field.
        // The database logic is implemented so that usernames are unique.
        // Therefore, we can lookup a user by name and return a unique result.
        // This is necessary because a user's id is not known at login.
        try {

            // SQL call string
            String SQL = "{call doesUserExist(?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setString(1, username);

            rs = cs.executeQuery();

            if (rs.next() == true)
            {
                // read data from ResultSet rs into User object
                String uname = rs.getString(1);

                System.out.println(rs.getRow() + " row(s) read.");
                System.out.println("User: " + uname + ", exists.\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {

            // close the connection upon returning
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        // read failed
        System.out.println("0 row(s) read. User does not exist.\n");

        return false;
    }

    @Override
    public User read(Integer userId)
    {
        return null;
    }

    public User read(String username)
    {

        // declarations
        CallableStatement cs   = null;
        Connection        conn = Database.getInstance()
                                         .getConnection();

        System.out.println("Fetching user...");

        // This query is done on the username field.
        // The database logic is implemented so that usernames are unique.
        // Therefore, we can lookup a user by name and return a unique result.
        // This is necessary because a user's id is not known at login.
        try {

            // SQL call string
            String SQL = "{call readUser(?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setString(1, username);

            rs = cs.executeQuery();

            if (rs.next() == true)
            {
                // read data from ResultSet rs into User object
                User temp = new User(rs.getInt(1),
                                     rs.getString(2),
                                     rs.getBytes(3),
                                     rs.getBytes(4),
                                     rs.getByte(5),
                                     rs.getString(6),
                                     rs.getString(7),
                                     rs.getString(8),
                                     rs.getString(9));

                System.out.println(rs.getRow() + " row(s) read.");
                System.out.println("User object: ID=" + temp.getUserId() + ", read successfully\n");

                // return User
                return temp;
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {

            // close the connection upon returning
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        // read failed
        System.out.println("0 row(s) read. User does not yet exist.\n");

        return null;
    }

    @Override
    public ArrayList<?> readAll()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // when calling this method, a null check should be used to check if user is inactive
    public ArrayList<byte[]> readHashData(String username)
    {

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        System.out.println("Reading password hash data...");

        try {

            // SQL call string
            String SQL = "{call readUserPasswordHash(?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setString(1, username);

            // execute the query
            rs = cs.executeQuery();

            // position cursor on first row (there can only be one row)
            // if valid data exists, read data from ResultSet rs
            if (rs.next() == true)
            {
                // build return ArrayList
                ArrayList<byte[]> data = new ArrayList<>(Arrays.asList(rs.getBytes(1), rs.getBytes(2)));

                // read successful
                System.out.println(rs.getRow() + " row(s) read.");
                System.out.println("User data read successfully.\n");

                return data;
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDAOImpl.class.getName())
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
        System.out.println("Read failed.\n");

        return null;
    }

    @Override
    public boolean update(Object obj)
    {
        User user = (User) obj;

        // declarations
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        // unique username check
        if (read(user.getUsername()) != null)
        {
            System.out.println("Username already in use.");

            return false;
        }

        try {

            // SQL call string
            String SQL = "{call updateUser(?, ?, ?, ?, ?, ?)}";

            // prepare stored procedure
            cs = conn.prepareCall(SQL);

            cs.setInt(1, user.getUserId());
            cs.setString(2, user.getUsername());
            cs.setBytes(3, user.getPassword());
            cs.setBytes(4, user.getSalt());
            cs.setByte(5, user.getActive());
            cs.setString(6, user.getUpdatedBy());

            // execute and store rows affected
            int updated = cs.executeUpdate();

            // check rows affected
            if (updated > 0)
            {
                // update successful
                System.out.println(updated + " row(s) affected.");
                System.out.println("User object: ID=" + user.getUserId() + ", update successful.\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDAOImpl.class.getName())
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
 * @(#)UserDAOImpl.java 2021.05.19 at 06:09:09 EDT
 * EOF
 */
