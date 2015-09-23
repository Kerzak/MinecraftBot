/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraft.commands;

import minecraft.exception.MinecraftException;
import minecraft.inventory.InventoryManager;
import minecraftbot.Id;

/**
 *
 * @author Kerzak
 */
public class CommandStoreItemToWorkbench implements Command {
    
    private boolean executed = false;
    
    private Id id;
    
    private InventoryManager inventoryManager;
    
    private int index;
    
    public CommandStoreItemToWorkbench(Id id, InventoryManager inventoryManager, int index) {
        this.id = id;
        this.inventoryManager = inventoryManager;
        this.index = index;
    }

    @Override
    public boolean isExecuted() {
        return this.executed;
    }

    @Override
    public void execute(){
        this.executed = inventoryManager.storeItemToWorkbench(index, id);
        
    }

   
}
