/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraft.commands;

import java.util.logging.Level;
import java.util.logging.Logger;
import minecraft.crafting.Crafting;
import minecraft.exception.MinecraftException;
import minecraft.inventory.InventoryManager;
import minecraftbot.ChatHandler;
import minecraftbot.Id;

/**
 *
 * @author Kerzak
 */
public class CommandGetItemFromChest implements Command {
    
    private boolean executed = false;
    
    private boolean confirmed = false;
    
    private InventoryManager inventoryManager;
    
    private Crafting crafting;
    
    private Id id;
    
    private int actionNumber;
    
    private ChatHandler chat;
    
    public CommandGetItemFromChest(InventoryManager inventoryManager, Id id, ChatHandler chat, Crafting crafting) {
        this.inventoryManager = inventoryManager;
        this.id = id;
        this.chat = chat;
        this.crafting = crafting;
    }

    @Override
    public boolean isExecuted() {
        return (this.executed);
    }

    @Override
    public void execute(){
        if (!executed) {
            try {
                executed = inventoryManager.getItemFromChest(id);
            } catch (MinecraftException ex) {
                chat.sendMessage(ex.getMessage());
                this.executed = true;
                crafting.cancelCrafting();
                inventoryManager.sleep(1000);
            }
        } 
    }
    
}
