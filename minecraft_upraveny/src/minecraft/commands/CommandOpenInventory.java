/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraft.commands;

import minecraft.crafting.Crafting;
import minecraft.exception.MinecraftException;
import minecraftbot.ChatHandler;

/**
 *
 * @author Kerzak
 */
public class CommandOpenInventory implements Command {
    
    private boolean executed = false;
    
    private Crafting crafting;
    
    private boolean openInventory = true;
    
    private ChatHandler chat;
    
    private int inventoryID;
    
    /**
     * 
     * @param crafting 
     * @param chat
     * @param inventoryID 0 for chest, 1 for workbench, 2 for furnace.  
     */
    public CommandOpenInventory(Crafting crafting, ChatHandler chat, int inventoryID) {
        this.crafting = crafting;
        this.chat = chat;
        this.inventoryID = inventoryID;
    }

    @Override
    public boolean isExecuted() {
        return this.executed;
    }

    @Override
    public void execute() {
//         try {
            this.executed = crafting.openInventory(inventoryID,openInventory);
            openInventory = false;
//        } catch (MinecraftException ex) {
//            chat.sendMessage(ex.getMessage());
//            this.executed = true;
//            crafting.sleep(1000);
//        }
    }
    
}
