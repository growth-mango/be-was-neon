package util;

public class RequestUtil {
    public static String getUrl(String firstLine){
        String[] tokens = firstLine.split(" ");
        return tokens[1];
    }
}
