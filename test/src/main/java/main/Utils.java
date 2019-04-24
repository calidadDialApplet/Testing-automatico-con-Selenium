package main;

import java.util.Random;

public class Utils {

    public static String generateUniqueID(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 6) {
            sb.append(Integer.toHexString(random.nextInt()));
        }
        return sb.toString();
    }


}
