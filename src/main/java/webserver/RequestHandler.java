package webserver;

import httpMessage.HttpRequest;
import httpMessage.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private static final String DEFAULT_PATH = "./src/main/resources/static";

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;
    private Router router = new Router();

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

            router.route(httpRequest, httpResponse, dos);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}

