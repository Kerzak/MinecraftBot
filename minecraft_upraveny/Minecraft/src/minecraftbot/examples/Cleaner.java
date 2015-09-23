
package minecraftbot.examples;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import minecraftbot.Id;
import minecraftbot.MinecraftBotHandler;
import minecraftbot.entity.Player;
import minecraftbot.entity.Stack;

/**
 * Bot that follows player around and picks
 * any nearby stacks.
 * Bot can be controlled using ingame chat.
 */
public class Cleaner extends MinecraftBotHandler{

    static String address = "localhost";
    static int port = 55219;
    
    /**
     * Stack that bot is currently trying to pickup
     */
    Stack targetStack;
    /**
     * Player that bot is currently following.
     */
    Player targetPlayer;
    /**
     * How many times bot tried to pick up current target stack.
     */
    int numOfTries = 0;
    
    @Override
    protected void initialize() {
        chat.sendMessage("Hello! I am Cleaner! Type 'cl help' for help");
    }

    @Override
    protected void logic() {
        //if bot doesn't have target player there's nothing to do
        if(targetPlayer==null)
            return;
        
        //if there is something to pick up
        if(collecting.isPickableNearby(10))
        {
            //and bot didnt try to pick up anything yet
            if(targetStack==null)
            {
                //bot tries to pick up nearest stack
                numOfTries = 1;
                targetStack = collecting.getNearestStack();
                navigation.navigate(targetStack.getLocation());
            }
            //if bot is currently trying to pickup a stack
            //but isnt navigating, that means it couldnt reach it
            else if(!navigation.isNavigating())
            {
                //so if it hasn't tried enough yet, he tries again
                if(numOfTries<10)
                {
                    numOfTries++;
                    navigation.navigate(targetStack.getLocation());
                }
                //if stack seems unpickable bot ignores it
                else
                {
                    collecting.ignoreStack(targetStack);
                    //also resets counter of tries
                    numOfTries = 0;
                }
            }
        }
        //if there is nothing to pick up, bot follows player
        else if(!navigation.isNavigating()
                && Location.getDistance(move.getLocation(), targetPlayer.getLocation())>6)
        {
            navigation.navigate(targetPlayer);
        }
    }

    @Override
    public void onItemPickUp(Id id, int count) {
        //if picked up something it assumes it was its target stack
        //it cancels current action so it can try to find another stacks 
        targetStack = null;
        navigation.stop();
        numOfTries = 0;
        //if picked item wasnt target stack, bot will just try to pick it up again later
    }
    
    @Override
    protected void readMessage(String sender, String text) {
        
        //bot ignores any messages that arent commands for him
        if(!text.startsWith("cl"))
            return;
        
        //bot tries to recognize player, that sent the message
        Player player = players.getPlayerNamed(sender);
        if(player == null)
        {
            //if bot doesnt know about the sender, we ignore the message
            chat.sendMessage("I don't know you, sorry!");
            return;
        }
        
        //if bot already has a target player, it won't listen to anyone else
        if(targetPlayer!=null && targetPlayer!=player)
        {
            chat.sendMessage("You're not boss of me!");
            return;
        }

        
        //now we can simply handle the command
        
        switch (text) {
            case "cl followme":
                targetPlayer = player;
                break;
            case "cl respawn":
                requestRespawn();
                break;
            case "cl stop":
                navigation.stop();
                targetPlayer = null;
                targetStack = null;
                break;
            case "cl drop":
                targetPlayer = null;
                move.lookAt(player);
                for(Id id : inventory.allIds())
                {
                    inventory.drop(id);
                }
                break;
            case "cl help":
                chat.sendMessage("Available commands: cl followme");
                chat.sendMessage("cl drop");
                chat.sendMessage("cl help <command>");
                chat.sendMessage("cl respawn");
                chat.sendMessage("cl stop");
                break;
            case "cl help respawn":
                chat.sendMessage("cl respawn - respawns bot");
                break;
            case "cl help stop":
                chat.sendMessage("cl stop - cancels following");
                break;
            case "cl help help":
                chat.sendMessage("cl help - shows help");
                break;
            case "cl help drop":
                chat.sendMessage("cl drop - drops all items, cancels following");
                break;
            case "cl help followme":
                chat.sendMessage("cl drop - bot will follow player and pick nearby stacks untill stopped");
                break;
            default:
                chat.sendMessage("Unknown command "+text);
                break;

        }
    }
    
    public static void main(String[] args) throws Exception {
        Cleaner bot = new Cleaner();
        bot.start("Cleaner", address, port);
    }
}
