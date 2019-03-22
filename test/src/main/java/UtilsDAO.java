import java.util.Random;

public class UtilsDAO {
    public static String generateUnicID(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 6) {
            sb.append(Integer.toHexString(random.nextInt()));
        }
        return sb.toString();
    }
}
