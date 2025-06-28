package com.media.net.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.JSONObject;

public class JsonReader {
    public static JSONObject getJsonData(String fileFullPath) {
        JSONObject jsonData = new JSONObject();

        try {
            Path path = Paths.get(fileFullPath);
            String fileContent = new String(Files.readAllBytes(path));
            jsonData = new JSONObject(fileContent);
        } catch (IOException msg) {
            System.out.println(msg.getMessage());
        }

        return jsonData;
    }
}
