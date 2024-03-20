package webserver;

import java.io.*;
import java.net.Socket;

import httpMessage.HttpRequest;
import httpMessage.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ContentType;

public class RequestHandler implements Runnable {
    private static final String DEFAULT_PATH = "./src/main/resources/static";

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;

    public RequestHandler(Socket connectionSocket) { // 소켓 타입의 인자를 받아 connection 필드에 저장
        this.connection = connectionSocket;
    }

    public void run() {
        // 클라이언트 연결 정보 로깅 : 클라이언트가 연결되면, IP 주소와 포트 번호를 로깅한다.
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = new HttpResponse(dos);

            httpRequest.getHeaders().printHeaders();

            processRequest(httpRequest, httpResponse, dos);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    // ⭐private 메서드들은 자신의 역할을 하는 클래스로 분리할 수 있음
    // 그럼 테스트할 가능성도 높아짐!
    private void processRequest(HttpRequest httpRequest, HttpResponse httpResponse, DataOutputStream dos) throws IOException {
        String method = httpRequest.getRequestLine().getMethod();
        String url = httpRequest.getRequestLine().getUri();

        if (url.startsWith("/create")) { // 회원가입 /create?name=mango&password=1234&nickname=ffff ...
            if ("GET".equals(method)){ // 메서드에 따라 다르게 처리하기
                processSignUpGet(httpRequest, httpResponse, dos);
            } else if ("POST".equals(method)) {
                processSignUpPost(httpRequest, httpResponse, dos);
            }

        } else {
            serveStaticResource(url, httpResponse, dos); // 그 외 static 리소스는 같은 방식으로 처리
        }
    }

    private void processSignUpPost(HttpRequest httpRequest, HttpResponse httpResponse, DataOutputStream dos) {
        User user = new User(httpRequest.getBody().getValue("userid"), httpRequest.getBody().getValue("password"), httpRequest.getBody().getValue("nickname"));
        // 그리고 다시 register.html 로 돌아간다 -> 200 아니고 302 응답
        httpResponse.response302(dos);
        logger.debug("User : {}", user);
    }

    private void processSignUpGet(HttpRequest httpRequest, HttpResponse httpResponse, DataOutputStream dos) {
        User user = new User(httpRequest.getRequestLine().getValue("userid"), httpRequest.getRequestLine().getValue("password"), httpRequest.getRequestLine().getValue("nickname"));
        httpResponse.response302(dos);
        logger.debug("User : {}", user);
    }

    private void serveStaticResource(String url, HttpResponse httpResponse, DataOutputStream dos) throws IOException {
        // 모든 정적 리소스를 공통된 방식으로 처리 -> 경로 바꿈 : static/registration/index.html -> static/register.html
        String filePath = DEFAULT_PATH + url;
        byte[] body = getHtml(filePath).getBytes();
        String contentType = getContentType(filePath);

        httpResponse.setBody(body);
        httpResponse.response200Header(dos, body.length, contentType);
        httpResponse.responseBody(dos, body);
    }

    // 파일 경로의 내용을 읽어, 그 내용을 단일 문자열로 반환하는 기능 (주로 HTML 파일이나 다른 텍스트 기반 파일의 내용을 읽어올 때 사용)
    private String getHtml(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                sb.append(currentLine);
            }
        } catch (IOException e) {
            throw new IOException("file not found : " + path);
        }
        return sb.toString();
    }

    // 파일 확장자에 따라 적절한 Content-Type을 반환한다
    private String getContentType(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex != -1) {
            String extension = filePath.substring(dotIndex + 1);
            return ContentType.findByExtension(extension).getMimeType();
        }
        return ContentType.DEFAULT.getMimeType();
    }
}
