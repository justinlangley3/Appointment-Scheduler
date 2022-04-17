/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Week.java   2021.05.19 at 06:09:32 EDT
 * Version: 1.0.0
 */



package Model.Dummy;

import java.util.ArrayList;

import javafx.collections.FXCollections;

//~--- classes ----------------------------------------------------------------

public class Week
{
    private final String friday;
    private final String monday;
    private final String saturday;
    private final String sunday;
    private final String thursday;
    private final String tuesday;
    private final String wednesday;

    //~--- constructors -------------------------------------------------------

    public Week(Integer[] days)
    {
        this.sunday    = (days [0] == 0)
                         ? ""
                         : Integer.toString(days [0]);
        this.monday    = (days [1] == 0)
                         ? ""
                         : Integer.toString(days [1]);
        this.tuesday   = (days [2] == 0)
                         ? ""
                         : Integer.toString(days [2]);
        this.wednesday = (days [3] == 0)
                         ? ""
                         : Integer.toString(days [3]);
        this.thursday  = (days [4] == 0)
                         ? ""
                         : Integer.toString(days [4]);
        this.friday    = (days [5] == 0)
                         ? ""
                         : Integer.toString(days [5]);
        this.saturday  = (days [6] == 0)
                         ? ""
                         : Integer.toString(days [6]);
    }

    //~--- methods ------------------------------------------------------------

    public int size()
    {
        return 7;
    }

    public ArrayList<String> toList()
    {
        ArrayList<String> out = new ArrayList<>();

        out.add(this.getSunday());
        out.add(this.getMonday());
        out.add(this.getTuesday());
        out.add(this.getWednesday());
        out.add(this.getThursday());
        out.add(this.getFriday());
        out.add(this.getSaturday());

        return out;
    }

    @Override
    public String toString()
    {
        return FXCollections.observableArrayList(this.getSunday(),
                                                 this.getMonday(),
                                                 this.getTuesday(),
                                                 this.getWednesday(),
                                                 this.getThursday(),
                                                 this.getFriday(),
                                                 this.getSaturday())
                            .toString()
                            .replaceAll("[{\\(\\[\\]\\)}]+", "");
    }

    //~--- get methods --------------------------------------------------------

    public String get(int i)
    {
        switch (i) {
          case 0 :
              return getSunday();

          case 1 :
              return getMonday();

          case 2 :
              return getTuesday();

          case 3 :
              return getWednesday();

          case 4 :
              return getThursday();

          case 5 :
              return getFriday();

          case 6 :
              return getSaturday();

          default :
              return null;
        }
    }

    public String getFriday()
    {
        return friday;
    }

    public String getMonday()
    {
        return monday;
    }

    public String getSaturday()
    {
        return saturday;
    }

    public String getSunday()
    {
        return sunday;
    }

    public String getThursday()
    {
        return thursday;
    }

    public String getTuesday()
    {
        return tuesday;
    }

    public String getWednesday()
    {
        return wednesday;
    }

    public Week getWeek()
    {
        return this;
    }
}

/*
 * @(#)Week.java 2021.05.19 at 06:09:32 EDT
 * EOF
 */
