/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraft.inventory;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import minecraft.exception.ErrorMessage;
import minecraft.exception.MinecraftException;
import minecraftbot.ChatHandler;
import minecraftbot.GameInfo;
import minecraftbot.IInventoryHandler;
import minecraftbot.IInventoryStorage;
import minecraftbot.Id;
import minecraftbot.InventoryHandler;
import minecraftbot.ItemType;
import minecraftbot.Locomotion;
import minecraftbot.entity.Entity;
import minecraftbot.network.MinecraftOutputStream;
import minecraftbot.packet.in.In32ConfirmTransaction;
import minecraftbot.packet.out.Out02UseEntity;
import minecraftbot.packet.out.Out09HeldItemChange;
import minecraftbot.packet.out.Out0DCloseWindow;
import minecraftbot.packet.out.Out0EClickWindow;
import minecraftbot.packet.out.Out0EClickWindowInfo;

/**
 *
 * @author Kerzak
 */
public class InventoryManager implements IInventoryHandler {
    
    private boolean busy, confirmed, stackUp;
    private byte moveHere, currentSlot, swapState, craftedMeta, craftedId, craftedCount, autocraft, autocraftCount;
    
    private int windowOpened = -1;
    
    private Slot[] mainInventoryItems;
    private Slot[] hotbarItems;
    
    private Out0EClickWindow out0EClickWindow;
    private Out0DCloseWindow out0DCloseWindow;
    private Out09HeldItemChange out09HeldItemChange;
    private In32ConfirmTransaction in32ConfirmTransaction;
    
    private IInventoryStorage currentInventory;
    private PlayersInventoryHandler playersInventoryHandler;
    private InventoryHandler inventoryHandler;
    
    MinecraftOutputStream out;
    
    public InventoryManager(MinecraftOutputStream outputStream, InventoryHandler inventoryHandler, Out0EClickWindow out0EClickWindow, Out0DCloseWindow out0DCloseWindow, Out09HeldItemChange out09HeldItemChange, In32ConfirmTransaction in32ConfirmTransaction) {
        this.out = outputStream;
        mainInventoryItems = new Slot[27];
        hotbarItems = new Slot[9];
        this.playersInventoryHandler = new PlayersInventoryHandler(mainInventoryItems, hotbarItems);
        currentInventory = playersInventoryHandler;
        this.inventoryHandler = inventoryHandler;
        this.out0EClickWindow = out0EClickWindow;
        this.out0DCloseWindow = out0DCloseWindow;
        this.out09HeldItemChange = out09HeldItemChange; 
        this.in32ConfirmTransaction = in32ConfirmTransaction;
    }
    
    public void requestInventoryOpen(Location loc, Locomotion move) {
        //System.out.println("Opening chest");
        move.lookAt(loc);
        try {
            out.write(8);
            out.writeInt((int)(loc.getX()));
            out.write((int)(loc.getY()));
            out.writeInt((int)(loc.getZ()));
           
            out.write(1);

            out.write(255);
            out.write(255);
            
            out.write(1);
            out.write(1);
            out.write(1);
            out.closePacket();
           // System.out.println("OPEN CHEST REQUEST SENT");
            
        } catch (Exception e) {
            System.err.println("Error while using entity.");
        }
        //System.out.println("CHEST opened");
    }
    
    public void openWindow(byte windowID, byte windowType, String windowTitle, int numberOfSlots) {
        switch (windowType) {
            case 0: {
                //CHEST
                currentInventory = new ChestHandler(windowID, windowTitle, numberOfSlots,mainInventoryItems,hotbarItems);
            } break;
            case 1: {
                //WORKBENCH
                currentInventory = new WorkbenchHandler(windowID, windowTitle, numberOfSlots, mainInventoryItems, hotbarItems);
            }
        }
    }
    
    public int getWindowOpened() {
        return this.windowOpened;
    }
    
    public IInventoryStorage getCurrentInventory() {
        return this.currentInventory;
    }
    
    public InventoryHandler getInventoryHandler() {
        return this.inventoryHandler;
    }
    
