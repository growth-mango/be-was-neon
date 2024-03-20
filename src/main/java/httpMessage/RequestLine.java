package httpMessage;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private String method;
    private String uri;
    private String protocolVersion;
    private Map<String, String> queryParameters = new HashMap<>();

    public RequestLine(String requestLine) {
        parseRequestLine(requestLine);
    }

    private void parseRequestLine(String requestLine) {
        String[] parts = requestLine.split(" ");

        this.method = parts[0];

        if (hasQueryParameter(parts[1])) {
            this.uri = getUrlBeforeQueryParameter(parts[1]);
            parseQueryParameter(parts[1]);
        } else {
            this.uri = parts[1]; // uri
        }

        this.protocolVersion = parts[2];

    }

    private boolean hasQueryParameter(String uri) {
        if (uri.contains("?")) {
            return true;
        }
        return false;
    }

    private void parseQueryParameter(String uri) {
        String parameter = getQueryParameter(uri);
        String[] params = parameter.split("&");
        for (String param : params) {
            String key = param.split("=")[0];
            String value = param.split("=")[1];
            queryParameters.put(key, value);
        }
    }

    private String getQueryParameter(String uri) {
        String[] tokens = uri.split("\\?"); // /user/create ? userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net
        return tokens[1];
    }

    private String getUrlBeforeQueryParameter(String uri) {
        String[] tokens = uri.split("\\?");
        return tokens[0];
    }

    /* Getter */

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public String getValue(String key) {
        return getQueryParameters().get(key);
    }
}
