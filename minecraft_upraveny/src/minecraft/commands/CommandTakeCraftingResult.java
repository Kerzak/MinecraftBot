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
public class CommandTakeCraftingResult implements Command {
    
    private boolean executed = false;
    
    private InventoryManager inventoryManager;
    
    private Crafting crafting;
    
    private Id id;
    
    private ChatHandler chat;
    
    private int craftedCount;
    
    public CommandTakeCraftingResult(InventoryManager inventoryManager, Id id, Crafting crafting, ChatHandler chat, int craftedCount) {
        this.inventoryManager = inventoryManager;
        this.id = id;
        this.crafting = crafting;
        this.chat = chat;
        this.craftedCount = craftedCount;
    }

    @Override
    public boolean isExecuted() {
        return this.executed;
    }

    @Override
    public void execute() {
        try {
            System.out.println("taking crafting result");
            inventoryManager.takeCraftingResult(id, craftedCount);
            this.executed = true;
        } catch (MinecraftException ex) {
            chat.sendMessage(ex.getMessage());
            this.executed = true;
            crafting.sleep(1000);
        }
    }
    
}
