/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.examples;

import java.io.IOException;
import minecraftbot.messaging.Message;
import minecraftbot.MinecraftBotHandler;

public class EmptyBot extends MinecraftBotHandler{

    static private String myName = "EmptyBot";
    private static final String address = "localhost";
    private static final int port = 25565;
    
    
    int logicStep = 0;
    
    /**
     * method for bots initialization
     * called after receiving information about bot location for first time
     */
    @Override
    protected void initialize() {
    }
    
    /**
     * main method for controlling the bot
     * called once every 250ms
     */
    @Override
    protected void logic() {
    }
    

    
    
    public static void main(String[] args) throws Exception {
        EmptyBot bot = new EmptyBot();
        bot.startBot(myName, address, port);
    }

}
