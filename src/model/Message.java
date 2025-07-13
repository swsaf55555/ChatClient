package model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Objects;

public class Message {
    private String type="";      // 消息类型：login, chat, group, response, notice...
    private String status="";    // 状态：ok, error, pending...
    private String message="";   // 文本内容（服务器消息、提示、聊天内容）
    private String sender="";    // 发送者用户名
    private String target="";    // 接收者（用户名或群组）
    private long timestamp=0;   // 时间戳
    private String username="";
    private String passwd="";

    public Message(String type,String username,String passwd,boolean send){
        this.type=type;
        this.username=username;
        this.passwd=passwd;
    }
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
    public Message(String sender,String target,String message,long timestamp){
        this.sender=sender;
        this.target=target;
        this.message=message;
        if(timestamp==1){
            this.timestamp=System.currentTimeMillis();
        }else{
            this.timestamp=timestamp;
        }

    }
    public Message(String type,String username,String passwd,String target,String message,boolean isChat){
        this.type=type;
        this.username=username;
        this.passwd=passwd;
        this.target=target;
        this.message=message;
    }

    public Message(JsonObject json) {
        this.type = json.has("type") ? json.get("type").getAsString() : "";
        this.status = json.has("status") ? json.get("status").getAsString() : "";
        this.message = json.has("message") ? json.get("message").getAsString() : "";
        this.sender = json.has("sender") ? json.get("sender").getAsString() : "";
        this.target = json.has("target") ? json.get("target").getAsString() : "";
        this.timestamp = json.has("timestamp") ? json.get("timestamp").getAsLong() : System.currentTimeMillis();
        this.username = json.has("username") ? json.get("username").getAsString() : "";
        this.passwd = json.has("passwd") ? json.get("passwd").getAsString() : "";
    }

    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        json.addProperty("username", username);
        json.addProperty("passwd", passwd);

        json.addProperty("message", message);
        json.addProperty("sender", sender);
        json.addProperty("target", target);
        json.addProperty("timestamp", timestamp);


        json.addProperty("status", status);




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
