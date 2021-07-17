package de.uwuwhatsthis.supportBotForDev.managers;

import de.uwuwhatsthis.supportBotForDev.objects.Embed;
import de.uwuwhatsthis.supportBotForDev.objects.ShowcaseServer;
import de.uwuwhatsthis.supportBotForDev.utils.Constants;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;

public class MessageLoggingChannelManager {
    private final ShowcaseServer showcaseServer;

    public MessageLoggingChannelManager(ShowcaseServer showcaseServer){
        this.showcaseServer = showcaseServer;
    }

    public void logMessage(Message message){
        if (showcaseServer == null){
            System.err.println("Showcase server is null!");
            return;
        }

        if (showcaseServer.getMessageLogChannel() == null){
            showcaseServer.getDebug().error("The message log channel is null, can't log user action!");
            return;
        }

        if (message.getAuthor().getIdLong() == message.getJDA().getSelfUser().getIdLong()){
            return; // we don't want messages from the bot
        }

        showcaseServer.getMessageLogChannel().sendMessage(new Embed("Message sent in demo server \"" + showcaseServer.getServerName() + "\" by \"" + message.getAuthor().getAsTag() + "\""
                , message.getContentRaw(), Color.GREEN).build()).queue();
    }
}
