package de.uwuwhatsthis.supportBotForDev.stuffs;

import de.uwuwhatsthis.supportBotForDev.objects.ShowcaseServer;
import de.uwuwhatsthis.supportBotForDev.utils.Constants;

public class ShowcaseServerStuff {

    public static boolean isShowcaseServer(Long guildID){

        ShowcaseServer currentServer = null;
        for (ShowcaseServer showcaseServer: Constants.SHOWCASE_SERVERS){
            if (showcaseServer.getGuildID().equals(guildID)){
                return true;
            }
        }

        return false;
    }

    public static ShowcaseServer getShowcaseServerFromGuildId(Long guildID){
        for (ShowcaseServer showcaseServer: Constants.SHOWCASE_SERVERS){
            if (showcaseServer.getGuildID().equals(guildID)) return showcaseServer;
        }

        return null;
    }
}
