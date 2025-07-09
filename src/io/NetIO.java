package io;

import chat.Chat;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.AppState;
import model.Message;
import ui.ChatUI;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class NetIO {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Thread listenThread;
    private boolean connected = false;

    private String host;
    private int port;
    private String USER = "";
    private String PASSWD = "";
    private Timer heartbeatTimer;
    private static NetIO instance = new NetIO();
    private volatile boolean allowReconnect = true;


    public static NetIO getInstance() {
        if (instance == null) instance = new NetIO();
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    private NetIO() {
    }

    public boolean connect(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            connected = true;
            System.out.println("连接服务器成功：" + host + ":" + port);
            return true;
        } catch (IOException e) {
            System.err.println("连接失败：" + e.getMessage());
            connected = false;
            return false;
        }
    }

    public boolean send(String message) {
        if (!connected || writer == null) return false;
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("发送失败：" + e.getMessage());
            connected = false;
            return false;
        }
    }

    public void startListening() {
        listenThread = new Thread(() -> {

            while (true) {
                try {
                    String line;
                    while (connected && (line = reader.readLine()) != null) {
                        onMessage(line);
                    }

                    System.err.println("监听断开，尝试重连...");
                    SwingUtilities.invokeLater(
                            ()->{
                                ChatUI.getInstance().setOffline();
                            }
                    );
                    connected = false;

                    if (!allowReconnect) {
                        System.out.println("已关闭重连机制，监听线程退出");
                        break; // 退出线程
                    }

                    Thread.sleep(1000);
                    if (reconnect()) {
                        startListening();  // 重连成功后重新启动监听
                        startHeartbeat();
                        return;
                    } else {
                        System.out.println("重连失败，3 秒后再次尝试...");
                    }

                } catch (IOException | InterruptedException e) {
                    connected = false;
                    System.err.println("监听线程异常：" + e.getMessage());
                    if (!allowReconnect) {
                        System.out.println("已关闭重连机制，监听线程退出");
                        break; // 主动退出
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ignored) {}
                }
            }

        });
        listenThread.setDaemon(true);
        listenThread.start();
    }

    public void onMessage(String message) {
        Message message1=Chat.parseMessage(message);
        System.out.println("UI线程收到消息：" + message);
        Chat.processMessage(message);
        SwingUtilities.invokeLater(() -> {

        });
    }

    public void sendHeartbeat() {
        if (isConnected()) {
            send("{\"type\":\"heartbeat\"}");
        }
    }

    private void startHeartbeat() {
        if (heartbeatTimer != null) {
            heartbeatTimer.cancel();
        }
        heartbeatTimer = new Timer(true);
        heartbeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendHeartbeat();
            }
        }, 5000, 10000);
    }

    public boolean reconnect() {
        disconnect(1);
        System.out.println("尝试重连...");
        boolean success = connect(host, port);
        if (success && !USER.isEmpty() && !PASSWD.isEmpty()) {
            Chat.sendLogin(USER, PASSWD);
        }
        return success;
    }
    public void disconnect(int i) {
        connected = false;       // 标记为断开
        try {
            if (heartbeatTimer != null) {
                heartbeatTimer.cancel();
                heartbeatTimer = null;
            }

            if (reader != null) {
                reader.close();
                reader = null;
            }

            if (writer != null) {
                writer.close();
                writer = null;
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null;
            }

            if (listenThread != null && listenThread.isAlive()) {
                listenThread.interrupt();  // 仅通知中断，不能强杀
                listenThread = null;
            }

            System.out.println("连接已主动断开。");
        } catch (IOException e) {
            System.err.println("断开连接异常：" + e.getMessage());
        }
    }

    public void disconnect() {
        allowReconnect = false;  // 禁止后续重连
        connected = false;       // 标记为断开
        try {
            if (heartbeatTimer != null) {
                heartbeatTimer.cancel();
                heartbeatTimer = null;
            }

            if (reader != null) {
                reader.close();
                reader = null;
            }

            if (writer != null) {
                writer.close();
                writer = null;
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null;
            }

            if (listenThread != null && listenThread.isAlive()) {
                listenThread.interrupt();  // 仅通知中断，不能强杀
                listenThread = null;
            }

            System.out.println("连接已主动断开。");
        } catch (IOException e) {
            System.err.println("断开连接异常：" + e.getMessage());
        }
    }


    public boolean isConnected() {
        return connected && socket != null && socket.isConnected() && !socket.isClosed();
    }

    public String receive() throws IOException {
        if (reader != null && connected) {
            return reader.readLine();
        }
        return null;
    }

    public boolean connectAndLogin(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        try {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            Chat.sendLogin(username, password);
            String reply = receive();
            if (reply == null) return false;
            JsonObject responseJson = JsonParser.parseString(reply).getAsJsonObject();
            Message response = new Message(responseJson);
            System.out.println("statu:" + response.getStatus() + response.getMessage());
            if ("ok".equals(response.getStatus()) && "login".equals(response.getMessage())) {
                connected = true;
                this.USER = username;
                this.PASSWD = password;
                startListening();
                startHeartbeat();
                return true;
            }
        } catch (IOException e) {
            this.USER = "";
            this.PASSWD = "";
//            disconnect();
            System.err.println("登录失败：" + e.getMessage());
        }
        return false;
    }
}
