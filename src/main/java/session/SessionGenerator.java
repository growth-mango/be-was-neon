package session;

import java.util.Random;

public class SessionGenerator {
    private static final int SESSION_LENGTH = 6;
    private static final Random random = new Random();

    public static String sessionId() {
        StringBuilder resultNum = new StringBuilder(SESSION_LENGTH);
        for (int i = 0; i < SESSION_LENGTH; i++) {
            int digit = random.nextInt(10);
            resultNum.append(digit);
        }
        return resultNum.toString();
    }

}
