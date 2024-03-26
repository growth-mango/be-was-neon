package webserver;

import db.Database;
import httpMessage.HttpRequest;
import httpMessage.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;

public class SignUp {
    private static final Logger logger = LoggerFactory.getLogger(SignUp.class);

    public void processSignUpPost(HttpRequest httpRequest, HttpResponse httpResponse, DataOutputStream dos) {
        User user = new User(httpRequest.getBody().getValue("userid"), httpRequest.getBody().getValue("password"), httpRequest.getBody().getValue("nickname"));
        // 로그인 정보와 비교하기 위해 db에 저장하기
        Database.addUser(user);
        logger.debug("Database : {}", Database.findAll());
        // 그리고 다시 register.html 로 돌아간다 -> 200 아니고 302 응답
        httpResponse.response302(dos);
        logger.debug("User : {}", user);
    }

    public void processSignUpGet(HttpRequest httpRequest, HttpResponse httpResponse, DataOutputStream dos) {
        User user = new User(httpRequest.getRequestLine().getValue("userid"), httpRequest.getRequestLine().getValue("password"), httpRequest.getRequestLine().getValue("nickname"));
        Database.addUser(user);
        httpResponse.response302(dos);
        logger.debug("User : {}", user);
    }
}
