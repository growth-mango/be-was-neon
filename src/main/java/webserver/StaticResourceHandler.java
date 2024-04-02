package webserver;

import httpMessage.HttpResponse;
import util.ContentType;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class StaticResourceHandler {
    private static final String DEFAULT_PATH = "./src/main/resources/static";

    public void serveStaticResource(String url, HttpResponse httpResponse, DataOutputStream dos) throws IOException {
        // 모든 정적 리소스를 공통된 방식으로 처리 -> 경로 바꿈 : static/registration/login.html -> static/register.html
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
