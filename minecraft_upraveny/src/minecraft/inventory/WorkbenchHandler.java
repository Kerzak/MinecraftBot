/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraft.inventory;

import minecraftbot.IInventoryStorage;
import minecraftbot.Id;

/**
 *
 * @author Kerzak
 */
public class WorkbenchHandler implements IInventoryStorage {

    private Slot craftingOutput;//0
    private Slot[] craftingInput;//1-9
    private Slot[] mainInventory;//10-36
    private Slot[] hotbar;//37-45
    
    public final int START_INDEX_CRAFTING_OUTPUT_1 = 0;
    public final int END_INDEX_CRAFTING_OUTPUT_1 = 0;
    public final int START_INDEX_CRAFTING_INPUT_2 = 1;
    public final int END_INDEX_CRAFTING_INPUT_2 = 9;
    public final int START_INDEX_MAIN_INVENTORY_3 = 10;
    public final int END_INDEX_MAIN_INVENTORY_3 = 36;
    public final int START_INDEX_HOTBAR_4 = 37;
    public final int END_INDEX_HOTBAR_4 = 45;
    
    private byte windowID;
    
    public WorkbenchHandler(byte windowID, String windowTitle, int numberOfSlots, Slot[] mainInventory, Slot[] hotbar) {
        this.mainInventory = mainInventory;
        this.hotbar = hotbar;
        craftingInput = new Slot[9];
        this.windowID = windowID;
    }
    
    public int getMyInventoryIndex(Id id) {
        
        for (int i = 0; i < mainInventory.length; i++) {
            if(mainInventory[i] != null && mainInventory[i].getId().getValue() == id.getValue()) return i + START_INDEX_MAIN_INVENTORY_3;
        }
        for (int i = 0; i < hotbar.length; i++) {
            if(hotbar[i] != null && hotbar[i].getId().getValue() == id.getValue()) return i + START_INDEX_HOTBAR_4;
        }
        return -1;
    }
    
    @Override
    public void setSlot(byte type, byte slot, int id, byte meta, byte count) {
        System.out.println("SETTING SLOT type: " + type + " slot: " + slot + " id: " + id + " meta: " + meta + " count: " + count);
        if (slot != -1 && id != -1 && count > 0) {
            if(slot <= END_INDEX_CRAFTING_OUTPUT_1) 
                craftingOutput = new Slot(slot, id, count);
            else if (slot <= END_INDEX_CRAFTING_INPUT_2) 
                craftingInput[slot - START_INDEX_CRAFTING_INPUT_2] = new Slot(slot, id, count);
            else if (slot <= END_INDEX_MAIN_INVENTORY_3) 
                mainInventory[slot - START_INDEX_MAIN_INVENTORY_3] = new Slot(slot, id, count);
            else if (slot <= END_INDEX_HOTBAR_4)
                hotbar[slot - START_INDEX_HOTBAR_4] = new Slot(slot, id, count);
        }
    }

    @Override
    public void confirmTransaction(short actionNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte getWindowId() {
        return this.windowID;
    }

    @Override
    public byte[] getSlotData(int slot) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Slot getSlot(short slot) {
        if (slot != -1) {
            if(slot <= END_INDEX_CRAFTING_OUTPUT_1) 
                return craftingOutput;
            else if (slot <= END_INDEX_CRAFTING_INPUT_2) 
                return craftingInput[slot - START_INDEX_CRAFTING_INPUT_2];
            else if (slot <= END_INDEX_MAIN_INVENTORY_3)
                return mainInventory[slot - START_INDEX_MAIN_INVENTORY_3];
            else if (slot <= END_INDEX_HOTBAR_4)
                return hotbar[slot - START_INDEX_HOTBAR_4];
        }
        return null;
    }

    @Override
    public short getIndex(Id id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getStartIndexMainInventory() {
        return this.START_INDEX_MAIN_INVENTORY_3;
    }

    @Override
    public int getStartIndexHotbar() {
        return  this.START_INDEX_HOTBAR_4;
    }

    @Override
    public void setSlotEmpty(int index) {
        if(index <= END_INDEX_CRAFTING_OUTPUT_1) 
            craftingOutput = null;
        else if (index <= END_INDEX_CRAFTING_INPUT_2) 
            craftingInput[index - START_INDEX_CRAFTING_INPUT_2] = null;
        else if (index <= END_INDEX_MAIN_INVENTORY_3)
            mainInventory[index - START_INDEX_MAIN_INVENTORY_3] = null;
        else if (index <= END_INDEX_HOTBAR_4)
            hotbar[index - START_INDEX_HOTBAR_4] = null;
    }
    
    @Override
    public InventoryType getInventoryType() {
        return InventoryType.WORKBENCH;
    }
}
