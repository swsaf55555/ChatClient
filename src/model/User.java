package model;

public class User {
    private String username;     // 用户名（唯一标识）
    private String nickname;     // 昵称（显示用）
    private String avatarPath;   // 头像路径（本地文件名或网络 URL）
    private boolean online;      // 是否在线


    public User(String username) {
        this.username = username;
        this.nickname = username;  // 默认昵称与用户名相同
        this.online = true;
    }

    // 可选：带头像/昵称构造器
    public User(String username, String nickname, String avatarPath, boolean online, String status) {
        this.username = username;
        this.nickname = nickname;
        this.avatarPath = avatarPath;
        this.online = online;
    }

    // Getters & Setters
    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public boolean isOnline() {
        return online;
    }


    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

}
