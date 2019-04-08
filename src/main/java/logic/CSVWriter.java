package logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter
{
    public static void appendDataToFile(CSVData data, String path)
    {
        File file = new File(path);
        try (FileWriter fileWriter = new FileWriter(file,true))
        {
            fileWriter.write(data.toString());
            fileWriter.append("\n");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
