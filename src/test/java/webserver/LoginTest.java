package webserver;

import db.Database;
import httpMessage.Body;
import httpMessage.HttpRequest;
import httpMessage.HttpResponse;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import session.SessionStore;

import java.io.DataOutputStream;

import static org.mockito.Mockito.*;

// verifyLogin 메소드를 테스트하기 위해, HttpRequest, HttpResponse, DataOutputStream, 그리고 Database 클래스의 동작을 모의(Mock) 객체로 대체
// 실제 데이터베이스나 네트워크 연결 없이 verifyLogin 메소드의 로직만을 순수하게 테스트할 수 있음
class LoginTest {
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private Body body;
    private DataOutputStream dos;
    private Login login;

    @BeforeEach
    void setUp() {
        httpRequest = mock(HttpRequest.class); // 모의 객체 생성 -> 클래스의 실제 구현을 사용하지 않고, 테스트에서 정의된 동작 모방 (테스트 시에 지정한 행동이나 결과를 반환하도록 설정)
        httpResponse = mock(HttpResponse.class);
        body = mock(Body.class);
        dos = mock(DataOutputStream.class);
        login = new Login();

        // Body 객체에 대한 모의 처리 추가
        Body mockBody = mock(Body.class);
        when(httpRequest.getBody()).thenReturn(mockBody);

        // 모의 데이터 생성
        when(httpRequest.getBody().getValue("userid")).thenReturn("testUser"); // 실제 formData (사용자가 입력한) 를 조회하지 않고, testUser 를 return
        when(httpRequest.getBody().getValue("password")).thenReturn("testPassword");
    }

    @Test
    @DisplayName("DB에 없는 사용자가 로그인 시도 시, 로그인 실패 응답이 호출되는지 확인한다")
    void testLoginWihInvalidUser() {
        try (MockedStatic<Database> mocked = Mockito.mockStatic(Database.class)) {
            mocked.when(() -> Database.findUserById("testUser")).thenReturn(null);

            // when : 테스트의 주된 행동, 메소드 호출 : 테스트 하려는 verifyLogin 메소드 호출
            login.verifyLogin(httpRequest, httpResponse, dos);

            // then : 행동의 결과로 기대되는 상황 검증 : response302failedLogin 메소드가 정확히 한 번 호출되었는지 검증(verify)
            verify(httpResponse, times(1)).response302failedLogin(dos);
        }
    }

    @Test
    @DisplayName("비밀번호 불일치 시, 로그인 실패 응답이 호출되는지 확인한다")
    void testLoginWithWrongPassword() {
        // 잘못된 비번 가진 사용자 설정
        User fakeUser = new User("testUser", "wrongPassword", "testMango");

        try (MockedStatic<Database> mockedStatic = Mockito.mockStatic(Database.class)) {
            mockedStatic.when(() -> Database.findUserById("testUser")).thenReturn(fakeUser);

            login.verifyLogin(httpRequest, httpResponse, dos);

            verify(httpResponse, times(1)).response302failedLogin(dos);
        }
    }

    @Test
    @DisplayName("DB에 있는 사용자가 올바른 아이디와 비밀번호를 입력했을 때, 로그인 성공 응답이 호출되는지 확인한다")
    void testSuccessLogin() {
        try (MockedStatic<Database> mockedDatabase = Mockito.mockStatic(Database.class)) {
            User fakeUser = new User("testUser", "testPassword", "testNickname");
            when(Database.findUserById("testUser")).thenReturn(fakeUser);

            try (MockedStatic<SessionStore> mockedSessionStore = Mockito.mockStatic(SessionStore.class)) {
                mockedSessionStore.when(() -> SessionStore.createSession(any(User.class))).thenReturn("sessionId");

                // 모의된 설정을 사용하는 테스트 코드 부분
                login.verifyLogin(httpRequest, httpResponse, dos);

                // 검증
                verify(httpResponse, times(1)).response302WithSession(dos, "sessionId");
            }
        }
    }
}