package Utils;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class File {
    public static String waitToDownload(String filePath, int wait) throws IOException
    {
        java.io.File tempFile = new java.io.File(filePath);
        //Wait for the download to finish
        int i = 0;
        for(; i < wait; i++){
            if(tempFile.exists()){break;}
            else
            {
                try
                {
                    System.out.println("Waiting the download to finish");
                    Thread.sleep(1000);

                } catch(Exception e)
                { }
            }
        }

        if(i == wait)
        {
            return "ERROR. Download failed";
        }
        return "Downloaded";
    }

    public static String waitToDownloadByExtension(String extension, int wait) throws IOException
    {
        java.io.File dir = new java.io.File(".");
        FileFilter fileFilter = new WildcardFileFilter("*."+ extension);

        //Wait for the download to finish
        int i = 0;
        for(; i < wait; i++){
            java.io.File[] files = dir.listFiles(fileFilter);
            if(files.length != 0){break;}
            else
            {
                try
                {
                    System.out.println("Waiting the download to finish");
                    Thread.sleep(1000);

                } catch(Exception e)
                { }
            }
        }

        if(i == wait)
        {
            return "ERROR. Download failed";
        }
        return "Downloaded";
    }

    public static void deleteExistingFile(String filePath) throws IOException
    {
        java.io.File tempFile = new java.io.File(filePath);
        Files.deleteIfExists(tempFile.toPath()); //Deletes the file if already exists

    }

    public static void deleteExistingFileByExtension(String extension) throws IOException {
        java.io.File dir = new java.io.File(".");
        FileFilter fileFilter = new WildcardFileFilter("*." + extension);
        java.io.File[] files = dir.listFiles(fileFilter);
        if(files.length != 0)Files.deleteIfExists(files[0].toPath());
    }
}
