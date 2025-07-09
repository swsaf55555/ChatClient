package io;

import com.google.gson.*;
import model.Message;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ChatHistory {
    private static final String HISTORY_DIR = "logs";  // 所有聊天记录文件存放目录
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static {
        File dir = new File(HISTORY_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // 保存某一位用户的聊天记录
    public static void saveHistory(String username, List<Message> messages) {
        File file = new File(HISTORY_DIR, username + ".json");
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(messages, writer);
        } catch (IOException e) {
            System.err.println("保存聊天记录失败: " + username + " " + e.getMessage());
        }
    }

    // 加载某一位用户的聊天记录
    public static List<Message> loadHistory(String username) {
        File file = new File(HISTORY_DIR, username + ".json");
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Message[] msgs = gson.fromJson(reader, Message[].class);
            return msgs != null ? Arrays.asList(msgs) : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("加载聊天记录失败: " + username + " " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // 批量保存当前所有记录
    public static void saveAll(Map<String, List<Message>> historyMap) {
        for (Map.Entry<String, List<Message>> entry : historyMap.entrySet()) {
            saveHistory(entry.getKey(), entry.getValue());
        }
    }

    // 加载目录下所有聊天记录（返回 Map<用户名, 聊天记录列表>）
    public static Map<String, List<Message>> loadAll() {
        Map<String, List<Message>> historyMap = new HashMap<>();
        File dir = new File(HISTORY_DIR);
        if (!dir.exists()) return historyMap;

        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null) return historyMap;

        for (File f : files) {
            String name = f.getName().replace(".json", "");
            historyMap.put(name, loadHistory(name));
        }
        return historyMap;
    }
}
