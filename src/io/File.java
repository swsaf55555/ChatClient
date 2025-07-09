package io;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

// 接口定义
public interface File {
    boolean fileOut(String filename, Gson json);

    boolean fileOut(String filename, String key, String value);

    JsonObject fileIn(String filename);

    String fileIn(String filename, String key);

    String fileIn(String filename, String key, String defaultValue);
}
