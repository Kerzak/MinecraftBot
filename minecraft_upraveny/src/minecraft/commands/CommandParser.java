/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraft.commands;

import java.util.Locale;
import minecraft.crafting.Crafting;
import minecraft.inventory.InventoryManager;
import minecraftbot.ChatHandler;
import minecraftbot.Id;

/**
 *
 * @author Kerzak
 */
public class CommandParser {
    
    Crafting crafting;
    
    InventoryManager inventoryManager;
    
    ChatHandler chat;
    
    public CommandParser(Crafting crafting, ChatHandler chat, InventoryManager inventoryManager) {
        this.crafting = crafting;
        this.chat = chat;
        this.inventoryManager = inventoryManager;
    }
    
    public  Command parseCommand(String commandStr) {
        //TODO: parse all commands
        if(commandStr.matches("craft item.*")) {
            if (!checkCraftingMode()) return null;
            String[] inputArr = commandStr.split("\\s");
            String itemStr = inputArr[2].toUpperCase(Locale.ROOT);
            
            Id id = Id.valueOf(itemStr);
            return new CommandCraftItem(crafting, id, chat, inventoryManager);
        }
        
        if(commandStr.matches("open inventory.*")) {
            if (!checkCraftingMode()) return null;
            String[] inputArr = commandStr.split("\\s");
            String inventoryStr = inputArr[2].toUpperCase(Locale.ROOT);
            int inventoryID = -1;
            switch (inventoryStr) {
                case "CHEST": inventoryID = 0; break;
                case "WORKBENCH": inventoryID = 1; break;
                case "FURNACE": inventoryID = 2; break;
            }
            return new CommandOpenInventory(crafting, chat, inventoryID);
        }
        
        if(commandStr.matches("clear inventory")) {
            return new CommandDropInventory(inventoryManager);
        }
        
        if(commandStr.matches("open inventory.*")) {
            if (!checkCraftingMode()) return null;
            String[] inputArr = commandStr.split("\\s");
            String inventoryName = inputArr[2].toUpperCase(Locale.ROOT);
            int inventory = -1;
            switch (inventoryName) {
                case "CHEST": inventory = 0; break;
                case "WORKBENCH": inventory = 1; break;
                case "FURNACE": inventory = 2; break;
            }
            return new CommandOpenInventory(crafting, chat, inventory);
        }
        
        if(commandStr.matches("move item.*" )) {
            String[] inputArr = commandStr.split("\\s");
            short startIndex = Short.parseShort(inputArr[2]);
            short endIndex = Short.parseShort(inputArr[3]);
            short count = Short.parseShort(inputArr[4]);
            
            return new CommandMoveItem(startIndex, endIndex, count, inventoryManager);
        }
        
        return null;
    }
    
    private boolean checkCraftingMode() {
        if (!crafting.isInCraftingMode()) {
            chat.sendMessage("Not in crafting mode. You have to \"start crafting\"");
            return false;
        }
        return true;
    }
    
}
