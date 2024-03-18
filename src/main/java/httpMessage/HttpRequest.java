package httpMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.RequestLineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class HttpRequest {
    private String requestLine;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> bodies = new HashMap<>();
    private Map<String, String> queries = new HashMap<>();
    private String requestURL;
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        this.requestLine = br.readLine(); // 요청 라인 읽기
        readHeaders(br); // 헤더 읽기
        RequestLineParser requestLineParser = new RequestLineParser(this.requestLine);
        this.requestURL = requestLineParser.getRequestURL(); // 요청 URL 저장
        this.queries = requestLineParser.getQueries(); // 쿼리 파라미터 저장
    }

    private void readHeaders(BufferedReader br) throws IOException {
        String line = requestLine;
        while ((line = br.readLine()) != null && !line.isEmpty()) { // 첫 번째 라인 (요청 라인) 은, 헤더가 아니기에 건너뛰고 시작한다.
            int separator = line.indexOf(":");
            if (separator != -1) {
                String name = line.substring(0, separator).trim();
                String value = line.substring(separator + 1).trim();
                headers.put(name, value);
            }
        }
    }

    public void printHeaders(Map<String, String> headers) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            logger.debug("Header Key: \"{}\" Value: \"{}\"", header.getKey(), header.getValue());
        }
    }

    public String getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getBodies() {
        return bodies;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public String getValue(String key){
        return getQueries().get(key);
    }
}