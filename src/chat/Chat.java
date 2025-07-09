package chat;

import com.google.gson.*;
import model.AppState;
import model.Message;
import model.User;
import io.NetIO;
import ui.ChatUI;
import ui.LoginUI;

import javax.swing.*;

public class Chat {
    private static final Gson gson = new Gson();

    /**
     * 登录请求
     */
    public static void sendLogin(String username, String password) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "login");
        json.addProperty("username", username);
        json.addProperty("passwd", password);
        NetIO.getInstance().send(json.toString());
    }

    /**
     * 发送注册请求
     */
    public static void sendRegister(String username, String password) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create");
        json.addProperty("username", username);
        json.addProperty("passwd", password);
        NetIO.getInstance().send(json.toString());
    }

    /**
     * 发送私聊消息
     */
    public static void sendPrivateMessage(String to, String content) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "chat");
        json.addProperty("username", AppState.getInstance().getCurrentUser().getUsername());
        json.addProperty("passwd",AppState.getInstance().getCurrentUser().getPasswd());
        json.addProperty("target", to);
        json.addProperty("message", content);
        NetIO.getInstance().send(json.toString());
    }
    public static void sendPrivateMessage(Message message){
        NetIO.getInstance().send(message.toJson());
    }
    /**
     * 群发消息
     */
    public static void sendGroupMessage(String content, String from) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "group");
        json.addProperty("username", from);
        json.addProperty("message", content);
        NetIO.getInstance().send(json.toString());
    }

    /**
     * 添加好友
     */
    public static void sendAddFriend(String owner, String target) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "add_friend");
        json.addProperty("username", owner);
        json.addProperty("target", target);
        NetIO.getInstance().send(json.toString());
    }

    /**
     * 删除好友
     */
    public static void sendRemoveFriend(String owner, String target) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "remove_friend");
        json.addProperty("username", owner);
        json.addProperty("target", target);
        NetIO.getInstance().send(json.toString());
    }

    /**
     * 获取联系人列表
     */
    public static void sendRequestContacts(String username) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "list_friend");
        json.addProperty("username", username);
        NetIO.getInstance().send(json.toString());
    }

    /**
     * 修改好友备注
     */
    public static void sendRemarkFriend(String owner, String target, String remark) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "remark_friend");
        json.addProperty("username", owner);
        json.addProperty("target", target);
        json.addProperty("remark", remark);
        NetIO.getInstance().send(json.toString());
    }

    /**
     * 构造退出请求
     */
    public static void sendLogout(String username, String password) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "logout");
        json.addProperty("username", username);
        json.addProperty("passwd", password);
        NetIO.getInstance().send(json.toString());
    }

    /**
     * 解析从服务端收到的 JSON 消息，转为 Message 对象
     */
    public static Message parseMessage(String jsonStr) {
        try {
            JsonObject json = gson.fromJson(jsonStr, JsonObject.class);
            return new Message(json);
        } catch (Exception e) {
            System.err.println("消息解析失败：" + e.getMessage());
            return null;
        }
    }
    public static void processMessage(String jsonStr){
        Message message=parseMessage(jsonStr);
        if (message != null) {
            switch (message.getType()){
                case "chat"->{
                    System.out.println("收到chat");
                    System.out.println(message.getMessage()+message.getType()+message.getSender());
                    SwingUtilities.invokeLater(
                        ()-> {
                            ChatUI.getInstance().ReceiveMessage(message.getSender(), message.getMessage());
                        }
                    );

                    //return message.getSender()+message.getMessage();
                }
                case "offlineMessage"->{
                    System.out.println("收到offlineMessage");
                    System.out.println(message.getMessage()+message.getType()+message.getSender());
                    SwingUtilities.invokeLater(
                            ()-> {
                                JsonObject data = JsonParser.parseString(jsonStr).getAsJsonObject();
                                long time = data.get("time").getAsLong();
                                ChatUI.getInstance().ReceiveMessage(message.getSender(), message.getMessage(),time);
                                ChatUI.getInstance().setContactNewInform(message.getSender());
                            }
                    );

                    //return message.getSender()+message.getMessage();
                }
                case "heartbeat"->{
                    SwingUtilities.invokeLater(
                            ()->{
                                ChatUI.getInstance().setOline();
                            }
                    );
                }
                case "list_friend"->{
                    System.out.println("收到list_friend");

                    SwingUtilities.invokeLater(
                            ()->{
                                JsonObject data = JsonParser.parseString(jsonStr).getAsJsonObject();
                                JsonArray friendsArray = data.getAsJsonArray("friends");
                                for (JsonElement element : friendsArray) {
                                    JsonObject friendObj = element.getAsJsonObject();
                                    String username = friendObj.get("username").getAsString();
                                    String remark = friendObj.get("remark").getAsString();
                                    ChatUI.getInstance().addContact(username,0);
                                }
                            }
                    );

                }
                case "add_friend"->{
                    System.out.println("收到add_friend");
                    SwingUtilities.invokeLater(
                            ()->{
                                JsonObject data=JsonParser.parseString(jsonStr).getAsJsonObject();
                                if(data.has("status") &&data.get("status").getAsString().equals("ok")){
                                    String username=data.get("message").getAsString();
                                    ChatUI.getInstance().addContact(username,"default_2.jpg",0);
                                    JOptionPane.showMessageDialog(ChatUI.getInstance(), "添加成功！现在可以和他对话了", "添加成功", JOptionPane.INFORMATION_MESSAGE);

                                }else{
                                    JOptionPane.showMessageDialog(ChatUI.getInstance(), data.get("message").getAsString() , "添加失败", JOptionPane.ERROR_MESSAGE);
                                }

                            }
                    );

                }
            }
        }
    }
}
