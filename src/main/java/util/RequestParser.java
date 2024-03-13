package util;

public class RequestParser {
    public static String getUrl(String firstLine){
        String[] tokens = firstLine.split(" ");
        return tokens[1];
    }
}
