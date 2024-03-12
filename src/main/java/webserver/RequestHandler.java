package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.RequestUtil;

public class RequestHandler implements Runnable {
    private static final String DEFAULT_PATH = "./src/main/resources/static";
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;

    public RequestHandler(Socket connectionSocket) { // 소켓 타입의 인자를 받아 connection 필드에 저장
        this.connection = connectionSocket;
    }

    public void run() {
        // 클라이언트 연결 정보 로깅 : 클라이언트가 연결되면, IP 주소와 포트 번호를 로깅한다.
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        // 입출력 스트림 준비 : 클라이언트와의 데이터 교환을 위해 입력 스트림과 출력 스트림을 준비한다.
            // 브라우저에서 서버쪽으로 들어오는 모든 데이터는 InputStream 에 담겨있음
            // 서버에서 브라우저로의 응답은 OutStream 에 실어서 보내다.
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // 일반적으로 헤더는 라인 단위로 구성된다. 라인 단위로 데이터를 읽기 위해 IntStream -> BufferedReader 로 변경한다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            // 첫 번째 라인에서 요청 URL 추츨 (/index.html)
            String line = br.readLine();
            String url = RequestUtil.getUrl(line);

            // 모든 Request Header 출력
            while (!line.isEmpty()){
                logger.debug("Header : {}", line);
                line = br.readLine();
            }
            // 헤더 정보 담아두는 작업 필요함
            String filePath = DEFAULT_PATH + url;
            byte[] body = getHtml(filePath).getBytes();

            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String getHtml(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                sb.append(currentLine);
            }
        }catch (IOException e){
            throw new IOException("file not found : " + path);
        }
        return sb.toString();
    }

    // HTTP 응답 헤더를 클라이언트에게 보낸다
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
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
