package de.uwuwhatsthis.supportBotForDev.events;

import de.uwuwhatsthis.supportBotForDev.objects.ShowcaseServer;
import de.uwuwhatsthis.supportBotForDev.stuffs.ShowcaseServerStuff;
import de.uwuwhatsthis.supportBotForDev.utils.Constants;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class GuildMemberRemoveEventListener extends ListenerAdapter {


    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event){
        User userLeft = event.getUser();
        Guild currentGuild = event.getGuild();

        if (!ShowcaseServerStuff.isShowcaseServer(currentGuild.getIdLong())) {
            if (Constants.DEBUG){
                System.out.println("INFO: Ignoring onGuildMemberRemove event on server \"" + event.getGuild().getName() + "\" in the onGuildMemberRemove event because it isn't a showcase server!");
            }
            return;
        };

        if (Constants.USERS_TO_IGNORE_WHEN_JOINING_SHOWCASE_SERVER.contains(userLeft.getIdLong())){
            if (Constants.DEBUG){
                System.out.println("INFO: Ignoring user with id \"" + userLeft.getId() + "\" because the user is in the ignore list!");
            }
            return;
        };

        ShowcaseServer currentShowcaseServer = ShowcaseServerStuff.getShowcaseServerFromGuildId(currentGuild.getIdLong());

        if (currentShowcaseServer == null){
            System.err.println("ERROR: Could not get the showcase server with id \"" + currentGuild.getIdLong() + "\"!");
            return;
        }

        if (currentShowcaseServer.getCurrentlyOccupyingTheServer() == null) {
            currentShowcaseServer.getDebug().debug("currentShowcaseServer.getCurrentlyOccupyingTheServer() == null");
            return;
        };
        if (!(currentShowcaseServer.getCurrentlyOccupyingTheServer().getUser().getIdLong() == userLeft.getIdLong())) {

            currentShowcaseServer.getDebug().debug(
                    "Somebody has left the showcase server that is not the person who joined! That is " + currentShowcaseServer.getCurrentlyOccupyingTheServer().getEffectiveName() + "!"
            );

            return;
        };

        // run cleanup part in a seperate thread to be able to wait 10 mins
        Thread thread = new Thread(currentShowcaseServer);
        currentShowcaseServer.setLeftServerAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        thread.start();

        currentShowcaseServer.getDebug().debug("User \"" + event.getUser().getName() + "\" has left the server");

        Constants.LOGGING_CHANNEL_MANAGER.userLeftServer(userLeft, currentShowcaseServer);
    }
}
