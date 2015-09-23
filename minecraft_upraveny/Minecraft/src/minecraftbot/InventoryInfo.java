
package minecraftbot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import minecraftbot.world.Block;


/**
 * Provides information about inventory 
 * and allows droping items.
 */
public class InventoryInfo implements IInventoryView{
    private final InventoryHandler handler;
    
    public InventoryInfo(InventoryHandler handler)
    {
        this.handler = handler;
    }
    
    /**
     * @return ID of item held in hand.
     */
    @Override
    public Id heldId()
    {
        //System.out.println("HELD IS "+handler.getSlotInfo()[36]);
        handler.setFirstSlotHeld();
        if(handler.getSlotInfo()[36]==null)
            return Id.Air;
        return handler.getSlotInfo()[36].getId();
    }
    
    
    /**
     * @param id Item ID to check.
     * @return Whether there is item with ID in the inventory.
     */
    @Override
    public boolean contains(Id id)
    {
        for (Map.Entry<Block, ArrayList<byte[]>> en : handler.getBlockInfo().entrySet()) {
            if(en.getKey().getId()==id)
                return true;
        }
        return false;
    }

    /**
     * @param type
     * @param id Item type to check.
     * @return Whether there is item type in the inventory.
     */
    @Override
    public boolean contains(ItemType type)
    {
        for (Map.Entry<Block, ArrayList<byte[]>> en : handler.getBlockInfo().entrySet()) {
            if(en.getKey().getId().getType()==type)
                return true;
        }
        return false;
    }
    
    /**
     * @param id Item ID to check.
     * @return How many items with ID is in the inventory.
     */
    @Override
    public int amountOf(Id id)
    {
        int amount = 0;
        for (Map.Entry<Block, ArrayList<byte[]>> en : handler.getBlockInfo().entrySet()) {
            if(en.getKey().getId()==id)
            {
                for(byte[] info : en.getValue())
                {
                    amount += info[1];
                }
            }
        }
        return amount;
    }
    
    /**
     * @param type Item type to check.
     * @return How many items of type is in the inventory.
     */
    @Override
    public int amountOf(ItemType type)
    {
        int amount = 0;
        for (Map.Entry<Block, ArrayList<byte[]>> en : handler.getBlockInfo().entrySet()) {
            if(en.getKey().getId().getType()==type)
            {
                for(byte[] info : en.getValue())
                {
                    amount += info[1];
                }
            }
        }
        return amount;
    }

    @Override
    public void drop(Id id) {
        handler.drop(id);
    }

    @Override
    public Set<Id> allIds() {
        HashSet<Id> set = new HashSet<>();
        for(Block b : handler.getBlockInfo().keySet())
        {
            set.add(b.getId());
        }
        return set;
    }
    

}
