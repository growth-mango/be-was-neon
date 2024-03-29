package webserver;

import db.Database;
import httpMessage.HttpRequest;
import httpMessage.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import session.SessionStore;

import java.io.DataOutputStream;

public class Login {
    private static final Logger logger = LoggerFactory.getLogger(Login.class);

    public void verifyLogin(HttpRequest httpRequest, HttpResponse httpResponse, DataOutputStream dos) {
        // 사용자가 로그인 폼에서 입력한 id와 password (post로 가정...)
        String inputId = httpRequest.getBodyParam("userid");
        String inputPassword = httpRequest.getBody().getValue("password");
        // db에서 사용자가 입력한 id 꺼내서 user에 저장하기
        User user = Database.findUserById(inputId);

        if (user == null) { // 사용자가 입력한 id의 유저가 db에 없거나
            logger.debug("User Not Found");
            httpResponse.response302failedLogin(dos);
        } else if (!user.getPassword().equals(inputPassword)) { // db에 저장된 password가 사용자가 입력한 password와 다르거나
            logger.debug("Password is incorrect");
            httpResponse.response302failedLogin(dos);
        } else { // 로그인 성공!
            logger.debug("Login Successful!!");
            String sessionId = SessionStore.createSession(user);
            httpResponse.response302WithSession(dos, sessionId);
        }
    }

}
