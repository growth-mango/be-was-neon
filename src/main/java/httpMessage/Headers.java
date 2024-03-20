package httpMessage;

import java.util.HashMap;
import java.util.Map;

public class Headers {
    private final Map<String, String> headers = new HashMap<>();

    public void add(String name, String value) {
        headers.put(name, value);
    }

    public String getValue(String name) {
        return headers.get(name);
    }

    public boolean containKey(String key) {
        return headers.containsKey(key);
    }

    public int getContentLength() {
        String value = headers.get("Content-Length");
        return value != null ? Integer.parseInt(value) : 0;
    }

    public void printHeaders() {
        headers.forEach((key, value) -> System.out.println("Header Key: \"" + key + "\" Value: \"" + value + "\""));
    }
}
