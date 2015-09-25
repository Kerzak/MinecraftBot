/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraft.inventory;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.logging.Level;
import java.util.logging.Logger;
import minecraft.exception.ErrorMessage;
import minecraft.exception.MinecraftException;
import minecraftbot.IInventoryStorage;
import minecraftbot.Id;
import minecraftbot.network.MinecraftOutputStream;
import minecraftbot.packet.in.In2DOpenWindow;
import minecraftbot.packet.out.Out02UseEntity;

/**
 *
 * @author Kerzak
 */
public class ChestHandler implements IInventoryStorage {
    
    private Slot[] chestItems;//0-26
    private Slot[] mainInventory;//27-53
    private Slot[] hotbar;//54-62
    
    public final int START_INDEX_CHEST_ITEMS_1 = 0;
    public final int END_INDEX_CHEST_ITEMS_1 = 26;
    public final int START_INDEX_MAIN_INVENTORY_2 = 27;
    public final int END_INDEX_MAIN_INVENTORY_2 = 53;
    public final int START_INDEX_HOTBAR_3 = 54;
    public final int END_INDEX_HOTBAR_3 = 62;
    
    private byte windowID;
    String windowTitle;
    
    MinecraftOutputStream out;
    
            
    public ChestHandler(byte windowID, String windowTitle, int numberOfSlots, Slot[] mainInventory, Slot[] hotbar) {
        chestItems = new Slot[27];
        this.windowID = windowID;
        this.windowTitle = windowTitle;     
        this.mainInventory = mainInventory;
        this.hotbar = hotbar;
    }
    
    public Slot[] getChestItems() {
        return this.chestItems;
    }
    
    public boolean isItemInChest(Id id) {
        for (int i = 0; i < chestItems.length; i++) {
            if(chestItems[i] != null) {
                System.out.println("NOT NULL");
                Slot slot = chestItems[i];
                System.out.println("SLOT OK");
                chestItems[i].getId();
                System.out.println("ID OK");
            }
            if (chestItems[i] != null && chestItems[i].getId() == id) {
                System.out.println("FOUND REQUESTED ITEM:" + chestItems[i].getId());
                return true;
            } else {
                System.out.println("NOT EQUAL ID OR NULL");
            }
        }
        return false;
    }
    
    public void takeItemFromChest(Id id) {
        
    }
    
    @Override
    public void setSlot(byte type, byte slot, int id, byte meta, byte count) {
        System.out.println("SETTING SLOT type: " + type + " slot: " + slot + " id: " + id + " meta: " + meta + " count: " + count);
        if (slot != -1) {
            if(slot <= END_INDEX_CHEST_ITEMS_1) 
                chestItems[slot] = new Slot(slot, id, count);
            else if (slot <= END_INDEX_MAIN_INVENTORY_2) 
                mainInventory[slot - START_INDEX_MAIN_INVENTORY_2] = new Slot(slot, id, count);
            else hotbar[slot - START_INDEX_HOTBAR_3] = new Slot(slot, id, count);
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
        Slot wantedSlot = null;
        if(slot <= END_INDEX_CHEST_ITEMS_1) 
            wantedSlot = chestItems[slot];
        else if (slot <= END_INDEX_MAIN_INVENTORY_2) 
            wantedSlot = mainInventory[slot - START_INDEX_MAIN_INVENTORY_2];
        else wantedSlot = hotbar[slot - START_INDEX_HOTBAR_3];
        return wantedSlot.getSlotData();
    }

    @Override
    public Slot getSlot(short slot) {
        if (slot != -1) {
            if(slot <= END_INDEX_CHEST_ITEMS_1) 
                return chestItems[slot];
            else if (slot <= END_INDEX_MAIN_INVENTORY_2) 
                return mainInventory[slot - START_INDEX_MAIN_INVENTORY_2];
            else return hotbar[slot - START_INDEX_HOTBAR_3];
        }
        return null;
    }

    @Override
    public short getIndex(Id id) {
        for (int i = 0; i < chestItems.length; i++) {
            if(chestItems[i] != null && chestItems[i].getId().equals(id)) return (short)(i);
        }
        for (int i = 0; i < mainInventory.length; i++) {
            if(mainInventory[i] != null && mainInventory[i].getId().equals(id)) return (short)(i + START_INDEX_MAIN_INVENTORY_2);
        }
        for (int i = 0; i < hotbar.length; i++) {
            if(hotbar[i] != null && hotbar[i].getId().equals(id)) return (short)(i + START_INDEX_HOTBAR_3);
        }
        return -1;
    }
    
    private void sleep(long millis) {
        try {
            Thread.currentThread().sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChestHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getStartIndexMainInventory() {
        return START_INDEX_MAIN_INVENTORY_2;
    }

    @Override
    public int getStartIndexHotbar() {
        return START_INDEX_HOTBAR_3;
    }
    
    public int getMyInventoryIndex(Id id) {
        for (int i = 0; i < mainInventory.length; i++) {
            if(mainInventory[i] != null && mainInventory[i].getId() != null && mainInventory[i].getId().getValue() == id.getValue()) return i + START_INDEX_MAIN_INVENTORY_2;
        }
        for (int i = 0; i < hotbar.length; i++) {
            if(hotbar[i] != null && hotbar[i].getId() != null && hotbar[i].getId().getValue() == id.getValue()) return i + START_INDEX_HOTBAR_3;
        }
        return -1;
    }
    
    public short getFirstEmptyIndex(Id id, int count) {
//        for (short i = 0; i < chestItems.length; i++) {
//            Slot ith = chestItems[i];
//            if(ith != null && ith.getId() != null && ith.getId().getValue() == id.getValue() && (64 - ith.getCount()) >= count) 
//                return (short)(i);
//        }
        
         for (short i = 0; i < chestItems.length; i++) {
            if(chestItems[i] == null) return (short)(i);
        }
        
        return -1;
        
        //TODO:throw excep.
    }
    
    public Slot[] getChestItemsCopy() {
        Slot[] chestItemsCopy = new Slot[chestItems.length];
        for (int i = 0; i < chestItemsCopy.length; i++) {
            if(chestItems[i] != null) chestItemsCopy[i] = chestItems[i].getCopy();
            else chestItems[i] = null;
        }
        return chestItemsCopy;
    }

    @Override
    public void setSlotEmpty(int index) {
        if(index <= END_INDEX_CHEST_ITEMS_1) 
            chestItems[index] = null;
        else if (index <= END_INDEX_MAIN_INVENTORY_2) 
            mainInventory[index - START_INDEX_MAIN_INVENTORY_2] = null;
        else hotbar[index - START_INDEX_HOTBAR_3] = null;
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.CHEST;
    }
}
