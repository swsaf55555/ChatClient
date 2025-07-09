package io;

public interface Net {
    /**
     * 初始化连接到服务器
     * @param host 服务器地址（如 "localhost"）
     * @param port 端口号（如 12345）
     * @return 是否连接成功
     */
    boolean connect(String host, int port);

    /**
     * 向服务器发送消息
     * @param message 要发送的内容（可为 JSON 字符串）
     * @return 是否发送成功
     */
    boolean send(String message);

    /**
     * 持续监听服务器发送来的消息（通常在独立线程中运行）
     */
    void startListening();

    /**
     * 处理收到的消息（可作为回调函数供上层调用）
     * @param message 收到的原始消息内容
     */
    void onMessage(String message);

    /**
     * 定期发送心跳包，保持连接活跃（可由定时器驱动）
     */
    void sendHeartbeat();

    /**
     * 当连接断开时，尝试自动重连服务器
     * @return 是否重连成功
     */
    boolean reconnect();

    /**
     * 主动断开与服务器的连接
     */
    void disconnect();

    /**
     * 获取当前连接状态
     * @return true 表示连接中，false 表示已断开
     */
    boolean isConnected();
}
