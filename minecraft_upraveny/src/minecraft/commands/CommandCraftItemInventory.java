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
public class CommandCraftItemInventory implements Command {
    
    private boolean executed = false;
    
    private Crafting crafting;
    
    private InventoryManager inventoryManager;
    
    private Id id;
    
    ChatHandler chat;
    
    public CommandCraftItemInventory(Crafting crafting, InventoryManager inventoryManager, ChatHandler chat, Id id) {
        this.crafting = crafting;
        this.inventoryManager = inventoryManager;
        this.chat = chat;
        this. id = id;
    }

    @Override
    public boolean isExecuted() {
        return this.executed;
    }

    @Override
    public void execute() {
        try {
            //crafting.addCommand(new CommandCloseInventory(inventoryManager));
            crafting.craftItemInInventory(id);
            executed = true;
        } catch (MinecraftException ex) {
            chat.sendMessage(ex.getMessage());
        }
    }
}
