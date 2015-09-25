/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraft.inventory;

import minecraftbot.IInventoryHandler;
import minecraftbot.IInventoryStorage;
import minecraftbot.Id;
import minecraftbot.ItemType;

/**
 *
 * @author Kerzak
 */
public class PlayersInventoryHandler implements IInventoryStorage, IInventoryHandler{
    private Slot craftingOutput; //0
    private Slot[] craftingInput; //1-4
    private Slot[] armor; //5-8
    private Slot[] mainInventory;//9-35
    private Slot[] hotbar;//36-44
    
    public final int START_INDEX_CRAFTING_OUTPUT_1 = 0;
    public final int END_INDEX_CRAFTING_OUTPUT_1 = 0;
    public final int START_INDEX_CRAFTING_INPUT_2 = 1;
    public final int END_INDEX_CRAFTING_INPUT_2 = 4;
    public final int START_INDEX_ARMOR_3 = 5;
    public final int END_INDEX_ARMOR_3 = 8;
    public final int START_INDEX_MAIN_INVENTORY_4 = 9;
    public final int END_INDEX_MAIN_INVENTORY_4 = 35;
    public final int START_INDEX_HOTBAR_5 = 36;
    public final int END_INDEX_HOTBAR_5 = 44;
    
    public PlayersInventoryHandler(Slot[] mainInventory, Slot[] hotbar) {
        craftingInput = new Slot[4];
        armor = new Slot[4];
        this.mainInventory = mainInventory;
        this.hotbar = hotbar;
    }

    @Override
    public void setSlot(byte type, byte slot, int id, byte meta, byte count) {
         System.out.println("SETTING SLOT type: " + type + " slot: " + slot + " id: " + id + " meta: " + meta + " count: " + count);
        if(slot != -1) {
            Slot newSlot = new Slot(slot, id, count);
            if (slot <= END_INDEX_CRAFTING_OUTPUT_1) {
                craftingOutput = newSlot;
            } else if (slot <= END_INDEX_CRAFTING_INPUT_2) {
                craftingInput[slot - START_INDEX_CRAFTING_INPUT_2] = newSlot;
            } else if (slot <= END_INDEX_ARMOR_3) {
                armor[slot - START_INDEX_ARMOR_3] = newSlot;
            } else if (slot <= END_INDEX_MAIN_INVENTORY_4) {
                mainInventory[slot - START_INDEX_MAIN_INVENTORY_4] = newSlot;
            } else {
                hotbar[slot - START_INDEX_HOTBAR_5] = newSlot;
            }
        }
    }

    @Override
    public void confirmTransaction(short actionNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte getWindowId() {
        return (byte)0;
    }

    @Override
    public byte[] getSlotData(int slot) {
        Slot wantedSlot = null;
       if (slot <= END_INDEX_CRAFTING_OUTPUT_1) {
           wantedSlot = craftingOutput;
       } else if (slot <= END_INDEX_CRAFTING_INPUT_2) {
           wantedSlot = craftingInput[slot - START_INDEX_CRAFTING_INPUT_2];
       } else if (slot <= END_INDEX_ARMOR_3) {
           wantedSlot = armor[slot - START_INDEX_ARMOR_3];
       } else if (slot <= END_INDEX_MAIN_INVENTORY_4) {
           wantedSlot =  mainInventory[slot - START_INDEX_MAIN_INVENTORY_4];
       } else {
           wantedSlot =  hotbar[slot - START_INDEX_HOTBAR_5];
       }
        return wantedSlot.getSlotData();
    }
    
    @Override
    public Slot getSlot(short slot) {
        Slot wantedSlot = null;
       if (slot <= END_INDEX_CRAFTING_OUTPUT_1) {
           wantedSlot = craftingOutput;
       } else if (slot <= END_INDEX_CRAFTING_INPUT_2) {
           wantedSlot = craftingInput[slot - START_INDEX_CRAFTING_INPUT_2];
       } else if (slot <= END_INDEX_ARMOR_3) {
           wantedSlot = armor[slot - START_INDEX_ARMOR_3];
       } else if (slot <= END_INDEX_MAIN_INVENTORY_4) {
           wantedSlot =  mainInventory[slot - START_INDEX_MAIN_INVENTORY_4];
       } else {
           wantedSlot =  hotbar[slot - START_INDEX_HOTBAR_5];
       }
        return wantedSlot;
    }

    @Override
    public short requestHeldChange(Id id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public short requestHeldChange(ItemType type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isConfirmed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public short getIndex(Id id) {
        if(craftingOutput.getId().equals(id)) return 0;
        for (int i = 0; i < craftingInput.length; i++) {
            if(craftingInput[i].getId().equals(id)) return (short)(i + START_INDEX_CRAFTING_INPUT_2);
        }
        for (int i = 0; i < armor.length; i++) {
            if(armor[i].getId().equals(id)) return (short)(i + START_INDEX_ARMOR_3);
        }
        for (int i = 0; i < mainInventory.length; i++) {
            if(mainInventory[i].getId().equals(id)) return (short)(i + START_INDEX_MAIN_INVENTORY_4);
        }
        for (int i = 0; i < hotbar.length; i++) {
            if(hotbar[i].getId().equals(id)) return (short)(i + START_INDEX_HOTBAR_5);
        }
        return -1;
        
    }

    @Override
    public int getStartIndexMainInventory() {
        return START_INDEX_MAIN_INVENTORY_4;
    }

    @Override
    public int getStartIndexHotbar() {
        return START_INDEX_HOTBAR_5;
    }

    @Override
    public void setSlotEmpty(int index) {
        if (index <= END_INDEX_CRAFTING_OUTPUT_1) {
           craftingOutput = null;
       } else if (index <= END_INDEX_CRAFTING_INPUT_2) {
           craftingInput[index - START_INDEX_CRAFTING_INPUT_2] = null;
       } else if (index <= END_INDEX_ARMOR_3) {
           armor[index - START_INDEX_ARMOR_3] = null;
       } else if (index <= END_INDEX_MAIN_INVENTORY_4) {
           mainInventory[index - START_INDEX_MAIN_INVENTORY_4] = null;
       } else {
           hotbar[index - START_INDEX_HOTBAR_5] = null;
       }
    }
    
     @Override
    public InventoryType getInventoryType() {
        return InventoryType.PLAYER;
    }
}
