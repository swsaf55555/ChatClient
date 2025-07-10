package io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class RememberMe {
    private static final File CONFIG_FILE = new File(
            new File(System.getProperty("user.home"), "Documents/ChatClient"),
            "remember.properties"
    );

    // 保存用户名和密码
    public static void save(String username, String password) {
        try {
            Properties props = new Properties();
            props.setProperty("username", username);
            props.setProperty("password", password);
            CONFIG_FILE.getParentFile().mkdirs();
            try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
                props.store(out, "Remembered credentials");
            }
        } catch (IOException e) {
            System.err.println("保存记住密码失败: " + e.getMessage());
        }
    }

    // 加载保存的用户名和密码
    public static String[] load() {
        if (!CONFIG_FILE.exists()) return null;
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            Properties props = new Properties();
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            String username = props.getProperty("username");
            String password = props.getProperty("password");
            return (username != null && password != null) ? new String[]{username, password} : null;
        } catch (IOException e) {
            System.err.println("读取记住密码失败: " + e.getMessage());
            return null;
        }
    }

    // 清除记住密码
    public static void clear() {
        if (CONFIG_FILE.exists()) CONFIG_FILE.delete();
    }
}
