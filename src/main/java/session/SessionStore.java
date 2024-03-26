package session;

import model.User;

import java.util.HashMap;
import java.util.Map;

public class SessionStore {
    private static final Map<String, User> sessions = new HashMap<>();

    public static String createSession(User user) {
        String sessionId = SessionGenerator.sessionId();
        sessions.put(sessionId, user);
        return sessionId;
    }

    public static User getUserBySessionId(String sessionId) {
        return sessions.get(sessionId);
    }

    public static void removeSession(String sessionId) { // 로그아웃에 사용하기 위함
        sessions.remove(sessionId);
    }
}
