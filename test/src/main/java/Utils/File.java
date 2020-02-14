package Utils;

import java.io.IOException;
import java.nio.file.Files;

public class File {
    public static String waitToDownload(String path, int wait) throws IOException {
        java.io.File tempFile = new java.io.File( path);
        boolean aux = Files.deleteIfExists(tempFile.toPath()); //Deletes the file if already exists
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

}
