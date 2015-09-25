/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot;

import com.sun.java.swing.plaf.windows.WindowsBorders;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import minecraft.inventory.InventoryType;
import minecraft.inventory.Slot;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.network.MinecraftOutputStream;
import minecraftbot.packet.out.Out09HeldItemChange;
import minecraftbot.packet.out.Out0DCloseWindow;
import minecraftbot.packet.out.Out0EClickWindow;
import minecraftbot.world.Block;

// Kerzak: WILL BE REPLACED BY minecraftbot.inventory.PlayersInvetoryHandler
/**
 * Handles inventory and inventory operations.
 * @author eZ
 */
public class InventoryHandler implements IInventoryHandler, IInventoryStorage{
    private final HashMap<Block, ArrayList<byte[]>> blockInfo;
    private final Block[] slotInfo;
    private boolean busy, confirmed, stackUp;
    private byte moveHere, currentSlot, swapState, craftedMeta, craftedId, craftedCount, autocraft, autocraftCount;
    
    
    private final Out0DCloseWindow out0DCloseWindow;
    private final Out09HeldItemChange out09HeldItemChange;
    private final Out0EClickWindow out0EClickWindow;

    public  InventoryHandler(Out0DCloseWindow out0DCloseWindow, Out09HeldItemChange out09HeldItemChange,Out0EClickWindow out0EClickWindow)
    {
        slotInfo = new Block[128];
        blockInfo = new HashMap<>();
        this.out0DCloseWindow = out0DCloseWindow;
        this.out09HeldItemChange = out09HeldItemChange;
        this.out0EClickWindow = out0EClickWindow;
    }
    
    /**
     * Sets information about a slot.
     * @param slot Slot to be set.
     * @param id ID of new block.
     * @param meta Meta information of new block.
     * @param count Number of blocks added.
     */
    @Override
    public void setSlot(byte type, byte slot, int id, byte meta, byte count) {
        /*
        System.out.println("SET SLOT DNU "+slot);
        System.out.println("ID:    "+Util.idMap.get(id));
        System.out.println("META:  "+meta);
        System.out.println("COUNT: "+count);*/
        if(type!=0)
            return;
        if(id<0)
        {
            try {
                if(blockInfo.containsKey(slotInfo[slot]))
            {
                for (Iterator it = blockInfo.get(slotInfo[slot]).iterator(); it.hasNext();) {
                    byte[] info = (byte[]) it.next();
                    if(info[0]==slot)
                    {
                        it.remove();
                        break;
                    }
                }
                if(blockInfo.get(slotInfo[slot]).isEmpty())
                    blockInfo.remove(slotInfo[slot]);
            }
            slotInfo[slot] = null;
            } catch (Exception e) {
            }
            
            
            
                        
        }
        else
        {
            Block newBlock = new Block(id, meta);
            if(id==17&&!busy)
            {
                autocraft = (count);
                autocraftCount = (byte)(count*4);
            }
            if(slotInfo[slot]!=null)
            {
                if(!slotInfo[slot].equals(newBlock))
                {
                    for (Iterator it = blockInfo.get(slotInfo[slot]).iterator(); it.hasNext();) {
                        byte[] info = (byte[]) it.next();
                        if(info[0]==slot)
                        {
                            it.remove();
                            break;
                        }
                    }
                    if(blockInfo.get(slotInfo[slot]).isEmpty())
                        blockInfo.remove(slotInfo[slot]);                
                }
            }
            if(blockInfo.containsKey(newBlock))
            {
                for(byte[] info : blockInfo.get(newBlock))
                {
                    if(info[0]==slot)
                    {
                        info[1]=count;
                        return;
                    }
                }
                blockInfo.get(newBlock).add(new byte[]{slot,count});
                if(slotInfo[slot]!=null)
                {
                    Block oldBlock = slotInfo[slot];
                    if(blockInfo.containsKey(oldBlock))
                    {
                        for(byte[] info : blockInfo.get(oldBlock))
                        {
                            if(info[0]==slot)
                            {
                                blockInfo.get(oldBlock).remove(info);
                                break;
                            }
                        }
                    }
                }
            }
            else{
                blockInfo.put(newBlock, new ArrayList<byte[]>());
                blockInfo.get(newBlock).add(new byte[]{slot,count});
            }
            slotInfo[slot]=newBlock;
            if(autocraft>0)
            {
                //System.out.println("AUTOCRAFT "+autocraft);
                if(busy)
                {
                    Util.logger.log(LogElement.Inventory, LogLevel.Error, "Inventory handler busy, cannot craft.");
                }
                else
                {
                    busy = true;
                    currentSlot = slot;
                    swapState = -1;
                    clickWindow(currentSlot);
                }
            }
        }
    }

