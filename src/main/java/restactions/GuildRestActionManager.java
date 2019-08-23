package restactions;

import message.MessageManager;
import message.enums.Emoji;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

import java.util.HashMap;

public class GuildRestActionManager {
    private static GuildRestActionManager singleton;
    private HashMap<Long, RestActionManager> restActionManagerMap;

    private GuildRestActionManager(){
        this.restActionManagerMap = new HashMap<>();
    }

    public void addRestAction(RestAction restAction){
        RestActionManager manager = restActionManagerMap.get(restAction.GUILD_ID);
        if(manager == null) manager = addRestActionManager(restAction.GUILD_ID);

        manager.addEmoteRestAction(restAction);
    }

    public void onReactionRemoved(MessageReactionRemoveEvent event){
        long textChannelID = MessageManager.getInstance().getTextChannelID(event.getGuild().getIdLong());
        if(event.getChannel().getIdLong() != textChannelID) return;

        RestActionManager manager = restActionManagerMap.get(event.getGuild().getIdLong());
        if(manager == null) return;

        if(event.getReactionEmote().getEmote() != null) manager.removeUserEmoteRelatedRestActions(event.getMessageIdLong(), event.getUser().getIdLong(), event.getReactionEmote().getEmote());
        else manager.removeUserEmoteRelatedRestActions(event.getMessageIdLong(), event.getUser().getIdLong(), event.getReactionEmote().getName());
    }

    public void onMessageDeleted(GuildMessageDeleteEvent event){
        RestActionManager manager = restActionManagerMap.get(event.getGuild().getIdLong());
        if(manager == null) return;

        manager.removeMessageRelatedRestActions(event.getMessageIdLong());
    }

    private synchronized RestActionManager addRestActionManager(long guildID){
        if(restActionManagerMap.get(guildID) == null) restActionManagerMap.put(guildID, new RestActionManager(guildID));
        return restActionManagerMap.get(guildID);
    }

    public static GuildRestActionManager getInstance(){
        if(singleton != null) return singleton;
        return createSingleton();
    }

    private static synchronized GuildRestActionManager createSingleton(){
        if(singleton == null) singleton = new GuildRestActionManager();

        return singleton;
    }
}
