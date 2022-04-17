/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)DAO.java   2021.05.19 at 06:09:04 EDT
 * Version: 1.0.0
 */



package Model.DAO;


import java.util.ArrayList;

//~--- interfaces -------------------------------------------------------------

//This interface is a template.
//An implementation for a DB table should include these methods at a minimum.
public interface DAO
{
    boolean create(Object addressObj);

    boolean delete(Integer id);

    boolean doesTableHaveData();

    Object read(Integer id);

    ArrayList<?> readAll();

    boolean update(Object addressObj);
}

/*
 * @(#)DAO.java 2021.05.19 at 06:09:04 EDT
 * EOF
 */
