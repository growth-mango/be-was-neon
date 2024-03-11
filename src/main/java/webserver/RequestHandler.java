package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
//        InputStream => InputStreamReader => BufferedReader
//        BufferedReader.readLine() 메소드 활용해 라인별로 http header 읽는다.
        // InputStream을 BufferedReader 로 바꿔주는 API를 확인한다?
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine(); // 첫 번재 라인에 해당하는 이 부분에서 index.html을 읽은 다음에 추출 -> src/main/resources/static 디렉토리에 있는 Hello World 대신 응답으로 쏴주면
            logger.debug("request line : {}", line);
            System.out.println("request : " + line); // 한 줄만 출력됨
            // 요청에 대한 모든 라인(데이터)을 찍어보려면? while 문 돌면서 찍어본다
            while (!line.equals("")){ // 공백문자열을 만나기 전 까지 , 공백문자열 만나면 while 문 빠져나감
                line = br.readLine();
                logger.debug("header : {}", line);
            }
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            // 예시 코드 : byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
//            byte[] body = "<h1>Hello World</h1>".getBytes();
            byte[] body = Files.readAllBytes(new File("./src/main/resources/static/index.html").toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            // 텍스트만 잘 나오게 처리가 되어 있음 -> img 도 나오게 처리 해줘야함
            // local host만 띄웠을 때, dafault로 index.html이 뜨게끔 처리를 해줘야 한다
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
