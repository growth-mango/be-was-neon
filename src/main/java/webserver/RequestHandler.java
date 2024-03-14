package webserver;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.RequestLineParser;

public class RequestHandler implements Runnable {
    private static final String DEFAULT_PATH = "./src/main/resources/static";
    private static final String SIGN_UP_URL_PATH = "/register.html";

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;
    private Map<String, String> httpHeaders = new HashMap<>();

    public RequestHandler(Socket connectionSocket) { // 소켓 타입의 인자를 받아 connection 필드에 저장
        this.connection = connectionSocket;
    }

    public void run() {
        // 클라이언트 연결 정보 로깅 : 클라이언트가 연결되면, IP 주소와 포트 번호를 로깅한다.
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            DataOutputStream dos = new DataOutputStream(out);

            // 첫 번째 라인에서 요청 URL 추츨 (/index.html)
            String line = br.readLine();
            RequestLineParser requestLineParser = new RequestLineParser(line);
            String url = requestLineParser.getRequestURL();

            // header 출력
            printHttpHeader(line, br);

            // 📌 만약에 path 가 create 로 시작하면
            if(url.startsWith("/create")) {
                // 파싱 한 정보를 User 에 넘긴다
                User user = new User(requestLineParser.getValue("userId"), requestLineParser.getValue("nickName"), requestLineParser.getValue("password"));
                // 그리고 다시 index.html 로 돌아간다 -> 200 아니고 302 코드 던지기
                response302(dos);
                return;
            }

            String filePath;
            if (url.equals(SIGN_UP_URL_PATH)) {
                filePath = "./src/main/resources/static/registration/index.html";
            } else {
                filePath = DEFAULT_PATH + url;
            }

            byte[] body = getHtml(filePath).getBytes();


            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

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

    private void printHttpHeader(String line, BufferedReader br) throws IOException{
        while ((line = br.readLine()) != null && !line.isEmpty()) { // 첫 번째 라인 (요청 라인) 은, 헤더가 아니기에 건너뛰고 시작한다.
            int separator = line.indexOf(":");
            if (separator != -1) {
                String name = line.substring(0, separator).trim();
                String value = line.substring(separator + 1).trim();
                httpHeaders.put(name, value);
            }
        }

        // Request Header 정돈해서 출력
        for (Map.Entry<String, String> header : httpHeaders.entrySet()) {
            logger.debug("Header Key: \"{}\" Value: \"{}\"", header.getKey(), header.getValue());
        }
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

    private void response302(DataOutputStream dos){
        String redirectURL = "/index.html";
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND\r\n");
            dos.writeBytes("Location: " + redirectURL + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
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
