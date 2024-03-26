package webserver;

import httpMessage.HttpRequest;
import httpMessage.HttpResponse;

import java.io.DataOutputStream;
import java.io.IOException;

public class Router {
    private SignUp signUp = new SignUp();
    private Login login = new Login();
    private StaticResourceHandler staticResourceHandler = new StaticResourceHandler();

    public void route(HttpRequest httpRequest, HttpResponse httpResponse, DataOutputStream dos) throws IOException {

        String method = httpRequest.getRequestLine().getMethod();
        String url = httpRequest.getRequestLine().getUri();

        if (url.startsWith("/create")) { // 회원가입 /create?name=mango&password=1234&nickname=ffff ...
            if ("GET".equals(method)) { // 메서드에 따라 다르게 처리하기
                signUp.processSignUpGet(httpRequest, httpResponse, dos);
            } else if ("POST".equals(method)) {
                signUp.processSignUpPost(httpRequest, httpResponse, dos);
            }

        } else if (url.startsWith("/login")) {
            if ("POST".equals(method)) {
                login.verifyLogin(httpRequest, httpResponse, dos);
            } else {
                staticResourceHandler.serveStaticResource(url, httpResponse, dos);
            }

        } else {
            staticResourceHandler.serveStaticResource(url, httpResponse, dos); // 그 외 static 리소스는 같은 방식으로 처리
        }
    }

}

