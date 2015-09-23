
package minecraftbot;

import minecraftbot.build.BlockPlan;

/**
 *
 */
public interface IInventoryStorage {
    /**
     * Sets inventory slot.
     * @param type Type of inventory.
     * @param slot Slot number.
     * @param id Id of new item.
     * @param meta Meta of new item.
     * @param count Count of new item.
     */
    public void setSlot(byte type, byte slot, int id, byte meta, byte count);
    
    /**
     * Starts recovery of inventory state.
     * Disables all inventory actions.
     */
    public void startRecovery();
    
    /**
     * Ends recovery of inventory state.
     * Allows inventory actions to be performed again.
     */
    public void endRecovery();
    
    /**
     * Confirms previous inventory action.
     * @param actionNumber Action number.
     */
    public void confirmTransaction(short actionNumber);
    
    /**
     * Informs inventory handler about block that bot is about to build.
     * @param plan Build plan information.
     */
    public void toBuild(BlockPlan plan);
    
    /**
     * Informs inventory handler about block change in the world.
     * Used to confim whether the block was build by the bot.
     * @param id Id of new block.
     * @param x X coordinate of the block.
     * @param y Y coordinate of the block.
     * @param z Z coordinate of the block.
     */
    public void blockChanged(int id, int x, int y, int z);
    
    
}
