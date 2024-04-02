package webserver;

import db.Database;
import httpMessage.Body;
import httpMessage.HttpRequest;
import httpMessage.HttpResponse;
import httpMessage.RequestLine;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.DataOutputStream;

import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;

class SignUpTest {
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private DataOutputStream dos;
    private SignUp signUp;

    @BeforeEach
    void setUp() {
        httpRequest = mock(HttpRequest.class);
        httpResponse = mock(HttpResponse.class);
        dos = mock(DataOutputStream.class);
        signUp = new SignUp();

        // Body 객체에 대한 모의 처리 추가
        Body mockBody = mock(Body.class);
        when(httpRequest.getBody()).thenReturn(mockBody);

        // RequestLine 객체에 대한 모의 처리 추가
        RequestLine mockRequestLine = mock(RequestLine.class);
        when(httpRequest.getRequestLine()).thenReturn(mockRequestLine);

        // 모의 데이터 생성
        when(httpRequest.getBody().getValue("userid")).thenReturn("testUser");
        when(httpRequest.getBody().getValue("password")).thenReturn("testPassword");
        when(httpRequest.getBody().getValue("nickname")).thenReturn("testNickname");

        when(httpRequest.getRequestLine().getValue("userid")).thenReturn("testUser");
        when(httpRequest.getRequestLine().getValue("password")).thenReturn("testPassword");
        when(httpRequest.getRequestLine().getValue("nickname")).thenReturn("testNickname");
    }

    @Test
    @DisplayName("POST 요청으로 회원 가입이 되는지 확인한다")
    void testProcessSignUpPost() {
        try (MockedStatic<Database> mockedDatabase = Mockito.mockStatic(Database.class)) {
            signUp.processSignUpPost(httpRequest, httpResponse, dos);
            User user = new User("testUser", "testPassword", "testNickname");

            mockedDatabase.verify(() -> Database.addUser(refEq(user)));

            verify(httpResponse).response302(dos);
        }
    }

    @Test
    @DisplayName("GET 요청으로 회원 가입이 되는지 확인한다")
    void testProcessSignUpGet() {
        try (MockedStatic<Database> mockedDatabase = Mockito.mockStatic(Database.class)) {
            signUp.processSignUpGet(httpRequest, httpResponse, dos);
            User user = new User("testUser", "testPassword", "testNickname");

            mockedDatabase.verify(() -> Database.addUser(refEq(user)));

            verify(httpResponse).response302(dos);
        }
    }
}