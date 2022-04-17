/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Time.java   2021.05.21 at 01:32:23 EDT
 * Version: 1.0.0
 */



package Util.Date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

//~--- classes ----------------------------------------------------------------

/*
* This is a utility class for working with datetimes.
* It also provides further compatibility when working with calendars.
* It is designed to be more powerful than Java built-ins and
* makes working with calendars, dates, and times easier.
*
* Added capabilities:
*   - Ability to access temporal fields when and where they are needed
*   - Incrementing/Decrementing temporal fields on the fly (for iterating)
*   - Chainable methods when working with Time objects
*   - Configuration of a DateTimeFormatter for displaying datetimes to the user
*   - Conversions to/from UTC
*   - toString() override for representing datetimes as a string (configurable)
*
* Much of this class is designed so that Time objects can "hook-in" to calendar
* objects making data manipulations simpler and easier
*/

public final class Time
{
    private final String      LOCAL_DATE           = "yyyy-MM-dd";
    private final String      LOCAL_DATETIME       = "yyyy-MM-dd HH:mm:ss";
    private final String      LOCAL_DATETIME_FULL  = "yyyy-MM-dd HH:mm:ss.nnn";
    private final String      LOCAL_DATETIME_SHORT = "yyyy-MM-dd HH:mm";
    private final String      TIMESTAMP            = "yyyy-MM-dd HH:mm:ss.nnn X";
    private final String      TIME_ONLY            = "HH:mm";
    private final String      ZONED_DATETIME       = "yyyy-MM-dd HH:mm:ss X";
    private final String      ZONED_DATETIME_FULL  = "yyyy-MM-dd HH:mm:ss.nnn X";
    private DateTimeFormatter FORMATTER;
    private ZoneId            zone;
    private ZonedDateTime     datetime;
    private int               day;
    private int               hours;
    private int               micros;
    private int               minutes;
    private int               month;
    private int               nano;
    private int               seconds;
    private int               week;
    private int               year;

    //~--- constant enums -----------------------------------------------------

    public enum FormatPattern {
        LOCAL_DATE, LOCAL_DATETIME, LOCAL_DATETIME_SHORT, LOCAL_DATETIME_FULL, ZONED_DATETIME, ZONED_DATETIME_FULL,
        TIMESTAMP, TIME_ONLY
    }

    //~--- constructors -------------------------------------------------------

