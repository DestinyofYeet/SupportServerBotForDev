package de.uwuwhatsthis.supportBotForDev.events;

import de.uwuwhatsthis.supportBotForDev.objects.ShowcaseServer;
import de.uwuwhatsthis.supportBotForDev.stuffs.ShowcaseServerStuff;
import de.uwuwhatsthis.supportBotForDev.utils.Constants;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

public class GuildMemberJoinEventListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event){
        Member memberJoined = event.getMember();
        Guild currentGuild = event.getGuild();

        if (!ShowcaseServerStuff.isShowcaseServer(currentGuild.getIdLong())) {
            if (Constants.DEBUG)
                System.out.println("INFO: Ignoring onGuildMemberJoin event since the guild \"" + currentGuild.getName() + "\" is not a showcase server!");
            return;
        };

        if (Constants.USERS_TO_IGNORE_WHEN_JOINING_SHOWCASE_SERVER.contains(memberJoined.getIdLong())) {
            if (Constants.DEBUG)
                System.out.println("INFO: Ignoring user with id \"" + memberJoined.getId() + "\" in the onGuildMemberJoin event because the user is on the ignore list!");
            return;
        };

        ShowcaseServer currentShowcaseServer = ShowcaseServerStuff.getShowcaseServerFromGuildId(currentGuild.getIdLong());

        for (ShowcaseServer showcaseServer: Constants.SHOWCASE_SERVERS){
            if (showcaseServer.getCurrentlyOccupyingTheServer() != null && showcaseServer.getCurrentlyOccupyingTheServer().getIdLong() == (memberJoined.getIdLong())){
                // user is already in any of the other two servers
                memberJoined.kick("Already in any of the other showcase servers!").queue();
                return;
            }
        }

        if (currentShowcaseServer == null){
            System.err.println("ERROR: Could not get the showcase server with id \"" + currentGuild.getIdLong() + "\"!");
            return;
        }

        memberJoined.kick("Time limit has expired!").queueAfter(3, TimeUnit.HOURS);
        if (currentShowcaseServer.getCurrentlyOccupyingTheServer() == null){
            currentShowcaseServer.setCurrentlyOccupyingTheServer(memberJoined);
            currentShowcaseServer.setJoinedServerAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            currentShowcaseServer.getDebug().debug("Set user \"" + memberJoined.getUser().getName() + "\" to the 'master' of the server!");
        }


        currentShowcaseServer.getDebug().debug("User \"" + event.getUser().getName() + "\" has joined the server!");

        Constants.LOGGING_CHANNEL_MANAGER.userJoinedServer(memberJoined, currentShowcaseServer);
    }
}
