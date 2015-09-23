

package minecraftbot;

import java.util.Set;

/**
 * Provides information about inventory 
 * and allows droping items.
 */
public interface IInventoryView {
    /**
     * @return Id of currently held item.
     */
    public Id heldId();
    
    /**
     * @param id Item id.
     * @return Whether there's item with particular id in the inventory.
     */
    public boolean contains(Id id);
    
    /**
     * @param type Item type.
     * @return  Whether there's an item of particular type in the inventory.
     */
    public boolean contains(ItemType type);
    
    /**
     * @param id Item id.
     * @return How many items of particular id are in the inventory.
     */
    public int amountOf(Id id);
    
    /**
     * @param type Item type.
     * @return How many items of particular type are in the inventory.
     */
    public int amountOf(ItemType type);
    
    /**
     * @return Set of all Item ids that are in the inventory.
     */
    public Set<Id> allIds();
    
    /**
     * Drops all items of particular id from the inventory.
     * @param id Item id.
     */
    public void drop(Id id);
}
