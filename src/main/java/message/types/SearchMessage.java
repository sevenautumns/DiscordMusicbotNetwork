package message.types;

import com.google.api.services.youtube.model.SearchResult;
import message.MessageEmbedBuilder;
import message.MessageManager;
import message.enums.Emoji;
import message.enums.Language;
import music.PlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import restactions.EmoteAddRestAction;
import restactions.EmoteRemoveRestAction;
import restactions.GuildRestActionManager;

import java.util.List;

public class SearchMessage extends BotMessage {
    public long userID;
    private List<SearchResult> searchResults;
    private String query;
    private static final long TIMEOUT_SEARCHMESSAGE = 1000 * 60 * 2;    //2 minutes


    public SearchMessage(JDA responsibleJDA, TextChannel channel, long userID, String query, List<SearchResult> searchResults, Language language) {
        super(responsibleJDA, System.currentTimeMillis() + TIMEOUT_SEARCHMESSAGE, channel.getGuild().getIdLong(), channel.getIdLong(), language, query, searchResults, userID);
    }

    @Override
    protected void setSearchResults(String query, List<SearchResult> results, long userID) {
        this.searchResults = results;
        this.query = query;
        this.userID = userID;
    }

    @Override
    protected void addMessageReactions(Message message) {
        GuildRestActionManager manager = GuildRestActionManager.getInstance();

        for(int i = 0; i < searchResults.size() && i < 10; i++){
            manager.addRestAction(new EmoteAddRestAction(GUILD_ID, CHANNELID, MESSAGE_ID, Emoji.values()[i]));
        }
        manager.addRestAction(new EmoteAddRestAction(GUILD_ID, CHANNELID, MESSAGE_ID, Emoji.RED_X_EMOJI));
    }

    @Override
    public void onMessageReaction(MessageReactionAddEvent event) {
        Emoji reaction = Emoji.getEmojiByUnicode(event.getReactionEmote().getName());

        if(event.getUser().getIdLong() != userID){
            GuildRestActionManager.getInstance().addRestAction(new EmoteRemoveRestAction(GUILD_ID, CHANNELID, event.getUser().getIdLong(), MESSAGE_ID, event.getReactionEmote().getEmote(), event.getReactionEmote().getName()));
            return;
        }
        if(!event.getMember().getVoiceState().inVoiceChannel()){
            deleteMessage();
            return;
        }
        if(!getGuild().getSelfMember().getVoiceState().inVoiceChannel()){
            deleteMessage();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != getGuild().getSelfMember().getVoiceState().getChannel().getIdLong()){
            deleteMessage();
            return;
        }
        if(reaction == null){
            GuildRestActionManager.getInstance().addRestAction(new EmoteRemoveRestAction(GUILD_ID, CHANNELID, event.getUser().getIdLong(), MESSAGE_ID, event.getReactionEmote().getEmote(), event.getReactionEmote().getName()));
            return;
        }

        PlayerManager playerManager = PlayerManager.getInstance();
        Guild guild = getGuild();

        switch (reaction){
            case A_ONE_EMOJI:
                playerManager.addTrackToPlayer(guild, RESPONSIBLE_BOT.getSelfUser().getIdLong(), userID, "https://www.youtube.com/watch?v=" + searchResults.get(0).getId().getVideoId());
                deleteMessage();
                break;
            case B_TWO_EMOJI:
                playerManager.addTrackToPlayer(guild, RESPONSIBLE_BOT.getSelfUser().getIdLong(), userID, "https://www.youtube.com/watch?v=" + searchResults.get(1).getId().getVideoId());
                deleteMessage();
                break;
            case C_THREE_EMOJI:
                playerManager.addTrackToPlayer(guild, RESPONSIBLE_BOT.getSelfUser().getIdLong(), userID, "https://www.youtube.com/watch?v=" + searchResults.get(2).getId().getVideoId());
                deleteMessage();
                break;
            case D_FOUR_EMOJI:
                playerManager.addTrackToPlayer(guild, RESPONSIBLE_BOT.getSelfUser().getIdLong(), userID, "https://www.youtube.com/watch?v=" + searchResults.get(3).getId().getVideoId());
                deleteMessage();
                break;
            case E_FIVE_EMOJI:
                playerManager.addTrackToPlayer(guild, RESPONSIBLE_BOT.getSelfUser().getIdLong(), userID, "https://www.youtube.com/watch?v=" + searchResults.get(4).getId().getVideoId());
                deleteMessage();
                break;
            case F_SIX_EMOJI:
                playerManager.addTrackToPlayer(guild, RESPONSIBLE_BOT.getSelfUser().getIdLong(), userID, "https://www.youtube.com/watch?v=" + searchResults.get(5).getId().getVideoId());
                deleteMessage();
                break;
            case G_SEVEN_EMOJI:
                playerManager.addTrackToPlayer(guild, RESPONSIBLE_BOT.getSelfUser().getIdLong(), userID, "https://www.youtube.com/watch?v=" + searchResults.get(6).getId().getVideoId());
                deleteMessage();
                break;
            case H_EIGHT_EMOJI:
                playerManager.addTrackToPlayer(guild, RESPONSIBLE_BOT.getSelfUser().getIdLong(), userID, "https://www.youtube.com/watch?v=" + searchResults.get(7).getId().getVideoId());
                deleteMessage();
                break;
            case I_NINE_EMOJI:
                playerManager.addTrackToPlayer(guild, RESPONSIBLE_BOT.getSelfUser().getIdLong(), userID, "https://www.youtube.com/watch?v=" + searchResults.get(8).getId().getVideoId());
                deleteMessage();
                break;
            case J_TEN_EMOJI:
                playerManager.addTrackToPlayer(guild, RESPONSIBLE_BOT.getSelfUser().getIdLong(), userID, "https://www.youtube.com/watch?v=" + searchResults.get(9).getId().getVideoId());
                deleteMessage();
                break;
            case RED_X_EMOJI:
                deleteMessage();
                break;
            default:
                GuildRestActionManager.getInstance().addRestAction(new EmoteRemoveRestAction(GUILD_ID, CHANNELID, event.getUser().getIdLong(), MESSAGE_ID, event.getReactionEmote().getEmote(), event.getReactionEmote().getName()));
                break;
        }
    }

