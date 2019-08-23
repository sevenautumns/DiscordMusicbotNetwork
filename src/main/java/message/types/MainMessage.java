package message.types;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import message.MessageEmbedBuilder;
import message.MessageManager;
import message.enums.Language;
import message.enums.Emoji;
import misc.Utility;
import music.BotAudioPlayer;
import music.PlayerManager;
import music.enums.AudioPlayerState;
import music.enums.Playmode;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.apache.commons.lang3.ArrayUtils;
import restactions.EmoteAddRestAction;
import restactions.EmoteRemoveRestAction;
import restactions.GuildRestActionManager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.CRC32;

public class MainMessage extends BotMessage {
    private static final int MAX_SONG_LETTER = 50;
    private static final long TIMEOUT_MAINMESSAGE = 4100713200000L;
    private Timer updateMessageTimer;

    public MainMessage(TextChannel textChannel, JDA responsibleBot, Language language) {
        super(responsibleBot, TIMEOUT_MAINMESSAGE, textChannel.getGuild().getIdLong(), textChannel.getIdLong(), language);

        updateMessageTimer = new Timer(true);
        TimerTask update = new UpdateMainMessage();
        updateMessageTimer.scheduleAtFixedRate(update, 0, 1000);
    }

    @Override
    protected void addMessageReactions(Message message) {
        GuildRestActionManager manager = GuildRestActionManager.getInstance();
        manager.addRestAction(new EmoteAddRestAction(GUILD_ID, CHANNELID, MESSAGE_ID, Emoji.FAST_BACKWARD_EMOJI));
        manager.addRestAction(new EmoteAddRestAction(GUILD_ID, CHANNELID, MESSAGE_ID, Emoji.PLAY_PAUSE_EMOJI));
        manager.addRestAction(new EmoteAddRestAction(GUILD_ID, CHANNELID, MESSAGE_ID, Emoji.FAST_FORWARD_EMOJI));
        manager.addRestAction(new EmoteAddRestAction(GUILD_ID, CHANNELID, MESSAGE_ID, Emoji.LOOP_ONE_EMOJI));
        manager.addRestAction(new EmoteAddRestAction(GUILD_ID, CHANNELID, MESSAGE_ID, Emoji.LOOP_ALL_EMOJI));
        manager.addRestAction(new EmoteAddRestAction(GUILD_ID, CHANNELID, MESSAGE_ID, Emoji.INBOX));
        manager.addRestAction(new EmoteAddRestAction(GUILD_ID, CHANNELID, MESSAGE_ID, Emoji.OUTBOX));
    }

    @Override
    public void onMessageReaction(MessageReactionAddEvent event) {
        Emoji reaction = Emoji.getEmojiByUnicode(event.getReactionEmote().getName());

        GuildRestActionManager.getInstance().addRestAction(new EmoteRemoveRestAction(GUILD_ID, CHANNELID, event.getUser().getIdLong(), MESSAGE_ID, event.getReactionEmote().getEmote(), event.getReactionEmote().getName()));
        if(reaction == null) return;

        if(!event.getMember().getVoiceState().inVoiceChannel()) return;

        VoiceChannel channel = event.getMember().getVoiceState().getChannel();
        PlayerManager playerManager = PlayerManager.getInstance();
        BotAudioPlayer player = playerManager.getAudioPlayer(channel);

        switch (reaction){
            case FAST_BACKWARD_EMOJI:
                if(player == null) return;
                player.previousSong();
                break;
            case FAST_FORWARD_EMOJI:
                if(player == null) return;
                player.nextSong();
                break;
            case PLAY_PAUSE_EMOJI:
                if(player == null) return;

                if(player.getAudioPlayerState() == AudioPlayerState.PLAYING) player.pauseSong();
                else if(player.getAudioPlayerState() == AudioPlayerState.PAUSED) player.resumeSong();

                break;
            case LOOP_ONE_EMOJI:
                if(player == null) return;
                if(player.getPlaymode() != Playmode.LOOPONE) player.changePlaymode(Playmode.LOOPONE);
                else if(player.getPlaymode() == Playmode.LOOPONE) player.changePlaymode(Playmode.NORMAL);
                break;
            case LOOP_ALL_EMOJI:
                if(player == null) return;
                if(player.getPlaymode() != Playmode.LOOPALL) player.changePlaymode(Playmode.LOOPALL);
                else if(player.getPlaymode() == Playmode.LOOPALL) player.changePlaymode(Playmode.NORMAL);
                break;
            case INBOX:
                if(player != null) return;
                playerManager.connectToVoiceChannel(channel);
                break;
            case OUTBOX:
                if(player == null) return;
                playerManager.disconnectFromVoiceChannel(channel);
                break;
        }
    }

