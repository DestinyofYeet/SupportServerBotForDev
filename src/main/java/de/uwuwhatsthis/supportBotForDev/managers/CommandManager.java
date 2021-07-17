package de.uwuwhatsthis.supportBotForDev.managers;

import de.uwuwhatsthis.supportBotForDev.objects.Args;
import de.uwuwhatsthis.supportBotForDev.objects.Embed;
import de.uwuwhatsthis.supportBotForDev.utils.Constants;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;


public class CommandManager extends ListenerAdapter {
    // "main" listener event


    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getChannelType().equals(ChannelType.PRIVATE) || event.getChannelType().equals(ChannelType.GROUP)){
            return;
        }

        try {
            String prefix = Constants.PREFIX;

            String splitCommandName = event.getMessage().getContentRaw().split(prefix)[1].split(" ")[0];

            if (!event.getMessage().getContentRaw().startsWith(prefix)) return;

            String commandName = splitCommandName.substring(0, 1).toUpperCase() + splitCommandName.substring(1).toLowerCase();



            Args args = new Args(event.getMessage().getContentRaw());

            Class c = null;

            try {
                // finds the class in the commands folder, capitalizes the first letter and invokes the execute method in the class
                // does a bit of mapping to get the right class name even if an alias is used
                c = Class.forName("de.uwuwhatsthis.supportBotForDev.commands." + commandName);

                Method execute = c.getDeclaredMethod("execute", MessageReceivedEvent.class, Args.class);
                Object o = c.newInstance();
                execute.invoke(o, event, args);

            } catch (ClassNotFoundException ignored){

            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
                event.getChannel().sendMessage(new Embed("Fatal Error", "Something failed: " + e.getMessage(), Color.RED).build()).queue();
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {

        } catch (HierarchyException e){
            event.getChannel().sendMessage(new Embed("Error", "You are trying to interact with somebody that has a higher role than the bot has!", Color.RED).build()).queue();
            // event.getChannel().sendMessage(new Embed("Error", "You are trying to interact with somebody that has a higher role than the bot has!", Color.RED).build()).queue();

        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