    /**
     * HashMap that provides list of byte array for each block in inventory.
     * Byte arrays' length is always 2.F
     * First byte points to the slot where the block is stored.
     * Second byte indicates how many block are in the slot. 
     * @return 
     */
    public HashMap<Block, ArrayList<byte[]>> getBlockInfo() {
        return blockInfo;
    }

    /**
     * @return Array in which n-nth element is block that is stored on n-th slot in inventory.
     */
    public Block[] getSlotInfo() {
        return slotInfo;
    }
    
    /**
     * Requests inventory handler to put block in main (hand) slot.
     * @param id Id of block to put in hand.
     * @return Number of request. -1 if handler is busy, -2 if theres no such block in inventory.
     */
    @Override
    public short requestHeldChange(Id id)
    {
        Util.logger.log(LogElement.Inventory, LogLevel.Debug, "Requesting change to "+id);

        
        if(busy)
            return -1;
        
        
        
        for(byte i = 9;i<=44;i++)
        {
            if(slotInfo[i]!=null&&slotInfo[i].getId()==id)
            {
                currentSlot = i;
                break;
            }
            if(i==44)
                return -2;
        }
        busy = true;
        swapState = -1;
        setFirstSlotHeld();
        clickWindow(currentSlot);
        return (short) out0EClickWindow.getActionNumber();
    }
    
    @Override
    public short requestHeldChange(ItemType type)
    {
        if(slotInfo[36]!=null&&slotInfo[36].getId().getType().equals(type))
            return -3;
        
        Id desired = Id.NONE;
        for(byte i = 9;i<=44;i++)
        {
            if(slotInfo[i]!=null&&slotInfo[i].getId().getType()==type)
            {
                desired = slotInfo[i].getId();
                break;
            }
            if(i==44)
                return -2;
        }
        return requestHeldChange(desired);
    }
    
    /**
     * Simulates clicking on inventory window.
     * Forces clicked slot to appear as empty.
     * @param slot
     * @param forceEmptySlot 
     */
    private void clickWindow(byte slot, boolean forceEmptySlot)
    {
        //System.out.println("CLICK ON "+slot);
        try
        {
            //actionNumber++;
            byte[] slotData = null;        
            if(autocraft>0&&slot==0)
            {
                slotData = writeCraftedItemData(currentSlot);
            }
            else if(forceEmptySlot)
                slotData = writeSlotData((byte)-1);
            else
                slotData = writeSlotData(slot);
            out0EClickWindow.sendMessage((byte)0,(short)slot,(byte)0,(byte)0,slotData);
        
        } catch (IOException ex) {
            System.err.println("IO Exception while requesting window click.");
            System.exit(0);
        }
    }


    /**
     * Simulates clicking on inventory window.
     * @param slot
     */
    private void clickWindow(byte slot)
    {
        clickWindow(slot, false);
    }
    
    /**
     * Writes information about slot to output stream.
     * @param slot Used slot.
     * @throws IOException 
     */
    private byte[] writeSlotData(byte slot)throws IOException
    {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(7);
        DataOutputStream out = new DataOutputStream(bOut);
        if(slot>=0)
        {
            Block blockData = slotInfo[slot];
            byte count=1;

            if(slotInfo[slot]!=null)
            {
                for (byte[] info : blockInfo.get(blockData)) {
                    if(info[0]==slot)
                        count = info[1];
                }
                //out.writeByte((byte)0);
                out.writeShort((short)blockData.getId().getValue());
                out.writeByte(count);
                out.writeByte((byte)0);
                out.writeByte(blockData.getMeta());
            }
        }
        out.writeByte((byte)0xff);
        out.writeByte((byte)0xff);
        return bOut.toByteArray();
    }
    
    /**
     * Writes information about item that will be crafted to output stream. 
     * @param sourceSlot Slot used as source material.
     * @throws IOException 
     */
    private byte[] writeCraftedItemData(byte sourceSlot) throws IOException
    {
        Block blockData = slotInfo[sourceSlot];
        craftedCount=1;
        craftedId=blockData.getId().getByteValue();
        craftedMeta=blockData.getMeta();

        if(blockData.getId().getByteValue()==17)
        {
            craftedCount = 4;
            craftedId = 5;
        }
        //System.out.println("CRAFTED COUNT IS "+craftedCount);
        
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(7);
        DataOutputStream out = new DataOutputStream(bOut);
        
        out.writeByte((byte)0);
        out.writeByte(craftedId);
        out.writeByte(craftedCount);
        out.writeByte((byte)0);
        out.writeByte(craftedMeta);
        out.writeByte((byte)0xff);
        out.writeByte((byte)0xff);
    
        return bOut.toByteArray();
    }

