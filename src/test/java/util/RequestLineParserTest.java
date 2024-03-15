package util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineParserTest {

    @Test
    void testParseRequestLineWithoutQueryParameters(){
        // case
        String requestLine = "GET /user/create HTTP/1.1";
        RequestLineParser parser = new RequestLineParser(requestLine);

        // when
        String requestURL = parser.getRequestURL();

        // then
        assertThat(requestURL).isEqualTo("/user/create"); // 실제, 기대하는 값
        assertThat(parser.getQueries().isEmpty()); // 쿼리 파라미터가 없으면 당연히 쿼리가 비어 있어야 함
    }

    @Test
    void testParserRequestLineWithQueryParameters(){
        // case
        String requestLine = "GET /create?userId=javajigi&password=password&nickname=박재성 HTTP/1.1";
        RequestLineParser parser = new RequestLineParser(requestLine);

        // when
        parser.parseQueryParameter(requestLine.split(" ")[1]);
        Map<String, String> queries = parser.getQueries();

        // then
        assertThat(parser.getRequestURL()).isEqualTo("/create");
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