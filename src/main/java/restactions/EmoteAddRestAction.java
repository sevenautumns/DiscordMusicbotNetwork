package restactions;

import message.enums.Emoji;
import net.dv8tion.jda.api.JDA;

public class EmoteAddRestAction extends RestAction {
    public final Emoji EMOJI;

    public EmoteAddRestAction(long GUILD_ID, long CHANNEL_ID, long MESSAGE_ID, Emoji EMOJI) {
        super(GUILD_ID, CHANNEL_ID, MESSAGE_ID);
        this.EMOJI = EMOJI;
    }

    @Override
    public void complete(JDA jda) {
        jda.getGuildById(GUILD_ID).getTextChannelById(CHANNEL_ID).addReactionById(MESSAGE_ID, EMOJI.unicode).complete();
    }
}
