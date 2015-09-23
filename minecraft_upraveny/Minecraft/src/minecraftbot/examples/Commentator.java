

package minecraftbot.examples;

import minecraftbot.Id;
import minecraftbot.MinecraftBotHandler;

/**
 * Bot that reacts to various events.
 */
public class Commentator extends MinecraftBotHandler{

    @Override
    protected void initialize() {
        chat.sendMessage("Hello! I am Commentator!");
    }
    
    @Override
    public void onItemPickUp(Id id, int count) {
        chat.sendMessage("Thank you for "+count+" of "+id+"!");
    }

    @Override
    protected void onPlayerConnect(String player) {
        chat.sendMessage("Hello Mr. "+player+", welcome to Minecraft!");
    }

    @Override
    protected void onPlayerKilled(String player, String attacker) {
        chat.sendMessage("Rest in piece "+player);
    }

    @Override
    protected void onPlayerDisconnect(String player) {
        chat.sendMessage("Farewell "+player+"!");
    }

    @Override
    protected void onDamageTaken(float amount) {
        chat.sendMessage("Ouch! Please don't hurt me!");
    }

    
    
    
    
 
    
}
