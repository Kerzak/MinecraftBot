
package minecraftbot.examples;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import minecraftbot.Id;
import minecraftbot.MinecraftBotHandler;
import minecraftbot.Util;
import minecraftbot.entity.Player;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;

/**
 * This bot is used for acquiring wood for player.
 * Bot can be controlled using ingame chat.
 */
public class Lumberjack extends MinecraftBotHandler{

    static String address = "localhost";
    static int port = 55940;
    
    /**
     * Current state of collecting wood.
     */
    String state = "stop";
    /**
     * Previous state of collecting wood.
     */
    String prevState = "stop";
    /**
     * Location of tree, that bot is trying to cut.
     */
    Location currentTarget;
    /**
     * Amount of wood bot is trying to collect.
     */
    int goal;
    
    @Override
    protected void initialize() {
        chat.sendMessage("Hello! I am Lumberjack! Type 'lj help' for help");
    }

    @Override
    protected void logic() {
        //for collecting wood bot uses diffent states to determine his actions
        //all other tasks are simple
        
        //logs changes in states
        if(!state.equals(prevState))
            Util.logger.log(LogElement.Custom, LogLevel.Info, "New state: " + state);
        
        //saves current state
        prevState = state;
        
        switch (state) {
            //bot is trying to find nearby tree or pickable stack
            case "find":
                //locates closest one
                currentTarget = world.getClosestResource(Id.Log,move.getLocation());
                
                //if goal is completed, bot ends the task
                if(inventory.amountOf(Id.Wood)>=goal)
                {
                    state="stop";
                    chat.sendMessage("Task finished!");
                }
                //if bot cannot find any wood
                else if (currentTarget == null) {
                    //tries to find any stacks of wood on the ground
                    if(collecting.isPickableNearby(8))
                    {
                        navigation.navigate(collecting.getNearestStack().getLocation());
                        state = "collect";
                        break;
                    }
                    //if there are no stacks, bot cancels the task
                    state = "stop";
                    chat.sendMessage("There's not enough wood in the world for me! :(");
                }
                //if nearest tree is too far away while a stack is nearby
                else if(Location.getDistance(currentTarget, move.getLocation()) > 10
                        && collecting.isPickableNearby(6))
                {
                    //bot tries to collect that stack
                    state="collect";
                    navigation.navigate(collecting.getNearestStack().getLocation());
                }
                //if bot found tree and there are no pickable stacks nearby
                else
                {
                    //it simply navigates to that tree
                    state = "navigate";
                    navigation.navigateToTree(currentTarget);
                }
                break;
            //bot is navigating to a tree
            case "navigate":
                //if navigation is not finished yet, bot waits
                
                //if navigation has finished ..
                if (!navigation.isNavigating()) {
                    //..and was successful
                    if(navigation.wasSuccessful())
                    {
                        //then it tries to cut that tree
                        state = "cut";
                        mining.mine(currentTarget);
                    }
                    //..and was unsuccessful, bot ignores the tree
                    //and tries to find a new one
                    else
                    {
                        world.ignoreResourceLocation(currentTarget);
                        state = "find";
                    }
                }
                break;
            //bot is cutting a tree
            case "cut":
                //if cutting is finished, bot tries to find another tree
                if (!mining.isMining()) {
                    chat.sendMessage("Where's another one?! Need "+(goal-inventory.amountOf(Id.Wood))+" more!");
                    state = "find";
                }
                break;
            //bot is trying to collect a stack
            case "collect":
                //if there is a stack to collect
                if(collecting.isPickableNearby(30))
                {
                    //bot navigates to it
                    if(!navigation.isNavigating())
                        navigation.navigate(collecting.getNearestStack().getLocation());
                }
                //if there arent any stacks to collect, bot tries to find a tree to cut
                else
                {
                    state = "find";
                }
                break;

        }
    }

    @Override
    protected void readMessage(String sender, String text) {
        
        //bot ignores any messages that arent commands for him
        if(!text.startsWith("lj"))
            return;
        
        //bot tries to recognize player, that sent the message
        Player player = players.getPlayerNamed(sender);
        if(player == null)
        {
            //if bot doesnt know about the sender, we ignore the message
            chat.sendMessage("I don't know you, sorry!");
            return;
        }
        
        //if bot received command to collect wood, we try to parse the goal
        if(text.startsWith("lj getwood"))
        {
            if(text.length()==10)
                //if message doesnt contain the goal, we use default value
                goal = 12;
            else
            {
                String[] s = text.split(" ");
                try
                {
                    goal = Integer.parseInt(s[2]);
                }
                catch(NumberFormatException | ArrayIndexOutOfBoundsException e)
                {
                    chat.sendMessage("Error while parsing number.");
                }
                text = "lj getwood";
            }
        }
        
        //now we can simply handle the command
        
        switch (text) {
            case "lj getwood":
                state = "find";
                break;
            case "lj givewood":
                move.lookAt(player);
                inventory.drop(Id.Wood);
                break;
            case "lj giveall":
                move.lookAt(player);
                for(Id id : inventory.allIds())
                {
                    inventory.drop(id);
                }
                break;
            case "lj stop":
                navigation.stop();
                state = "stop";
                break;
            case "lj comehere":
                navigation.navigate(player);
                break;
            case "lj respawn":
                requestRespawn();
                break;
            case "lj help":
                chat.sendMessage("Available commands: lj getwood");
                chat.sendMessage("lj getwood <number>");
                chat.sendMessage("lj giveall");
                chat.sendMessage("lj givewood");
                chat.sendMessage("lj help");
                chat.sendMessage("lj help <command>");
                chat.sendMessage("lj respawn");
                chat.sendMessage("lj stop");
                chat.sendMessage("lj comehere");
                break;
            case "lj help respawn":
                chat.sendMessage("lj respawn - respawns bot");
                break;
            case "lj help getwood":
                chat.sendMessage("lj getwood X - tries to collect X wood, default value of X is 12");
                break;
            case "lj help giveall":
                chat.sendMessage("lj giveall - drops everything from inventory, cancels current task");
                break;
            case "lj help givewood":
                chat.sendMessage("lj givewood - drops wood from inventory, cancels current task");
                break;
            case "lj help help":
                chat.sendMessage("lj help - shows help");
                break;
            case "lj help stop":
                chat.sendMessage("lj stop - stops bot, cancels current task");
                break;
            case "lj help comehere":
                chat.sendMessage("lj comehere - cancels current task, navigates bot to player");
                break;
            default:
                chat.sendMessage("Unknown command "+text);
                break;
        }
    }

    public static void main(String[] args)  {
        Lumberjack bot = new Lumberjack();
        bot.start("Lumberjack", address, port);
    }
    
}
