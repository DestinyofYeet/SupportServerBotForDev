package de.uwuwhatsthis.supportBotForDev.commands;

import de.uwuwhatsthis.supportBotForDev.objects.Args;
import de.uwuwhatsthis.supportBotForDev.objects.Embed;
import de.uwuwhatsthis.supportBotForDev.objects.ShowcaseServer;
import de.uwuwhatsthis.supportBotForDev.stuffs.Status;
import de.uwuwhatsthis.supportBotForDev.utils.Constants;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDateTime;

public class Demo implements Runnable{
    /*
    Command to lock demo servers
     */
    private Permission permissionNeeded = Permission.ADMINISTRATOR;

    private MessageReceivedEvent event;
    private Args args;

    public void execute(MessageReceivedEvent event, Args args){
        this.event = event;
        this.args = args;
        if (!event.getMember().hasPermission(permissionNeeded)) return;

        if (args.isEmpty()){
            event.getChannel().sendMessage(new Embed("Demo server lock", "Currently the demo servers are " + (Constants.isLocked ? "locked" : "unlocked") + ".\n\nTo change the state, run this command with the argument \"lock\" or \"unlock\"", Color.GREEN).build()).queue();
            return;
        }

        // creating a new Thread to not fuck the main discord thread while calling Thread.sleep
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        switch (args.get(0)){

            case "lock":
                if (Constants.isLocked){
                    event.getChannel().sendMessage(new Embed("Error", "The demo servers are already locked!", Color.RED).build()).queue();
                    return;
                }

                for (ShowcaseServer server: Constants.SHOWCASE_SERVERS){
                    server.setStatus(Status.ERROR);
                    server.setTxtState(Status.OCCUPIED);
                    server.getTxtChannel().getHistory().retrievePast(20).queue(history -> {
                        history.forEach(message -> {
                            message.delete().queue();
                        });
                    });
                }

                event.getChannel().sendMessage(new Embed("Demo server lock", "The demo server have been locked!", Color.GREEN).build()).queue();
                Constants.isLocked = true;
                Constants.commandRan = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                break;

            case "unlock":
                if (!Constants.isLocked){
                    event.getChannel().sendMessage(new Embed("Error", "The demo servers are already unlocked!", Color.GREEN).build()).queue();
                    return;
                }

                long secondsFromCommandBeingRan = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - Constants.commandRan;
                long secondsLeft = 10*60 - secondsFromCommandBeingRan;

                if (secondsLeft > 0){
                    event.getChannel().sendMessage(new Embed("Demo server lock", "Waiting " + secondsLeft + " seconds to not hit the discord api limit!", Color.GREEN).build()).queue();

                    while (secondsLeft > 0){
                        secondsLeft --;

                        try {
                            Thread.sleep(secondsLeft);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }
                for (ShowcaseServer server: Constants.SHOWCASE_SERVERS){
                    // server.setLeftServerAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
                    server.unlockServer();
                }

                event.getChannel().sendMessage(new Embed("Demo server lock", "The demo servers are now being unlocked, this takes around 40 seconds!", Color.GREEN).build()).queue();
                Constants.isLocked = false;
                break;

            default:
                event.getChannel().sendMessage(new Embed("Error", "Invalid argument: " + args.get(0), Color.RED).build()).queue();
        }
    }
}
