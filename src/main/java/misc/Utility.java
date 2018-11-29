package misc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utility {
    public static void loadProperties(String File, Properties properties){
        InputStream input = null;
        try {
            input = new FileInputStream(File);
            properties.load(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String longToStringTime(long time){
        time = time / 1000;
        long minutes = time % 3600;
        long hours = ((time - minutes) / 3600);
        long secs = minutes % 60;
        minutes = ((minutes - secs) / 60);

        StringBuilder stringBuilder = new StringBuilder();
        if(hours > 0) {
            if(hours < 10) stringBuilder.append(0);
            stringBuilder.append(hours).append(":");
        }

        if(minutes < 10) stringBuilder.append(0);
        stringBuilder.append(minutes).append(":");

        if(secs < 10) stringBuilder.append(0);
        stringBuilder.append(secs);

        return stringBuilder.toString();
    }
}
