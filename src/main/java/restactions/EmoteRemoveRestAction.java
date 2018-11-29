package restactions;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;

public class EmoteRemoveRestAction extends RestAction {
    public final Emote EMOTE;
    public final String EMOTE_UNICODE;
    public final long USER_ID;

    public EmoteRemoveRestAction(long GUILD_ID, long CHANNEL_ID, long USER_ID, long MESSAGE_ID, Emote emote, String emoteUnicode) {
        super(GUILD_ID, CHANNEL_ID, MESSAGE_ID);
        this.EMOTE = emote;
        this.EMOTE_UNICODE = emoteUnicode;
        this.USER_ID = USER_ID;
    }

    @Override
    public void complete(JDA jda) {
        User user = jda.getUserById(USER_ID);
        if(EMOTE != null) jda.getGuildById(GUILD_ID).getTextChannelById(CHANNEL_ID).removeReactionById(MESSAGE_ID, EMOTE, user).complete();
        else jda.getGuildById(GUILD_ID).getTextChannelById(CHANNEL_ID).removeReactionById(MESSAGE_ID, EMOTE_UNICODE, user).complete();
    }
}
