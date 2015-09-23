/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot;

import java.util.ArrayList;
import java.util.Map;
import minecraftbot.world.Block;

/**
 *
 * @author eZ
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
            return Id.AIR;
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
     * @param id Item type to check.
     * @return How many items of type is in the inventory.
     */
    @Override
    public int amountOf(ItemType type)
    {
        int amount = 0;
        for (Map.Entry<Block, ArrayList<byte[]>> en : handler.getBlockInfo().entrySet()) {
            System.out.println(en.getKey().getId()+"");
            System.out.println(en.getKey().getId().getType()+"");
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
    

}
