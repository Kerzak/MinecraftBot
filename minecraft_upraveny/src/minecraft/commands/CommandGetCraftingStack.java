/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraft.commands;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import minecraft.crafting.Crafting;
import minecraft.crafting.Pattern;
import minecraft.exception.MinecraftException;
import minecraft.inventory.InventoryManager;
import minecraftbot.ChatHandler;
import minecraftbot.Id;

/**
 *
 * @author Kerzak
 */
public class CommandGetCraftingStack implements Command {
    
    private boolean executed = false;
    
    private Crafting crafting;
    
    private InventoryManager inventoryManager;
    
    private Id id;
    
    private Stack craftingStack;
    
    private ChatHandler chat;
    
    private boolean first = true;
    
    public CommandGetCraftingStack(Crafting crafting, Id id, ChatHandler chat, InventoryManager inventoryManager) {
        this.crafting = crafting;
        this.id = id;
        this.chat = chat;
        this.inventoryManager = inventoryManager;
    }
    
    public Stack getCraftingStack() {
        return craftingStack;
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public void execute() {
        try {
            craftingStack = crafting.getWorkbenchCraftingStack(id);
            while(!this.craftingStack.isEmpty()) {
                //open Chest; TODO: inventory enum
                if(first) {
                    first = false;
                } else {
                    crafting.addCommand(new CommandOpenInventory(crafting, chat, 0));
                }
                Id currentlyWantedId = (Id) this.craftingStack.pop();
                crafting.addCommand(new CommandGetResourcesForCrafting(crafting, currentlyWantedId, chat));
                crafting.addCommand(new CommandOpenInventory(crafting, chat, 1));
                crafting.addCommand(new CommandCraftItemFromResources(crafting,currentlyWantedId, chat));
                int count = Pattern.valueOf(currentlyWantedId.toString()).getCount();
                System.out.println("//////////////////COUNT: " + count);
                crafting.addCommand(new CommandTakeCraftingResult(inventoryManager, currentlyWantedId, crafting, chat, count));
                crafting.addCommand(new CommandOpenInventory(crafting, chat, 0));
                crafting.addCommand(new CommandStoreItemToChest(currentlyWantedId, inventoryManager, crafting, count));
            }
            this.executed = true;
        } catch (MinecraftException ex) {
            this.executed = true;
            crafting.cancelCrafting();
            chat.sendMessage(ex.getMessage());
        }
    }
    
}
