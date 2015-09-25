/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraft.commands;

import minecraft.crafting.Crafting;
import minecraft.inventory.InventoryManager;

/**
 *
 * @author Kerzak
 */
public class CommandMoveItem implements Command {

    private boolean executed = false;
    
    private InventoryManager inventoryManager;
    
    private short startIndex;
    
    private short endIndex;
    
    private short count;
    
    public CommandMoveItem(short startIndex, short endIndex, short count, InventoryManager inventoryManager) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.count = count;
        this.inventoryManager = inventoryManager;
    }
    
    @Override
    public boolean isExecuted() {
        return this.executed;
    }

    @Override
    public void execute() {
        inventoryManager.moveItem(startIndex, endIndex, count);
        this.executed = true;
    }
    
}
