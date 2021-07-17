package de.uwuwhatsthis.supportBotForDev.stuffs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonStuff {
    // very many json file interaction

    public static String getFileContent(String filePath){
        File file = new File(filePath);

        try{
            return new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStringFromJson(String filePath, String key) {
        File file = new File(filePath);

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);

            if (json.get(key) instanceof String)
                return (String) json.get(key);


        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    public static Integer getIntFromJson(String filePath, String key) {
        File file = new File(filePath);

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);

            if (json.get(key) instanceof Integer)
                return (Integer) json.get(key);


        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    public static Long getLongFromJson(String filePath, String key) {
        File file = new File(filePath);

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);

            if (json.get(key) instanceof Long)
                return (Long) json.get(key);


        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    public static List<Long> getLongListFromJson(String filePath, String key){
        File file = new File(filePath);

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);

            final JSONArray array = json.getJSONArray(key);
            return new ArrayList<Long>(){{
                for (Iterator<Object> it = array.iterator(); it.hasNext(); ) {
                    Long currentChannelId = (Long) it.next();
                    add(currentChannelId);
                }
            }};



        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    public static List<String> getStringListFromJson(String filePath, String key){
        File file = new File(filePath);

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);

            final JSONArray array = json.getJSONArray(key);
            return new ArrayList<String>(){{
                for (Object object : array) {
                    String currentChannelId = (String) object;
                    add(currentChannelId);
                }
            }};



        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    public static boolean getBoolFromJson(String filePath, String key, Boolean defaultValue){
        String content = getFileContent(filePath);

        if (content == null) return defaultValue;

        final JSONObject json = new JSONObject(content);

        try{
            return json.getBoolean(key);
        } catch ( JSONException noted){
            return defaultValue;
        }
    }


    public static void writeToJsonFile(String filePath, String key, String value){
        File file = new File(filePath);

        try{
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);

            json.put(key, value);

            FileWriter fw = new FileWriter(file);
            fw.write(json.toString(2));
            fw.flush();
            fw.close();

        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public static void writeToJsonFile(String filePath, String key, int value){
        File file = new File(filePath);

        try{
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);

            json.put(key, value);

            FileWriter fw = new FileWriter(file);
            fw.write(json.toString(2));
            fw.flush();
            fw.close();

        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public static void writeToJsonFile(String filePath, String key, long value){
        File file = new File(filePath);

        try{
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);

            json.put(key, value);

            FileWriter fw = new FileWriter(file);
            fw.write(json.toString(2));
            fw.flush();
            fw.close();

        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public static void writeLongListToJsonFile(String filePath, String key, List<Long> value){
        File file = new File(filePath);

        try{
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);

            json.put(key, value);

            FileWriter fw = new FileWriter(file);
            fw.write(json.toString(2));
            fw.flush();
            fw.close();

        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public static void writeStringListToJsonFile(String filePath, String key, List<String> value){
        File file = new File(filePath);

        try{
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);

            json.put(key, value);

            FileWriter fw = new FileWriter(file);
            fw.write(json.toString(2));
            fw.flush();
            fw.close();

        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public static void writeToJsonFile(String filePath, String content){
        File file = new File(filePath);

        try {
            FileWriter fw = new FileWriter(file);

            fw.write(content);
            fw.flush();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteKeyFromJsonFile(String filePath, String key){
        File file = new File(filePath);

        try{
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);

            json.remove(key);

            FileWriter fw = new FileWriter(file);
            fw.write(json.toString(2));
            fw.flush();
            fw.close();

        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public static void clearJsonFile(String filePath){
        File file = new File(filePath);

        try{

            FileWriter fw = new FileWriter(file);
            fw.write("{}");
            fw.flush();
            fw.close();

        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public static List<String> getAllKeys(String filePath){
        File file = new File(filePath);

        try{
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
            final JSONObject json = new JSONObject(content);

            return new ArrayList<String>(){{
                this.addAll(json.keySet());
            }};

        } catch (Exception exception){
            exception.printStackTrace();
        }
        return null;
    }
}
