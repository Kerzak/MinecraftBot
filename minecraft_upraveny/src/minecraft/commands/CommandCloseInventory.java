/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraft.commands;

import minecraft.inventory.InventoryManager;

/**
 *
 * @author Kerzak
 */
public class CommandCloseInventory implements Command {

    private boolean executed = false;
    
    private InventoryManager inventoryManager;
    
    public CommandCloseInventory(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }
    
    @Override
    public boolean isExecuted() {
        return this.executed;
    }

    @Override
    public void execute() {
        //TODO: close inventory, may not be needed
    }
    
}
