package de.uwuwhatsthis.supportBotForDev.events;

import de.uwuwhatsthis.supportBotForDev.managers.LoggingChannelManager;
import de.uwuwhatsthis.supportBotForDev.objects.ShowcaseServer;
import de.uwuwhatsthis.supportBotForDev.utils.Constants;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyEventListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event){
        Guild mainSupportServer = event.getJDA().getGuildById(Constants.MAIN_SUPPORT_SERVER_ID);
        if (mainSupportServer == null){
            System.err.println("ERROR: Unable to find main support server with id \"" + Constants.MAIN_SUPPORT_SERVER_ID + "\"! Shutting down!");
            event.getJDA().shutdown();
            return;
        }
        Constants.MAIN_SUPPORT_SERVER = mainSupportServer;

        for (ShowcaseServer showcaseServer: Constants.SHOWCASE_SERVERS){
            showcaseServer.init(event.getJDA());
        }

        Constants.LOGGING_CHANNEL_MANAGER = new LoggingChannelManager();

        System.out.println("INFO: Debugging is " + (Constants.DEBUG ? "enabled" : "disabled"));
        System.out.println("INFO: Running on version: " + Constants.VERSION);
        System.out.println("INFO: " + Constants.SHOWCASE_SERVERS.length + " server(s) have been initialized!");
        for (ShowcaseServer showcaseServer: Constants.SHOWCASE_SERVERS){
            System.out.println("- " + showcaseServer.getServerName() + " : " + showcaseServer.getRealServerName());
        }
    }
}
