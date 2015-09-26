/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraft.crafting;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import minecraft.commands.Command;
import minecraft.commands.CommandGetItemFromChest;
import minecraft.commands.CommandParser;
import minecraft.commands.CommandStoreItemToWorkbench;
import minecraft.commands.CommandTakeCraftingResult;
import minecraft.exception.ErrorMessage;
import minecraft.exception.MinecraftException;
import minecraft.inventory.ChestHandler;
import minecraft.inventory.InventoryManager;
import minecraft.inventory.Slot;
import minecraft.inventory.WorkbenchHandler;
import minecraftbot.ChatHandler;
import minecraftbot.Digging;
import minecraftbot.GameInfo;
import minecraftbot.Id;
import minecraftbot.InventoryHandler;
import minecraftbot.Locomotion;
import minecraftbot.MinecraftBotHandler;
import minecraftbot.build.Building;
import minecraftbot.navigation.Navigation;
import minecraftbot.network.MinecraftInputStream;
import minecraftbot.network.MinecraftOutputStream;
import minecraftbot.packet.in.In2DOpenWindow;
import minecraftbot.packet.out.Out02UseEntity;

/**
 *
 * @author Kerzak
 */

public class Crafting {
    
    LinkedList<Command> commands;
    
    private Locomotion move;
    
    boolean newCommand = true;
    
    boolean firstExecutionOfCommand = true;
    
    boolean cancelCrafting = false;
    
    Command currentCommand;
    
    private ChatHandler chat;
    
    public Crafting(MinecraftOutputStream outputStream, Locomotion move, InventoryManager inventoryManager, ChatHandler chat) {
        this.out = outputStream;        
        this.move = move;
        this.inventoryManager = inventoryManager;
        commands = new LinkedList<Command>();
        this.chat = chat;
    }
    
    
    /////////////
    //VARIABLES//
    /////////////
    
     MinecraftOutputStream out;
             
    
    /**
     * Indicates, whether bot is in crafting mode. Bot can only perform crafting 
     * in crafting mode.
     */
    private boolean inCraftingMode = false;
    
    /**
     * The location of the chest, where user puts resources for crafting
     */
    private Location chestLocation;
    
    /**
     * The location of the workbench that bot uses to craftInWorkbench.
     */
    private Location workbenchLocation;
    
    /**
     * The location of the furnace that bot uses to craftInWorkbench
     */
    private Location furnaceLocation;
    
    private InventoryManager inventoryManager;
    
    private int counter = 0;
    
    ///////////////
    //OUT PACKETS//
    ///////////////
    
    
    
    //////////////
    //IN PACKETS//
    //////////////
    
    In2DOpenWindow packetInOpenWindow;
    
    public boolean isInCraftingMode(){
        return this.inCraftingMode;
    }
    
    public Location getWorkbenchLocation() {
        return this.workbenchLocation;
    }
    
    public Location getChestLocation() {
        return this.chestLocation;
    }
    
    public Location getFurnaceLocation() {
        return this.furnaceLocation;
    }
    
    public Locomotion getMove() {
        return this.move;
    }
    
    /**
     * Get into crafting mode. Place CHEST and Crafting table in front of you.
     */
    public void initializeCrafting(Locomotion move, Building build, ChatHandler chat) {
        /// TODO: bot may not have workbench or chest
        workbenchLocation = move.getLocation();
        workbenchLocation = workbenchLocation.setX(workbenchLocation.getX() + 1);
        build.block(Id.WORKBENCH, workbenchLocation);
        chestLocation = workbenchLocation;
        chestLocation = chestLocation.setZ(chestLocation.getZ() + 1);
        build.block(Id.CHEST, chestLocation);
        furnaceLocation = workbenchLocation;
        furnaceLocation = furnaceLocation.setZ(workbenchLocation.getZ() - 1);
        build.block(Id.FURNACE, furnaceLocation);
        inCraftingMode = true;
        chat.sendMessage("Crafting mode active. Ready for crafting tasks");
       this.packetInOpenWindow = new In2DOpenWindow(inventoryManager);
    }
    
