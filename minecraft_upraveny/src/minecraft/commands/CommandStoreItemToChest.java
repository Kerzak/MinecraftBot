/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraft.commands;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import minecraft.crafting.Crafting;
import minecraft.exception.MinecraftException;
import minecraft.inventory.InventoryManager;
import minecraftbot.Id;

/**
 *
 * @author Kerzak
 */
public class CommandStoreItemToChest implements Command {
    
    private boolean executed = false;
    
    private InventoryManager inventoryManager;
    
    private Id id;
    
    private boolean openChest = true;
    
    private Crafting crafting;
    
    private int count;

    public CommandStoreItemToChest(Id id, InventoryManager inventoryManager, Crafting crafting, int count) {
        this.inventoryManager = inventoryManager;
        this.id = id;
        this.crafting = crafting;
        this.count = count;
    }
    
    @Override
    public boolean isExecuted() {
        return this.executed;
    }

    @Override
    public void execute() {
        this.executed = inventoryManager.storeItemToChest(id, count);
    }
    
}
