package io;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileIO implements File {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // 写入整个 Gson 对象到文件（覆盖）
    @Override
    public boolean fileOut(String filename, Gson json) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8)) {
            gson.toJson(JsonParser.parseString(json.toString()), writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 写入指定 key-value 到 JSON 文件
    @Override
    public boolean fileOut(String filename, String key, String value) {
        try {
            JsonObject jsonObject = fileIn(filename); // 尝试读取原文件内容
            if (jsonObject == null) {
                jsonObject = new JsonObject(); // 不存在或解析失败则创建新对象
            }
            jsonObject.addProperty(key, value);

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8)) {
                gson.toJson(jsonObject, writer);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 从文件中读取整个 JSON 对象
    @Override
    public JsonObject fileIn(String filename) {
        try (Reader reader = new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    // 从文件中读取 key 对应的值（无默认值）
    @Override
    public String fileIn(String filename, String key) {
        return fileIn(filename, key, "");
    }

    // 从文件中读取 key 对应的值（有默认值）
    @Override
    public String fileIn(String filename, String key, String defaultValue) {
        JsonObject jsonObject = fileIn(filename);
        if (jsonObject != null && jsonObject.has(key)) {
            JsonElement val = jsonObject.get(key);
            return val.isJsonNull() ? defaultValue : val.getAsString();
        }
        return defaultValue;
    }


}
