package httpMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;


public class HttpResponse {
    private DataOutputStream dos;
    private StringBuilder headers = new StringBuilder();
    private byte[] body;
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void addHeader(String name, String value) {
        headers.append(name).append(": ").append(value).append("\r\n");
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void response302(DataOutputStream dos) {
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

    // register.html 을 클라이언트에게 보낸다.
    public void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


}