    public boolean isMyInventoryEmpty() {
        for (int i = currentInventory.getStartIndexMainInventory(); i < currentInventory.getStartIndexMainInventory() + 36; i++) {
            if (currentInventory.getSlot((short)i) != null) return false;
        }
        return true;
    }
    
    public void showHotbar(ChatHandler chat) {
        chat.sendMessage("---HOTBAR---");
        String hotbar = "";
        String toBeAdded = "";
        sleep(1000);
         for (int i = 0; i < hotbarItems.length; i++) {
            //sleep(1000);
            if (hotbarItems[i] == null) toBeAdded = (" **" + i + ": EMPTY** ");
            else toBeAdded=  " **" + i + " ITEM: " + hotbarItems[i].getId().name() + " COUNT: " + hotbarItems[i].getCount() + "** ";
            if (hotbar.length() + toBeAdded.length() > 99) {
                chat.sendMessage(hotbar);
                hotbar = toBeAdded;
                sleep(1000);
            } else {
                hotbar += toBeAdded;
            }
        }
        
        if (!hotbar.equals("")) {
            chat.sendMessage(hotbar);
        }
    }
    
    public void showInventory(ChatHandler chat) {
        chat.sendMessage("---MAIN INVENTORY---");
        String inventory = "";
        String toBeAdded = "";
        sleep(1000);
        for (int i = 0; i < mainInventoryItems.length; i++) {
            //sleep(1000);
            if (mainInventoryItems[i] == null) toBeAdded = (" **" + i + ": EMPTY** ");
            else toBeAdded=  " **" + i + " ITEM: " + mainInventoryItems[i].getId().name() + " COUNT: " + mainInventoryItems[i].getCount() + "** ";
            if (inventory.length() + toBeAdded.length() > 99) {
                chat.sendMessage(inventory);
                inventory = toBeAdded;
                sleep(1000);
            } else {
                inventory += toBeAdded;
            }
        }
        
        if (!inventory.equals("")) {
            chat.sendMessage(inventory);
            sleep(1000);
        }
        
        
        showHotbar(chat);
    }
    
    public boolean isItemInChest(Id id) throws MinecraftException {
        System.out.println(currentInventory.getClass() + "    " + ChestHandler.class);
        if (!currentInventory.getClass().equals(ChestHandler.class)) throw new MinecraftException(ErrorMessage.M02);
        return ((ChestHandler)currentInventory).isItemInChest(id);
    }
    
    public boolean getItemFromChest(Id id) throws MinecraftException {
        if (id.getByteValue() == Id.NONE.getByteValue()) return true;
        if (!currentInventory.getClass().equals(ChestHandler.class)) {
            System.out.println("CHEST NOT OPEN");
            return false;
        } else {
            System.out.println("CHEST OPEN");
        }
        if (!isItemInChest(id)) throw new MinecraftException(ErrorMessage.M03);
        short itemIndex = currentInventory.getIndex(id);
        short emptyIndex = getFirstEmptyIndex();
        moveItem(itemIndex, emptyIndex,1);
        return true;
    }
    
    public boolean storeItemToWorkbench(int index, Id id) {
        if (id.getByteValue() == Id.NONE.getByteValue()) return true;
        if (!currentInventory.getClass().equals(WorkbenchHandler.class)) {
            System.out.println("WB NOT OPEN");
            return false;
        } else {
            System.out.println("WB OPEN");
        }
        int itemIndex = ((WorkbenchHandler)currentInventory).getMyInventoryIndex(id);
        moveItem((short)itemIndex, (short) (index + 1),1);
        return true;
    }
    
    public boolean storeItemToChest(Id id, int count) {
        if (id.getByteValue() == Id.NONE.getByteValue()) return true;
        if (!currentInventory.getClass().equals(ChestHandler.class)) {
            System.out.println("Chest NOT OPEN");
            return false;
        } else {
            System.out.println("Chest OPEN");
        }
        int itemIndex = ((ChestHandler)currentInventory).getMyInventoryIndex(id);
        short chestAvailableIndex = ((ChestHandler)currentInventory).getFirstEmptyIndex(id, count);
        moveItem((short)itemIndex,  chestAvailableIndex,count);
        return true;
    }
    
