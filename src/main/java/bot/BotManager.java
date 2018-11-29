package bot;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import misc.Utility;
import music.PlayerManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BotManager {
    private static BotManager singleton;
    private HashMap<Long, JDA> botMap;

    private BotManager(){
        botMap = new HashMap<>();

        Properties botkeys = new Properties();
        Utility.loadProperties("botkeys.properties", botkeys);

        for(Object key : botkeys.keySet().toArray()){
            try {
                JDA jda = new JDABuilder(AccountType.BOT)
                        .setToken(botkeys.getProperty((String) key))
                        .setAudioSendFactory(new NativeAudioSendFactory())
                        .addEventListener(new MusicBotListener())
                        .build()
                        .awaitReady();
                jda.getPresence().setGame(null);

                botMap.put(jda.getSelfUser().getIdLong(), jda);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }
    }

    public JDA getJDA(long botID){
        return botMap.get(botID);
    }

    public static String getBotName(JDA jda, Guild guild){
        return guild.getMemberById(jda.getSelfUser().getId()).getNickname();
    }

    public JDA getFirstBot(Guild guild){
        for (Member m: guild.getMembers() ) {
            if(!m.getUser().isBot()) continue;
            if(botMap.keySet().contains(m.getUser().getIdLong())) return botMap.get(m.getUser().getIdLong());
        }
        return null;
    }

    public long getFirstBotID(Guild guild){
        for (Member m: guild.getMembers()){
            if(!m.getUser().isBot()) continue;
            if(botMap.keySet().contains(m.getUser().getIdLong())) return m.getUser().getIdLong();
        }
        return -1;
    }

    public JDA getRandomBot(Guild guild){
        ArrayList<JDA> bots = new ArrayList<>();
        for (long id: (Long[]) botMap.keySet().toArray()) {
            if(guild.getMemberById(id) == null) continue;

            bots.add(botMap.get(id));
        }

        return bots.get(ThreadLocalRandom.current().nextInt(0, bots.size()));
    }

    public JDA[] getBots(long guildID){
        ArrayList<JDA> bots = new ArrayList<>();

        for (JDA jda: botMap.values()){
            if(jda.getGuildById(guildID) != null) bots.add(jda);
        }

        JDA[] jdas = new JDA[bots.size()];
        return bots.toArray(jdas);
    }

    public synchronized void checkChannelJoinEvent(VoiceChannel channel, long botID){ //TODO es sind unendlich viele Bots in einem channel möglich
        List<Member> members = channel.getMembers();
        for (Member m: members){
            if(m.getUser().getIdLong() == botID) continue;
            if(isMusicBot(m.getUser().getIdLong())){
                PlayerManager.getInstance().disconnectFromVoiceChannel(channel, botID);
                return;
            }
        }
    }

    public void checkChannelLeaveEvent(VoiceChannel channel){ //TODO if bot is moved always disconnect
        for (Member m: channel.getMembers()){
            if(!m.getUser().isBot()) return;
        }

        for (Member m: channel.getMembers()){
            if(!isMusicBot(m.getUser().getIdLong())) continue;
            PlayerManager.getInstance().disconnectFromVoiceChannel(channel, m.getUser().getIdLong());
        }
    }

    public boolean isMusicBot(long ID){
        for (Long i: botMap.keySet()) {
            if(i == ID) return true;
        }
        return false;
    }

    public static BotManager getInstance(){
        if(singleton != null) return singleton;

        return createSingleton();
    }

    private static synchronized BotManager createSingleton(){
        if(singleton == null) singleton = new BotManager();
        return singleton;
    }
}
