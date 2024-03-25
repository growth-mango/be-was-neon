package model;

public class User {
    private String userId;
    private String password;
    private String nickName;

    public User(String userId, String password, String nickName) {
        this.userId = userId;
        this.password = password;
        this.nickName = nickName;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword(String password) {
        return this.password;
    }

    public String getNickName() {
        return nickName;
    }


    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", nickName=" + nickName + "]";
    }
}
