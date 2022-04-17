/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)AppointmentDAOImpl.java   2021.05.21 at 12:04:11 EDT
 * Version: 1.0.0
 */



package Model.DAO.Impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.Instant;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.Appointment;

import Model.DAO.DAO;

import Util.DB.Database;

import Util.Date.Time;
import Util.Date.Time.FormatPattern;

//~--- classes ----------------------------------------------------------------

/*
Methods:
  create(Appointment appointment),      returns Boolean
  read(Integer appointmentId),          returns Appointment
  readAllCurrent(),                     returns ArrayList<Appointment>
  readAllPast(),                        returns ArrayList<Appointment>
  update(Appointment appointment),      returns Boolean
  delete(Integer appointmentId),        returns Boolean
*/

public class AppointmentDAOImpl
        implements DAO
{
    private ResultSet rs = null;

    //~--- constructors -------------------------------------------------------

    public AppointmentDAOImpl() {}

    //~--- methods ------------------------------------------------------------

    @Override
    public boolean create(Object obj)
    {
        Appointment       appointment = (Appointment) obj;
        CallableStatement cs          = null;
        Connection        conn        = Database.getInstance()
                                                .getConnection();

        try {
            String SQL = "{call createAppointment(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

            cs = conn.prepareCall(SQL);

            cs.setInt(1, appointment.getCustomerId());
            cs.setInt(2, appointment.getUserId());
            cs.setString(3, appointment.getTitle());
            cs.setString(4, appointment.getSummary());
            cs.setString(5, appointment.getLocation());
            cs.setString(6, appointment.getContact());
            cs.setString(7, appointment.getCategory());
            cs.setString(8, appointment.getUrl());
            cs.setString(9, new Time(appointment.getStart()).setFormatPattern(Time.FormatPattern.LOCAL_DATETIME)
                                                            .toUTC()
                                                            .toString());
            cs.setString(10,
                         new Time(appointment.getEnd()).setFormatPattern(Time.FormatPattern.LOCAL_DATETIME)
                                                       .toUTC()
                                                       .toString());
            cs.setString(11, appointment.getCreatedBy());
            cs.setString(12, appointment.getUpdatedBy());
            cs.registerOutParameter(13, java.sql.Types.INTEGER);
            cs.registerOutParameter(14, java.sql.Types.TIMESTAMP);
            cs.registerOutParameter(15, java.sql.Types.TIMESTAMP);
            cs.execute();
            cs.getInt(13);

            if (cs.wasNull())
            {
                throw new SQLException();
            }
            else
            {
                appointment.setAppointmentId(cs.getInt(13));

                // get the "created" timestamp from its OUT parameter as an instant
                Instant timestamp = cs.getTimestamp(14)
                                      .toInstant();

                // updated the objects "created" field with the timestamp using method chaining
                appointment.setCreated(new Time(FormatPattern.TIMESTAMP).ofInstant(timestamp)
                                                                        .toString());

                // updated the timestamp to match the "updated" OUT parameter
                timestamp = cs.getTimestamp(15)
                              .toInstant();

                // update the objects "updated" field with the updated timestamp
                appointment.setUpdated(new Time(FormatPattern.TIMESTAMP).ofInstant(timestamp)
                                                                        .toString());
                System.out.println("1 row(s) inserted.");
                System.out.println("Appointment object: ID=" + appointment.getAppointmentId() + ", create successful.");
                System.out.print("Object updated with DB data\nID=" + appointment.getAppointmentId() + " | Created="
                                 + appointment.getCreated() + " | Updated=" + appointment.getUpdated() + "\n\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(AppointmentDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        System.out.println("0 row(s) inserted.");

        return false;
    }

    @Override
    public boolean delete(Integer appointmentId)
    {
        CallableStatement cs   = null;
        Connection        conn = Database.getInstance()
                                         .getConnection();

        try {
            String SQL = "{call deleteAppointment(?)}";

            cs = conn.prepareCall(SQL);

            cs.setInt(1, appointmentId);

            int deleted = cs.executeUpdate();

            if (deleted > 0)
            {
                System.out.println(deleted + " row(s) dropped.");
                System.out.println("Appointment object: ID=" + appointmentId + ", delete successful.\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(AppointmentDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

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
            String SQL = "{call doesAppointmentTableHaveData()}";

            cs = conn.prepareCall(SQL);
            rs = cs.executeQuery();

            if (rs.next() == true)
            {
                // read data from ResultSet rs into a City object
                int result = rs.getInt(1);

                if (result > 0)
                {
                    System.out.println("appointmentTableHasData()=TRUE\n");

                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentDAOImpl.class.getName())
                  .log(Level.SEVERE, null, ex);
        }

        System.out.println("appointmentTableHasData()=FALSE\n");

        return false;
    }

    public boolean doesTableHaveDataForDate(String date)
    {
        Connection        conn = Database.getInstance()
                                         .getConnection();
        CallableStatement cs   = null;

        try {
            String SQL = "{call doesAppointmentTableHaveDataForDate(?)}";

            cs = conn.prepareCall(SQL);

            cs.setString(1, date);

            rs = cs.executeQuery();

            if (rs.next() == true)
            {
                // read data from ResultSet rs into a City object
                int result = rs.getInt(1);

                if (result > 0)
                {
                    System.out.println("appointmentTableHasDataForDate(" + date + ")=TRUE\n");

                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentDAOImpl.class.getName())
                  .log(Level.SEVERE, null, ex);
        }

        System.out.println("appointmentTableHasData()=FALSE\n");

        return false;
    }

    @Override
    public Appointment read(Integer appointmentId)
    {
        CallableStatement cs   = null;
        Connection        conn = Database.getInstance()
                                         .getConnection();

        try {
            String SQL = "{call readAppointment(?)}";

            cs = conn.prepareCall(SQL);

            cs.setInt(1, appointmentId);

            rs = cs.executeQuery();

            if (rs.next() == true)
            {
                Appointment temp = new Appointment(rs.getInt(1),
                                                   rs.getInt(2),
                                                   rs.getInt(3),
                                                   rs.getString(4),
                                                   rs.getString(5),
                                                   rs.getString(6),
                                                   rs.getString(7),
                                                   rs.getString(8),
                                                   rs.getString(9),
                                                   new Time(rs.getTimestamp(10).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   new Time(rs.getTimestamp(11).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   new Time(rs.getTimestamp(12).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   rs.getString(13),
                                                   new Time(rs.getTimestamp(14).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   rs.getString(15));

                System.out.println(rs.getRow() + " row(s) read.");
                System.out.println("Appointment object: ID=" + appointmentId + ", read successfully.\n");

                return temp;
            }
        } catch (SQLException e) {
            Logger.getLogger(AppointmentDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        System.out.println("0 row(s) read.");

        return null;
    }

    @Override
    public ArrayList<?> readAll()
    {

        // may or may not implement this as of yet.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ArrayList<String[]> readAllByUser()
    {
        ArrayList<String[]> data = new ArrayList<>();
        CallableStatement   cs   = null;
        Connection          conn = Database.getInstance()
                                           .getConnection();

        try {
            String SQL = "{call report_appointments_by_user()}";

            cs = conn.prepareCall(SQL);
            rs = cs.executeQuery();

            int rows = 0;

            while (rs.next()) {
                String[] temp = {
                    String.valueOf(rs.getInt(1)), String.valueOf(rs.getInt(2)), String.valueOf(rs.getInt(3)),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8),
                    rs.getString(9), new Time(rs.getTimestamp(10).toInstant(),
                                              FormatPattern.LOCAL_DATETIME).toString(),
                    new Time(rs.getTimestamp(11).toInstant(),
                             FormatPattern.LOCAL_DATETIME).toString(),
                    new Time(rs.getTimestamp(12).toInstant(),
                             FormatPattern.LOCAL_DATETIME).toString(), rs.getString(13),
                    new Time(rs.getTimestamp(14).toInstant(),
                             FormatPattern.LOCAL_DATETIME).toString(), rs.getString(15)
                };

                data.add(temp);

                rows++;
            }

            if (rows > 0)
            {
                System.out.println(rows + " row(s) read.");
                System.out.println("ArrayList of size: " + data.size() + " created, read successfully.\n");

                return data;
            }
        } catch (SQLException e) {
            Logger.getLogger(AppointmentDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        System.out.println("0 row(s) read.");

        return null;
    }

    public ArrayList<Appointment> readAllCurrent()
    {
        ArrayList<Appointment> appointments = new ArrayList<>();
        CallableStatement      cs           = null;
        Connection             conn         = Database.getInstance()
                                                      .getConnection();

        try {
            String SQL = "{call readAllAppointments_Current()}";

            cs = conn.prepareCall(SQL);
            rs = cs.executeQuery();

            int rows = 0;

            while (rs.next()) {
                Appointment temp = new Appointment(rs.getInt(1),
                                                   rs.getInt(2),
                                                   rs.getInt(3),
                                                   rs.getString(4),
                                                   rs.getString(5),
                                                   rs.getString(6),
                                                   rs.getString(7),
                                                   rs.getString(8),
                                                   rs.getString(9),
                                                   new Time(rs.getTimestamp(10).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   new Time(rs.getTimestamp(11).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   new Time(rs.getTimestamp(12).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   rs.getString(13),
                                                   new Time(rs.getTimestamp(14).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   rs.getString(15));

                appointments.add(temp);

                rows++;
            }

            if (rows > 0)
            {
                System.out.println(rows + " row(s) read.");
                System.out.println("ArrayList of size: " + appointments.size() + " created, read successfully.\n");

                return appointments;
            }
        } catch (SQLException e) {
            Logger.getLogger(AppointmentDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        System.out.println("0 row(s) read.");

        return null;
    }

    public ArrayList<Appointment> readAllPast()
    {
        ArrayList<Appointment> appointments = new ArrayList<>();
        CallableStatement      cs           = null;
        Connection             conn         = Database.getInstance()
                                                      .getConnection();

        try {
            String SQL = "{call readAllAppointments_Past()}";

            cs = conn.prepareCall(SQL);
            rs = cs.executeQuery();

            int rows = 0;

            while (rs.next()) {
                Appointment temp = new Appointment(rs.getInt(1),
                                                   rs.getInt(2),
                                                   rs.getInt(3),
                                                   rs.getString(4),
                                                   rs.getString(5),
                                                   rs.getString(6),
                                                   rs.getString(7),
                                                   rs.getString(8),
                                                   rs.getString(9),
                                                   new Time(rs.getTimestamp(10).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   new Time(rs.getTimestamp(11).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   new Time(rs.getTimestamp(12).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   rs.getString(13),
                                                   new Time(rs.getTimestamp(14).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   rs.getString(15));

                appointments.add(temp);

                rows++;
            }

            if (rows > 0)
            {
                System.out.println(rows + " row(s) read.");
                System.out.println("ArrayList of size: " + appointments.size() + " created, read successfully.\n");

                return appointments;
            }
        } catch (SQLException e) {
            Logger.getLogger(AppointmentDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        System.out.println("0 row(s) read.");

        return null;
    }

    public ArrayList<Appointment> readAppointmentFromDate(String datestring)
    {
        ArrayList<Appointment> appointments = new ArrayList<>();
        CallableStatement      cs           = null;
        Connection             conn         = Database.getInstance()
                                                      .getConnection();

        try {
            String SQL = "{call readAppointmentsFromDateSelection(?)}";

            cs = conn.prepareCall(SQL);

            cs.setString(1, datestring);

            rs = cs.executeQuery();

            int rows = 0;

            while (rs.next()) {
                Appointment temp = new Appointment(rs.getInt(1),
                                                   rs.getInt(2),
                                                   rs.getInt(3),
                                                   rs.getString(4),
                                                   rs.getString(5),
                                                   rs.getString(6),
                                                   rs.getString(7),
                                                   rs.getString(9),
                                                   rs.getString(8),
                                                   new Time(rs.getTimestamp(10).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   new Time(rs.getTimestamp(11).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   new Time(rs.getTimestamp(12).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   rs.getString(13),
                                                   new Time(rs.getTimestamp(14).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   rs.getString(15));

                appointments.add(temp);

                rows++;
            }

            if (rows > 0)
            {
                System.out.println(rows + " row(s) read.");
                System.out.println("ArrayList of size: " + appointments.size() + " created, read successfully.\n");

                return appointments;
            }
        } catch (SQLException e) {
            Logger.getLogger(AppointmentDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        System.out.println("0 row(s) read.");

        return null;
    }

    public ArrayList<Appointment> readByMonth(byte month, short year)
    {
        ArrayList<Appointment> appointments = new ArrayList<>();
        CallableStatement      cs           = null;
        Connection             conn         = Database.getInstance()
                                                      .getConnection();

        try {
            String SQL = "{call fetch_appointments_by_month(?, ?)}";

            cs = conn.prepareCall(SQL);

            cs.setByte(1, month);
            cs.setInt(2, year);

            rs = cs.executeQuery();

            int rows = 0;

            while (rs.next()) {
                Appointment temp = new Appointment(rs.getInt(1),
                                                   rs.getInt(2),
                                                   rs.getInt(3),
                                                   rs.getString(4),
                                                   rs.getString(5),
                                                   rs.getString(6),
                                                   rs.getString(7),
                                                   rs.getString(8),
                                                   rs.getString(9),
                                                   new Time(rs.getTimestamp(10).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   new Time(rs.getTimestamp(11).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   new Time(rs.getTimestamp(12).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   rs.getString(13),
                                                   new Time(rs.getTimestamp(14).toInstant(),
                                                            FormatPattern.LOCAL_DATETIME).toString(),
                                                   rs.getString(15));

                appointments.add(temp);

                rows++;
            }

            if (rows > 0)
            {
                System.out.println(rows + " row(s) read.");
                System.out.println("ArrayList of size: " + appointments.size() + " created, read successfully.\n");

                return appointments;
            }
        } catch (SQLException e) {
            Logger.getLogger(AppointmentDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        System.out.println("0 row(s) read.");

        return null;
    }

    public ArrayList<String[]> readByWeek(int week, int year)
    {
        ArrayList<String[]> data = new ArrayList<>();
        CallableStatement   cs   = null;
        Connection          conn = Database.getInstance()
                                           .getConnection();

        try {
            String SQL = "{call read_appointments_by_week(?, ?)}";

            cs = conn.prepareCall(SQL);

            cs.setByte(1, (byte) week);
            cs.setInt(2, year);

            rs = cs.executeQuery();

            int rows = 0;

            while (rs.next()) {
                String[] temp = {
                    String.valueOf(rs.getInt(1)), String.valueOf(rs.getInt(2)), String.valueOf(rs.getInt(3)),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8),
                    rs.getString(9), new Time(rs.getTimestamp(10).toInstant(),
                                              FormatPattern.LOCAL_DATETIME).toString(),
                    new Time(rs.getTimestamp(11).toInstant(),
                             FormatPattern.LOCAL_DATETIME).toString(),
                    new Time(rs.getTimestamp(12).toInstant(),
                             FormatPattern.LOCAL_DATETIME).toString(), rs.getString(13),
                    new Time(rs.getTimestamp(14).toInstant(),
                             FormatPattern.LOCAL_DATETIME).toString(), rs.getString(15)
                };

                data.add(temp);

                rows++;
            }

            if (rows > 0)
            {
                System.out.println(rows + " row(s) read.");
                System.out.println("ArrayList of size: " + data.size() + " created, read successfully.\n");

                return data;
            }
        } catch (SQLException e) {
            Logger.getLogger(AppointmentDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        System.out.println("0 row(s) read.");

        return null;
    }

    public ArrayList<String[]> readMonthlyByType()
    {
        ArrayList<String[]> data = new ArrayList<>();
        CallableStatement   cs   = null;
        Connection          conn = Database.getInstance()
                                           .getConnection();

        try {
            String SQL = "{call report_appointments_monthly_by_category()}";

            cs = conn.prepareCall(SQL);
            rs = cs.executeQuery();

            int rows = 0;

            while (rs.next()) {
                String[] temp = {
                    String.valueOf(rs.getInt(1)), String.valueOf(rs.getInt(2)), String.valueOf(rs.getInt(3)),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8),
                    rs.getString(9), new Time(rs.getTimestamp(10).toInstant(),
                                              FormatPattern.LOCAL_DATETIME).toString(),
                    new Time(rs.getTimestamp(11).toInstant(),
                             FormatPattern.LOCAL_DATETIME).toString(),
                    new Time(rs.getTimestamp(12).toInstant(),
                             FormatPattern.LOCAL_DATETIME).toString(), rs.getString(13),
                    new Time(rs.getTimestamp(14).toInstant(),
                             FormatPattern.LOCAL_DATETIME).toString(), rs.getString(15)
                };

                data.add(temp);

                rows++;
            }

            if (rows > 0)
            {
                System.out.println(rows + " row(s) read.");
                System.out.println("ArrayList of size: " + data.size() + " created, read successfully.\n");

                return data;
            }
        } catch (SQLException e) {
            Logger.getLogger(AppointmentDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        System.out.println("0 row(s) read.");

        return null;
    }

    @Override
    public boolean update(Object obj)
    {
        Appointment       appointment = (Appointment) obj;
        CallableStatement cs          = null;
        Connection        conn        = Database.getInstance()
                                                .getConnection();

        try {
            String SQL = "{call updateAppointment(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

            cs = conn.prepareCall(SQL);

            cs.setInt(1, appointment.getAppointmentId());
            cs.setInt(2, appointment.getCustomerId());
            cs.setInt(3, appointment.getUserId());
            cs.setString(4, appointment.getTitle());
            cs.setString(5, appointment.getSummary());
            cs.setString(6, appointment.getLocation());
            cs.setString(7, appointment.getContact());
            cs.setString(8, appointment.getCategory());
            cs.setString(9, appointment.getUrl());
            cs.setString(10,
                         new Time(appointment.getStart()).setFormatPattern(FormatPattern.LOCAL_DATETIME)
                                                         .setZone(ZoneId.systemDefault())
                                                         .update()
                                                         .toUTC()
                                                         .toString());
            cs.setString(11,
                         new Time(appointment.getEnd()).setFormatPattern(FormatPattern.LOCAL_DATETIME)
                                                       .setZone(ZoneId.systemDefault())
                                                       .update()
                                                       .toUTC()
                                                       .toString());
            cs.setString(12, appointment.getUpdatedBy());

            int updated = cs.executeUpdate();

            if (updated != 0)
            {
                System.out.println(updated + " row(s) affected.");
                System.out.println("Appointment object: ID=" + appointment.getAppointmentId()
                                   + ", update successful.\n");

                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(AppointmentDAOImpl.class.getName())
                  .log(Level.SEVERE, null, e);
        } finally {
            Database.getInstance()
                    .closeResultSet(rs);
            Database.getInstance()
                    .closeStatement(cs);
        }

        System.out.println("0 row(s) affected.");

        return false;
    }
}

/*
 * @(#)AppointmentDAOImpl.java 2021.05.21 at 12:04:11 EDT
 * EOF
 */
