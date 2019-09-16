package de.autumnal.discordmusicbotnetwork;

import de.autumnal.discordmusicbotnetwork.bot.BotManager;
import de.autumnal.discordmusicbotnetwork.message.MessageManager;

import java.awt.*;
import java.io.*;

public class App {
    public static void main(String[] args){
        //Console console = System.console();
        //if(console == null && !GraphicsEnvironment.isHeadless()){
        //    try {
        //        String filename = new java.io.File(App.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
        //        Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar \"" + filename + "\""});
        //    } catch (IOException e) {
        //        PrintWriter writer = null;
        //        try {
        //            writer = new PrintWriter("startError.txt", "UTF-8");
        //        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
        //            ex.printStackTrace();
        //        }
        //        writer.print(e.getMessage());
        //        writer.close();
        //    } finally {
        //        System.exit(0);
        //    }
        //}

        BotManager.getInstance();
        MessageManager.getInstance();
    }
}