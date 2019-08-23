package restactions;

import bot.BotManager;
import message.enums.Emoji;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;

import java.util.Arrays;
import java.util.Vector;

public class RestActionManager {
    public final long GUILD_ID;
    private final Vector<RestAction> restActionQueue;
    private final Vector<JDA> botQueue;
    private boolean running;
    private RestActionWorker worker;

    public RestActionManager(long guildID){
        this.GUILD_ID = guildID;
        this.restActionQueue = new Vector<>();
        this.botQueue = new Vector<>();
        this.running = false;
        this.worker = new RestActionWorker();

        BotManager manager = BotManager.getInstance();
        JDA[] bots = manager.getBots(GUILD_ID);
        botQueue.addAll(Arrays.asList(bots));
    }

    public synchronized void addEmoteRestAction(RestAction action){
        restActionQueue.add(action);
        if(running) return;

        running = true;
        this.worker = new RestActionWorker();
        this.worker.start();
    }

    private synchronized boolean workOnFirstElementInQueue(){
        if(restActionQueue.size() <= 0) {
            running = false;
            return false;
        }

        RestAction action = restActionQueue.remove(0);
        JDA bot = botQueue.remove(0);
        botQueue.add(bot);

        try {
            action.complete(bot);
        }catch (Exception e){
            System.err.println("Couldn't complete RestAction");
        }

        if(restActionQueue.size() <= 0){
            running = false;
            return false;
        }
        running = true;
        return true;
    }

    public synchronized void removeMessageRelatedRestActions(long messageID){
        RestAction[] actions = new RestAction[restActionQueue.size()];
        actions = restActionQueue.toArray(actions);

        for(RestAction r: actions){
            if(r.MESSAGE_ID == messageID){
                restActionQueue.remove(r);
            }
        }
    }

    public synchronized void removeUserEmoteRelatedRestActions(long messageID, long userID, Emote emote){
        RestAction[] actions = new RestAction[restActionQueue.size()];
        actions = restActionQueue.toArray(actions);

        for(RestAction r: actions) {
            if(r.MESSAGE_ID != messageID) continue;
            if(!(r instanceof EmoteRemoveRestAction)) continue;
            EmoteRemoveRestAction action = (EmoteRemoveRestAction) r;

            if(action.USER_ID != userID) continue;
            if(action.EMOTE.getIdLong() != emote.getIdLong()) continue;

            restActionQueue.remove(r);
        }
    }

    public synchronized void removeUserEmoteRelatedRestActions(long messageID, long userID, String emoteUnicode){
        RestAction[] actions = new RestAction[restActionQueue.size()];
        actions = restActionQueue.toArray(actions);

        for(RestAction r: actions) {
            if(r.MESSAGE_ID != messageID) continue;
            if(!(r instanceof EmoteRemoveRestAction)) continue;
            EmoteRemoveRestAction action = (EmoteRemoveRestAction) r;

            if(action.USER_ID != userID) continue;
            if(!action.EMOTE_UNICODE.equals(emoteUnicode)) continue;

            restActionQueue.remove(r);
        }
    }

    private class RestActionWorker extends Thread{
        @Override
        public void run() {
            while (workOnFirstElementInQueue()){}
        }
    }
}
