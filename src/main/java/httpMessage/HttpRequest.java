package httpMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HttpRequest {
    private String requestLine;
    private Map<String, String> httpHeaders = new HashMap<>();
    private Map<String, String> httpBodies = new HashMap<>();

    public HttpRequest(BufferedReader br) throws IOException {
        this.requestLine = br.readLine(); // 요청 라인 읽기
        readHeaders(br); // 헤더 읽기
    }

    private void readHeaders(BufferedReader br) throws IOException {
        String line = requestLine;
        while ((line = br.readLine()) != null && !line.isEmpty()) { // 첫 번째 라인 (요청 라인) 은, 헤더가 아니기에 건너뛰고 시작한다.
            int separator = line.indexOf(":");
            if (separator != -1) {
                String name = line.substring(0, separator).trim();
                String value = line.substring(separator + 1).trim();
                httpHeaders.put(name, value);
            }
        }
    }

    public String getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public Map<String, String> getHttpBodies() {
        return httpBodies;
    }
}