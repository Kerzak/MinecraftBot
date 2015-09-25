/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraft.crafting;

import minecraft.assertService.AssertService;
import minecraft.exception.MinecraftException;
import minecraftbot.Id;

/**
 * 
 * @author Kerzak
 * 
 * Enumerates crafting patterns
 */
public enum Pattern {
    
    //(1,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE),
    WOOD (4,Id.LOG,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE),
    FURNACE (1,Id.COBBLESTONE,Id.COBBLESTONE,Id.COBBLESTONE,Id.COBBLESTONE,Id.NONE,
            Id.COBBLESTONE,Id.COBBLESTONE,Id.COBBLESTONE,Id.COBBLESTONE),
    STICK(4,Id.WOOD,Id.NONE,Id.NONE,Id.WOOD,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE),
    TORCH(4,Id.COAL,Id.NONE,Id.NONE,Id.STICK,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE),
    BLOCK_OF_COAL(1,Id.COAL,Id.COAL,Id.COAL,Id.COAL,Id.COAL,Id.COAL,Id.COAL,Id.COAL,Id.COAL),
    IRON_BLOCK(1,Id.IRON_INGOT,Id.IRON_INGOT,Id.IRON_INGOT,Id.IRON_INGOT,Id.IRON_INGOT,Id.IRON_INGOT,Id.IRON_INGOT,Id.IRON_INGOT,Id.IRON_INGOT),
    BOOKSHELF(1,Id.WOOD,Id.WOOD,Id.WOOD,Id.BOOK,Id.BOOK,Id.BOOK,Id.WOOD,Id.WOOD,Id.WOOD),
    CLAY_BLOCK(1,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE,Id.NONE),
    ;
    
    private Id[] workbenchPattern = new Id[9];
    private Id[] inventoryPattern = new Id[4];
    private int count;
    
    AssertService assertService = new AssertService();
    
    /**
     * 
     * Creates pattern for workbench. If able, also creates pattern for inventory.
     * 
     * @param idIJ represents item to be placed at IJth position at crafting matrix
     *
     */
    Pattern(int count, Id id11,Id id12,Id id13,Id id21,Id id22,Id id23,Id id31,Id id32,Id id33) {
        this.workbenchPattern[0] = id11;
        this.workbenchPattern[1] = id12;
        this.workbenchPattern[2] = id13;
        this.workbenchPattern[3] = id21;
        this.workbenchPattern[4] = id22;
        this.workbenchPattern[5] = id23;
        this.workbenchPattern[6] = id31;
        this.workbenchPattern[7] = id32;
        this.workbenchPattern[8] = id33;
        
        this.count = count;
        
        
        if (Id.NONE.equals(id13) && Id.NONE.equals(id23) && Id.NONE.equals(id33) 
                && Id.NONE.equals(id32) && Id.NONE.equals(id31)) {
            //able to craft in inventory
            this.inventoryPattern[0] = id11;
            this.inventoryPattern[1] = id12;
            this.inventoryPattern[2] = id21;
            this.inventoryPattern[3] = id22;
        } else {
            //unable to craft in inventory
            this.inventoryPattern = null;
        } 
    }
    
    /**
     * 
     * @return pattern for workbench as an array. Array has order same as in packet.
     */
    public Id[] getWorkbenchPattern() {
        return this.workbenchPattern;
    }
    
    /**
     * 
     * @return pattern for workbench as an array. Array ha order same as in packet.
     * @throws MinecraftException if item is not craftable in inventory.
     */
    public Id[] getInventoryPattern() throws MinecraftException {
        assertService.assertInventoryPatternExists(inventoryPattern);
        return this.inventoryPattern;
    }
    
    public static boolean hasPattern(String id) {
        for (Pattern pattern: values()) {
            if (pattern.toString().equals(id)) return true;
        }
        return false;
    }
    
    public int getCount() {
        return this.count;
    }
}