    /**
     * This is the default constructor.
     *
     * <p> Initializes the object with the current system time
     *
     * <p>Defaults to a DateTimeFormatter of:
     * <p><code>yyyy-MM-dd HH:mm:ss.SSS<code>
     * <p>The formatter can be changed by chaining the setFormatPattern() method
     *
     */
    public Time()
    {
        this.setFormatPattern(FormatPattern.LOCAL_DATETIME);
        this.setDatetime(java.time.ZonedDateTime.now());
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());
        this.setZone(ZoneId.systemDefault());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;
    }

    /**
     * This constructor allows choosing which pattern to use for formatting dates
     *
     * <p> Initializes the object with the current system time
     *
     * <p>Acceptable enum values are:
     * <ul>
     * <p><code>FormatPattern.LOCAL_DATE</code>
     * <li>  - "yyyy-MM-dd"
     * <li>  -> 1970-01-01<p>
     * <p><code>FormatPattern.LOCAL_DATETIME</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss"
     * <li>  -> 1970-01-01 00:00:00<p>
     * <p><code>FormatPattern.LOCAL_DATETIME_FULL</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS"
     * <li>  -> 1970-01-01 00:00:00.000<p>
     * <p><code>FormatPattern.ZONED_DATETIME</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss X"
     * <li>  -> 1970-01-01 00:00:00 -00:00<p>
     * <p><code>FormatPattern.ZONED_DATETIME_FULL</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS X"
     * <li>  -> 1970-01-01 00:00:00.000 -00:00<p>
     * <p><code>FormatPattern.TIMESTAMP</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS X"
     * <li>  -> 1970-01-01 00:00:00.000 -00:00
     * </ul>
     *
     * @param pattern the chosen FormatPattern enum value
     */
    public Time(FormatPattern pattern)
    {
        this.setFormatPattern(pattern);
        this.setDatetime(java.time.ZonedDateTime.now());
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());
        this.setZone(ZoneId.systemDefault());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;
    }

    /**
     * Initializes the object from a datetime string
     *
     * <p>Defaults to a DateTimeFormatter of:
     * <p><code>yyyy-MM-dd HH:mm:ss.SSS<code>
     *
     * <p>The formatter can be changed by chaining the setFormatPattern() method
     *
     * <p>Acceptable patterns are:
     * <ul>
     * <p><code>FormatPattern.LOCAL_DATE</code>
     * <li>  - "yyyy-MM-dd"
     * <li>  -> 1970-01-01<p>
     * <p><code>FormatPattern.LOCAL_DATETIME</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss"
     * <li>  -> 1970-01-01 00:00:00<p>
     * <p><code>FormatPattern.LOCAL_DATETIME_FULL</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS"
     * <li>  -> 1970-01-01 00:00:00.000<p>
     * <p><code>FormatPattern.ZONED_DATETIME</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss X"
     * <li>  -> 1970-01-01 00:00:00 -00:00<p>
     * <p><code>FormatPattern.ZONED_DATETIME_FULL</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS X"
     * <li>  -> 1970-01-01 00:00:00.000 -00:00<p>
     * <p><code>FormatPattern.TIMESTAMP</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS X"
     * <li>  -> 1970-01-01 00:00:00.000 -00:00
     * </ul>
     *
     * <p>For the datetime string to parse, it must match one of the patterns above.
     *
     * @param datetime
     */
    public Time(String datetime)
    {
        this.setFormatPattern(FormatPattern.LOCAL_DATETIME_FULL);
        this.setDatetime(this.parse(datetime));
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());

        this.zone = ZoneId.of("Z");
        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;
    }

    public Time(Time time)
    {
        this.FORMATTER = time.FORMATTER;
        this.datetime  = time.getDatetime();
        this.year      = time.getYear();
        this.month     = time.getMonth();
        this.day       = time.getDay();
        this.hours     = time.getHours();
        this.minutes   = time.getMinutes();
        this.seconds   = time.getSeconds();
        this.nano      = time.getNano();
        this.zone      = time.getZone();
        this.week      = time.getWeek();
    }

    /**
     * Initializes this object from an Instant.
     * <p>It will automatically convert the Instant from UTC to local time
     * <p>Additionally, it sets the DateTimeFormatter from the given enum value
     *
     * <p>Acceptable enum values are:
     * <ul>
     * <p><code>FormatPattern.LOCAL_DATE</code>
     * <li>  - "yyyy-MM-dd"
     * <li>  -> 1970-01-01<p>
     * <p><code>FormatPattern.LOCAL_DATETIME</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss"
     * <li>  -> 1970-01-01 00:00:00<p>
     * <p><code>FormatPattern.LOCAL_DATETIME_FULL</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS"
     * <li>  -> 1970-01-01 00:00:00.000<p>
     * <p><code>FormatPattern.ZONED_DATETIME</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss X"
     * <li>  -> 1970-01-01 00:00:00 -00:00<p>
     * <p><code>FormatPattern.ZONED_DATETIME_FULL</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS X"
     * <li>  -> 1970-01-01 00:00:00.000 -00:00<p>
     * <p><code>FormatPattern.TIMESTAMP</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS X"
     * <li>  -> 1970-01-01 00:00:00.000 -00:00
     * </ul>
     *
     * @param instant an Instant, a moment in time on the UTC timeline
     * @param pattern the chosen FormatPattern when formatting date objects
     */
    public Time(Instant instant, FormatPattern pattern)
    {
        this.setFormatPattern(pattern);
        this.setDatetime(this.ofInstant(instant)
                             .getDatetime());
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());
        this.setZone(ZoneId.of("Z"));

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;
    }

    /**
     * Initializes this object from a datetime string.
     *
     * <p>Additionally, it sets the DateTimeFormatter from the given enum value
     *
     * <p>Acceptable enum values are:
     * <ul>
     * <p><code>FormatPattern.LOCAL_DATE</code>
     * <li>  - "yyyy-MM-dd"
     * <li>  -> 1970-01-01<p>
     * <p><code>FormatPattern.LOCAL_DATETIME</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss"
     * <li>  -> 1970-01-01 00:00:00<p>
     * <p><code>FormatPattern.LOCAL_DATETIME_FULL</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS"
     * <li>  -> 1970-01-01 00:00:00.000<p>
     * <p><code>FormatPattern.ZONED_DATETIME</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss X"
     * <li>  -> 1970-01-01 00:00:00 -00:00<p>
     * <p><code>FormatPattern.ZONED_DATETIME_FULL</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS X"
     * <li>  -> 1970-01-01 00:00:00.000 -00:00<p>
     * <p><code>FormatPattern.TIMESTAMP</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS X"
     * <li>  -> 1970-01-01 00:00:00.000 -00:00
     * </ul>
     *
     * <p>For the datetime string to parse, it must match one of the patterns above.
     *
     * @param datetime a datetime string
     * @param pattern the chosen FormatPattern enum value
     */
    public Time(String datetime, FormatPattern pattern)
    {
        this.FORMATTER = DateTimeFormatter.ofPattern(this.getFormatPattern(pattern));

        this.setDatetime(this.parse(datetime));
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());
        this.setZone(ZoneId.systemDefault());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Advances by the number of days provided
     * @param d Integer number of days to advance
     * @return this
     */
    public Time addDays(int d)
    {
        this.setDatetime(this.getDatetime()
                             .plusDays(d));
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    public Time addMinutes(int m)
    {
        this.setDatetime(this.getDatetime()
                             .plusMinutes(m));
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Advance by the number of months provided
     * @param m Integer number of months to advance
     * @return this
     */
    public Time addMonths(int m)
    {
        this.setDatetime(this.getDatetime()
                             .plusMonths(m));
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Advances by the number of weeks provided
     * @param w Integer number of weeks to advance
     * @return this
     */
    public Time addWeeks(int w)
    {
        this.setDatetime(this.getDatetime()
                             .plusWeeks(w));
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Advances by the number of years provided
     * @param y Integer number of years to advance
     * @return this
     */
    public Time addYears(int y)
    {
        this.setDatetime(this.getDatetime()
                             .plusYears(y));
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Decrements by the number of days provided
     * @param d Integer number of days to decrement
     * @return this
     */
    public Time minusDays(int d)
    {
        this.setDatetime(this.getDatetime()
                             .minusDays(d));
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Decrements by the number of months provided
     * @param m Integer number of months to decrement
     * @return this
     */
    public Time minusMonths(int m)
    {
        this.setDatetime(this.getDatetime()
                             .minusMonths(m));
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Decrements by the number of weeks provided
     * @param w Integer number of weeks to decrement
     * @return this
     */
    public Time minusWeeks(int w)
    {
        this.setDatetime(this.getDatetime()
                             .minusWeeks(w));
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Decrements by the number of years provided
     * @param y Integer number of years to decrement
     * @return this
     */
    public Time minusYears(int y)
    {
        this.setDatetime(this.getDatetime()
                             .minusYears(y));
        this.setYear(this.datetime.getYear());
        this.setMonth(this.datetime.getMonthValue());
        this.setDay(this.datetime.getDayOfMonth());
        this.setHours(this.datetime.getHour());
        this.setMinutes(this.datetime.getMinute());
        this.setSeconds(this.datetime.getSecond());
        this.setNano(this.datetime.getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Advances the time by one day
     * @return this
     */
    public Time nextDay()
    {
        this.setDatetime(this.getDatetime()
                             .plusDays(1L));
        this.setYear(this.getDatetime()
                         .getYear());
        this.setMonth(this.getDatetime()
                          .getMonthValue());
        this.setDay(this.getDatetime()
                        .getDayOfMonth());
        this.setHours(this.getDatetime()
                          .getHour());
        this.setMinutes(this.getDatetime()
                            .getMinute());
        this.setSeconds(this.getDatetime()
                            .getMinute());
        this.setNano(this.getDatetime()
                         .getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Advances the time by one month
     * @return this
     */
    public Time nextMonth()
    {
        this.setDatetime(this.getDatetime()
                             .plusMonths(1L));
        this.setYear(this.getDatetime()
                         .getYear());
        this.setMonth(this.getDatetime()
                          .getMonthValue());
        this.setDay(this.getDatetime()
                        .getDayOfMonth());
        this.setHours(this.getDatetime()
                          .getHour());
        this.setMinutes(this.getDatetime()
                            .getMinute());
        this.setSeconds(this.getDatetime()
                            .getMinute());
        this.setNano(this.getDatetime()
                         .getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Advances the time by one week
     * @return this
     */
    public Time nextWeek()
    {
        this.setDatetime(this.getDatetime()
                             .plusWeeks(1));
        this.setYear(this.getDatetime()
                         .getYear());
        this.setMonth(this.getDatetime()
                          .getMonthValue());
        this.setDay(this.getDatetime()
                        .getDayOfMonth());
        this.setHours(this.getDatetime()
                          .getHour());
        this.setMinutes(this.getDatetime()
                            .getMinute());
        this.setSeconds(this.getDatetime()
                            .getMinute());
        this.setNano(this.getDatetime()
                         .getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Advances the time by one year
     * @return this
     */
    public Time nextYear()
    {
        this.setDatetime(this.getDatetime()
                             .plusYears(1L));
        this.setYear(this.getDatetime()
                         .getYear());
        this.setMonth(this.getDatetime()
                          .getMonthValue());
        this.setDay(this.getDatetime()
                        .getDayOfMonth());
        this.setHours(this.getDatetime()
                          .getHour());
        this.setMinutes(this.getDatetime()
                            .getMinute());
        this.setSeconds(this.getDatetime()
                            .getMinute());
        this.setNano(this.getDatetime()
                         .getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Sets all fields from the system clock at the current moment
     * @return this
     */
    public Time now()
    {
        this.setDatetime(ZonedDateTime.now());
        this.setYear(this.getDatetime()
                         .getYear());
        this.setMonth(this.getDatetime()
                          .getMonthValue());
        this.setDay(this.getDatetime()
                        .getDayOfMonth());
        this.setHours(this.getDatetime()
                          .getHour());
        this.setMinutes(this.getDatetime()
                            .getMinute());
        this.setSeconds(this.getDatetime()
                            .getMinute());
        this.setNano(this.getDatetime()
                         .getNano());
        this.setZone(this.getDatetime()
                         .getZone());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * This method behaves as a setter for the datetime field of this class.
     * <p> It accepts an instant, and converts it to the local system time.
     * @param instant an instant, typically from a database timestamp
     * @return this
     */
    public Time ofInstant(Instant instant)
    {
        ZonedDateTime conversion = (ZonedDateTime) ZonedDateTime.ofInstant(instant, ZoneId.of("Z"))
                                                                .withZoneSameInstant(ZoneId.systemDefault());

        this.setDatetime(conversion);

        return this;
    }

    /**
     * Parses a string into a ZonedDateTime
     * <p> Accepted formats:
     * <p><code>yyyy-MM-dd</code>
     * <p><code>yyyy-MM-dd hh:mm:ss[.SSSSSS]</code>
     * <p><code>yyyy-MM-dd hh:mm:ss[.SSSSSS] X</code>
     * <p><code>yyyy-MM-dd hh:mm:ss[.SSSSSS] XX</code>
     * <p><code>yyyy-MM-dd hh:mm:ss[.SSSSSS] XXX</code>
     * <p> Note: microseconds are optional for all of these
     * @param datetime a string representing a date/datetime
     * @return ZonedDateTime
     */
    public ZonedDateTime parse(String datetime)
    {

        // pattern for checking if a date matches an accepted format
        String pattern =
            "^((([0-9]{4})([-])(0[1-9]?|1[012]?)([-])((0[1-9])|[12]\\d|30|31)){1})(((((([ ]{1})([01]?\\d|21|22|23){1})([:]{1})([012345]\\d)[:]{1})(([012345]\\d))){1})(([.]{1}\\d{1,6})?)?((([ ])((([-]((0\\d)|1[01]))|([+]((0[0-9])|1[0123])))([\\:]?(00|30|45))?|(([-]12([\\:]?00)?)|([+]14([\\:]?00)?)|(Z|z))))?))?$";

        // extracting date, time, zone tokens from the datetime string
        String[] tokens = datetime.split("[\\s]");

        // check if the pattern matches before continuing
        if (!datetime.matches(pattern))
        {
            // The pattern doesn't match to begin with
            throw new DateTimeParseException("Datetime does not conform any of these formats:" + "\n\tyyyy-MM-dd"
                                             + "\n\tyyyy-MM-dd hh:mm:ss" + "\n\tyyyy-MM-dd hh:mm:ss X"
                                             + "\n\tyyyy-MM-dd hh:mm:ss.SSSSSS" + "\n\tyyyy-MM-dd hh:mm:ss.SSSSSS X",
                                             "",
                                             0);
        }

        // iterate through the tokens, storing in appropriate fields
        for (int i = 0 ; i < tokens.length ; i++) {
            switch (i) {
              case 0 :    // date

                  // split the date into further token components
                  String[] d = tokens [i].split("-");

                  this.setYear(Integer.valueOf(d [0]));
                  this.setMonth(Integer.valueOf(d [1]));
                  this.setDay(Integer.valueOf(d [2]));

                  break;

              case 1 :    // time

                  // split the time string into further token components
                  String[] t = tokens [i].split("[:.]");

                  // check if fractional seconds exist
                  if (t.length == 4)
                  {
                      // store
                      this.setNano(Integer.valueOf(t [3]));
                  }

                  // store
                  this.setHours(Integer.valueOf(t [0]));
                  this.setMinutes(Integer.valueOf(t [1]));
                  this.setSeconds(Integer.valueOf(t [2]));

                  break;

              case 2 :    // zone

                  /*
                   * Works with any of these formats:
                   * Z
                   * +00, -00
                   * +0000, -0000, +0030, -0030, +0045, -0045
                   * +00:00, -00:00, +00:30, -00:30, +00:45, -00:45
                   *
                   * Zones cannot be less than 12 or greater than 14
                   *
                   * Additionally, increments beyond the hour, can only be
                   * 30 or 45 minutes, no other times are accepted by our
                   * regex pattern check that occured in the beginning of this
                   * method
                   */

                  // Store ZoneId from the offest held by its token
                  this.setZone(ZoneId.ofOffset("", ZoneOffset.of(tokens [i])));

                  break;

              default :

                  // we can only have 1-3 tokens, so something went wrong
                  throw new DateTimeParseException("Datetime does not conform any of these formats:" + "\n\tyyyy-MM-dd"
                                                   + "\n\tyyyy-MM-dd hh:mm:ss" + "\n\tyyyy-MM-dd hh:mm:ss X"
                                                   + "\n\tyyyy-MM-dd hh:mm:ss.SSSSSS"
                                                   + "\n\tyyyy-MM-dd hh:mm:ss.SSSSSS X",
                                                   "",
                                                   i);
            }
        }

        // null check for zone in order to parse as best we can
        if (this.getZone() == null)
        {
            this.setZone(ZoneId.of("Z"));
        }

        return ZonedDateTime.of(this.getYear(),
                                this.getMonth(),
                                this.getDay(),
                                this.getHours(),
                                this.getMinutes(),
                                this.getSeconds(),
                                this.getNano(),
                                this.getZone());
    }

    /**
     * Decrements the time by one day
     * @return this
     */
    public Time prevDay()
    {
        this.setDatetime(this.getDatetime()
                             .minusDays(1L));
        this.setYear(this.getDatetime()
                         .getYear());
        this.setMonth(this.getDatetime()
                          .getMonthValue());
        this.setDay(this.getDatetime()
                        .getDayOfMonth());
        this.setHours(this.getDatetime()
                          .getHour());
        this.setMinutes(this.getDatetime()
                            .getMinute());
        this.setSeconds(this.getDatetime()
                            .getMinute());
        this.setNano(this.getDatetime()
                         .getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Decrements the time by one month
     * @return this
     */
    public Time prevMonth()
    {
        this.setDatetime(this.getDatetime()
                             .minusMonths(1L));
        this.setYear(this.getDatetime()
                         .getYear());
        this.setMonth(this.getDatetime()
                          .getMonthValue());
        this.setDay(this.getDatetime()
                        .getDayOfMonth());
        this.setHours(this.getDatetime()
                          .getHour());
        this.setMinutes(this.getDatetime()
                            .getMinute());
        this.setSeconds(this.getDatetime()
                            .getMinute());
        this.setNano(this.getDatetime()
                         .getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Decrements the time by one week
     * @return this
     */
    public Time prevWeek()
    {
        this.setDatetime(this.getDatetime()
                             .minusWeeks(1));
        this.setYear(this.getDatetime()
                         .getYear());
        this.setMonth(this.getDatetime()
                          .getMonthValue());
        this.setDay(this.getDatetime()
                        .getDayOfMonth());
        this.setHours(this.getDatetime()
                          .getHour());
        this.setMinutes(this.getDatetime()
                            .getMinute());
        this.setSeconds(this.getDatetime()
                            .getMinute());
        this.setNano(this.getDatetime()
                         .getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    /**
     * Decrements the time by one year
     * @return this
     */
    public Time prevYear()
    {
        this.setDatetime(this.getDatetime()
                             .minusYears(1L));
        this.setYear(this.getDatetime()
                         .getYear());
        this.setMonth(this.getDatetime()
                          .getMonthValue());
        this.setDay(this.getDatetime()
                        .getDayOfMonth());
        this.setHours(this.getDatetime()
                          .getHour());
        this.setMinutes(this.getDatetime()
                            .getMinute());
        this.setSeconds(this.getDatetime()
                            .getMinute());
        this.setNano(this.getDatetime()
                         .getNano());

        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    public LocalDateTime toLocal()
    {
        return this.getDatetime()
                   .withZoneSameInstant(ZoneId.systemDefault())
                   .toLocalDateTime();
    }

    /**
     * Get the string representation of a datetime
     * <p>Defaults to: <code>yyyy-MM-dd hh:mm:ss</code>
     * @return a string
     */
    @Override
    public String toString()
    {
        return FORMATTER.format(this.getDatetime());
    }

    /**
     * Converts the held time to UTC (i.e. +0)
     * @return this
     */
    public Time toUTC()
    {
        this.setDatetime(this.getDatetime()
                             .withZoneSameInstant(ZoneId.of("UTC")));
        this.setYear(this.getDatetime()
                         .getYear());
        this.setMonth(this.getDatetime()
                          .getMonthValue());
        this.setDay(this.getDatetime()
                        .getDayOfMonth());
        this.setHours(this.getDatetime()
                          .getHour());
        this.setMinutes(this.getDatetime()
                            .getMinute());
        this.setSeconds(this.getDatetime()
                            .getMinute());
        this.setNano(this.getDatetime()
                         .getNano());
        this.setZone(ZoneId.of("Z"));

        return this;
    }

    /**
     * <p>Returns the ZonedDateTime stored by this object
     *
     * <p>This method is required to update the datetime field properly
     * when other temporal fields of this object are changed
     *
     * <p>This method should be chained on top of any call to setters or next/prev
     * methods that update the year, month, day, hours, minutes, seconds, nano, or zone
     * fields.
     *
     * @return ZonedDateTime
     */
    public Time update()
    {
        this.datetime = ZonedDateTime.of(this.getYear(),
                                         this.getMonth(),
                                         this.getDay(),
                                         this.getHours(),
                                         this.getMinutes(),
                                         this.getSeconds(),
                                         this.getNano(),
                                         this.getZone());
        this.week = this.datetime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);;

        return this;
    }

    //~--- get methods --------------------------------------------------------

    public int getDay()
    {
        return this.day;
    }

    /**
     * Returns the hours value
     * @return int
     */
    public int getHours()
    {
        return this.hours;
    }

    public LocalDate getLocal()
    {
        return LocalDate.of(this.year, this.month, this.day);
    }

    public LocalDateTime getLocalDateTime()
    {
        return LocalDateTime.of(this.year, this.month, this.day, this.hours, this.minutes, this.seconds, this.nano);
    }

    /**
     * Returns the minutes value
     * @return int
     */
    public int getMinutes()
    {
        return this.minutes;
    }

    /**
     * Returns the month value
     * @return int
     */
    public int getMonth()
    {
        return this.month;
    }

    /**
     * Returns the nano (fractional seconds) value
     * @return int
     */
    public int getNano()
    {
        return nano;
    }

    /**
     * Returns the seconds value
     * @return int
     */
    public int getSeconds()
    {
        return this.seconds;
    }

    public int getWeek()
    {
        return this.week;
    }

    /**
     * Returns the year value
     * @return int
     */
    public int getYear()
    {
        return this.year;
    }

    /**
     * Returns the zone field
     * @return ZoneId
     */
    public ZoneId getZone()
    {
        return this.zone;
    }

    /**
     * Returns true if the given year is a leap year, otherwise false
     * @param y Integer denoting year being tested
     * @return Boolean
     */
    public Boolean isLeapYear(int y)
    {
        if (((y % 4 == 0) && (y % 100 != 0)) || (y % 400 == 0))
        {
            return true;
        }

        return false;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Sets the datetime field via ZonedDateTime.
     * @param datetime ZonedDateTime
     */
    public void setDatetime(ZonedDateTime datetime)
    {
        this.datetime = datetime;
    }

    /**
     * Sets the day field via Integer. Returns early if less than 1 or
     * greater than allowable days in the given month
     * @param d Integer representing the day
     * @return this
     */
    public Time setDay(int d)
    {
        Boolean leap        = isLeapYear(d);
        int[][] daysInMonth = new int [][] {
            {    // common year
                0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
            },
            {    // leap year
                0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
            }
        };
        int     m           = this.getMonth();
        int     days        = daysInMonth [leap
                                           ? 1
                                           : 0][m];

        if ((d < 1) || (d > days))
        {
            return this;
        }

        this.day = d;

        return this;
    }

    /**
     * Sets the pattern for formatting date strings.
     * <p>Acceptable enum values are:
     * <ul>
     * <p><code>FormatPattern.LOCAL_DATE</code>
     * <li>  - "yyyy-MM-dd"
     * <li>  -> 1970-01-01<p>
     * <p><code>FormatPattern.LOCAL_DATETIME</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss"
     * <li>  -> 1970-01-01 00:00:00<p>
     * <p><code>FormatPattern.LOCAL_DATETIME_FULL</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS"
     * <li>  -> 1970-01-01 00:00:00.000<p>
     * <p><code>FormatPattern.ZONED_DATETIME</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss X"
     * <li>  -> 1970-01-01 00:00:00 -00:00<p>
     * <p><code>FormatPattern.ZONED_DATETIME_FULL</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS X"
     * <li>  -> 1970-01-01 00:00:00.000 -00:00<p>
     * <p><code>FormatPattern.TIMESTAMP</code>
     * <li>  - "yyyy-MM-dd HH:mm:ss.SSS X"
     * <li>  -> 1970-01-01 00:00:00.000 -00:00
     * </ul>
     *
     *
     * @param pattern a FormatPattern enum
     * @return this
     */
    public Time setFormatPattern(FormatPattern pattern)
    {
        this.FORMATTER = DateTimeFormatter.ofPattern(this.getFormatPattern(pattern));

        return this;
    }

    /**
     * Set the hours field via Integer. Returns early if less than 0 or
     * greater than 23.
     * @param h
     * @return
     */
    public Time setHours(int h)
    {
        if ((h < 0) || (h > 23))
        {
            return this;
        }

        this.hours = h;

        return this;
    }

    /**
     * Set the minutes field via Integer. Returns early if the less than 0 or
     * greater than 59.
     * @param minutes
     * @return
     */
    public Time setMinutes(int minutes)
    {
        if ((minutes < 0) || (minutes > 59))
        {
            return this;
        }

        this.minutes = minutes;

        return this;
    }

    /**
     * Set the month field via Integer. Returns early if  less than 1 or
     * greater than 12.
     * @param m Integer representing month value
     * @return this
     */
    public Time setMonth(int m)
    {
        if ((m < 1) || (m > 12))
        {
            return this;
        }

        this.month = m;

        return this;
    }

    /**
     * Set fractional seconds via Integer. Returns early if less than 0 or
     * greater than 999999.
     * @param nano Integer representing fractions of a seconds
     * @return this
     */
    public Time setNano(int nano)
    {
        if ((nano < 0) || (nano > 999999))
        {
            return this;
        }

        this.nano = nano;

        return this;
    }

    /**
     * Set the seconds field via Integer. Returns early if less than 0 or
     * greater than 59.
     * @param s integer representing seconds
     * @return this
     */
    public Time setSeconds(int s)
    {
        if ((seconds < 0) || (seconds > 59))
        {
            return this;
        }

        this.seconds = s;

        return this;
    }

    public Time setWeek(int w)
    {
        this.week = w;

        return this;
    }

    /**
     * Set the year field via Integer. Returns early if less than 0.
     * @param y integer representing a year
     * @return this
     */
    public Time setYear(int y)
    {
        if (y < 0)
        {
            return this;
        }

        this.year = y;

        return this;
    }

    /**
     * Set the timezone field via ZoneId
     * @param z ZoneId
     * @return this
     */
    public Time setZone(ZoneId z)
    {
        this.zone = z;

        return this;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Returns the ZonedDateTime stored by this object
     * @return ZonedDateTime
     */
    private ZonedDateTime getDatetime()
    {
        return this.datetime;
    }

    /**
     * This method takes the FormatPattern enum value given and gets the
     * corresponding formatting String. The string returned can be used
     * with a DateTimeFormatter.
     * @param p a FormatPattern enum value
     * @return String representing the formatting pattern
     */
    private String getFormatPattern(FormatPattern p)
    {
        switch (p) {
          case LOCAL_DATE :
              return this.LOCAL_DATE;

          case LOCAL_DATETIME :
              return this.LOCAL_DATETIME;

          case LOCAL_DATETIME_SHORT :
              return this.LOCAL_DATETIME_SHORT;

          case LOCAL_DATETIME_FULL :
              return this.LOCAL_DATETIME_FULL;

          case ZONED_DATETIME :
              return this.ZONED_DATETIME;

          case ZONED_DATETIME_FULL :
              return this.ZONED_DATETIME_FULL;

          case TIMESTAMP :
              return this.TIMESTAMP;

          case TIME_ONLY :
              return this.TIME_ONLY;

          default :
              return this.LOCAL_DATE;
        }
    }
}

/*
 * @(#)Time.java 2021.05.21 at 01:32:23 EDT
 * EOF
 */
