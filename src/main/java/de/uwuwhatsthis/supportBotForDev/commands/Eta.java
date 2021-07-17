package de.uwuwhatsthis.supportBotForDev.commands;

import de.uwuwhatsthis.supportBotForDev.managers.CommandManager;
import de.uwuwhatsthis.supportBotForDev.objects.Args;
import de.uwuwhatsthis.supportBotForDev.objects.Embed;
import de.uwuwhatsthis.supportBotForDev.objects.ShowcaseServer;
import de.uwuwhatsthis.supportBotForDev.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Eta extends CommandManager {
    /*
    Command to look at the estimated time a user is using a server
     */

    public void execute(MessageReceivedEvent event, Args args){
        if (event.getGuild().getIdLong() != Constants.MAIN_SUPPORT_SERVER_ID) return;

        StringBuilder builder = new StringBuilder();

        for (ShowcaseServer server: Constants.SHOWCASE_SERVERS){
            builder.append("**").append(server.getServerName()).append(":**\n")
                    .append("Current estimated waiting time: ").append(server.convertSecondsToHoursAndMinutes(server.getEtaTimeAvailable() / server.getEtaEntries())).append("\n")
                    .append("Total times used: ").append(server.getEtaEntries()).append("\n")
                    .append("Longest time: ").append(server.convertSecondsToMinutesHoursAndSecondsPrecise(server.getLongestTime())).append("\n")
                    .append("Shortest time: ").append(server.convertSecondsToMinutesHoursAndSecondsPrecise(server.getShortestTime())).append("\n")
                    .append("\n");
        }

        event.getChannel().sendMessage(new Embed("Estimated Demo-Server times", builder.toString(), 1127848).build()).queue();
    }
}
