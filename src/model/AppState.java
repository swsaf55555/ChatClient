package model;

import io.NetIO;
import ui.ChatUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class AppState {
    private static  AppState instance = new AppState();

    private User currentUser;
    private final List<User> onlineUsers;
    private final Map<String, List<Message>> messageHistory;

    private NetIO netIO; //

    private AppState() {
        onlineUsers = new CopyOnWriteArrayList<>();
        messageHistory = new HashMap<>();
    }

    public static AppState getInstance() {
        if (instance == null) instance = new AppState();
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    //  新增 NetIO 管理方法
    public void setNetIO(NetIO netIO) {
        this.netIO = netIO;
    }

    public NetIO getNetIO() {
        return netIO;
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
