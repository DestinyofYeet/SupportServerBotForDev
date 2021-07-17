package de.uwuwhatsthis.supportBotForDev.main;

import de.uwuwhatsthis.supportBotForDev.events.*;
import de.uwuwhatsthis.supportBotForDev.managers.CommandManager;
import de.uwuwhatsthis.supportBotForDev.utils.Constants;
import de.uwuwhatsthis.supportBotForDev.stuffs.JsonStuff;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Main {

    public static void main(String[] args){

        Constants.init();

        JDABuilder jdaBuilder = JDABuilder.createDefault(JsonStuff.getStringFromJson(Constants.configPath, "bot_token"));
        jdaBuilder.enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));

        jdaBuilder.addEventListeners(new GuildMemberJoinEventListener());
        jdaBuilder.addEventListeners(new GuildMemberRemoveEventListener());
        jdaBuilder.addEventListeners(new ReadyEventListener());
        jdaBuilder.addEventListeners(new VoiceChannelDeleteEventListener());
        jdaBuilder.addEventListeners(new CommandManager());
        jdaBuilder.addEventListeners(new GuildMessageEventListener());

        try {
            jdaBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

    }


}
