/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Gregorian.java   2021.05.21 at 01:39:50 EDT
 * Version: 1.0.0
 */



package Util.Date;

import static java.lang.Math.floor;

import Util.Date.Time.FormatPattern;

//~--- classes ----------------------------------------------------------------

public class Gregorian
        extends java.util.GregorianCalendar
{
    private final int[][] daysInMonth = new int [][] {
        {    // common year
            0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        },
        {    // leap year
            0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        }
    };
    private final int[][] startOfYear = new int [][] {
        {    // week begins Sunday
            3, 1, -1, -2, -3, 3, 2, 1, -1, -3, -4, 3, 2, -1, -2, -3, -4, 2, 1, -1, -2, -4, 3, 2, 1, -2, -3, -4
        },
        {    // week begins Monday
            4, 2, 1, -1, -2, 4, 3, 2, 1, -2, -3, 4, 3, 1, -1, -2, -3, 3, 2, 1, -1, -3, 4, 3, 2, -1, -2, -3
        }
    };
    private String        date;
    private Time          datetime;
    private WeekStarts    startOfWeek;

    //~--- constant enums -----------------------------------------------------

    public enum WeekStarts { DEFAULT, ISO, MONDAY, SUNDAY, USA }

    //~--- constructors -------------------------------------------------------

    public Gregorian()
    {
        datetime         = new Time(FormatPattern.LOCAL_DATE).now();
        this.date        = this.datetime.toString();
        this.startOfWeek = WeekStarts.DEFAULT;
    }

    public Gregorian(String datetime)
    {
        this.datetime = new Time(datetime);

        this.datetime.setFormatPattern(FormatPattern.LOCAL_DATE);

        this.date        = this.datetime.toString();
        this.startOfWeek = WeekStarts.DEFAULT;
    }

    //~--- methods ------------------------------------------------------------

    public int dayOfWeek()
    {
        int y        = this.getDatetime()
                           .getYear();
        int m        = this.getDatetime()
                           .getMonth();
        int d        = this.getDatetime()
                           .getDay();
        int shift [] = {
            0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4
        };

        // year correction for Jan, Feb
        y -= ((m < 3)
              ? 1
              : 0);

        // calculation for the day of the week a date falls on
        return (y + y / 4 - y / 100 + y / 400 + shift [m - 1] + d) % 7;
    }

    public int dayOfWeek(int y, int m, int d)
    {

        /*
         *                        Sakamoto's algorithm
         *         calculating the DOW (day of the week) a date occurs
         */

        /*
         *  DOW shift by month
         *  These shift values allow us to calculate from the beginning of a month. This is
         *  better than checking how many days in the year have elapsed to the current date,
         *  or which months have passed
         *
         *  Calculated as:
         *  (Jan)   Month 1          = 0
         *          shift(month + 1) = ( days(month) + shift(month) ) % 7
         *          where,
         *              days() returns the number of days for the month in range [1-12]
         *
         *                                               0: (Jan)
         *          Ex: shift(2) = ( ( 31 + 0 ) % 7 )  = 3: (Feb)
         *              shift(3) = ( ( 28 + 3 ) % 7 )  = 3: (Mar)
         *              shift(4) = ( ( 31 + 3 ) % 7 )  = 6: (Apr)
         *              shift(5) = ( ( 30 + 6 ) % 7 )  = 1: (May)
         *              ---------------------------------------- ... and so on
         *
         *  DOW for a date following a common year has a shift of 1, and 2 following a leap year.
         *      (365 % 7 vs. 366 % 7: i.e. a leap year has one extra day in Feb)
         *
         *  A "virtual year" is used in the DOW calculation.
         *  Leap years are added at the end of the virtual year (i.e. Feb 29)
         *
         *  This allows us to shift Mar->Dec separately, but then the need arises to
         *  calculate Jan, Feb in a prior year
         *
         *  Thinking in terms of the "display year" ...
         *
         *  Without normalizing:
         *      Decrementing the year for Jan, Feb means:
         *          - shifts from Mar->Dec will be ahead by an extra +1 day each year (1 year ahead)
         *
         *  Given the shift array we originally computed, the values would cause a leap in
         *  every common year, and two leaps in every leap year
         *
         *  We decrement by 1, the values in our shift array for Mar->Dec
         *  This normalizes the values and closes the gap described above.
         */

        int shift [] = {
            0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4
        };

        // year correction for Jan, Feb
        y -= ((m < 3)
              ? 1
              : 0);

        // calculation for the day of the week a date falls on
        return (y + y / 4 - y / 100 + y / 400 + shift [m - 1] + d) % 7;
    }

    public String[] daysOfYearToDates(int y, int[] days)
    {
        String[] dates    = new String [7];
        int[][]  ordinals = new int [][] {
            {    // common year
                -1, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365
            },
            {    // leap year
                -1, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366
            }
        };
        int      idx      = isLeapYear(y)
                            ? 1
                            : 0;
        int      last     = isLeapYear(y)
                            ? 366
                            : 365;

        // process day values into date strings
        for (int i = 0 ; i < 7 ; i++) {
            if (days [i] < 0)                                                   // day is in prior year
            {
                dates [i] = this.toDateString((y - 1), 12, (32 + days [i]));    // Dec (prior year)
            }
            else if ((days [i] > last))                                         // day is in next year
            {
                int next = days [i] - last;

                // Jan (next year)
                dates [i] = this.toDateString((y + 1), 1, next);                                // add date to current week
            }
            else if ((days [i] > 0) && (days [i] <= last))                                      // day is in current year
            {
                int j = 0;

                while (days [i] > ordinals [idx][j]) {                                          // find the month
                    j++;
                }

                dates [i] = this.toDateString(y, j - 1, (days [i] - ordinals [idx][j - 1]));    // add date to current week
            }
            else                                                                                // invalid
            {
                System.out.println("An invalid date was encountered ...");

                dates [i] = null;
            }
        }

        return dates;
    }

    public String toDateString(int y, int m, int d)
    {
        String year = "";

        if (y < 1000)
        {
            year = "0" + Integer.toString(y);
        }
        else if (y < 100)
        {
            year = "00" + Integer.toString(y);
        }
        else if (y < 10)
        {
            year = "000" + Integer.toString(y);
        }
        else if (y < 1)
        {
            return null;
        }
        else
        {
            year = Integer.toString(y);
        }

        String month = "";

        if (m > 12)
        {
            return null;
        }
        else if (m < 10)
        {
            month = "0" + Integer.toString(m);
        }
        else if (m < 1)
        {
            return null;
        }
        else
        {
            month = Integer.toString(m);
        }

        String day = "";

        if (d < 10)
        {
            day = "0" + Integer.toString(d);
        }
        else if (d < 1)
        {
            return null;
        }
        else
        {
            day = Integer.toString(d);
        }

        this.setDate(year + "-" + month + "-" + day);

        return this.getDate();
    }

    @Override
    public String toString()
    {
        return this.datetime.toString();
    }

    public String toString(FormatPattern pattern)
    {
        this.datetime.setFormatPattern(pattern);

        return this.datetime.toString();
    }

    public Gregorian update()
    {
        this.set(this.datetime.getYear(),
                 this.datetime.getMonth(),
                 this.datetime.getDay(),
                 this.datetime.getHours(),
                 this.datetime.getMinutes(),
                 this.datetime.getSeconds());

        return this;
    }

    //~--- get methods --------------------------------------------------------

    public String getDate()
    {
        return this.date;
    }

    public Time getDatetime()
    {
        return this.datetime;
    }

    public int getDaysInMonth(int y, int m)
    {
        return this.daysInMonth [this.isLeapYear(y)
                                 ? 1
                                 : 0][m];
    }

    public WeekStarts getStartOfWeek()
    {
        return this.startOfWeek;
    }

    public int getStartOfYear()
    {
        return startOfYear [this.getFirstDayOfWeek() - 1][this.getDatetime().getYear() % 28];
    }

    public String[] getWeek(int y, int w)
    {
        int   doy   = 0;                 // day of year (in week years)
        int   nth   = 0;                 // 1st day of nth week requested
        int[] days  = new int [7];       // days of the selected week
        int   weeks = (isLongYear(y))    // # weeks in the year
                      ? 53
                      : 52;

        /* get s value, depending on whether Sun/Mon is the start of week */
        switch (getStartOfWeek()) {
          case DEFAULT :
          case SUNDAY :
          case USA :
              doy = startOfYear [0][(y % 28)];

              break;

          case ISO :
          case MONDAY :
              doy = startOfYear [1][(y % 28)];

              break;
        }

        // Compute start day of requested week
        if (w < 1)                                             // test if requested week is in a prior year
        {
            return null;
        }
        else if (w == 1)                                       // week 1
        {
            if (doy > 0)                                       // Jan
            {
                // check if the current year is a 53 week year
                if (weeks == 53)
                {
                    nth = doy - 7;                             // offset 1 week to prior year
                }
                else
                {
                    nth = doy;                                 // # days (+) into current year
                }
            }
            else if (doy < 0)                                  // Dec, prior year
            {
                nth = doy;                                     // # days (-) in prior year
            }
            else                                               // invalid day
            {
                return null;
            }
        }
        else if ((w > 1) && (w <= weeks))                      // nth week
        {
            if (doy > 0)                                       // Jan
            {
                if (weeks == 53)
                {
                    nth = doy + (7 * (w - 1)) - 7;             // nth week
                }
                else
                {
                    nth = doy + (7 * (w - 1));                 // nth week
                }
            }
            else if (doy < 0)                                  // Dec, prior year
            {
                nth = (7 * (w - 1)) + doy + 1;                 // nth week - # days in prior year
            }
            else                                               // invalid day
            {
                return null;
            }
        }
        else if (w > weeks)
        {                                                      // test if requested week is in a future year
            int inc_y = (int) (y + Math.round(w / 52.143));    // try to determine year
            int inc_w;

            // try to determine week
            inc_w = (int) (floor(((w * 7) % 1461) / 7) % 52);

            // retrieve using recursion
            return getWeek(inc_y, inc_w);
        }

        // Process day values for the requested week
        if (nth < 0)
        {
            int i = 0;               // # days in prior year

            while ((nth < 0)) {
                days [i] = nth++;    // set values from prior year

                ++i;                 // increment # days
            }

            // set n to 1, 1st of the year
            // set remaining array values until we have a week
            nth = 1;

            while (i < 7) {
                days [i++] = nth++;
            }

            return this.daysOfYearToDates(y, days);
        }
        else if (nth > 0)
        {
            for (int i = 0 ; i < 7 ; i++) {
                days [i] = nth++;
            }

            return this.daysOfYearToDates(y, days);
        }
        else
        {
            return null;
        }
    }

    // returns true if a year has 53 weeks, otherwise false
    public boolean isLongYear(int y)
    {
        return ((y + floor(y / 4) - floor(y / 100) + floor(y / 400)) % 7) == 4
               || ((y - 1 + floor((y - 1) / 4) - floor((y - 1) / 100) + floor((y - 1) / 400)) % 7) == 3;
    }

    //~--- set methods --------------------------------------------------------

    public void setDate(String d)
    {
        this.datetime = new Time(d);
        this.date     = this.datetime.toString();
    }

    public void setDatetime(Time time)
    {
        this.datetime = time;

        return;
    }

    public void setStartOfWeek(WeekStarts day)
    {
        this.startOfWeek = day;

        switch (day) {
          case DEFAULT :
          case SUNDAY :
          case USA :
              this.setFirstDayOfWeek(1);
              this.setMinimalDaysInFirstWeek(4);

              break;

          case ISO :
          case MONDAY :
              this.setFirstDayOfWeek(2);
              this.setMinimalDaysInFirstWeek(4);

              break;
        }
    }
}

/*
 * @(#)Gregorian.java 2021.05.21 at 01:39:50 EDT
 * EOF
 */
