package util;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    ICO("ico", "image/x-icon"),
    JPEG("jpg", "image/jpeg"),
    JS("js", "application/javascript"),
    PNG("png", "image/png"),
    SVG("svg", "image/svg+xml"),
    DEFAULT("", "text/html");

    private final String extension;
    private final String mimeType;

    ContentType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getMimeType(){
        return mimeType;
    }

    public static ContentType findByExtension(String extension){
        for (ContentType type : values() ) {
            if(type.extension.equals(extension)){
                return type;
            }
        }
        return DEFAULT;
    }
}
