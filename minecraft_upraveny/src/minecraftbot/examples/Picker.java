/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.examples;

import java.io.IOException;
import minecraftbot.messaging.Message;
import minecraftbot.MinecraftBotHandler;
import minecraftbot.entity.Stack;

/**
 * Tries to collect items fallen on the ground.
 * Fails miserably (?) whenever he tries to locate item, that was thrown while he was in game.
 * @author eZ
 */
public class Picker extends MinecraftBotHandler{

    static private String myName = "Cleaner";
    private static final String address = "localhost";
    private static final int port = 1241;
    
    Stack targetItem;
    
    
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
        if(collecting.isPickableNearby(100))
        {
            if(targetItem==null)
            {
                targetItem = collecting.getNearestStack();
                System.out.println(targetItem.getLocation());
                navigation.navigate(targetItem.getLocation());
            }
            else if(!navigation.isNavigating())
            {
                targetItem = null;
            }
        }
        else if(navigation.isNavigating())
        {
            navigation.stop();
        }
    }
    
    
     /**
     * called when chat message is received
     * @param msg incoming message
     * @throws java.io.IOException
     */
    @Override
    protected void readMessage(String sender, String text){
    }
    
    
    public static void main(String[] args) throws Exception {
        Picker bot = new Picker();
        bot.startBot(myName, address, port);
    }

}
