package message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.ArrayUtils;

public class MessageEmbedBuilder {
    private StringBuilder description;
    private String footer;

    public MessageEmbedBuilder(){
        description = new StringBuilder();
        description.append("```css\r\n");
        footer = "";
    }

    public MessageEmbedBuilder addText(String text){
        description.append(text);
        return this;
    }

    public MessageEmbedBuilder addLineBreak(){
        description.append("\r\n");
        return this;
    }

    public MessageEmbedBuilder setFooter(String footer){
        if(footer != null) this.footer = footer;
        return this;
    }

    public MessageEmbed build(){
        addLineBreak().addText("```");

        EmbedBuilder eb = new EmbedBuilder();
        eb.appendDescription(description.toString());
        if(footer != null) eb.setFooter(footer, TRANSPARENT_PIXEL);

        return eb.build();
    }

    public final static String TRANSPARENT_PIXEL = "https://upload.wikimedia.org/wikipedia/commons/c/ce/Transparent.gif";
}
