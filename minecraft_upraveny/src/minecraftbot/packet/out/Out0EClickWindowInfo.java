/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.packet.out;

import minecraft.inventory.Slot;

/**
 *
 * @author Kerzak
 */
public class Out0EClickWindowInfo {
    private byte windowId;
    private short slot;
    private Slot slotData;
    private int actionNumber;
    
    public Out0EClickWindowInfo(byte windowId, short slot, Slot slotData, int actionNumber) {
        this.windowId = windowId;
        this.slot = slot;
        this.slotData= slotData;
        this.actionNumber = actionNumber;
    }
    
    public byte getWindowId() {
        return windowId;
    }
    
    public short getSlot() {
        return this.slot;
    }
    
    public Slot getSlotData() {
        return this.slotData;
    }
    
    public int getActionNumber() {
        return this.actionNumber;
    }
}
