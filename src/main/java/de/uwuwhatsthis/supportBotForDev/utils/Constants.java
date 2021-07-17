package de.uwuwhatsthis.supportBotForDev.utils;

import de.uwuwhatsthis.supportBotForDev.managers.LoggingChannelManager;
import de.uwuwhatsthis.supportBotForDev.objects.ShowcaseServer;
import de.uwuwhatsthis.supportBotForDev.stuffs.JsonStuff;
import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {

    public static boolean isLocked = false; // are the demo servers locked?
    public static long commandRan = 0;


    public final static String configPath = "data/config.json", cacheFilePath = "data/cache.json";

    public final static String GREEN_CIRCLE = "\uD83D\uDFE2", ORANGE_CIRCLE = "\uD83D\uDFE0", RED_CIRCLE = "\uD83D\uDD34",
            SHRUG = "¯\\_(ツ)_/¯";

    public static Long USER_TO_CONTACT_WHEN_SERVER_IS_DOWN_ID = null, MAIN_SUPPORT_SERVER_ID = null, BACKUP_ID = null, LOGGING_CHANNEL_ID = null;
    public static Guild MAIN_SUPPORT_SERVER = null;
    public static LoggingChannelManager LOGGING_CHANNEL_MANAGER = null;

    public static String PREFIX;

    public static List<Long> USERS_TO_IGNORE_WHEN_JOINING_SHOWCASE_SERVER = null;

    public static ShowcaseServer[] SHOWCASE_SERVERS = null;

    public static boolean DEBUG;

    public static String VERSION = "1.1";



    public static void init(){

        DEBUG = JsonStuff.getBoolFromJson(configPath, "debug", false);

        List<Map<String, Map<String, Long>>> rawServerData = new ArrayList<>();

        String serversString = JsonStuff.getFileContent(configPath);
        JSONObject config = new JSONObject(serversString);

        JSONObject servers = config.getJSONObject("servers");

        // loads the servers

        for (String currentServer : servers.keySet()){

            JSONObject currentServerObject = servers.getJSONObject(currentServer);

            rawServerData.add(new HashMap<String, Map<String, Long>>(){{
                put(currentServer, new HashMap<String, Long>(){{
                    for (String key: new String[]{"id", "vc_channel", "txt_channel", "log_channel_id"}){
                        put(key, currentServerObject.getLong(key));
                    }
                }});
            }});
        }

        SHOWCASE_SERVERS = new ShowcaseServer[rawServerData.size()];

        int iter = 0;
        for (Map<String, Map<String, Long>> map: rawServerData){
            SHOWCASE_SERVERS[iter] = new ShowcaseServer(map);
            iter++;
        }



        USER_TO_CONTACT_WHEN_SERVER_IS_DOWN_ID = JsonStuff.getLongFromJson(configPath, "user_to_contact_when_server_is_down_id");
        USERS_TO_IGNORE_WHEN_JOINING_SHOWCASE_SERVER = JsonStuff.getLongListFromJson(configPath, "users_to_ignore_when_joining_a_showcase_server");
        MAIN_SUPPORT_SERVER_ID = JsonStuff.getLongFromJson(configPath, "main_support_server_id");
        BACKUP_ID = JsonStuff.getLongFromJson(configPath, "backup_id");
        LOGGING_CHANNEL_ID = JsonStuff.getLongFromJson(configPath, "logging_channel_id");
        PREFIX = JsonStuff.getStringFromJson(configPath, "prefix");

    }
}
