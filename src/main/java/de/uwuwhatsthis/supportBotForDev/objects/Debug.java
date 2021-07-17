package de.uwuwhatsthis.supportBotForDev.objects;

import de.uwuwhatsthis.supportBotForDev.utils.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Debug {

    private final ShowcaseServer server;

    public Debug(ShowcaseServer server){
        this.server = server;
    }

    private String getFormat(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("d.M.u H:m:s")) + " - " + server.getServerName();
    }

    public void debug(String message){
        if (Constants.DEBUG){
            System.out.println("Debug - " + getFormat() + ": " + message);
        }
    }

    public void error(String message){
        System.err.println("ERROR - " + getFormat() + ": " + message);
    }
}
