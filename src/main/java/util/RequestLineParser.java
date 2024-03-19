package util;

import java.util.HashMap;
import java.util.Map;

// 요청 라인에서 url path  추출, query param  추출해야해
public class RequestLineParser {
    private String requestURL; // 인스턴스 변수 => 인스턴스 생성할 때 마다 값이 초기화 된다.
    private Map<String, String> queries = new HashMap<>();

    // 객체 생성 할때, RequestLine (즉, 첫 번째 라인) 세팅 해야 함 -
    // Request Line : Method URI(path + query string(parameter)를 포함하는 보다 넓은 범위 HTTPversion(protocol)
    public RequestLineParser(String firstLine) {
        String[] tokens = firstLine.split(" ");// GET /register.html?12341234=asdfsdf&asdfsadf&sdfasdf HTTP1.1 ...
        String uri = tokens[1];
        if (hasQueryParameter(uri)) { // 물음표를 포함하는지 확인한다. // 물음표가 있으면?
            requestURL = getUrlBeforeQueryParameter(uri); // uri에서 물음표 떼고 path만 반환하다
        } else {
            requestURL = uri; // GET /user/create -> /user/create
        }
    }

    private boolean hasQueryParameter(String uri) {
        if (uri.contains("?")) {
            return true;
        }
        return false;
    }

    // http://localhost:8080/create?username=dsdf&nickname=sdf&password=sdfsd
    // 예) GET /user/create?userId=javajigi&password=password&nickname=%EB%B0%95%EC%9E%AC%EC%84%B1
    private String getUrlBeforeQueryParameter(String uri) { // uri 에서 물음표(쿼리 파라미터) 이전 path 를 얻는다
        String[] tokens = uri.split("\\?"); // /user/create ? userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net
        return tokens[0]; // only path
    }

    public void parseQueryParameter(String uri) { // -> 사실 인스턴스 변수에 쿼리 파라미터를 저장하는 것 외에는 테스트 용도로밖에 안쓰이는데... public 으로 두는게 맞나.. 근데 또 테스트는 해야되겠고...
        String parameter = getQueryParameter(uri);
        String[] params = parameter.split("&");
        for(String param : params){
            String key = param.split("=")[0];
            String value = param.split("=")[1];
            queries.put(key, value);
        }
    }

    // 물음표 뒤의 쿼리 파라미터 반환한다
    private String getQueryParameter(String uri) {
        String[] tokens = uri.split("\\?"); // /user/create ? userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net
        return tokens[1];
    }

    public String getRequestURL() {
        return requestURL;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public String getValue(String key){
        return getQueries().get(key);
    }
}
