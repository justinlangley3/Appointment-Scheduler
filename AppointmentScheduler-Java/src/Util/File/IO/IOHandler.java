/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)IOHandler.java   2021.05.19 at 06:11:01 EDT
 * Version: 1.0.0
 */



package Util.File.IO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;

//~--- classes ----------------------------------------------------------------

public class IOHandler
{
    public IOHandler() {}

    //~--- methods ------------------------------------------------------------

    public static void newCSV(File file, String header, List<String[]> data) throws IOException
    {

        /*
         *  Create the file at the given path with name, and .csv extension
         *  CSV is a plaintext format. We don't need to work with any
         *  magic bytes, file encodings, or particular file structures.
         *  The structure is simply one entry per line with all values comma
         *  seperated. Naturally the first entry is the header.
         */

        FileWriter writer = new FileWriter(file.getAbsoluteFile());
        String     csv    = header + "\n";

        // write data
        for (int i = 0 ; i < data.size() ; i++) {

            // loop values on each line
            for (int j = 0 ; j < data.get(i).length ; j++) {
                if (j == data.get(i).length - 1)
                {
                    csv += data.get(i) [j] + "\n";
                }
                else
                {
                    csv += data.get(i) [j] + ",";
                }
            }
        }

        csv += "\r\n";

        writer.write(csv);
        writer.close();
    }

    //~--- get methods --------------------------------------------------------

    public static Boolean hasReadWriteAccess(File file)
    {
        if ((!file.exists()))
        {
            file.mkdirs();
        }

        return file.canRead() && file.canWrite();
    }
}

/*
 * @(#)IOHandler.java 2021.05.19 at 06:11:01 EDT
 * EOF
 */
