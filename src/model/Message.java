package model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Message {
    private String type;      // 消息类型：login, chat, group, response, notice...
    private String status;    // 状态：ok, error, pending...
    private String message;   // 文本内容（服务器消息、提示、聊天内容）
    private String sender;    // 发送者用户名
    private String target;    // 接收者（用户名或群组）
    private long timestamp;   // 时间戳

    public Message(String type, String status, String message) {
        this.type = type;
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public Message(String type, String status, String message, String sender, String target) {
        this(type, status, message);
        this.sender = sender;
        this.target = target;
    }

    public Message(JsonObject json) {
        this.type = json.has("type") ? json.get("type").getAsString() : "";
        this.status = json.has("status") ? json.get("status").getAsString() : "";
        this.message = json.has("message") ? json.get("message").getAsString() : "";
        this.sender = json.has("sender") ? json.get("sender").getAsString() : "";
        this.target = json.has("target") ? json.get("target").getAsString() : "";
        this.timestamp = json.has("timestamp") ? json.get("timestamp").getAsLong() : System.currentTimeMillis();
    }

    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        json.addProperty("status", status);
        json.addProperty("message", message);
        json.addProperty("sender", sender);
        json.addProperty("target", target);
        json.addProperty("timestamp", timestamp);
        return json;
    }

    public String toJson() {
        return toJsonObject().toString();
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getTarget() {
        return target;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