    @Override
    protected MessageEmbed buildMessageEmbed() {
        MessageEmbedBuilder builder = new MessageEmbedBuilder();
        BotAudioPlayer[] audioPlayers = PlayerManager.getInstance().getGuildAudioPlayer(GUILD_ID);

        if(audioPlayers.length <= 0){
            builder.addText(Text.getText(Text.NOACTIVEPLAYER, LANGUAGE));

            builder.addLineBreak().addLineBreak();
        }else {
            for(BotAudioPlayer ap: audioPlayers){
                String name = ap.BOT.getGuildById(GUILD_ID).getSelfMember().getNickname();
                if (name == null) name = ap.BOT.getGuildById(GUILD_ID).getSelfMember().getEffectiveName();

                builder.addText("#").addText(name.replace(' ', '_')).addText(" ");
                switch (ap.getAudioPlayerState()){
                    case PLAYING:
                        builder.addText(Text.getText(Text.PLAYING, LANGUAGE));
                        break;
                    case PAUSED:
                        builder.addText(Text.getText(Text.PAUSED, LANGUAGE));
                        break;
                    case STOPPED:
                        builder.addText(Text.getText(Text.STOPPED, LANGUAGE));
                        break;
                    default:
                        break;
                }
                switch (ap.getPlaymode()){
                    case LOOPALL:
                        builder.addText(" ").addText(Emoji.LOOP_ALL_EMOJI.unicode);
                        break;
                    case LOOPONE:
                        builder.addText(" ").addText(Emoji.LOOP_ONE_EMOJI.unicode);
                        break;
                    default:
                        break;
                }
                AudioTrack[] playlist = ap.getPlaylist();

                if(playlist.length > 0){
                    String title = playlist[0].getInfo().title;
                    if(title.length() > MAX_SONG_LETTER) title = title.substring(0, 47) + "...";

                    builder.addLineBreak()
                            //.addText(Text.getText(Text.CURRENT, LANGUAGE))
                            .addText(title)
                            .addText(" ")
                            .addText("[")
                            .addText(Utility.longToStringTime(playlist[0].getPosition()))
                            .addText("/")
                            .addText(Utility.longToStringTime(playlist[0].getDuration()))
                            .addText("]");

                    if(playlist.length > 1){
                        title = playlist[1].getInfo().title;
                        if(title.length() > MAX_SONG_LETTER) title = title.substring(0, 47) + "...";

                        builder.addLineBreak()
                                .addText(Text.getText(Text.NEXT, LANGUAGE))
                                .addText(title);
                    }
                }
                builder.addLineBreak().addLineBreak();
            }
        }
        builder.addLineBreak();

        builder.addText(Emoji.FAST_BACKWARD_EMOJI.unicode).addText(" ").addText(Text.getText(Text.BUTTONBACKWARDS, LANGUAGE)).addText("; ")
                .addText(Emoji.PLAY_PAUSE_EMOJI.unicode).addText(" ").addText(Text.getText(Text.BUTTONPLAYPAUSE, LANGUAGE)).addText("; ")
                .addText(Emoji.FAST_FORWARD_EMOJI.unicode).addText(" ").addText(Text.getText(Text.BUTTONFORWARD, LANGUAGE)).addText(";").addLineBreak()
                .addText(Emoji.LOOP_ONE_EMOJI.unicode).addText(" ").addText(Text.getText(Text.BUTTONLOOPONE, LANGUAGE)).addText("; ")
                .addText(Emoji.LOOP_ALL_EMOJI.unicode).addText(" ").addText(Text.getText(Text.BUTTONLOOPALL, LANGUAGE)).addText(";").addLineBreak()
                .addText(Emoji.INBOX.unicode).addText(" ").addText(Text.getText(Text.BUTTONINBOX, LANGUAGE)).addText("; ").addLineBreak()
                .addText(Emoji.OUTBOX.unicode).addText(" ").addText(Text.getText(Text.BUTTONOUTBOX, LANGUAGE));

        return builder.build();
    }

    @Override
    public void onMessageDeleted() {
        updateMessageTimer.cancel();

        MessageManager.getInstance().newMainMessage(GUILD_ID, RESPONSIBLE_BOT);
    }

    private enum Text{
        NOACTIVEPLAYER ("[There is no active Musicbot]", "[Kein aktiver Musikbot]"),
        PLAYING ("[PLAYING]", "[SPIELT]"),
        PAUSED ("[PAUSED]", "[PAUSIERT]"),
        STOPPED ("[NO SONGS IN PLAYLIST]", "[KEINE LIEDER IN DER PLAYLIST]"),
        CURRENT ("[CURRENT SONG] ", "[AKTUELLES LIED] "),
        NEXT ("[NEXT] ", "[NÄCHSTES] "),
        BUTTONBACKWARDS ("Previous Song", "Vorheriges Lied"),
        BUTTONPLAYPAUSE ("Resume/Pause Song", "Lied fortsetzten/pausieren"),
        BUTTONFORWARD ("Next Song", "Nächstes Lied"),
        BUTTONLOOPONE ("Repeat one Song", "Ein Lied wiederholen"),
        BUTTONLOOPALL ("Repeat all Songs", "Alle Lieder wiederholen"),
        BUTTONINBOX ("Make Bot join your Voicechannel", "Lässt Bot dir beitreten"),
        BUTTONOUTBOX ("Make Bot leave your Voicechannel", "Lässt Bot dich verlassen");

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

    private class UpdateMainMessage extends TimerTask{
        private long checksum;

        @Override
        public void run() {
            try {
                task();
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void task(){
            MessageEmbed embed = buildMessageEmbed();
            long check = generateChecksum(embed);
            if(checksum != check) getTextChannel().editMessageById(MESSAGE_ID, buildMessageEmbed()).complete();
            checksum = check;
        }

        private long generateChecksum(MessageEmbed embed){
            CRC32 checksum = new CRC32();
            checksum.update(embed.getDescription().getBytes());
            return checksum.getValue();
        }
    }
}
