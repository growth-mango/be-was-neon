package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
public class RequestUtilTest {

    @Test
    @DisplayName("/index.html 이 잘 추출된다")
    public void testGetUrl(){
        // 실제값 , 기대값
        assertThat("/index.html").isEqualTo(RequestUtil.getUrl("GET /index.html HTTP1.1"));
    }
}
