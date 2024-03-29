package httpMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Headers {
    private static final Logger logger = LoggerFactory.getLogger(Headers.class);

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

    public void readHeaders(BufferedReader br) throws IOException {
        String line;
      
        while ((line = br.readLine()) != null && !line.isEmpty()) { // 첫 번째 라인 (요청 라인) 은, 헤더가 아니기에 건너뛰고 시작한다.
            int separator = line.indexOf(":");
            if (separator != -1) {
                String name = line.substring(0, separator).trim();
                String value = line.substring(separator + 1).trim();
                add(name, value);
            }
        }
    }

    public void printHeaders() {
        headers.forEach((key, value) -> logger.info("Header Key: \"" + key + "\" Value: \"" + value + "\""));
    }
}
