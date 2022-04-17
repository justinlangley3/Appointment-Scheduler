/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Month.java   2021.05.19 at 06:08:36 EDT
 * Version: 1.0.0
 */



package Model.Dummy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//~--- classes ----------------------------------------------------------------

public class Month
{
    private final Week zero, one, two, three, four, five;

    //~--- constructors -------------------------------------------------------

    public Month(Integer[][] m)
    {
        this.zero  = new Week(m [0]);
        this.one   = new Week(m [1]);
        this.two   = new Week(m [2]);
        this.three = new Week(m [3]);
        this.four  = new Week(m [4]);
        this.five  = new Week(m [5]);
    }

    //~--- get methods --------------------------------------------------------

    public ObservableList<Week> getWeeks()
    {
        return FXCollections.observableArrayList(zero, one, two, three, four, five);
    }
}

/*
 * @(#)Month.java 2021.05.19 at 06:08:36 EDT
 * EOF
 */