    public void takeCraftingResult(Id id, int craftedCount) throws MinecraftException {
        //TODO: check curr. inventory type
        
        currentInventory.setSlot((byte)0,(byte)0, id.getValue(), (byte)0, (byte)craftedCount);
        Id idResult = currentInventory.getSlot((short)0).getId();
        short availableIndex = getFirstEmptyIndex();
        moveItem((short)0, availableIndex, craftedCount);
        
    }
    
    public void moveItem(short startIndex, short endIndex, int count) {
        System.out.println("MOVING ITEM, start index: " + startIndex + " endIndex: " + endIndex);
        Slot oldStartIndexSlot = currentInventory.getSlot(startIndex);
        Slot oldEndIndexSlot = currentInventory.getSlot(endIndex);
        
        leftClickWindow(startIndex);        
        for (int i = 0; i < count; i++) {
            rightClickWindow(endIndex);
            Slot slotEndIndex = currentInventory.getSlot(endIndex);
            if(slotEndIndex == null){
                currentInventory.setSlot((byte)0, (byte) endIndex, oldStartIndexSlot.getId().getValue(), (byte)0, (byte)1);
            } else {
                currentInventory.getSlot(endIndex).incCountBy(1);
            }
        }
        leftClickWindowForceEmpty(startIndex);
               
        if (currentInventory.getSlot(startIndex).getCount() - count > 0)currentInventory.getSlot(startIndex).decCountBy(count);
        else currentInventory.setSlotEmpty(startIndex);
        
        
//        if (oldStartIndexSlot != null) {
//            Slot newStartIndexSlot = new Slot(startIndex, oldStartIndexSlot.getId().getValue(), (byte)oldStartIndexSlot.getCount());
//            refreshSlotInMyInventory(startIndex, newStartIndexSlot);
//        }
//        if(oldEndIndexSlot != null) {
//            Slot newEndIndexSlot = new Slot(endIndex, oldEndIndexSlot.getId().getValue(), (byte)(oldEndIndexSlot.getCount() + 1));
//            refreshSlotInMyInventory(endIndex, newEndIndexSlot);
//        } else {
//            Slot newEndIndexSlot = new Slot(endIndex, oldStartIndexSlot.getId().getValue(), (byte)1);
//            refreshSlotInMyInventory(endIndex, newEndIndexSlot);
//        }
        
        
    }
    
//    /**
//     * Only in chest
//     * 
//     * @param id
//     * @param count
//     * @return
//     * @throws MinecraftException 
//     */
//    public short getFirstAvailableIndex(Id id, int count) throws MinecraftException {
//        
//        //try to add to existing stack first
//        for (short i = 0; i < mainInventoryItems.length; i++) {
//            Slot ith = mainInventoryItems[i];
//            if(ith != null) System.out.println("id: " + ith.getId().getValue() + "free space" + (64 - ith.getCount()) );
//            if(ith != null && ith.getId().getValue() == id.getValue() && (64 - ith.getCount()) >= count) 
//                return (short)(i + currentInventory.getStartIndexMainInventory());
//        }
//        for (short i = 0; i < hotbarItems.length; i++) {
//            Slot ith = hotbarItems[i];
//            if(ith != null && ith.getId() != null && ith.getId().getValue() == id.getValue() && (64 - ith.getCount()) >= count) 
//                return (short)(i + currentInventory.getStartIndexHotbar());
//        }
//        //if not succeeded get empty instead
//        return getFirstEmptyIndex();
//    }
    
    /**
     * 
     * 
     * @return first empty index in personal inventory (main inventory and hotbar)
     * @throws MinecraftException 
     */
    public short getFirstEmptyIndex() throws MinecraftException {
        for (short i = 0; i < mainInventoryItems.length; i++) {
            if(mainInventoryItems[i] == null) return (short)(i + currentInventory.getStartIndexMainInventory());
        }
        for (short i = 0; i < hotbarItems.length; i++) {
            if(hotbarItems[i] == null) return (short)(i + currentInventory.getStartIndexHotbar());
        }
        throw  new MinecraftException(ErrorMessage.M04);
    }
    
