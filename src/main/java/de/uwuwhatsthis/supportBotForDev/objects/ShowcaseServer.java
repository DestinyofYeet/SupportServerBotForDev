package de.uwuwhatsthis.supportBotForDev.objects;

import de.uwuwhatsthis.supportBotForDev.managers.CacheManager;
import de.uwuwhatsthis.supportBotForDev.managers.MessageLoggingChannelManager;
import de.uwuwhatsthis.supportBotForDev.stuffs.Status;
import de.uwuwhatsthis.supportBotForDev.utils.Constants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ShowcaseServer implements Runnable {
    private String serverName;
    private String realServerName;
    private Long guildID;
    private Long vcChannelID;
    private Long txtChannelID;
    private Long messageLogChannelID;
    private Member currentlyOccupyingTheServer;
    private Long leftServerAt;
    private Long joinedServerAt;

    private Guild guild; // corresponds to this showcase guild
    private VoiceChannel vcChannel, mainSupportServerTimer, showcaseServerTimer; // corresponds to the status voicechannel on the main support server
    private TextChannel txtChannel; // corresponds to the textchannel on the main support server
    private TextChannel messageLogChannel;
    private CacheManager cacheManager;
    private ServerTimer serverTimer;
    private Thread serverTimerThread;
    private Debug debug;
    private MessageLoggingChannelManager messageLoggingManager;

    private boolean available = true, resetting = false, isLocked = false;
    private Long availableIn = 0L, etaTimeAvailable, etaEntries, timeLeft, etaTimeDisplayed, soonHits, longestTime, shortestTime;

    public ShowcaseServer(Map<String, Map<String, Long>> data){
        String realKey = null;
        for (String key: data.keySet()){
            realKey = key;
        }

        Map<String, Long> realData = data.get(realKey);

        setup(realKey, realData.get("id"), realData.get("vc_channel"), realData.get("txt_channel"), realData.get("log_channel_id"));
    }

    private void setup(String serverName, Long guildID, Long vcChannelID, Long txtChannelID, Long messageLogChannelID){
        this.serverName = serverName;
        this.guildID = guildID;
        this.vcChannelID = vcChannelID;
        this.txtChannelID = txtChannelID;
        this.messageLogChannelID = messageLogChannelID;

        this.realServerName = null;
        this.guild = null;
        this.vcChannel = null;
        this.txtChannel = null;
        this.messageLogChannel = null;

        this.currentlyOccupyingTheServer = null;

        debug = new Debug(this);

        cacheManager = new CacheManager(this);
        cacheManager.loadCache();

        serverTimer = new ServerTimer(this);
        serverTimerThread = new Thread(serverTimer);
        serverTimer.setRun(false);
        serverTimerThread.start();

        this.etaEntries = cacheManager.getEtaEntries();
        this.etaTimeAvailable = cacheManager.getEtaTimeAvailable();
        this.longestTime = cacheManager.getLongestTime();
        this.shortestTime = cacheManager.getShortestTime();

        if (etaTimeAvailable != null){
            etaTimeDisplayed = etaTimeAvailable / etaEntries;
        }
    }

    @Override
    public void run(){
        resetting = true;
        serverTimer.setRun(false);
        available = false;

        runCleanup();

        if (!isLocked){
            availableIn = 60*10 - (leftServerAt - joinedServerAt);

            debug.debug("Adding (leftServerAt - joinedServerAt) = " + (leftServerAt - joinedServerAt) + " seconds to the eta time!");
            addToETATime(leftServerAt - joinedServerAt);

            debug.debug("Waiting " + availableIn + " seconds till server is unlocked!");

            while(availableIn > 0){
                availableIn--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

        debug.debug("Using channel \"" + guild.getTextChannelsByName("readme", true).get(0) + "\" to create an invite!");
        debug.debug("Waiting 30 seconds for the backup to complete in case we didn't have to wait before");

        // sleep for 30 seconds before making the invite to wait for the backup to load
        try {
            Thread.sleep(1000*30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        guild.getTextChannelsByName("readme", true).get(0).createInvite().setMaxAge(0).setTemporary(false).queue(invite -> {

            debug.debug("Successfully created invite");

            txtChannel.getManager().putPermissionOverride(Constants.MAIN_SUPPORT_SERVER.getPublicRole(), EnumSet.of(Permission.VIEW_CHANNEL), null).queue( e -> {
                    debug.debug("Unhid txt channel!");
                    debug.debug("Sending invite into \"" + txtChannel.getName() + "\"!");

                    txtChannel.sendMessage(invite.getUrl()).queue();
                }
            );
        });

        debug.debug("The server is now available again");

        removeCurrentlyOccupyingTheServer();

        available = true;
        resetting = false;
        isLocked = false;
    }

    public void init(JDA jda){
        this.guild = jda.getGuildById(guildID);
        if (this.guild == null){
            guildNotFoundNotifier(jda, serverName, guildID);
            return;
        }

        realServerName = guild.getName();

        messageLoggingManager = new MessageLoggingChannelManager(this);

        vcChannel = Constants.MAIN_SUPPORT_SERVER.getVoiceChannelById(vcChannelID);
        if (vcChannel == null){
            channelNotFoundNotifier(vcChannelID, Constants.MAIN_SUPPORT_SERVER);
        }

        txtChannel = Constants.MAIN_SUPPORT_SERVER.getTextChannelById(txtChannelID);
        if (txtChannel == null){
            channelNotFoundNotifier(txtChannelID, Constants.MAIN_SUPPORT_SERVER);
        }

        messageLogChannel = Constants.MAIN_SUPPORT_SERVER.getTextChannelById(messageLogChannelID);
        if (messageLogChannel == null){
            channelNotFoundNotifier(messageLogChannelID, Constants.MAIN_SUPPORT_SERVER);
        }

        setStatus(Status.AVAILABLE);
    }

    public String getServerName() {
        return serverName;
    }

    public void runCleanup(){
        MessageHistory history = new MessageHistory(txtChannel);
        history.retrievePast(20).queue(past -> {
            txtChannel.purgeMessages(past);
        });

        debug.debug("Attempting to load members");
        guild.loadMembers().onSuccess(members -> {
            debug.debug("Successfully loaded members");
            for (Member member: members){
                debug.debug("Attempting to kick \"" + member.getEffectiveName() + "\"");
                try{
                    member.kick("Started backup sequence!").queue();
                    debug.debug("Successfully kicked \"" + member.getEffectiveName() + "\"");
                } catch (HierarchyException noted ){
                    debug.debug("Failed to kick \"" + member.getEffectiveName() + "\" because of the server hierarchy!");
                }

            }
        }).onError(e -> {
            debug.error("Failed to get members list!");
        });

        debug.debug("About to create temp channel");

        guild.createTextChannel("temp").queue(textChannel -> {

            debug.debug("About to load backup");

            textChannel.sendMessage("c!load-backup " + Constants.BACKUP_ID.toString()).queue();

            debug.debug("About to confirm backup loading");

            textChannel.sendMessage("-confirm").queueAfter(2, TimeUnit.SECONDS);

            debug.debug("Backup creation confirmed, waiting till server is unlocked to continue");
        });
    }

    public void setStatus(Status status){

        switch (status){
            case AVAILABLE:
                setVcStatus(Constants.GREEN_CIRCLE);
                setTxtState(status);
                break;

            case OCCUPIED:
                setVcStatus(Constants.ORANGE_CIRCLE);
                setTxtState(status);
                break;

            case ERROR:
                setVcStatus(Constants.RED_CIRCLE);
                setTxtState(status);
                break;
        }


    }

    public void setTxtState(Status status){
        if (txtChannel == null){
            System.err.println("ERROR: The text channel has to be initialized first!");
            return;
        }

        if (status.equals(Status.OCCUPIED))
            txtChannel.getManager().putPermissionOverride(Constants.MAIN_SUPPORT_SERVER.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL)).queue();
        else if (status.equals(Status.AVAILABLE)){
            txtChannel.getManager().putPermissionOverride(Constants.MAIN_SUPPORT_SERVER.getPublicRole(), EnumSet.of(Permission.VIEW_CHANNEL), null).queue();
        }
    }

    public void setVcStatus(String status){
        if (vcChannel == null){
            System.err.println("ERROR: The voice channel has to be initialized first");
            return;
        }

        try{
            vcChannel.getManager().setName("Status: " + status).queue(null,  e -> {
                System.err.println("Error: Could not modify vc (" + vcChannel.getId() + ") name due to: " + e.getMessage());
            });
        } catch (InsufficientPermissionException e){
            System.err.println("Error: Could not modify vc (" + vcChannel.getId() + ") name due to: " + e.getMessage());
        }
    }

    private void guildNotFoundNotifier(JDA jda, String server, Long id){
        System.err.println("ERROR: Unable to find \"" + server + "\" with id \"" + id + "\"! Shutting down!");
        jda.retrieveUserById(Constants.USER_TO_CONTACT_WHEN_SERVER_IS_DOWN_ID).queue(user -> {
            user.openPrivateChannel().queue(dmChannel -> {
                dmChannel.sendMessage("\"" + server + "\" with id \"" + id + "\" is down!").queue();
            });
        });
    }

    private static void channelNotFoundNotifier(long channelID, Guild guild){
        System.err.println("WARNING: Could not find channel with ID \"" + channelID + "\" on server " + guild.getName());
    }

    public void setCurrentlyOccupyingTheServer(Member currentlyOccupyingTheServer) {
        this.currentlyOccupyingTheServer = currentlyOccupyingTheServer;
        setStatus(Status.OCCUPIED);
        soonHits = 0L;

        showcaseServerTimer = guild.getVoiceChannelsByName("timer", true).get(0);
        timeLeft = (long) 3*60*60;

        if (etaTimeAvailable == null)
            createEtaAvailableChannel("Available in: No data");
        else {

            etaTimeDisplayed = etaTimeAvailable / etaEntries;

            String time = convertSecondsToHoursAndMinutes(etaTimeDisplayed);

            if (time.equals("0min"))
                createEtaAvailableChannel("Available in: soon");
            else
                createEtaAvailableChannel("Available in: ~" + time);
        }

        setShowcaseServerTimerChannelName(convertSecondsToHoursAndMinutes(timeLeft));
        serverTimer.setRun(true);

    }

    public void removeCurrentlyOccupyingTheServer(){
        this.currentlyOccupyingTheServer = null;

        if (mainSupportServerTimer != null)
            mainSupportServerTimer.delete().queue();

        setStatus(Status.AVAILABLE);

    }

    public String parseEtaTimeAvailableName(String time){

        String setChannelTo = "Available in: ";
        if (time.equals("0min")){
            soonHits ++;

            if (soonHits >= 3) setChannelTo += Constants.SHRUG;
            else setChannelTo += "soon";



        } else {
            setChannelTo += "~" + time;
        }
        return setChannelTo;
    }

    public void setEtaTimeAvailableChannelName(String time){
        if (mainSupportServerTimer == null) return;

        debug.debug("Received " + time + " as an argument");

        String setChannelTo = parseEtaTimeAvailableName(time);

        debug.debug("Setting eta channel to \"" + setChannelTo + "\"!");
        mainSupportServerTimer.getManager().setName(setChannelTo).queue();

    }

    public void setShowcaseServerTimerChannelName(String time){
        showcaseServerTimer.getManager().setName("Time left: " + time).queue();
    }

    public void addToETATime(Long seconds){
        if (shortestTime == null){
            debug.debug("shortestTime has no value! Setting it to " + seconds);
            shortestTime = seconds;

        } else if (seconds < shortestTime){
            debug.debug("New value for shortestTime time is: " + seconds + "! Because " + seconds + "<" + shortestTime + "!");
            shortestTime = seconds;
        }



        if (longestTime == null){
            debug.debug("longestTime has no value! Setting it to " + seconds);
            longestTime = seconds;

        } else if (seconds > longestTime){
            debug.debug("New value for longestTime time is: " + seconds + "! Because " + seconds + ">" + longestTime + "!");
            longestTime = seconds;
        }



        if (etaTimeAvailable == null){
            etaTimeAvailable = seconds;
            etaEntries = 1L;

        } else {
            debug.debug("Adding " + seconds + " seconds to eta time");
            debug.debug("raw eta before: " + etaTimeAvailable);

            etaTimeAvailable += seconds;

            debug.debug("raw eta after: " + etaTimeAvailable);
            etaEntries += 1L;


            debug.debug("Running eta calculation: " + etaTimeAvailable + "/" + etaEntries + " = " + (etaTimeAvailable / etaEntries));
            etaTimeDisplayed = etaTimeAvailable / etaEntries;

            debug.debug("eta after calculation: " + etaTimeDisplayed);
        }

        debug.debug("Saving cache");
        cacheManager.saveCache();
    }

    public void updateTimerChannels(){
        if (etaTimeAvailable != null){
            etaTimeDisplayed = etaTimeDisplayed - 5*60;
            debug.debug("Calculated new etaTimeDisplayed: " + etaTimeDisplayed + " seconds");
            setEtaTimeAvailableChannelName(convertSecondsToHoursAndMinutes(etaTimeDisplayed));
        }

        timeLeft =  timeLeft - 5*60;
        debug.debug("Updating timer channel to \"" + convertSecondsToHoursAndMinutes(timeLeft) + "\"");
        setShowcaseServerTimerChannelName(convertSecondsToHoursAndMinutes(timeLeft));
    }

    public String convertSecondsToHoursAndMinutes(Long seconds){
        int minutes = 0;
        int hours = 0;

        if (seconds > 60){
            minutes = (int) (seconds / 60);
            seconds = seconds - (minutes * 60L);
        }

        if (minutes > 60){
            hours = minutes / 60;
            minutes = minutes - (hours * 60);
        }

        minutes = (int) (Math.floor((minutes + 2.5) / 5) * 5);

        if (hours > 0)
            return hours + "h " + minutes + "min";

        return minutes + "min";

    }

    public String convertSecondsToMinutesHoursAndSecondsPrecise(Long seconds){
        if (seconds == null)return "No data";

        String stringToReturn;
        int minutes = 0;
        int hours = 0;

        if (seconds > 60){
            minutes = (int) (seconds / 60);
            seconds = seconds - (minutes * 60L);
        }

        if (minutes > 60){
            hours = minutes / 60;
            minutes = minutes - (hours * 60);
        }

        stringToReturn = seconds + "s";

        if (minutes > 0){
            stringToReturn = minutes + "min " + stringToReturn;
        }

        if (hours > 0){
            stringToReturn = hours + "h " + stringToReturn;
        }



        return stringToReturn;
    }

    public void createEtaAvailableChannel(String title){
        if (vcChannel == null && txtChannel == null){
            System.err.println("ERROR: Could not create a timer channel, due to the vcChannel and the txtChannel not being found!");
            mainSupportServerTimer = null;
        } else {

            final Category parentCategory;

            if (vcChannel != null){
                parentCategory = vcChannel.getParent();
            } else {
                parentCategory = txtChannel.getParent();
            }

            if (parentCategory == null){
                System.err.println("ERROR: The status vc needs to be in a category in order to create a time channel!");

            } else {

                boolean createNew = true;
                for (VoiceChannel vc: parentCategory.getVoiceChannels()){
                    if (vc.getName().startsWith("Available in:")){
                        mainSupportServerTimer = vc;
                        createNew = false;
                        break;
                    }
                }

                if (createNew){

                    debug.debug("Creating new MainSupportServer timer channel!");

                    parentCategory.createVoiceChannel(title).queue(voiceChannel -> {
                        voiceChannel.getManager()
                                // .setPosition(parentCategory.getVoiceChannels().size() + 1)
                                .putPermissionOverride(Constants.MAIN_SUPPORT_SERVER.getPublicRole(), null, EnumSet.of(Permission.VOICE_CONNECT))
                                .queue();
                        mainSupportServerTimer = voiceChannel;
                    });
                }
            }

        }

    }

    public void unlockServer(){
        isLocked = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    public void setLeftServerAt(Long leftServerAt) {
        this.leftServerAt = leftServerAt;
    }

    public void setJoinedServerAt(Long joinedServerAt) {
        this.joinedServerAt = joinedServerAt;
    }

    public String getRealServerName() {
        return realServerName;
    }

    public void setEtaTimeAvailable(Long etaTimeAvailable) {
        this.etaTimeAvailable = etaTimeAvailable;
    }

    public Long getVcChannelID() {
        return vcChannelID;
    }

    public Long getTxtChannelID() {
        return txtChannelID;
    }

    public Long getLeftServerAt() {
        return leftServerAt;
    }

    public Long getJoinedServerAt() {
        return joinedServerAt;
    }

    public Guild getGuild() {
        return guild;
    }

    public VoiceChannel getVcChannel() {
        return vcChannel;
    }

    public VoiceChannel getMainSupportServerTimer() {
        return mainSupportServerTimer;
    }

    public VoiceChannel getShowcaseServerTimer() {
        return showcaseServerTimer;
    }

    public TextChannel getTxtChannel() {
        return txtChannel;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public boolean isAvailable() {
        return available;
    }

    public Long getAvailableIn() {
        return availableIn;
    }

    public Long getEtaTimeAvailable() {
        return etaTimeAvailable;
    }

    public Long getEtaEntries() {
        return etaEntries;
    }

    public Long getGuildID() {
        return guildID;
    }

    public Member getCurrentlyOccupyingTheServer() {
        return currentlyOccupyingTheServer;
    }

    public Debug getDebug() {
        return debug;
    }

    public Long getTimeLeft() {
        return timeLeft;
    }

    public void setShowcaseServerTimer(VoiceChannel showcaseServerTimer) {
        this.showcaseServerTimer = showcaseServerTimer;
    }

    public Long getLongestTime() {
        return longestTime;
    }

    public Long getShortestTime() {
        return shortestTime;
    }

    public boolean isResetting() {
        return resetting;
    }

    public MessageLoggingChannelManager getMessageLoggingManager() {
        return messageLoggingManager;
    }

    public Long getMessageLogChannelID() {
        return messageLogChannelID;
    }

    public TextChannel getMessageLogChannel() {
        return messageLogChannel;
    }
}
