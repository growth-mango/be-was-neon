package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineParserTest {

    @Test
    @DisplayName("쿼리 파라미터가 없는 경우에 경로 추출이 잘 되는지 확인한다.")
    void testUrlParseRequestLineWithoutQueryParameters(){
        // case
        String requestLine = "GET /user/create HTTP/1.1";
        RequestLineParser parser = new RequestLineParser(requestLine);

        // when
        String requestURL = parser.getRequestURL();

        // then
        assertThat(requestURL).isEqualTo("/user/create"); // 실제, 기대하는 값
    }

    @Test
    @DisplayName("쿼리 파라미터가 있는 경우에 경로 추출이 잘 되는지 확인 한다.")
    void testUrlParserRequestLineWithQueryParameters(){
        // case
        String requestLine = "GET /create?userId=javajigi&password=password&nickname=박재성 HTTP/1.1";
        RequestLineParser parser = new RequestLineParser(requestLine);

        // when
        parser.parseQueryParameter(requestLine.split(" ")[1]);

        // then
        assertThat(parser.getRequestURL()).isEqualTo("/create");
    }

    @Test
    @DisplayName("쿼리 파라미터가 있는 경우에 쿼리 스트링이 키 : 값 으로 잘 분류되는지 확인 한다.")
    void testQueriesParserRequestLineWithQueryParameter(){
        // case
        String requestLine = "GET /create?userId=javajigi&password=password&nickname=박재성 HTTP/1.1";
        RequestLineParser parser = new RequestLineParser(requestLine);

        // when
        parser.parseQueryParameter(requestLine.split(" ")[1]);
        Map<String, String> queries = parser.getQueries();

        // then
        assertThat(queries).containsAllEntriesOf(
                Map.of(
                        "userId", "javajigi",
                        "password", "password",
                        "nickname", "박재성"
                )
        );
    }

    @Test
    void testGetValueFromQueryParameters(){
        // case
        String requestLine = "GET /create?userId=javajigi&password=password&nickname=박재성 HTTP/1.1";
        RequestLineParser parser = new RequestLineParser(requestLine);

        // when
        parser.parseQueryParameter(requestLine.split(" ")[1]);
        String userId = parser.getValue("userId");
        String nickname = parser.getValue("nickname");

        // then
        assertThat(userId).isEqualTo("javajigi");
        assertThat(nickname).isEqualTo("박재성");
    }
}