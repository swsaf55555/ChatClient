package io;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class NetIO  {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Thread listenThread;
    private boolean connected = false;

    private String host;
    private int port;

    private Timer heartbeatTimer;


    public boolean connect(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            connected = true;

            startListening();
            startHeartbeat();

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
                    connected = false;

                    // 等待 1 秒后重连
                    Thread.sleep(1000);
                    if (!reconnect()) {
                        System.err.println("重连失败，3 秒后再次尝试...");
                    }

                } catch (IOException | InterruptedException e) {
                    System.err.println("监听线程异常：" + e.getMessage());
                    connected = false;
                    try {
                        Thread.sleep(3000); // 防止CPU打满
                    } catch (InterruptedException ignored) {}
                }
            }
        });
        listenThread.setDaemon(true);
        listenThread.start();
    }



    public void onMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            // 将消息添加到 ChatUI 的界面中
            System.out.println("UI线程收到消息：" + message);
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
        }, 5000, 10000); // 5秒后启动，每10秒发送一次
    }


    public boolean reconnect() {
        disconnect();
        System.out.println("尝试重连...");
        return connect(host, port);
    }


    public void disconnect() {
        try {
            connected = false;
            if (heartbeatTimer != null) heartbeatTimer.cancel();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("断开连接异常：" + e.getMessage());
        }
    }


    public boolean isConnected() {
        return connected && socket != null && socket.isConnected() && !socket.isClosed();
    }
}
