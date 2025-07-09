package model;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class AppState {
    private static final AppState instance = new AppState();

    private User currentUser;  // 当前登录的用户
    private final List<User> onlineUsers;  // 在线用户列表
    private final Map<String, List<Message>> messageHistory; // 与每位用户的聊天记录

    private AppState() {
        onlineUsers = new CopyOnWriteArrayList<>();
        messageHistory = new HashMap<>();
    }

    public static AppState getInstance() {
        return instance;
    }

    // 当前用户
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // 在线用户管理
    public List<User> getOnlineUsers() {
        return onlineUsers;
    }

    public void addOnlineUser(User user) {
        onlineUsers.removeIf(u -> u.getUsername().equals(user.getUsername()));
        onlineUsers.add(user);
    }

    public void removeOnlineUser(String username) {
        onlineUsers.removeIf(u -> u.getUsername().equals(username));
    }

    // 聊天记录管理
    public void addMessage(String chatWith, Message msg) {
        messageHistory.computeIfAbsent(chatWith, k -> new ArrayList<>()).add(msg);
    }

    public List<Message> getMessages(String chatWith) {
        return messageHistory.getOrDefault(chatWith, new ArrayList<>());
    }
}
