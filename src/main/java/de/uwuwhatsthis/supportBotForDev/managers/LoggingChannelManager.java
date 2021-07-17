package de.uwuwhatsthis.supportBotForDev.managers;

import de.uwuwhatsthis.supportBotForDev.objects.Embed;
import de.uwuwhatsthis.supportBotForDev.objects.ShowcaseServer;
import de.uwuwhatsthis.supportBotForDev.utils.Constants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class LoggingChannelManager {
    private final TextChannel loggingChannel;
    private final HashMap<User, LocalDateTime> userMap;

    public LoggingChannelManager(){
        this.loggingChannel = Constants.MAIN_SUPPORT_SERVER.getTextChannelById(Constants.LOGGING_CHANNEL_ID);
        userMap = new HashMap<>();

        if (this.loggingChannel == null){
            System.err.println("ERROR: Unable to find logging channel (" + Constants.LOGGING_CHANNEL_ID + ") in main support server!");
        }
    }

    public void userJoinedServer(Member member, ShowcaseServer server){
        userMap.put(member.getUser(), LocalDateTime.now());
        loggingChannel.sendMessage(new Embed("User joined a demo server",
                        "User: " + member.getUser().getName() + ",\n" +
                                "ID: `" + member.getId() + "`,\n" +
                                "Joined server:  " + server.getServerName() + "!", Color.green)
        .build()
        ).queue();
    }

    public void userLeftServer(User user, ShowcaseServer server){
        LocalDateTime dateTime = userMap.get(user);

        if (dateTime == null) return;
        userMap.remove(user);

        String timeJoined = dateTime.format(DateTimeFormatter.ofPattern("H:m:s"));
        String timeLeft = LocalDateTime.now().format(DateTimeFormatter.ofPattern("H:m:s"));

        loggingChannel.sendMessage(new Embed("User left a demo server",
                        "User: " + user.getName() + ",\n" +
                        "ID: `" + user.getId() + "`,\n" +
                        "Server: " + server.getServerName() + ",\n" +
                        "Time joined: " + timeJoined + ",\n " +
                        "Time left: " + timeLeft, Color.green)
        .build()
        ).queue();
    }
}
