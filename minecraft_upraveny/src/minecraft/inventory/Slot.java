/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraft.inventory;

import minecraftbot.Id;

/**
 *
 * @author Kerzak
 */
public class Slot {
    private short slot;
    private Id id;
    private byte count;
    private byte meta;
   
    //NTB data
    
    
    public Slot( short slot_num, int slotId, byte count) {
        this.slot = slot_num;
        for (int i = 0; i < Id.values().length; i++) {
            if (Id.values()[i].getValue() == slotId) this.id = Id.values()[i];
        }
        this.count = count;
    }
    
    public Slot(short slot_num, Id id, byte count) {
        this.slot = slot_num;
        this.id = id;
        this.count = count;
    }
    
    public Slot getCopy() {
        if(this.id == null) return null;
        return new Slot(this.slot, this.id, this.count);
    }
    
    /**
     * 
     * @return true if slot is empty, false otherwise
     */
    public boolean isEmpty() {
        return (this.id.getValue() == -1);
    }
    
    public Id getId() {
        return this.id;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public byte getMeta() {
        return this.meta;
    }
    
    public void decCountBy(int i) {
        this.count -= i;
    }
    
    public void incCountBy(int i) {
        this.count += i;
    }
    
    public byte[] getSlotData() {
        byte[] result = null;
        if (id.getValue() == -1) {
            result = new byte[1];
            result[0] = -1;
        } else {
            result = new byte[4];
            result[0] = id.getByteValue();
            result[1] = count;
            result[2] = meta;
            result[3] = (byte)0;
            System.out.println("SLOTDATA: id: " + id.getByteValue() + " count " + count + " meta: " + meta + " anotherByte: " + 0);
        }
        return result;
    }
    
}