    /**
     * Swaps main and current slot in inventory.
     */
    private void changeHeld()
    {
        
        Block temp = slotInfo[currentSlot];
        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Swapping slot "+currentSlot);

        
        for(byte[] info: blockInfo.get(temp))
        {
            if(info[0]==currentSlot)
            {
                info[0] = 36;
                break;
            }
        }
        
        if(blockInfo.get(slotInfo[36])!=null)
        {
            for(byte[] info: blockInfo.get(slotInfo[36]))
            {
                if(info[0]==36)
                {
                    info[0] = currentSlot;
                    break;
                }
            }
        }
        
        slotInfo[currentSlot] = slotInfo[36];
        slotInfo[36] = temp;
        
        
    }
    
    /**
     * Called when server confirms inventory transaction.
     * @param actionNumber Number of action confirmed.
     */
    @Override
    public void confirmTransaction(short actionNumber) {
        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "State is "+swapState);
        //System.out.println("MY ACTION NUMBER IS "+this.actionNumber);
        //System.out.println("SENT ACTION NUMBER IS "+actionNumber);
        if(actionNumber==(short) out0EClickWindow.getActionNumber())
        {
            switch(swapState)
            {
                case -1:
                    if(autocraft>0)
                        clickWindow((byte)1);
                    else
                        clickWindow((byte)36);
                    break;
                case 0:
                    if(autocraft>0)
                    {
                        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Autocraft state "+autocraft);
                        clickWindow((byte)0);
                        if(autocraft>1)
                        {
                            autocraft--;
                            swapState--;
                            Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Lowering swap state");
                        }
                    }
                    else
                        clickWindow(currentSlot, true);
                    break;
                case 1:
                    if(autocraft>0)
                    {
                        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "State 1 - autocrafting");
                        stackUp = false;
                        Block newBlock = new Block(craftedId, craftedMeta);
                        if(blockInfo.containsKey(newBlock))
                        {
                            moveHere = -1;
                            Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Logs already in inventory");
                            for(byte[] info : blockInfo.get(newBlock))
                            {
                                if(info[1]+autocraftCount<=64)
                                {
                                    Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Adding logs to"+info[0]);
                                    moveHere = info[0];
                                    autocraftCount = (byte)(info[1]+autocraftCount);
                                    stackUp = true;
                                    clickWindow(moveHere);
                                    break;
                                }
                            }
                            if(moveHere==-1)
                            {
                                Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Nowhere to put the logs");
                                clickWindow(currentSlot, true);                                
                            }
                        }
                        else
                        {
                            Util.logger.log(LogElement.Inventory, LogLevel.Trace, "No logs in inventory");
                            clickWindow(currentSlot, true);
                        }
                    }
                    else
                    {
                        confirmed = true;
                        busy = false;
                        out0DCloseWindow.sendMessage((byte)0);
                        swapState=6;
                        changeHeld();
                    }
                    break;
                case 2:
                        confirmed = true;
                        busy = false;
                        out0DCloseWindow.sendMessage((byte)0);
                        autocraft = 0;
                        busy = false;
                        if(stackUp)
                        {
                            setSlot((byte)0,currentSlot,(byte)-1,(byte)0,(byte)0);
                            setSlot((byte)0,moveHere,craftedId,craftedMeta,autocraftCount);
                        }
                        else
                            setSlot((byte)0,currentSlot,craftedId,craftedMeta,autocraftCount);
                        craftedCount = 0;
                    break;
            }
            swapState++;
        }
    }
    
    /**
     * Checks whether requested action was confirmed.
     * Can return true only once.
     * @return 
     */
    @Override
    public boolean isConfirmed() {
        if(confirmed)
            busy = false;
        else
            return false;
        confirmed = false;
        return true;
    }

    public boolean isBusy() {
        return busy;
    }

    /**
     * Selects first action bar slot as active.
     */
    public void setFirstSlotHeld() {
        out09HeldItemChange.sendMessage(0);
    }

    public void logInventory() {
        for (Map.Entry<Block, ArrayList<byte[]>> entry : blockInfo.entrySet()) {
            Block block = entry.getKey();
            ArrayList<byte[]> arrayList = entry.getValue();
            Util.logger.log(LogElement.Inventory, LogLevel.Error, "---------------------");
            Util.logger.log(LogElement.Inventory, LogLevel.Error, block.toString());
            for (byte[] bs : arrayList) {
                Util.logger.log(LogElement.Inventory, LogLevel.Error, bs[0]+" - "+bs[1]);
            }

        } 
    }

    @Override
    public byte getWindowId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] getSlotData(int slot) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Slot getSlot(short slot) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public short getIndex(Id id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getStartIndexMainInventory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getStartIndexHotbar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSlotEmpty(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
     @Override
    public InventoryType getInventoryType() {
        return InventoryType.PLAYER;
    }
}
