package httpMessage;

import java.util.HashMap;
import java.util.Map;

public class Body {
    private String content;
    private Map<String, String> formData = new HashMap<>();

    public Body(String content) {
        this.content = content;
        parseFormData(content);
    }

    private void parseFormData(String content) {
        if (content == null || content.isEmpty()) return;
        String[] pairs = content.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = keyValue[0];
            String value = keyValue.length > 1 ? keyValue[1] : "";
            formData.put(key, value);
        }
    }

    public String getContent() {
        return content;
    }

    public Map<String, String> getFormData() {
        return formData;
    }

    public String getValue(String key){
        return formData.get(key);
    }
}
