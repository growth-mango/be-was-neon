package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

// 이 서버는 클라이언트의 요청을 받아 처리하고,
// index.html 파일의 내용을 클라이언트에게 응답으로 보내는 역할을 한다.
public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class); // 로그 메시지를 출력하기 위해 사용된다.

    private Socket connection; // 클라이언트와의 네트워크 연결을 나타낸다.

    public RequestHandler(Socket connectionSocket) { // 소켓 타입의 인자를 받아 connection 필드에 저장
        this.connection = connectionSocket;
    }

    public void run() { // Runnable 인터페이스를 구현한 것!
        // 클라이언트 연결 정보 로깅 : 클라이언트가 연결되면, IP 주소와 포트 번호를 로깅한다.
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        // 입출력 스트림 준비 : 클라이언트와의 데이터 교환을 위해 입력 스트림과 출력 스트림을 준비한다.
        // 브라우저에서 서버쪽으로 들어오는 모든 데이터는 InputStream 에 담겨있음
        // 서버에서 브라우저로의 응답은 OutStream 에 실어서 보내다.
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // 일반적으로 헤더는 라인 단위로 구성된다. 라인 단위로 데이터를 읽기 위해 BufferedReader 로 변경
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine(); // 첫 번째 라인에 해당하는 이 부분에서 index.html을 읽은 다음에 추출 -> src/main/resources/static 디렉토리에 있는 Hello World 대신 응답으로 쏴주면

            if(line == null){ // null 처리, null인 경우 무시한다.
                return;
            }

            // 첫 번째 요청 라인 로깅
            logger.debug("Request Line : {}", line);

            // 헤더 라인 읽고 출력 -> 빈 라인이 나타날때 까지 반복 -> 모든 Request Header 출력
            while (!line.equals("")){
                line = br.readLine();
                logger.debug("Header : {}", line);
            }

            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            // 응답 데이터 준비 : index.html 파일을 읽어들여 클라이언트에게 보낼 데이터로 준비한다.
            String url = HttpRequestUtils.getUrl(line);
            DataOutputStream dos = new DataOutputStream(out);
            String filePath = "./src/main/resources/static/index.html" + url;
            File file = new File(filePath);
            byte[] body = Files.readAllBytes(file.toPath());
            // HTTP 응답 보내기 : 클라이언트에게 HTTP 상태 코드 200 OK 를 포함한 응답 헤더를 보내고
            // 준비된 데이터를 응답 본문으로 보낸다
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    // HTTP 응답 헤더를 클라이언트에게 보낸다
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
//            dos.writeBytes("Content-Type: " + getContentType(fileName) + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    // index.html 을 클라이언트에게 보낸다.
    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