    /**
     * 
     * @return first empty index in personal inventory (main inventory and hotbar)
     * @throws MinecraftException if inventory is empty
     */
    public short getFirstNotEmptyIndex() throws MinecraftException {
        for (short i = 0; i < mainInventoryItems.length; i++) {
            if(mainInventoryItems[i] != null) return (short)(i + currentInventory.getStartIndexMainInventory());
        }
        for (short i = 0; i < hotbarItems.length; i++) {
            if(hotbarItems[i] != null) return (short)(i + currentInventory.getStartIndexHotbar());
        }
        throw  new MinecraftException(ErrorMessage.M08);
    }
    
    private void startLeftMouseDrag() {
        System.out.println("START LEFT MOUSE DRAG");
        out0EClickWindow.sendMessageForceEmpty(currentInventory.getWindowId(), (short)-999,(byte) 0, (byte) 5);
    }
    
    private void endLeftMouseDrag() {
         System.out.println("END LEFT MOUSE DRAG");
        out0EClickWindow.sendMessageForceEmpty(currentInventory.getWindowId(), (short)-999,(byte) 2, (byte) 5);
    }
    
    private void leftClickWindow(short slot) {
        Slot slotData = currentInventory.getSlot(slot);
        //System.out.println("WINID: " + currentInventory.getWindowId() + " SLOTDATA: ID: " + slotData.getId());
        if (slotData != null) {
            System.out.println("NOT EMPTY LEFT CLICK");
            out0EClickWindow.sendMessage((byte) (currentInventory.getWindowId()), slot,(byte) 0, (byte) 0, slotData);
        }
        else {
            System.out.println("EMPTY LEFT CLICK");
            out0EClickWindow.sendMessageForceEmpty((byte) (currentInventory.getWindowId()), slot, (byte)0, (byte)0);
        }
        //in32ConfirmTransaction.newClick(new Out0EClickWindowInfo(currentInventory.getWindowId(), slot, slotData, out0EClickWindow.getActionNumber()));
    }
    
    public void rightClickWindow(short slot) {
        Slot slotData = currentInventory.getSlot(slot);
        //System.out.println("WINID: " + currentInventory.getWindowId() + " SLOTDATA: ID: " + slotData.getId());
        if (slotData != null) {
            System.out.println("NOT EMPTY RIGHT CLICK");
            out0EClickWindow.sendMessage(currentInventory.getWindowId(), slot,(byte) 1, (byte) 0, slotData);
        }
        else {
            System.out.println("EMPTY RIGHT CLICK");
            out0EClickWindow.sendMessageForceEmpty(currentInventory.getWindowId(), slot, (byte)1, (byte)0);
        }
        //in32ConfirmTransaction.newClick(new Out0EClickWindowInfo(currentInventory.getWindowId(), slot, slotData, out0EClickWindow.getActionNumber()));
    }
    
    public void shiftClick(short slot) {
         Slot slotData = currentInventory.getSlot(slot);
         if (slotData != null) {
             System.out.println("SHIFT CLICK");
             out0EClickWindow.sendMessage(currentInventory.getWindowId(), slot, (byte)1, (byte)1, slotData);
         }
    }
    
    private void leftClickWindowForceEmpty(short slot) {
        System.out.println("FORCED EMPTY LEFT CLICK");
        //Slot slotData = currentInventory.getSlot(slot);
        //System.out.println("WINID: " + currentInventory.getWindowId() + " SLOTDATA: ID: " + slotData.getId());
        out0EClickWindow.sendMessageForceEmpty(currentInventory.getWindowId(), slot,(byte) 0, (byte) 0);
        //in32ConfirmTransaction.newClick(new Out0EClickWindowInfo(currentInventory.getWindowId(), slot, slotData, out0EClickWindow.getActionNumber()));
    }
       
