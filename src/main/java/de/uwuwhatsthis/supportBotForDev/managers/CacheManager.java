package de.uwuwhatsthis.supportBotForDev.managers;

import de.uwuwhatsthis.supportBotForDev.objects.ShowcaseServer;
import de.uwuwhatsthis.supportBotForDev.stuffs.JsonStuff;
import de.uwuwhatsthis.supportBotForDev.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;

public class CacheManager {

    private final ShowcaseServer showcaseServer;

    private Long guildID = null, vcChannelID = null, txtChannelID = null, etaTimeAvailable = null, etaEntries = null, shortestTime = null, longestTime = null;

    public CacheManager(ShowcaseServer server){
        this.showcaseServer = server;

    }

    public void loadCache(){
        String cacheFile = JsonStuff.getFileContent(Constants.cacheFilePath);

        if (cacheFile == null){
            System.err.println("ERROR: Could not read cache file!");
            return;
        }

        JSONObject cache;
        try{
            cache = new JSONObject(cacheFile);
        } catch (JSONException noted){
            cache = new JSONObject();
        }


        JSONObject servers;
        try{
            servers = cache.getJSONObject("servers");
        } catch (JSONException noted){
            showcaseServer.getDebug().debug("Cache file is empty, returning!");
            return;
        }


        JSONObject currentServer;
        try{
            currentServer = servers.getJSONObject(showcaseServer.getServerName());
        } catch (JSONException noted){

            showcaseServer.getDebug().debug("No cache found!");

            return;
        }

        guildID = getValue(currentServer, "id");
        vcChannelID = getValue(currentServer, "vc_channel");
        txtChannelID = getValue(currentServer, "txt_channel");

        etaTimeAvailable = getValue(currentServer, "etaTimeAvailable");
        etaEntries = getValue(currentServer, "etaEntries");
        shortestTime = getValue(currentServer, "shortestTime");
        longestTime = getValue(currentServer, "longestTime");
    }

    public void saveCache(){
        Long guildId = showcaseServer.getGuildID(),
                vcChannelID = showcaseServer.getVcChannelID(),
                txtChannelID = showcaseServer.getTxtChannelID(),
                etaTimeAvailable = showcaseServer.getEtaTimeAvailable(),
                etaEntries = showcaseServer.getEtaEntries(),
                shortestTime = showcaseServer.getShortestTime(),
                longestTime = showcaseServer.getLongestTime();


        String cacheFileContent = JsonStuff.getFileContent(Constants.cacheFilePath);

        if (cacheFileContent == null){
            System.err.println("ERROR: Failed to read cached file!");
            return;
        }

        JSONObject cacheFile;

        try{
            cacheFile = new JSONObject(cacheFileContent);
        } catch (JSONException noted){

            showcaseServer.getDebug().debug("Creating new cacheFile object");

            cacheFile = new JSONObject();
        }


        JSONObject serverInfo;

        try{
            serverInfo = cacheFile.getJSONObject("servers");
        } catch ( JSONException noted){

            showcaseServer.getDebug().debug("Creating new serverInfo object");

            serverInfo = new JSONObject();
        }


        JSONObject currentServerInfo;

        try{
            currentServerInfo = serverInfo.getJSONObject(showcaseServer.getServerName());
        } catch (JSONException noted){

            showcaseServer.getDebug().debug("Creating new currentServerInfo object");

            currentServerInfo = new JSONObject();
        }


        currentServerInfo.put("id", guildId);
        currentServerInfo.put("vc_channel", vcChannelID);
        currentServerInfo.put("txt_channel", txtChannelID);
        currentServerInfo.put("etaTimeAvailable", etaTimeAvailable);
        currentServerInfo.put("etaEntries", etaEntries);
        currentServerInfo.put("shortestTime", shortestTime);
        currentServerInfo.put("longestTime", longestTime);


        serverInfo.put(showcaseServer.getServerName(), currentServerInfo);
        showcaseServer.getDebug().debug("Put currentServerInfo into serverInfo");


        cacheFile.put("servers", serverInfo);
        showcaseServer.getDebug().debug("Put serverInfo in cacheFile");

        JsonStuff.writeToJsonFile(Constants.cacheFilePath, cacheFile.toString(2));
    }

    private Long getValue(JSONObject object, String key){
        try{
            return object.getLong(key);
        } catch (JSONException noted){
            return null;
        }
    }

    public ShowcaseServer getShowcaseServer() {
        return showcaseServer;
    }

    public Long getGuildID() {
        return guildID;
    }

    public Long getVcChannelID() {
        return vcChannelID;
    }

    public Long getTxtChannelID() {
        return txtChannelID;
    }

    public Long getEtaTimeAvailable() {
        return etaTimeAvailable;
    }

    public Long getEtaEntries() {
        return etaEntries;
    }

    public Long getShortestTime() {
        return shortestTime;
    }

    public Long getLongestTime() {
        return longestTime;
    }
}