    @Override
    protected MessageEmbed buildMessageEmbed() {
        MessageManager.getInstance().deleteUserMessage(GUILD_ID, userID);

        MessageEmbedBuilder builder = new MessageEmbedBuilder();

        Guild guild = RESPONSIBLE_BOT.getGuildById(GUILD_ID);
        Member member = guild.getMemberById(userID);
        String name = member.getNickname();
        if(name == null) name = RESPONSIBLE_BOT.getGuildById(GUILD_ID).getMemberById(userID).getEffectiveName();

        builder.addText("[").addText(query).addText("] #")
                .addText(name.replace(' ', '_'))
                .addLineBreak();
        for(int i = 0; i < searchResults.size() && i < 10; i++){
            SearchResult result = searchResults.get(i);
            builder.addText(String.valueOf(i+1)).addText(": ")
                    .addText(" : \"")
                    .addText(result.getSnippet().getTitle()
                            .replace("\"", "")
                            .replace("@", "")
                            .replace("#", "")
                            .replace("[" , "(")
                            .replace("]", ")"))
                    .addText("\"")
                    .addLineBreak();
        }

        builder.setFooter(Emoji.RED_X_EMOJI.unicode + Text.getText(Text.BUTTONCLOSE, LANGUAGE));

        return builder.build();
    }

    @Override
    public void onMessageDeleted() {
        return;
    }

    private enum Text{
        BUTTONCLOSE ("To close this Message", "Um diese Nachricht zu schließen");

        public final String eng;
        public final String ger;

        Text(String eng, String ger){
            this.eng = eng;
            this.ger = ger;
        }

        public static String getText(Text text, Language language){
            switch (language){
                case ENGLISCH:
                    return text.eng;
                case GERMAN:
                    return text.ger;
                default:
                    return text.eng;
            }
        }
    }
}
