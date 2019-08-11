import bot.BotManager;
import message.MessageManager;

public class App {
    public static void main(String[] args){
        BotManager.getInstance();
        MessageManager.getInstance();
    }
}
