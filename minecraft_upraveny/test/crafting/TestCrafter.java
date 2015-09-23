/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crafting;

import minecraftbot.Id;
import minecraftbot.MinecraftBotHandler;
import minecraftbot.Util;
import minecraftbot.build.Structure;
import minecraftbot.entity.Player;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Kerzak
 */
public class TestCrafter extends MinecraftBotHandler{

    static private String myName = "MyBot";
    static private String address = "localhost";
    static private int port = 28557;

    public boolean isRunning = false;
    
    Structure dom;
   
    @Before
    public void beforeTest() {
        this.startBot(myName, address, port);
    }
    
    @Test
    public void test1() {
        System.out.println("test1");
        
    }
    
    @Override
    protected void initialize() {
        
        chat.sendMessage("hello world!!");
        dom = new Structure();
        dom.platform(Id.WOOD,0,5,4,0,5);
        dom.wall(Id.WOOD, 0,0,0,0,3,5);
        dom.wall(Id.WOOD, 0,0,0,5,3,0);
        dom.wall(Id.WOOD, 5,0,5,0,3,5);
        dom.wall(Id.WOOD, 5,0,5,5,3,0);
        
        Util.logger.add(LogElement.PacketId, LogLevel.Trace);
    }
    @Override
    protected void logic() {
    }

    @Override
     protected void readMessage(String sender, String text){
        super.readMessage(sender, text);
        Player target;
        switch (text) {
            case "n":
                target = players.getPlayerNamed(sender);
                if(target==null) {
                    chat.sendMessage("I dont know you");
                    break;
                }
                navigation.navigate(target.getLocation());
                break;
            case "c":
                
               inventoryManager.requestHeldChange(Id.COBBLESTONE);
                
                break;
            case "w": {
                
                inventoryManager.requestHeldChange(Id.LOG);
                
            } break;
            case "start crafting": {
                crafting.initializeCrafting(move, build, chat);
            } break;
            case "finish crafting": {
                crafting.finishCrafting(navigation,dig);
            } break;
            
            case "show inventory" : {
                crafting.showInventory(chat);
            } break;
            case "chest": {
                crafting.getItemFromChest(Id.COBBLESTONE);
            } break;
            case "test": {
              
            } break;
            default: {
                crafting.addCommand(commandParser.parseCommand(text));
            }
        }
            
     }
    
}