    /**
     * Craft item according to itemId from resources contained in chest at 
     * chestLocation.
     * @param itemId Id of item to be crafted.
     * @param openChest
     * @return 
     * @throws minecraft.exception.MinecraftException
     */
    public boolean craftInWorkbench(Id itemId, boolean openWorkbench) throws MinecraftException {
        
        //get blocks needed to craftInWorkbench item
        Pattern pattern = Pattern.valueOf(itemId.toString());
        Id[] workbenchPattern = pattern.getWorkbenchPattern();
        for (int i = 0; i < workbenchPattern.length; i++) {
            (new CommandStoreItemToWorkbench(workbenchPattern[i], inventoryManager, i)).execute();
        }
        return true;
    }
    
    /**
     * Craft item with given id in players inventory. Notice that not every item 
     * can be crafted in inventory. 
     * 
     * @param id Id of item to be crafted
     */
    public void craftItemInInventory(Id id) throws MinecraftException {
        Pattern pattern = Pattern.valueOf(id.toString());
        if (!pattern.hasInventoryPattern()) {
            chat.sendMessage(ErrorMessage.M01.toString());
            return;
        }
        Id[] inventoryPattern = pattern.getInventoryPattern();
        
        for (int i = 0; i < inventoryPattern.length; i++) {
            if (inventoryPattern[i].getValue() != Id.NONE.getValue()) {
                short index = (short) inventoryManager.getCurrentInventory().getMyInventoryIndex(inventoryPattern[i]);
                inventoryManager.moveItem(index, (short) (i + 1), 1);
            }
        }
        int count = pattern.getCount();
        inventoryManager.getCurrentInventory().setSlot((byte)0,(byte)0, id.getValue(), (byte)0, (byte)count);
        short firstEmptyIndex = inventoryManager.getFirstEmptyIndex();
        inventoryManager.moveItem((short)0, firstEmptyIndex, count);
    }
    
    public boolean openInventory(int inventoryID, boolean openInventory) {
        System.out.println("Opening inventory");

        //TODO: enum for inventories
        switch (inventoryID) {
            case 0: {
                if(inventoryManager.getCurrentInventory().getClass().equals(ChestHandler.class)) {
                    System.out.println("Inventory(chest) opened succesfully");
                    return true;
                }
                if(openInventory)inventoryManager.requestInventoryOpen(chestLocation, move);
            } break;
            case 1 : {
                if(inventoryManager.getCurrentInventory().getClass().equals(WorkbenchHandler.class)) {
                    System.out.println("Inventory(workbench) opened succesfully");
                    return true;
                }
                if(openInventory)inventoryManager.requestInventoryOpen(workbenchLocation, move);
            } break;
//            case 2 : {
//                if(openInventory)inventoryManager.requestInventoryOpen(furnaceLocation, move);
//                //TODO: FurnaceHandler, if(inventoryManager.getCurrentInventory().getClass().equals(FurnaceHandler.class)) return true;
//            }
        }
        return false;
    }
    
    public boolean getResourcesFromChest(Id itemId) throws MinecraftException {
        System.out.println("Getting resources from chest");
        Pattern pattern = Pattern.valueOf(itemId.toString());
        Id[] workbenchPattern = pattern.getWorkbenchPattern();
  
        for (int i = 0; i < workbenchPattern.length; i++) {
            //(new CommandGetItemFromChest(inventoryManager, workbenchPattern[i], chat, this)).execute();
            inventoryManager.getItemFromChest(workbenchPattern[i]);
        }
        return true;
    }
    
