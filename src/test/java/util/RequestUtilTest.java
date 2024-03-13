package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
public class RequestUtilTest {

    @Test
    @DisplayName("/index.html 이 추출되면 테스트가 성공한다")
    public void testGetUrl(){
        // 실제값 , 기대값
        assertThat("/index.html").isEqualTo(RequestUtil.getUrl("GET /index.html HTTP1.1"));
    }
}
