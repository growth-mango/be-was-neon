package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

public class HttpRequestUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class); // logger 는 로그 메시지를 출력하기 위해 사용된다.

    public static String getUrl(String firstLine) {
        // 첫 번째 라인에서 요청 URL 추출, 스페이스 기반으로 스플릿
        String[] tokens = firstLine.split(" ");
        if (tokens.length > 1) {
            String path = tokens[1];
            logger.debug("request path : {}", path);
            return path;
        } else {
            logger.error("Invalid Request Line: {}", firstLine);
            return "";
        }
    }
}