    /**
     * 
     * @param id
     * @return array of patterns that will be used for crafting item
     */
    public Stack getCraftingStack(Id id) throws MinecraftException {
        Stack result = new Stack();
        Slot[] chestItems = ((ChestHandler)inventoryManager.getCurrentInventory()).getChestItemsCopy();
        System.out.println("+CHEST ITEMS COPY+");
        for (int i = 0; i < chestItems.length; i++) {
            if(chestItems[i] == null) System.out.println("EMPTY");
            else System.out.println("id: " + chestItems[i].getId() + " count: " + chestItems[i].getCount());
        }
        System.out.println("+CHEST ITEMS COPY+");
        try {
            result = newStackFloor(result, id, chestItems);
        } catch (MinecraftException ex) {
            if (result.size() == 1) throw new MinecraftException(ErrorMessage.M06);
            else throw new MinecraftException(ErrorMessage.M07);
        }
        return result;
    }
    
    public Stack newStackFloor(Stack oldStack, Id id, Slot[] chestItems) throws MinecraftException {
        oldStack.push(id);
        System.out.println("NEW ID ON STACK: " + id);
        Pattern pattern = null;
        if (Pattern.hasPattern(id.toString())) pattern = Pattern.valueOf(id.toString());
        else throw new MinecraftException(ErrorMessage.M06);
        Id[] workbenchPattern = pattern.getWorkbenchPattern();
        for (int i = 0; i < workbenchPattern.length; i++) {
            if(!removeIfContains(workbenchPattern[i], chestItems)) {
                System.out.println("does not contain " + workbenchPattern[i]);
                oldStack = newStackFloor(oldStack, workbenchPattern[i], chestItems);
            } else {
                System.err.println("contains " + workbenchPattern[i]);
            }
        }
        addCraftedToChestItems(chestItems, id, pattern.getCount());
        return oldStack;
    }
    
    private void addCraftedToChestItems(Slot[] chestItems, Id id, int count) {
        for (int i = 0; i < chestItems.length; i++) {
            if (chestItems[i] != null && chestItems[i].getId().getValue() == id.getValue() && (64 - chestItems[i].getCount() >= count)) {
                chestItems[i].incCountBy(count);
                return;
            }
        }
        for (int i = 0; i < chestItems.length; i++) {
            if(chestItems[i] == null || chestItems[i].getId().getValue() == Id.NONE.getValue()) {
                chestItems[i] = new Slot((short)i, id, (byte)count);
                return;
            }
        }
        //TODO: throw exception
    }
    
    /**
     * 
     * Removes one Slot with id.
     * 
     * @param id id that we are looking for
     * @param array 
     * @return true if it contains wanted Id, false otherwise
     */
    private static boolean removeIfContains(Id id, Slot[] array) {
        if(id == null || id.getValue() == Id.NONE.getValue()) return true;
        for (int i = 0; i < array.length; i++) {
            if(array[i] != null && array[i].getId().getValue() == id.getValue() && array[i].getCount() > 0) {
                array[i].decCountBy(1);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Finish crafting. Allows actions not allowed in crafting mode to be done.
     */
    public void finishCrafting(Navigation navigation, Digging dig) {
        dig.dig(chestLocation);
        navigation.navigate(chestLocation);
        dig.dig(workbenchLocation);
        navigation.navigate(workbenchLocation);
        inCraftingMode = false;
    }
    
    public void showInventory(ChatHandler chat) {
        inventoryManager.showInventory(chat);
    }
    
    public void getItemFromChest(Id id) {
        try {
            inventoryManager.getItemFromChest(id);
        } catch (MinecraftException ex) {
            Logger.getLogger(Crafting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addCommand(Command cmd) {
        commands.addLast(cmd);
    }
    
    public void loopStep() throws MinecraftException {
            if(!commands.isEmpty() && (currentCommand == null || currentCommand.isExecuted())) {
                currentCommand = commands.removeFirst();
            }
            if (currentCommand != null) {
                System.out.println("*********************************************** COMMAND EXECUTION" + currentCommand.getClass().toString() +  " ***********************************************");
                currentCommand.execute();
                if (currentCommand.isExecuted()) currentCommand = null;
                //sleep(5000);
            }
    }
    
    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(Crafting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Clear command queue.
     */
    public void cancelCrafting() {
        this.commands.clear();
    }
}
