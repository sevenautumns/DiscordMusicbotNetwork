package restactions;

import net.dv8tion.jda.core.JDA;

public abstract class RestAction {
    public final long MESSAGE_ID;
    public final long GUILD_ID;
    public final long CHANNEL_ID;

    public RestAction(long GUILD_ID, long CHANNEL_ID, long MESSAGE_ID){
        this.GUILD_ID = GUILD_ID;
        this.CHANNEL_ID = CHANNEL_ID;
        this.MESSAGE_ID = MESSAGE_ID;
    }

    public abstract void complete(JDA jda);
}
