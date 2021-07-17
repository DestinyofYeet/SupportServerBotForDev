package de.uwuwhatsthis.supportBotForDev.events;

import de.uwuwhatsthis.supportBotForDev.objects.ShowcaseServer;
import de.uwuwhatsthis.supportBotForDev.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildMessageEventListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
        for (ShowcaseServer server: Constants.SHOWCASE_SERVERS){
            if (server.getGuildID() == event.getGuild().getIdLong()){
                server.getMessageLoggingManager().logMessage(event.getMessage());
            }
        }
    }
}
