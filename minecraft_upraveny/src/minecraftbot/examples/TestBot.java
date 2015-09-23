/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.examples;

import java.io.IOException;
import minecraftbot.messaging.Message;
import minecraftbot.MinecraftBotHandler;

public class TestBot extends MinecraftBotHandler{

    static private String myName = "Tester";
    private static final String address = "localhost";
    private static final int port = 49370;
    
    
    int logicStep = 0;
    
    /**
     * method for bots initialization
     * called after receiving information about bot location for first time
     */
    @Override
    protected void initialize() {
        chat.sendMessage("HELLO");
   }
    
    /**
     * main method for controlling the bot
     * called once every 250ms
     */
    @Override
    protected void logic() {
    }
    
    
     /**
     * called when chat message is received
     * @param msg incoming message
     * @throws java.io.IOException
     */

    @Override
    protected void readMessage(String sender, String text){
        {
            dig.dig(1, 1, 1);
            //chat.sendMessage(inventory.amountOf(ItemType.Axe)+"");
            /* item picking
            if(collecting.isPickableNearby(30))
            {
                chat.sendMessage(collecting.getNearestStack().getLocation().toString());
            }
            else{
                chat.sendMessage("NO ITEMS AROUND");
            }*/
        }
    }
    
    
    public static void main(String[] args) throws Exception {
        TestBot bot = new TestBot();
        bot.startBot(myName, address, port);
    }

}
