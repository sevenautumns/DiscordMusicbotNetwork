package music;

import bot.BotManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import message.MessageManager;
import misc.LogBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerManager {
    private HashMap<String, BotAudioPlayer> playerMap;
    private static PlayerManager singleton;
    private final AudioPlayerManager AUDIOPLAYERMANAGER;

    private PlayerManager(){
        this.playerMap = new HashMap<>();
        this.AUDIOPLAYERMANAGER = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(AUDIOPLAYERMANAGER);
        AudioSourceManagers.registerLocalSource(AUDIOPLAYERMANAGER);
    }

    public BotAudioPlayer getAudioPlayer(long guildID, long botID){
        return playerMap.get(String.valueOf(guildID) + botID);
    }

    public BotAudioPlayer getAudioPlayer(VoiceChannel channel){
        JDA[] bots = BotManager.getInstance().getBots(channel.getGuild().getIdLong());
        for (JDA b: bots) {
            if(!b.getGuildById(channel.getGuild().getIdLong()).getSelfMember().getVoiceState().inVoiceChannel())continue;
            if(b.getGuildById(channel.getGuild().getIdLong()).getSelfMember().getVoiceState().getChannel().getIdLong() != channel.getIdLong()) continue;

            return playerMap.get(channel.getGuild().getId() + b.getSelfUser().getId());
        }
        return null;
    }

    public JDA connectToVoiceChannel(VoiceChannel channel){
        BotManager botManager = BotManager.getInstance();

        List<Member> alreadyInChannel = channel.getMembers();
        for(Member m: alreadyInChannel){
            if(!m.getUser().isBot())continue;
            if(botManager.isMusicBot(m.getUser().getIdLong())) return null;
        }


        JDA[] bots = botManager.getBots(channel.getGuild().getIdLong());
        ArrayList<JDA> botList = new ArrayList<>();
        for (JDA b: bots){
            if(!b.getGuildById(channel.getGuild().getIdLong()).getSelfMember().getVoiceState().inVoiceChannel()) botList.add(b);
        }
        if(botList.size() == 0) return null;

        JDA bot = botList.get(ThreadLocalRandom.current().nextInt(0, botList.size()));
        channel = bot.getVoiceChannelById(channel.getIdLong());

        if(bot.getGuildById(channel.getGuild().getIdLong()).getSelfMember().getVoiceState().inVoiceChannel()) return connectToVoiceChannel(channel);
        AudioManager manager = bot.getGuildById(channel.getGuild().getIdLong()).getAudioManager();

        manager.openAudioConnection(channel);

        BotAudioPlayer player = new BotAudioPlayer(bot, AUDIOPLAYERMANAGER);
        playerMap.put(channel.getGuild().getId() + bot.getSelfUser().getId(), player);

        manager.setSendingHandler(player.getSendHandler());

        return bot;
    }

    public void disconnectFromVoiceChannel(VoiceChannel channel){
        List<Member> channelMemberList = channel.getMembers();
        Member[] channelMemberArray = new Member[channelMemberList.size()];
        BotManager botManager = BotManager.getInstance();

        for(Member m: channelMemberList.toArray(channelMemberArray)){
            if(!m.getUser().isBot()) continue;
            if(!botManager.isMusicBot(m.getUser().getIdLong())) continue;


            BotAudioPlayer player = playerMap.get(channel.getGuild().getId() + m.getUser().getId());
            if(player != null) player.destroy();

            playerMap.remove(channel.getGuild().getId() + m.getUser().getId());
            Guild guild = botManager.getJDA(m.getUser().getIdLong()).getGuildById(channel.getGuild().getIdLong());
            guild.getAudioManager().closeAudioConnection();
        }
    }

    public void disconnectFromVoiceChannel(VoiceChannel channel, long botID){
        JDA jda = BotManager.getInstance().getJDA(botID);
        Guild guild = jda.getGuildById(channel.getGuild().getIdLong());
        if(!guild.getSelfMember().getVoiceState().inVoiceChannel()) return;
        if(guild.getSelfMember().getVoiceState().getChannel().getIdLong() != channel.getIdLong()) return;

        BotAudioPlayer player = playerMap.get(channel.getGuild().getId() + jda.getSelfUser().getId());
        if(player != null) player.destroy();

        playerMap.remove(channel.getGuild().getId() + jda.getSelfUser().getId());
        guild.getAudioManager().closeAudioConnection();
    }

    public BotAudioPlayer[] getGuildAudioPlayer(long guildID){
        JDA[] bots = BotManager.getInstance().getBots(guildID);
        ArrayList<BotAudioPlayer> player = new ArrayList<>();

        for(JDA b: bots){
            BotAudioPlayer a = playerMap.get(guildID + b.getSelfUser().getId());
            if(a == null) continue;

            player.add(a);
        }

        BotAudioPlayer[] playerArray = new BotAudioPlayer[player.size()];
        return player.toArray(playerArray);
    }

    public void addTrackToPlayer(final Guild guild, final long botID, final long userID, final String query){
        final BotAudioPlayer player = playerMap.get(guild.getId() + botID);
        if (player == null) {
            System.err.println(LogBuilder.Build(botID, guild.getIdLong(), userID, query) + "Failed because there is no Player");
            return;
        }

        AUDIOPLAYERMANAGER.loadItem(query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                player.addTrack(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                for(AudioTrack track: audioPlaylist.getTracks()){
                    player.addTrack(track);
                }
            }

            @Override
            public void noMatches() {
                MessageManager.getInstance().createMusicSearchMessage(guild, botID, userID, query);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                BotManager.getInstance().getRandomBot(guild).getUserById(userID).openPrivateChannel().complete().sendMessage("Something went wrong, please try again!").queue();
            }
        });
    }

    public static PlayerManager getInstance(){
        if(singleton != null) return singleton;

        return createSingleton();
    }

    private static synchronized PlayerManager createSingleton(){
        if(singleton == null) singleton = new PlayerManager();
        return singleton;
    }
}
