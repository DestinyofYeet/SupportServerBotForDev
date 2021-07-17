package de.uwuwhatsthis.supportBotForDev.objects;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Embed {
    // improved embed builder (embed builder v 2)

    private EmbedBuilder builder;

    public Embed(String title, String message, Color color){
        try{
            builder = new EmbedBuilder();
            builder.addField(title, message, false);
            builder.setColor(color);
            builder.setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")));

        } catch (IllegalArgumentException noted){
            builder = null;
        }

    }

    public Embed(String title, String message, Integer color){
        try{
            builder = new EmbedBuilder();
            builder.addField(title, message, false);
            builder.setColor(color);
            builder.setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")));

        } catch (IllegalArgumentException noted){
            builder = null;
        }

    }

    public Embed(String title, String message, Color color, String additionalFooter){
        try{
            EmbedBuilder builder = new EmbedBuilder();
            builder.addField(title, message, false);
            builder.setColor(color);
            builder.setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")) + " | " + additionalFooter);

        } catch (IllegalArgumentException noted){
            builder = null;
        }

    }

    public Embed addField(String title, String message, boolean inline){
        try{
            builder.addField(title, message, inline);
            return this;

        } catch (IllegalArgumentException noted){
            return null;
        }
    }

    public Embed setImage(String image){
        builder.setImage(image);
        return this;
    }

    public Embed setThumbnail(String image){
        builder.setThumbnail(image);
        return this;
    }

    public Embed addField(String title, String message){ return addField(title,message, false);
    }

    public MessageEmbed build(){
        if (builder != null)
            return builder.build();
        else
            return null;
    }

}
