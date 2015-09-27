/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crafting;

import junit.framework.Assert;
import minecraft.exception.MinecraftException;
import minecraft.inventory.InventoryType;
import minecraftbot.examples.Crafter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Kerzak
 */
public class TestsCrafting {
    
    static private String myName = "MyBot";
    static private String address = "localhost";
    /**
     * It is necessary to set this port before connecting to a new server.
     */
    static private int port = 11059;
    
    static Crafter bot;
    static Crafter commander;
    
    @BeforeClass
    public static  void beforeClass() throws InterruptedException {
        bot = new Crafter();
        bot.startBot(myName, address, port);
         Thread.sleep(250);
        bot.start();
         Thread.sleep(250);
        commander = new Crafter();
         Thread.sleep(250);
        commander.startBot("Commander", address, port);
         Thread.sleep(250);
        commander.start();
        Thread.sleep(250);
        commander.getChat().sendMessage("start crafting");
        Thread.sleep(250);
    }
    
    @Before
    public void beforeTest() throws InterruptedException {
        Thread.sleep(1000);
    }
    
    /**
     * Difficult to test because we cannot ensure that bot has resources in chest.
     * 
     * @throws InterruptedException 
     */
    @Test
    public void testCraftItemTorch() throws InterruptedException {
        commander.getChat().sendMessage("craft item torch");
        Thread.sleep(2000);
    }
    
    /**
     * Opens chest at chestLocation. This chest is used during crafting mode.
     */
    @Test
    public void testOpenChest() throws InterruptedException {
        commander.getChat().sendMessage("open inventory chest");
        Thread.sleep(500);
        Assert.assertTrue(bot.getInventoryManager().getCurrentInventory().getInventoryType().equals(InventoryType.CHEST));
    }
    
    /**
     * Opens workbench at workbenchLocation. This chest is used during crafting mode.
     */
    @Test
    public void testOpenWorkbench() throws InterruptedException, MinecraftException {
        commander.getChat().sendMessage("open inventory workbench");
        Thread.sleep(500);
        Assert.assertTrue(bot.getInventoryManager().getCurrentInventory().getInventoryType().equals(InventoryType.WORKBENCH));
    }
    
    /**
     * Opens furnace at furnaceLocation. This chest is used during crafting mode.
     */
    @Test
    public void testOpenFurnace() throws InterruptedException {
        //TODO: implement after comunication with furnace will be running
    }
    
    @Test
    public void moveOneItemInChest() throws InterruptedException, MinecraftException {
        commander.getChat().sendMessage("open inventory chest");
        Thread.sleep(250);
        Assert.assertTrue(bot.getInventoryManager().getCurrentInventory().getInventoryType().equals(InventoryType.CHEST));
        
        short firstNotEmptyIndex = bot.getInventoryManager().getFirstNotEmptyIndex();
        short firstEmptyIndex = bot.getInventoryManager().getFirstEmptyIndex();
        
        commander.getChat().sendMessage("move item " + firstNotEmptyIndex + " " + firstEmptyIndex + " 1");

        Thread.sleep(250);
        commander.getChat().sendMessage("open inventory chest");
        Thread.sleep(250);
        Assert.assertTrue(bot.getInventoryManager().getCurrentInventory().getInventoryType().equals(InventoryType.CHEST));
        Assert.assertTrue(bot.getInventoryManager().getCurrentInventory().getSlot(firstEmptyIndex) != null);
    }
    
    @Test
    public void testShowInventory() throws InterruptedException {
        commander.getChat().sendMessage("show inventory");
        Thread.sleep(5000);
        //no exception => OK
    }
}
