

package minecraftbot.examples;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import minecraftbot.MinecraftBotHandler;
import minecraftbot.Util;
import minecraftbot.build.Structure;
import minecraftbot.entity.Player;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;

/**
 * Builds structures saved in directory "structures"
 * Bot can be controlled using ingame chat.
 */
public class Builder extends MinecraftBotHandler{

    static String address = "localhost";
    static int port = 55940;
    
    /**
     * Map containing all loaded structures and their names.
     */
    Map<String, Structure> structureMap;
    
    @Override
    protected void initialize() {
        chat.sendMessage("Hello! I am Builder! Type 'bl help' for help");
        structureMap = new HashMap<>();
        //directory with structure files
        File directory = new File("structures");
        //if directory does not exists we can create it
        if(!directory.exists())
            directory.mkdir();
        
        //loads all structure files from directory
        File[] files = directory.listFiles();
        for (File file : files) {
            try
            {
                Structure structure = Structure.fromFile(file.getPath());
                structureMap.put(file.getName(), structure);
            }
            catch(Exception ex)
            {
                Util.logger.log(LogElement.Custom, LogLevel.Error, ex.getMessage());
            }
        }
        chat.sendMessage("Loaded "+structureMap.size()+" structures!");
    }

    @Override
    protected void readMessage(String sender, String text) {
        
        //bot ignores any messages that arent commands for him
        if(!text.startsWith("bl"))
            return;
        
        //bot tries to recognize player, that sent the message
        Player player = players.getPlayerNamed(sender);
        if(player == null)
        {
            //if bot doesnt know about the sender, we ignore the message
            chat.sendMessage("I don't know you, sorry!");
            return;
        }
       
        //structure that will be built
        Structure toBuild = null;
        
        //parsing build command
        if(text.startsWith("bl build"))
        {
            String[] msg = text.split(" ");

            
            String structureName;
            try
            {
                structureName = msg[2];
            }
            catch(IndexOutOfBoundsException ex)
            {
                chat.sendMessage("error while parsing message");
                return;
            }
            if(!structureMap.containsKey(structureName))
            {
                chat.sendMessage("unknown structure, use 'bl structures' to list loaded structures");
                return;
            }
            if(msg.length>4)
            {
                chat.sendMessage("error while parsing message");
                return;
            }
            toBuild = structureMap.get(structureName);
            if(msg.length==4)
            {
                switch(msg[3])
                {
                    case "flipx":
                        toBuild=toBuild.getFlippedX();
                        break;
                    case "flipz":
                        toBuild=toBuild.getFlippedZ();
                        break;
                    case "flipxz":
                        toBuild=toBuild.getFlippedXZ();
                        break;
                    case "rotatecw":
                        toBuild=toBuild.getRotatedClockwise();
                        break;
                    case "rotateccw":
                        toBuild=toBuild.getRotatedCounterClockwise();
                        break;
                    default:
                        chat.sendMessage("unkown modifier "+msg[3]+ "use 'bl modifiers' to list available modifiers");
                        return;
                            
                }
            }
            text = "bl build";
        }
        
        //now we can simply handle the command
        
        switch (text) {
            
            case "bl build":
                build2.buildStructure(toBuild, player.getLocation());
                break;
            case "bl structures":
                String msg = "loaded structures: ";
                boolean first = true;
                for (Map.Entry<String, Structure> entry : structureMap.entrySet()) {
                    String name = entry.getKey();
                    if(!first)
                        msg += ", ";
                    msg+=name;
                    first = false;
                }
                chat.sendMessage(msg);
                break;
            case "bl modifiers":
                chat.sendMessage("modifiers: <none> flipx, flipz, flipxz, rotatecw, rotateccw");
                break;
            case "bl respawn":
                requestRespawn();
                break;
            case "bl help":
                chat.sendMessage("Available commands: bl build <structure> <modifier>");
                chat.sendMessage("bl structures");
                chat.sendMessage("bl modifiers");
                chat.sendMessage("bl help <command>");
                chat.sendMessage("bl respawn");
                break;
            case "bl help respawn":
                chat.sendMessage("bl respawn - respawns bot");
                break;
            case "bl help help":
                chat.sendMessage("bl help - shows help");
                break;
            case "bl help modifiers":
                chat.sendMessage("bl modifiers - lists all supported modifiers");
                break;
            case "bl help structures":
                chat.sendMessage("bl structures - lists all correctly loaded structures");
                break;
            default:
                chat.sendMessage("Unknown command "+text);
                break;

        }
    }
    
    public static void main(String[] args) throws Exception {
        Builder bot = new Builder();
        bot.start("Builder", address, port);
    }
}
