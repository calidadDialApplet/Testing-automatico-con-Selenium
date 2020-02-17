package Utils;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;


//*
// This class is for manage files.
// */
public class File {

    //Waits until the file is downloaded
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

    //Waits until the file is download searching it by his extension
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

    //Deletes the file to prevent them from accumulating
    public static void deleteExistingFile(String filePath) throws IOException
    {
        java.io.File tempFile = new java.io.File(filePath);
        Files.deleteIfExists(tempFile.toPath()); //Deletes the file if already exists

    }

    //Deletes the file to prevent them from accumulating searching it by his extension
    public static void deleteExistingFileByExtension(String extension) throws IOException {
        java.io.File dir = new java.io.File(".");
        FileFilter fileFilter = new WildcardFileFilter("*." + extension);
        java.io.File[] files = dir.listFiles(fileFilter);
        if(files.length != 0)Files.deleteIfExists(files[0].toPath());
    }
}
