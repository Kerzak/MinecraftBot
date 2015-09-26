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
import minecraft.exception.MinecraftException;
import minecraft.inventory.InventoryManager;
import minecraftbot.ChatHandler;
import minecraftbot.Id;

/**
 *
 * @author Kerzak
 */
public class CommandCraftItemWorkbench implements Command {

    private boolean executed = false;
    
    private Crafting crafting;
    
    private InventoryManager inventoryManager;
    
    Id id;
    
    private ChatHandler chat;
    
    public CommandCraftItemWorkbench(Crafting crafting, Id id, ChatHandler chat, InventoryManager inventoryManager) {
        this.crafting = crafting;
        this.id = id;
        this.chat = chat;
        this.inventoryManager = inventoryManager;
    }
    
    @Override
    public boolean isExecuted() {
        return this.executed;
    }

    @Override
    public void execute() {
        //open Chest; TODO: inventory enum
        crafting.addCommand(new CommandOpenInventory(crafting, chat, 0));
        //get stack of items that we have to craftInWorkbench to finsh the task
        crafting.addCommand(new CommandGetCraftingStack(crafting, id, chat, inventoryManager));
        this.executed = true;
    }
    
}
