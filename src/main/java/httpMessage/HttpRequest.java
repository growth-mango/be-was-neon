package httpMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {
    private RequestLine requestLine;
    private Headers headers = new Headers();
    private Body body;

    // ⭐HttpRequest 는 다른 클래스에서 받아 결과적으로 만들어지는 데이터 구조를 가지는 클래스
    // 그 다른 클래스가 어떤 클래스인지 생성할 때 결정하지 않아도 됨 -> HttpRequest 를 생성하기 위해 필요한 값만 받으면 생성할 수 있어야 함
    // 그래야 테스트 관점에서도 HttpRequest 만 생성하고 테스트하기 수월하다
    // 즉, 다양한 시나리오를 수용할 수 있도록 유연성을 높여야함! 현재는 InputStream 을 통해서만 생성가능...
    // 생성자 오버 로딩 : 생성자 여러 개 제공
    // 빌더 패턴 : 객체 생성 과정을 단계별로 수행할 수 있게 해줌
    public HttpRequest(InputStream in) throws IOException { // HttpRequest 객체 생성될 때 초기화 ...
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String initialLine = br.readLine();
        this.requestLine = new RequestLine(initialLine); // 요청 라인
        this.headers.readHeaders(br); // 헤더

        // Content-Length 헤더 참고해서 본문 길이 얻기 -> 이를 이용해 본문 데이터 읽기
        // Content-Length? 표현 데이터의 길이 (바이트단위) ex) 본문 hello, Content-Length : 5
        if (headers.containKey("Content-Length")) {
            int contentLength = headers.getContentLength();
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars, 0, contentLength); // 0부터 contentLength 까지 bodyChars 에 저장
            this.body = new Body(new String(bodyChars));
        }

    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Body getBody() {
        return body;
    }
}