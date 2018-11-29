package message;

import message.types.BotMessage;
import java.util.HashMap;
import java.util.TimerTask;

public class DeleteTimeoutedMessages extends TimerTask {
    private final HashMap<Long, HashMap<Long, BotMessage>> MESSAGEMAP;

    public DeleteTimeoutedMessages(HashMap<Long, HashMap<Long, BotMessage>> messageMap){
        this.MESSAGEMAP = messageMap;
    }

    public void run() {
        try {
            task();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void task(){
        long currentTime = System.currentTimeMillis();

        for (Object obj: MESSAGEMAP.values().toArray()) {
            HashMap<Long, BotMessage> messageMap = (HashMap<Long, BotMessage>) obj;
            for (Object obj2: messageMap.values().toArray()){
                BotMessage message = (BotMessage) obj2;
                if(message.TIMEOUT > currentTime) continue;

                message.deleteMessage();
                MESSAGEMAP.get(message.GUILD_ID).remove(message.MESSAGE_ID);
            }
        }
    }
}
