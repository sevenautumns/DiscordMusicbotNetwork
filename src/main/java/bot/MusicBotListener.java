package bot;

import message.MessageManager;
import music.PlayerManager;
import net.dv8tion.jda.core.events.guild.GuildReadyEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import restactions.GuildRestActionManager;

public class MusicBotListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(BotManager.getInstance().getFirstBotID(event.getGuild()) != event.getJDA().getSelfUser().getIdLong()) return;

        MessageManager.getInstance().onGuildMessageReceived(event);
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(BotManager.getInstance().getFirstBotID(event.getGuild()) != event.getJDA().getSelfUser().getIdLong()) return;

        MessageManager.getInstance().onMessageReactionAdd(event);
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if(BotManager.getInstance().getFirstBotID(event.getGuild()) != event.getJDA().getSelfUser().getIdLong()) return;

        GuildRestActionManager.getInstance().onReactionRemoved(event);
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        MessageManager.getInstance().onGuildReady(event);
    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        if(BotManager.getInstance().getFirstBotID(event.getGuild()) != event.getJDA().getSelfUser().getIdLong()) return;

        MessageManager.getInstance().onGuildMessageDelete(event);
        GuildRestActionManager.getInstance().onMessageDeleted(event);
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if(!event.getMember().getUser().isBot()) return;
        if(BotManager.getInstance().getFirstBotID(event.getGuild()) != event.getJDA().getSelfUser().getIdLong()) return;
        if(!BotManager.getInstance().isMusicBot(event.getMember().getUser().getIdLong())) return;

        BotManager.getInstance().checkChannelJoinEvent(event.getChannelJoined(), event.getMember().getUser().getIdLong());
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if(BotManager.getInstance().getFirstBotID(event.getGuild()) != event.getJDA().getSelfUser().getIdLong()) return;

        if(!event.getMember().getUser().isBot()) {
            BotManager.getInstance().checkChannelLeaveEvent(event.getChannelLeft());
            return;
        }
        else if(!BotManager.getInstance().isMusicBot(event.getMember().getUser().getIdLong())) return;

        PlayerManager.getInstance().disconnectFromVoiceChannel(event.getChannelJoined());
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if(event.getMember().getUser().isBot()) return;
        if(BotManager.getInstance().getFirstBotID(event.getGuild()) != event.getJDA().getSelfUser().getIdLong()) return;

        BotManager.getInstance().checkChannelLeaveEvent(event.getChannelLeft());

    }
}
