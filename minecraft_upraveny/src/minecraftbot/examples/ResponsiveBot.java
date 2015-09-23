/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.examples;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.IOException;
import minecraftbot.messaging.Message;
import minecraftbot.Id;
import minecraftbot.MinecraftBotHandler;
import minecraftbot.Util;
import minecraftbot.build.Structure;
import minecraftbot.entity.Player;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.world.Block;

/**
 * Bot that reacts to chat messages:
 * w, s, a, d - simple movement
 * n - navigate to player
 * j - jump
 * jX (X = w, s, a, d) - jump to side
 * t X - X is integer, turn by X degrees
 * h - say player location
 * i - say own location
 * k - dig underneath himself
 * c - teleport to player, only possible if player is within los
 * f - toggle gravity (only for bot)
 * b X Y Z - say information about block on X Y Z
 * @author eZ
 */
public class ResponsiveBot extends MinecraftBotHandler{

    static private String myName = "MyBot";
    static private String address = "localhost";
static private int port = 25565;
    
    Structure dom;
    
    int logicStep = 0;
    
    /**
     * method for bots initialization
     * called after  receiving information about bot location for first time
     */
    @Override
    protected void initialize() {
//        Util.logger.elements.put(LogElement.Chat,LogLevel.Trace);
        chat.sendMessage("hello world!!");
        dom = new Structure();
        dom.platform(Id.WOOD,0,5,4,0,5);
        dom.wall(Id.WOOD, 0,0,0,0,3,5);
        dom.wall(Id.WOOD, 0,0,0,5,3,0);
        dom.wall(Id.WOOD, 5,0,5,0,3,5);
        dom.wall(Id.WOOD, 5,0,5,5,3,0);
    }
    
    /**
     * main method for controlling the bot
     * called once every 250ms
     */
    @Override
    protected void logic() {
        if(!players.getPlayers().isEmpty())
        {
          //  move.setLocation(players.getPlayerNamed("asdf").getLocation());
        }
        /*
        move.moveForward();
        if(logicStep==3)
        {
            move.turnHorizontal(90);
            logicStep = 0;
        }
        logicStep++;
        //*/
    }
    
    
     /**
     * called when chat message is received
     * @param msg incoming message
     * @throws java.io.IOException
     */
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
            case "l":
                target = players.getPlayerNamed(sender);
                if(target==null)
                {
                    chat.sendMessage("I dont know you");
                    break;
                }
                move.lookAt(target.getLocation());
                 break;
            case "h":
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
            case "f":
                move.toggleFloating();
                chat.sendMessage("Gravity: :"+!move.isFloating());
                break;
            case "i":
                chat.sendMessage("I'm @ "+move.getLocation());
                break;
            case "k":
                dig.dig(new Location(move.getLocation().addY(-1)));
                break;
            case "o":
                build2.buildStructure(dom, move.getLocation().addX(-2).addZ(-2));
                break;
            case "n":
                target = players.getPlayerNamed(sender);
                if(target==null)
                {
                    chat.sendMessage("I dont know you");
                    break;
                }
                navigation.navigate(target.getLocation());
                break;
            case "c":
                target = players.getPlayerNamed(sender);
                if(target==null)
                {
                    chat.sendMessage("I dont know you");
                    break;
                }
                move.setLocation(target.getLocation());
                break;
        }
        if(text.startsWith("t "))
        {
            move.turnHorizontal(Integer.parseInt(text.substring(2)));
        }
        if(text.startsWith("b "))
        {
            String[] s = text.split(" ");
            try
            {
                Block b = world.getBlock(Integer.parseInt(s[1]),
                                                Integer.parseInt(s[2]),
                                                Integer.parseInt(s[3]));
            chat.sendMessage("ID: "+b.getId()+ "  Meta: "+b.getMeta());
            }
            catch(Exception e)
            {
                chat.sendMessage("error while parsing");
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        ResponsiveBot bot = new ResponsiveBot();
        bot.startBot(myName, address, port);
    }

}
