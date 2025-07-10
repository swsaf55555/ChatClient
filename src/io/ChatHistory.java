package io;

import com.google.gson.*;
import model.Message;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ChatHistory {
    // 根目录为：我的文档\ChatClient\logs
    private static final String HISTORY_ROOT = new File(
            new File(System.getProperty("user.home"), "Documents"), "ChatClient/logs"
    ).getAbsolutePath();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // 保存聊天记录（按登录用户目录分隔）
    public static void saveHistory(String loginUser, String contactUser, List<Message> messages) {
        File userDir = new File(HISTORY_ROOT, loginUser);
        if (!userDir.exists()) userDir.mkdirs();

        File file = new File(userDir, contactUser + ".json");
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(messages, writer);
        } catch (IOException e) {
            System.err.println("保存聊天记录失败: " + file.getAbsolutePath() + " " + e.getMessage());
        }
    }

    // 加载聊天记录
    public static List<Message> loadHistory(String loginUser, String contactUser) {
        File file = new File(HISTORY_ROOT + "/" + loginUser, contactUser + ".json");
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Message[] msgs = gson.fromJson(reader, Message[].class);
            return msgs != null ? Arrays.asList(msgs) : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("加载聊天记录失败: " + file.getAbsolutePath() + " " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // 加载当前用户的所有聊天记录
    public static Map<String, List<Message>> loadAll(String loginUser) {
        Map<String, List<Message>> historyMap = new HashMap<>();
        File userDir = new File(HISTORY_ROOT, loginUser);
        if (!userDir.exists() || !userDir.isDirectory()) return historyMap;

        File[] files = userDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return historyMap;

        for (File file : files) {
            String contact = file.getName().replace(".json", "");
            historyMap.put(contact, loadHistory(loginUser, contact));
        }
        return historyMap;
    }

    // 批量保存
    public static void saveAll(String loginUser, Map<String, List<Message>> historyMap) {
        for (Map.Entry<String, List<Message>> entry : historyMap.entrySet()) {
            saveHistory(loginUser, entry.getKey(), entry.getValue());
        }
    }
}
