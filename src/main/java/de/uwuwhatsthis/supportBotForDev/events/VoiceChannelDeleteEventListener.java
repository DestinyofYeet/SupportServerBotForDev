package de.uwuwhatsthis.supportBotForDev.events;

import de.uwuwhatsthis.supportBotForDev.objects.ShowcaseServer;
import de.uwuwhatsthis.supportBotForDev.stuffs.ShowcaseServerStuff;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceChannelDeleteEventListener extends ListenerAdapter {

    @Override
    public void onVoiceChannelDelete (VoiceChannelDeleteEvent event){
        ShowcaseServer showcaseServer = ShowcaseServerStuff.getShowcaseServerFromGuildId(event.getGuild().getIdLong());

        if (showcaseServer == null) return;

        VoiceChannel timerChannel = showcaseServer.getShowcaseServerTimer();
        if (timerChannel == null) return;

        if (timerChannel.getIdLong() == event.getChannel().getIdLong() && !showcaseServer.isResetting()){
            Category category = null;
            try{
                category = showcaseServer.getGuild().getCategoriesByName("Cynthia Eternal", true).get(0);
            } catch (IndexOutOfBoundsException ignored){}

            if (category != null){
                category.createVoiceChannel("Time left: " + showcaseServer.convertSecondsToHoursAndMinutes(showcaseServer.getTimeLeft())).queue(showcaseServer::setShowcaseServerTimer);

            } else {
                showcaseServer.getGuild().createVoiceChannel("Time left: " + showcaseServer.convertSecondsToHoursAndMinutes(showcaseServer.getTimeLeft())).setPosition(0).queue(showcaseServer::setShowcaseServerTimer);
            }
        }
    }
}
