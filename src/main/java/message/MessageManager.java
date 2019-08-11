package message;

import bot.BotManager;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import message.enums.Commands;
import message.enums.Language;
import message.types.BotMessage;
import message.types.MainMessage;
import message.types.SearchMessage;
import misc.LogBuilder;
import misc.Utility;
import music.APIManager;
import music.BotAudioPlayer;
import music.PlayerManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.GuildReadyEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import sun.rmi.runtime.Log;

import java.util.*;
import java.util.regex.Pattern;

public class MessageManager {
    private final HashMap<Long, HashMap<Long, BotMessage>> guildMessageMap;
    private Timer timeoutDelete;
    private Properties botChannelProperties;
    private Properties guildLanguageProperties;
    private HashMap<Long, ArrayList<Long>> ignoredMessages;
    private static MessageManager singleton;

    private MessageManager() {
        this.guildMessageMap = new HashMap<>();
        this.ignoredMessages = new HashMap<>();

        botChannelProperties = new Properties();
        guildLanguageProperties = new Properties();
        Properties ignoredMessages = new Properties();

        Utility.loadProperties("botchannel.properties", botChannelProperties);
        Utility.loadProperties("language.properties", guildLanguageProperties);
        Utility.loadProperties("ignoredMessages.properties", ignoredMessages);

        for (Object o: ignoredMessages.keySet()) {
            Long guildID = Long.valueOf((String) o);
            this.ignoredMessages.put(guildID, new ArrayList<Long>());

            String[] messageIDs = ((String)ignoredMessages.get(guildID.toString())).split(Pattern.quote("."));
            for (String mID: messageIDs) {
                if(mID != null && mID.length() > 0) this.ignoredMessages.get(guildID).add(Long.valueOf(mID));
            }
        }

        timeoutDelete = new Timer(true);
        TimerTask delete = new DeleteTimeoutedMessages(guildMessageMap);
        timeoutDelete.scheduleAtFixedRate(delete, 0, 10000);
    }