    @Override
    public short requestHeldChange(Id id) {
        closeCurrentInventory();
        currentInventory = playersInventoryHandler;
        short slot = -1;
        for (short i = 0; i < mainInventoryItems.length; i++) {
            if(mainInventoryItems[i] != null && mainInventoryItems[i].getId() == id) slot = (short) (playersInventoryHandler.START_INDEX_MAIN_INVENTORY_4 + i);
        }
        for (short i = 0; i < hotbarItems.length; i++) {
            if(hotbarItems[i] != null && hotbarItems[i].getId() == id) slot = (short)(playersInventoryHandler.START_INDEX_HOTBAR_5 + i);
        }
        if(slot == -1) try {
            throw new MinecraftException(ErrorMessage.M05);
        } catch (MinecraftException ex) {
            System.out.println(ex.getMessage());
        }
        out09HeldItemChange.sendMessage(0);
        leftClickWindow(slot);
        //sleep(1000);
        System.out.println("FIRST CLICK");
        leftClickWindow((short) playersInventoryHandler.START_INDEX_HOTBAR_5);
        //sleep(1000);
        System.out.println("SECOND CLICK");
        leftClickWindowForceEmpty(slot);
        System.out.println("THIRD CLICK");
        Slot temp = hotbarItems[0];
        if(slot < playersInventoryHandler.START_INDEX_HOTBAR_5) {
            hotbarItems[0] = mainInventoryItems[slot - playersInventoryHandler.START_INDEX_MAIN_INVENTORY_4];
            mainInventoryItems[slot - playersInventoryHandler.START_INDEX_MAIN_INVENTORY_4] = temp;
        } else {
             hotbarItems[0] = hotbarItems[slot - playersInventoryHandler.START_INDEX_HOTBAR_5];
             hotbarItems[slot - playersInventoryHandler.START_INDEX_HOTBAR_5] = temp;
        }
        return 0;
    }
    
    
    
    public void closeCurrentInventory() {
        if (currentInventory != null) {
            out0DCloseWindow.sendMessage(currentInventory.getWindowId());
            currentInventory = null;
        }
    }
    
    public void refreshSlotInMyInventory(int index, Slot slotData) {
        int myIndex = index - currentInventory.getStartIndexMainInventory();
        if (myIndex > -1 && myIndex < 27) {
            System.out.println("REFRESH INDEX MAIN INVENTORY: " + (index - currentInventory.getStartIndexMainInventory()));
            mainInventoryItems[index - currentInventory.getStartIndexMainInventory()] = slotData;
        } else {
            myIndex = index - currentInventory.getStartIndexHotbar();
            if (myIndex > -1 && myIndex < 9) {
                System.out.println("REFRESH INDEX HOTBAR: " + (index - currentInventory.getStartIndexHotbar()));
                hotbarItems[index - currentInventory.getStartIndexHotbar()] = slotData;
            }
        }
    }
    
    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(InventoryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Drop each item in my inventory and hotbar.
     */
    public void dropInventory() {
        for (int i = currentInventory.getStartIndexMainInventory(); i < currentInventory.getStartIndexMainInventory() + 36; i++) {
            dropItemAt(i);
        }
    }
    
    public void dropItemAt(int index) {
        Slot slot = currentInventory.getSlot((short)index);
        if(slot != null) {
            out0EClickWindow.sendMessageForceEmpty(currentInventory.getWindowId(), (short)index, (byte)1, (byte)4);
            currentInventory.setSlotEmpty(index);
        } 
    }
    
    public Slot[] getMyInventoryCopy() {
        Slot[] result = new Slot[36];
        for (int i = 0; i < mainInventoryItems.length; i++) {
            if (mainInventoryItems[i] != null) result[i] = mainInventoryItems[i].getCopy();
        }
        for (int i = 0; i < hotbarItems.length; i++) {
            if (hotbarItems[i] != null) result[i] = hotbarItems[i].getCopy();
        }
        return result;
    }

    @Override
    public short requestHeldChange(ItemType type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isConfirmed() {
        return true;
    }

}
