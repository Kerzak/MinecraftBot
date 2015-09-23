/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot;

import minecraft.inventory.Slot;

/**
 *
 * @author eZ
 */
public interface IInventoryStorage {
    public void setSlot(byte type, byte slot, int id, byte meta, byte count);
    
    public void confirmTransaction(short actionNumber);
    
    public byte getWindowId();
    
    public byte[] getSlotData(int slot);
    
    public Slot getSlot(short slot);
    
    public short getIndex(Id id);
    
    public int getStartIndexMainInventory();
    
    public int getStartIndexHotbar();
    
    public void setSlotEmpty(int index);
}