    public synchronized void onGuildReady(final GuildReadyEvent event) {
        if (guildMessageMap.get(event.getGuild().getIdLong()) != null) return;

        final Guild guild = event.getGuild();
        if (ignoredMessages.get(guild.getIdLong()) == null) ignoredMessages.put(guild.getIdLong(), new ArrayList<Long>());

        String botChannelID = botChannelProperties.getProperty(String.valueOf(guild.getIdLong()), "");
        if (botChannelID.equals("")) return;
        final TextChannel textChannel = guild.getTextChannelById(botChannelID);
        if (textChannel == null) return;

        guildMessageMap.put(guild.getIdLong(), new HashMap<Long, BotMessage>());

        new Thread() {
            @Override
            public void run() {
                try {
                    List<Message> leftMessages = textChannel.getHistory().retrievePast(100).complete();
                    for (Object o : leftMessages.toArray()) {
                        Message m = (Message) o;
                        if(ignoredMessages.get(guild.getIdLong()).contains(m.getIdLong())) leftMessages.remove(o);
                    }

                    while (leftMessages.size() > 0) {
                        if (leftMessages.size() >= 2) textChannel.deleteMessages(leftMessages).complete();
                        else textChannel.deleteMessageById(leftMessages.get(0).getId()).complete();

                        leftMessages = textChannel.getHistory().retrievePast(100).complete();

                        for (Object o : leftMessages.toArray()) {
                            Message m = (Message) o;
                            if(ignoredMessages.get(guild.getIdLong()).contains(m.getIdLong())) leftMessages.remove(o);
                        }
                    }

                    newMainMessage(guild.getIdLong(), event.getJDA());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void newMainMessage(long guildID, JDA bot) {
        Guild guild = bot.getGuildById(guildID);
        String botChannelID = botChannelProperties.getProperty(String.valueOf(guild.getIdLong()), "");
        if (botChannelID.equals("")) return;
        final TextChannel textChannel = guild.getTextChannelById(botChannelID);

        BotMessage message = new MainMessage(textChannel, bot, Language.getLanguageFromString(guildLanguageProperties.getProperty(String.valueOf(guildID), "")));
        guildMessageMap.get(guildID).put(message.MESSAGE_ID, message);
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (BotManager.getInstance().isMusicBot(event.getAuthor().getIdLong())) {
            System.out.println(LogBuilder.Build(event.getJDA(), event.getMessage()) + "Ignored Because Message was send by MusicBot");
            return;
        }
        if (!event.getChannel().getId().equals(botChannelProperties.getProperty(String.valueOf(event.getGuild().getIdLong()), ""))){
            System.out.println(LogBuilder.Build(event.getJDA(), event.getMessage()) + "Ignored because it was not send in a BotChannel");
            return;
        }

        event.getMessage().delete().queue();

        if (!event.getMember().getVoiceState().inVoiceChannel()) {
            Language language = Language.getLanguageFromString(guildLanguageProperties.getProperty(event.getGuild().getId(), ""));
            PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();

            switch (language) {
                case ENGLISCH:
                    channel.sendMessage("You are not in a Voicechannel. Please join a Voicechannel to use a Musicbot").queue();
                    break;
                case GERMAN:
                    channel.sendMessage("Du bist in keinen Sprachkanal. Bitte geh in einen Sprachkanal um einen Musikbot zu nutzen").queue();
                    break;
            }
            System.out.println(LogBuilder.Build(event.getJDA(), event.getMessage()) + "Ignored because User was not in a VoiceChannel");
            return;
        }
        reactToMessage(event);
    }

    public void createMusicSearchMessage(Guild guild, long botID, long userID, String query) {
        JDA bot = BotManager.getInstance().getJDA(botID);
        if (bot == null) return;

        guild = bot.getGuildById(guild.getIdLong());
        if (guild == null) return;

        String botChannelId = botChannelProperties.getProperty(guild.getId(), "");
        if (botChannelId.equals("")) return;

        TextChannel channel = guild.getTextChannelById(botChannelId);
        if (channel == null) return;

        List<SearchResult> results = APIManager.getInstance().youtubeSearch(query, 10);
        Language language = Language.getLanguageFromString(guildLanguageProperties.getProperty(guild.getId(), ""));

        if (results.size() == 0) {
            PrivateChannel pChannel = bot.getUserById(userID).openPrivateChannel().complete();
            switch (language) {
                case ENGLISCH:
                    pChannel.sendMessage("There where no results for the query:\r\n" + query).queue();
                    break;
                case GERMAN:
                    pChannel.sendMessage("Es gab keine Ergebnisse für die Suche:\r\n" + query).queue();
                    break;
            }
            return;
        }

        BotMessage message = new SearchMessage(bot, channel, userID, query, results, language);
        guildMessageMap.get(guild.getIdLong()).put(message.MESSAGE_ID, message);
    }

    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (BotManager.getInstance().isMusicBot(event.getUser().getIdLong())) return;

        HashMap<Long, BotMessage> messageMap = guildMessageMap.get(event.getGuild().getIdLong());
        if (messageMap == null) return;

        BotMessage message = messageMap.get(event.getMessageIdLong());
        if (message == null) return;

        message.onMessageReaction(event);
    }

    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        HashMap<Long, BotMessage> messageMap = guildMessageMap.get(event.getGuild().getIdLong());
        if (messageMap == null) return;

        BotMessage message = messageMap.get(event.getMessageIdLong());
        if (message == null) return;

        messageMap.remove(message.MESSAGE_ID);
        message.onMessageDeleted();
    }

    private void reactToMessage(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().charAt(0) != '.') {
            PlayerManager manager = PlayerManager.getInstance();
            VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            if(channel == null) {
                System.out.println(LogBuilder.Build(event.getJDA(), event.getMessage()) + "Ignored because Bot couldn't get VoiceChannel");
                return;
            }
            BotAudioPlayer player = manager.getAudioPlayer(channel);
            JDA bot;
            if (player == null) bot = manager.connectToVoiceChannel(event.getMember().getVoiceState().getChannel());
            else bot = player.BOT;

            if(bot == null) {
                System.err.println(LogBuilder.Build(event.getJDA(), event.getMessage()) + "No Bot in VoiceChannel");
                return;
            }

            System.out.println(LogBuilder.Build(event.getJDA(), event.getMessage()) + "Initiate Song Add Process");
            manager.addTrackToPlayer(event.getGuild(), bot.getSelfUser().getIdLong(), event.getAuthor().getIdLong(), event.getMessage().getContentRaw());
            return;
        }
        Commands command;
        String[] messageSplit = event.getMessage().getContentRaw().split(" ");
        messageSplit[0] = messageSplit[0].toUpperCase();
        try {
            command = Commands.valueOf(messageSplit[0].substring(1));
        } catch (Exception e) {
            System.out.println(LogBuilder.Build(event.getJDA(), event.getMessage()) + "Ignored because Message is no Command");
            return;
        }

        switch (command) {
            case JUMP:
                try {
                    long numb = Long.parseLong(messageSplit[1]) * 1000;
                    PlayerManager.getInstance().getAudioPlayer(event.getMember().getVoiceState().getChannel()).jump(numb);
                    System.out.println(LogBuilder.Build(event.getJDA(), event.getMessage()) + "Jumped");
                } catch (Exception e) {
                    System.out.println(LogBuilder.Build(event.getJDA(), event.getMessage()) + "Ignored because JUMP failed");
                }
                break;
            case FORWARD:
                try {
                    long numb = Long.parseLong(messageSplit[1]) * 1000;
                    PlayerManager.getInstance().getAudioPlayer(event.getMember().getVoiceState().getChannel()).forward(numb);
                    System.out.println(LogBuilder.Build(event.getJDA(), event.getMessage()) + "Forwarded");
                } catch (Exception e) {
                    System.out.println(LogBuilder.Build(event.getJDA(), event.getMessage()) + "Ignored because FORWARD failed");
                }
                break;
        }
    }

    public void deleteUserMessage(long guildID, long userID) {
        HashMap<Long, BotMessage> messageMap = guildMessageMap.get(guildID);

        for (Object o : messageMap.values().toArray()) {
            BotMessage message = (BotMessage) o;
            if (!(message instanceof SearchMessage)) continue;
            SearchMessage searchMessage = (SearchMessage) message;

            if (searchMessage.userID != userID) continue;

            message.deleteMessage();
        }
    }

    public long getTextChannelID(long guildID){
        return Long.valueOf(botChannelProperties.getProperty(String.valueOf(guildID), "0"));
    }

    public static MessageManager getInstance() {
        if (singleton != null) return singleton;

        return createSingleton();
    }

    private static synchronized MessageManager createSingleton(){
        if(singleton == null) singleton = new MessageManager();
        return singleton;
    }
}
