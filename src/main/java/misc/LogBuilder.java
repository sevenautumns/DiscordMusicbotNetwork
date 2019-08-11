package misc;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogBuilder {
    public static String Build(JDA bot, Message message, boolean handled){
        StringBuilder stb = new StringBuilder(Build(bot, message));
        if(handled) stb.append("Handle Message, Content-Raw: ");
        else return stb.append("Ignored Message ").toString();
        return stb.append(message.getContentRaw()).toString();
    }

    public static String Build(JDA bot, Message message){
        StringBuilder stb = BasicBuilder(bot, message.getGuild(), message.getAuthor())
                .append("[Message: ").append(message.getId()).append("] ");
        return stb.toString();
    }

    public static String Build(long bot, long guild, long user, String query){
        StringBuilder stb = new StringBuilder(DateStringNow()).append("[Bot: ")
                .append(bot).append("] [Guild: ").append(guild)
                .append("] [Sender: ").append(user).append("] [Query: ")
                .append(query).append("] ");
        return stb.toString();
    }

    public static StringBuilder BasicBuilder(JDA bot, Guild guild, User user){
        return new StringBuilder()
                .append(DateStringNow()).append(" ")
                .append("[Bot: ").append(bot.getSelfUser().getId()).append("] ")
                .append("[Guild: ").append(guild.getId()).append("] ")
                .append("[Sender: ").append(user.getId()).append("] ");
    }

    public static String DateStringNow(){
        Date todaysDate = new Date();
        DateFormat df2 = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss]");
        return df2.format(todaysDate);
    }
}
