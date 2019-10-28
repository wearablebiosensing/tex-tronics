package andrewpeltier.smartglovefragments.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/** ======================================
 *
 *              IOUtil Class
 *
 *  ======================================
 *
 *
 * @author mcons on 2/28/2018.
 * @version 1.0
 */

public class IOUtil
{
    /** readFile()
     *
     * Called by the TexTronics Manager service when a device is disconnected or it is called to publish. We
     * take the CSV file that we have created from the exercise data, convert it to a byte array, and return
     * the byte array. This is used to publish the data to an MQTT server.
     *
     * @param file                  -Input file to read
     * @return                  A byte array of the contents of the file
     * @throws IOException
     */
    public static byte[] readFile(File file) throws IOException
    {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}
