
package minecraftbot.examples;

import minecraftbot.Id;
import minecraftbot.MinecraftBotHandler;
import minecraftbot.entity.Player;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import minecraftbot.world.Block;

/**
 * Bot that reacts to chat various chat messages:
 w, s, a, d - simple movement
 navigate - navigate to player
 navigateto X Y Z - navigates to location X Y Z
 j - jump
 jX (X = w, s, a, d) - jump to side
 door X Y Z - uses door at X Y Z
 hello - greet player, tell him his player location
 info - say own location
 mine - mine underneath itself
 whatis X Y Z - say information about block on X Y Z
 howmany item-id - says how many items of certain kind has in inventory
 */
public class ResponsiveBot extends MinecraftBotHandler{

    private static final String myName = "MyBot";
    private static final String address = "localhost";
    private static final int port = 59699;
    
    
 
    @Override
    protected void initialize() {
        chat.sendMessage("hello world!!");
    }

    @Override
    protected void readMessage(String sender, String text){
        super.readMessage(sender, text);
        Player target;
        if(text.startsWith("j"))
        {
            move.jump();
            text = text.substring(1);
        }
        switch(text)
        {
            case "drop":
                for(Id id : inventory.allIds())
                {
                    inventory.drop(id);
                }
                break;
            case "look":
                target = players.getPlayerNamed(sender);
                if(target==null)
                {
                    chat.sendMessage("I dont know you");
                    break;
                }
                move.lookAt(target.getLocation());
                break;
            case "hello":
                target = players.getPlayerNamed(sender);
                if(target==null)
                {
                    chat.sendMessage("I dont know you");
                    break;
                }
                move.lookAt(target.getLocation());
                chat.sendMessage("Hello "+ sender+", you are at "+target.getLocation().toString());
                break;
            case "w":
                move.moveForward();
                break;
            case "a":
                move.moveLeft();
                break;
            case "s":
                move.moveBack();
                break;
            case "d":
                move.moveRight();
                break;
            case "respawn":
                requestRespawn();
                break;
            case "info":
                chat.sendMessage("I'm @ "+move.getLocation());
                break;
            case "mine":
                mining.mine(move.getLocation().addY(-1));
                break;
            case "navigate":
                target = players.getPlayerNamed(sender);
                if(target==null)
                {
                    chat.sendMessage("I dont know you");
                    break;
                }
                navigation.navigate(target.getLocation());
                break;
        }
        if(text.startsWith("turnby "))
        {
            move.turnHorizontal(Integer.parseInt(text.substring(2)));
        }
        if(text.startsWith("whatis "))
        {
            String[] s = text.split(" ");
            try
            {
                Block b = world.getBlock(Integer.parseInt(s[1]),
                                                Integer.parseInt(s[2]),
                                                Integer.parseInt(s[3]));
            chat.sendMessage("Block info - ID: "+b.getId()+ "  Meta: "+b.getMeta()+" Type: "+b.getId().getType());
            }
            catch(Exception e)
            {
                chat.sendMessage("error while parsing");
            }
        }
        if(text.startsWith("door "))
        {
            String[] s = text.split(" ");
            try
            {
                Location loc = new Location(Integer.parseInt(s[1]),
                                                Integer.parseInt(s[2]),
                                                Integer.parseInt(s[3]));
                move.openDoor(loc);
            }
            catch(Exception e)
            {
                chat.sendMessage("error while parsing");
            }
        }
        if(text.startsWith("navigateto "))
        {
            String[] s = text.split(" ");
            try
            {
                Location loc = new Location(Integer.parseInt(s[1]),
                                                Integer.parseInt(s[2]),
                                                Integer.parseInt(s[3]));
                navigation.navigate(loc);
            }
            catch(Exception e)
            {
                chat.sendMessage("error while parsing");
            }
        }
        if(text.startsWith("howmany "))
        {
            String[] s = text.split(" ");
            try
            {
                Id id = Id.valueOf(s[1]);
                chat.sendMessage(inventory.amountOf(id));
            }
            catch(Exception e)
            {
                chat.sendMessage("error while parsing");
            }
        }
    }
    
    public static void main(String[] args)  {
        ResponsiveBot bot = new ResponsiveBot();
        bot.start(myName, address, port);
    }

}
