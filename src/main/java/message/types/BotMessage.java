package message.types;

import com.google.api.services.youtube.model.SearchResult;
import message.enums.Language;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.List;

public abstract class BotMessage {
    public final long MESSAGE_ID;
    public final long GUILD_ID;
    public final long CHANNELID;
    public final long TIMEOUT;
    public final Language LANGUAGE;
    protected final JDA RESPONSIBLE_BOT;

    protected BotMessage(JDA responsibleJDA, long timeout, long guildID, long channelID, Language language){
        this.TIMEOUT = timeout;
        this.RESPONSIBLE_BOT = responsibleJDA;
        this.GUILD_ID = guildID;
        this.CHANNELID = channelID;
        this.LANGUAGE = language;

        Message message = getTextChannel().sendMessage(buildMessageEmbed()).complete();

        this.MESSAGE_ID = message.getIdLong();
        addMessageReactions(message);
    }

    protected BotMessage(JDA responsibleJDA, long timeout, long guildID, long channelID, Language language, String query, List<SearchResult> searchResults, long userID){
        this.TIMEOUT = timeout;
        this.RESPONSIBLE_BOT = responsibleJDA;
        this.GUILD_ID = guildID;
        this.CHANNELID = channelID;
        this.LANGUAGE = language;
        setSearchResults(query, searchResults, userID);

        Message message = getTextChannel().sendMessage(buildMessageEmbed()).complete();

        this.MESSAGE_ID = message.getIdLong();
        addMessageReactions(message);
    }

    protected void setSearchResults(String query, List<SearchResult> results, long userID){}

    protected abstract void addMessageReactions(Message message);

    public abstract void onMessageReaction(MessageReactionAddEvent event);

    protected abstract MessageEmbed buildMessageEmbed();

    protected Guild getGuild(){
        return RESPONSIBLE_BOT.getGuildById(GUILD_ID);
    }

    protected TextChannel getTextChannel(){
        return getGuild().getTextChannelById(CHANNELID);
    }

    protected Message getMessage(){
        return getTextChannel().retrieveMessageById(MESSAGE_ID).complete();
    }

    public void deleteMessage(){
        getTextChannel().deleteMessageById(MESSAGE_ID).complete();
    }

    public abstract void onMessageDeleted();
}
