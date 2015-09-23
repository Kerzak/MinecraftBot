

package minecraftbot;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import minecraftbot.build.BlockPlan;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.packet.out.Out09HeldItemChange;
import minecraftbot.packet.out.Out0DCloseWindow;
import minecraftbot.packet.out.Out0EClickWindow;
import minecraftbot.world.Block;

/**
 * Handles inventory and inventory operations.
 * @author eZ
 */
public class InventoryHandler implements IInventoryHandler, IInventoryStorage{
    private final HashMap<Block, ArrayList<byte[]>> blockInfo;
    private final Block[] slotInfo;
    private boolean busy, stackUp, recovering, checkBuild;
    private ActionType busyReason;
    private byte moveHere, currentSlot, swapState, craftedMeta, craftedId, craftedCount, autocraft, autocraftCount;
    private short actionNumber = 1, requestActionNumber;
    private int dropStack=0, dropSlot = -1;
    private Id dropId;
    private InventoryAction currentAction;
    private BlockPlan planToCheck;
 

    private final Queue<InventoryAction> actionQueue;
    private final MinecraftBotHandler botHandler;
    private final Set<Short> confirmedActions;
    private final Out0DCloseWindow out0DCloseWindow;
    private final Out09HeldItemChange out09HeldItemChange;
    private final Out0EClickWindow out0EClickWindow;

    public  InventoryHandler(MinecraftBotHandler botHandler, Out0DCloseWindow out0DCloseWindow, Out09HeldItemChange out09HeldItemChange,Out0EClickWindow out0EClickWindow)
    {
        slotInfo = new Block[128];
        blockInfo = new HashMap<>();
        confirmedActions = new HashSet<>();
        confirmedActions.add((short)0);
        actionQueue = new LinkedList<>();
        this.botHandler = botHandler;
        this.out0DCloseWindow = out0DCloseWindow;
        this.out09HeldItemChange = out09HeldItemChange;
        this.out0EClickWindow = out0EClickWindow;
    }
    
    /**
     * Sets information about a slot.
     * @param type Type of inventory window
     * @param slot Slot to be set
     * @param id ID of new block
     * @param meta Meta information of new block
     * @param count Number of blocks added
     */
    @Override
    public void setSlot(byte type, byte slot, int id, byte meta, byte count) {
        
        checkBuild = false;
        
        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Set slot IN "+Util.idMap.get(id));

        if(type!=0)
            return;
        
        if(slot<0)
        {
            Util.logger.log(LogElement.Inventory, LogLevel.Error, "Trying to set slot "+slot);
            return;
        }

        
        
        if(id<0)
        {
            
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
                        
        }
        else
        {
            Util.logger.log(LogElement.Inventory, LogLevel.Trace, "ID not zero");
            Block newBlock = new Block(id, meta);
            if(id==17)
            {
                actionQueue.add(new AutocraftingAction(slot));
            }
            if(slotInfo[slot]!=null)
            {
                Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Slot info not null");
                if(!slotInfo[slot].equals(newBlock))
                {
                    Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Old is NOT equal new");
                    for (Iterator it = blockInfo.get(slotInfo[slot]).iterator(); it.hasNext();) {
                        byte[] info = (byte[]) it.next();
                        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Iteration "+info[0]+" "+info[1]+" need "+slot);
                        if(info[0]==slot)
                        {
                            Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Yeah");
                            it.remove();
                            break;
                        }
                        else
                            Util.logger.log(LogElement.Inventory, LogLevel.Trace, "NOPE");
                    }
                    if(blockInfo.get(slotInfo[slot]).isEmpty())
                        blockInfo.remove(slotInfo[slot]);
                    else
                        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "NOT EMPTY");
                }
                else
                    Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Old IS equal new");                    
            }
            else
                Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Slot info IS null");
            if(blockInfo.containsKey(newBlock))
            {
                Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Block info contains newblock");
                for(byte[] info : blockInfo.get(newBlock))
                {
                    if(info[0]==slot)
                    {
                        if(!recovering&&count-info[1]>0)
                        {
                            if(autocraft>0)
                                botHandler.onItemCraft(newBlock.getId(),count-info[1]);
                            else
                                botHandler.onItemPickUp(newBlock.getId(),count-info[1]);
                        }
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
                Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Block info DOES NOT contain newblock");
                if(!recovering)
                    botHandler.onItemPickUp(newBlock.getId(),count);
                blockInfo.put(newBlock, new ArrayList<byte[]>());
                blockInfo.get(newBlock).add(new byte[]{slot,count});
            }
            slotInfo[slot]=newBlock;
        }
       /* if(id>0)
            logInventory();*/
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
        Util.logger.log(LogElement.Inventory, LogLevel.Error, "Processing request to change to "+id);
        setFirstSlotHeld();
        if(slotInfo[36]!=null&&slotInfo[36].getId().equals(id))
        {
            return  (short)0;
        }
        
        if(busy)
        {
            return -1;
        }
        
        
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
        setBusy(true, ActionType.Swapping);
        swapState = -1;
        clickWindow(currentSlot);
        requestActionNumber = actionNumber;
        return actionNumber;
    }
    
    @Override
    public short requestHeldChange(ItemType type)
    {
        Util.logger.log(LogElement.Inventory, LogLevel.Error, "Processing request to change to "+type);
        setFirstSlotHeld();
        if(slotInfo[36]!=null&&slotInfo[36].getId().getType().equals(type))
        {
            return  (short)(0);
        }
        
        Id desired = Id.None;
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
     * Can force clicked slot to appear as empty.
     * @param slot
     * @param forceEmptySlot 
     */
    private void clickWindow(short slot, boolean forceEmptySlot)
    {
        try
        {
            actionNumber++;
            if(actionNumber>20)
                actionNumber = 1;
            byte[] slotData = null;        
            if(autocraft>0&&slot==0)
            {
                slotData = writeCraftedItemData(currentSlot);
            }
            else if(forceEmptySlot)
                slotData = writeSlotData((byte)-1);
            else
                slotData = writeSlotData(slot);
            out0EClickWindow.sendMessage((byte)0,(short)slot,(byte)0,actionNumber,(byte)0,slotData);
        
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
    private byte[] writeSlotData(short slot)throws IOException
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
     * Information about item that will be crafted. 
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
        if(actionNumber==this.actionNumber)
        {
            switch(swapState)
            {
                case -1:
                    if(autocraft>0)
                        clickWindow((byte)1);
                    else if(dropStack>0)
                    {
                        slotInfo[dropSlot] = null;
                        for (Iterator<Map.Entry<Block, ArrayList<byte[]>>> it = blockInfo.entrySet().iterator(); it.hasNext();) {
                            Map.Entry<Block, ArrayList<byte[]>> entry = it.next();
                            Block block = entry.getKey();
                            ArrayList<byte[]> info = entry.getValue();
                            if(block.getId().equals(dropId))
                            {
                                for (Iterator<byte[]> it2It = info.iterator(); it2It.hasNext();) {
                                    byte[] bs = it2It.next();
                                    if(bs[0]==dropSlot)
                                    {
                                        it2It.remove();
                                        break;
                                    }
                                }
                            }
                            if(info.isEmpty())
                            {
                                it.remove();
                                break;
                            }
                        }
                        out0DCloseWindow.sendMessage((byte)0);
                        drop(dropId);
                        dropStack--;
                        if(dropStack==0)
                        {
                            dropSlot = -1;
                            setBusy(false, ActionType.Dropping);
                        }
                        return;
                        
                    }
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
                    else if(dropStack >0)
                    {
                        drop(dropId);
                        dropStack--;
                        if(dropStack==0)
                        {
                            dropSlot = -1;
                            setBusy(false, ActionType.Dropping);
                        }
                        out0DCloseWindow.sendMessage((byte)0);
                        return;
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
                        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "State 1");
                        confirmedActions.add(requestActionNumber);
                        setBusy(false, ActionType.Swapping);
                        out0DCloseWindow.sendMessage((byte)0);
                        swapState=6;
                        changeHeld();
                    }
                    break;
                case 2:
                        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "State 2");
                        confirmedActions.add(requestActionNumber);
                        out0DCloseWindow.sendMessage((byte)0);
                        setBusy(false, ActionType.Autocrafting);
                        if(stackUp)
                        {
                            Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Stack up");
                            setSlot((byte)0,currentSlot,(byte)-1,(byte)0,(byte)0);
                            setSlot((byte)0,moveHere,craftedId,craftedMeta,autocraftCount);
                        }
                        else
                        {
                            Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Dont stack up");
                            setSlot((byte)0,currentSlot,craftedId,craftedMeta,autocraftCount);
                        }
                        autocraft = 0;
                        craftedCount = 0;
                    break;
            }
            swapState++;
        }
    }
    
    /**
     * @param actionNumber Action number.
     * @return Whether particular action has been succesfully completed.
     */
    @Override
    public boolean isConfirmed(short actionNumber) {
        return confirmedActions.contains(actionNumber);
    }

    /**
     * Initiates autocrafting of wooden planks.
     * @param slot Slot number of source wood.
     */
    private void autocraft(byte slot)
    {
        if(busy)
        {
            Util.logger.log(LogElement.Inventory, LogLevel.Error, "Inventory handler busy with "+busyReason +", cannot craft.");
        }
        else if(slotInfo[slot]==null||!slotInfo[slot].getId().equals(Id.Log))
        {
            Util.logger.log(LogElement.Inventory, LogLevel.Error, "Item on slot is "+slotInfo[slot] +", nothing to autocraft.");
        }
        else
        {
            byte count = 0;
            for(byte[] info : blockInfo.get(slotInfo[slot]))
            {
                if(info[0]==slot)
                    count = info[1];
            }
            autocraft = (count);
            autocraftCount = (byte)(count*4);
            setBusy(true, ActionType.Autocrafting);
            currentSlot = slot;
            swapState = -1;
            clickWindow(currentSlot);
        }
    }
    
    /**
     * Requests dropping all items with particular Id.
     * @param id Id of items to drop.
     */
    public void drop(Id id)
    {
        actionQueue.add(new DroppingAction(id));
    }
    
    /**
     * Executes dropping action of dropping items with particular id.
     * @param id Id of items to drop.
     */
    private void dropReally(Id id)
    {
        if(busy&&(dropStack == 0||id!=dropId))
        {
            Util.logger.log(LogElement.Inventory, LogLevel.Error, "Cannot drop "+id+", handler is busy with "+busyReason);
            actionQueue.add(new DroppingAction(id));
            return;
        }
        swapState = -1;
        setBusy(true, ActionType.Dropping);
        dropId = id;
        boolean found = false;
        for (int i = dropSlot+1; i < slotInfo.length; i++) {
            if(slotInfo[i]!=null&&slotInfo[i].getId().equals(id))
            {
                found =true;
                dropSlot = i;
                dropStack++;
                clickWindow((byte) i);
                break;
            }
        }
        if(!found)
        {
            setBusy(false, ActionType.Dropping);        
            Util.logger.log(LogElement.Inventory, LogLevel.Error, "No "+id+" in inventory to drop.");
        }
    }
    
    /**
     * @return Whether the handler is busy with some ongoing inventory action.
     */
    public boolean isBusy() {
        return busy;
    }

    /**
     * Selects first action bar slot as active.
     */
    public void setFirstSlotHeld() {
        out09HeldItemChange.sendMessage(0);
    }

    /**
     * Tries to set whether inventory is busy or not.
     * @param value Whether inventory should be busy or not.
     * @param reason Type of action to be performed.
     */
    private void setBusy(boolean value, ActionType reason)
    {
        if(!busy)
        {
            if(value)
            {
                Util.logger.log(LogElement.Inventory,LogLevel.Debug,"Inventory handler is now busy with "+reason);
                busy = true;
                busyReason = reason;
                return;
            }
            else
            {
                if(reason!=null)
                    Util.logger.log(LogElement.Inventory,LogLevel.Warn,"Inventory handler wasnt busy, cant set it to false ("+reason+")");
                return;
            }
        }
        if(!value)
        {
            if(reason.equals(busyReason))
            {
                Util.logger.log(LogElement.Inventory,LogLevel.Debug,"Inventory handler isnt busy anymore with "+busyReason);
                busy = false;
                return;
            }
            else
            {
                Util.logger.log(LogElement.Inventory,LogLevel.Debug,"Inventory handler is busy with "+busyReason+", cant free it by "+reason);
                busy = false;
                return;
            }
        }
        Util.logger.log(LogElement.Inventory,LogLevel.Debug,"Inventory handler is busy with "+busyReason+", cant be busy with "+reason);

        
        
    }

    /**
     * Updates the state of the inventory.
     */
    @Override
    public void update()
    {
        if(!busy&&!actionQueue.isEmpty())
        {
           currentAction = actionQueue.poll();
           
           currentAction.execute();
        }
    }

    /**
     * Starts recovery of inventory state.
     * Disables all inventory actions.
     */
    @Override
    public void startRecovery() {
        out0DCloseWindow.sendMessage((byte)0);
        recovering = true;
        setBusy(false, busyReason);
        setBusy(true, ActionType.Recovery);
        if(busy&&currentAction!=null)
            actionQueue.add(currentAction);
        for (Iterator<InventoryAction> it = actionQueue.iterator(); it.hasNext();) {
            InventoryAction action = it.next();
            if(action.type.equals(ActionType.Autocrafting))
                it.remove();
        }
    }

    /**
     * Ends recovery of inventory state.
     * Allows inventory actions to be performed again.
     */
    @Override
    public void endRecovery() {
        recovering = false;
        setBusy(false, ActionType.Recovery);
    }

    /**
     * Informs inventory handler about block that bot is about to build.
     * @param plan Build plan information.
     */
    @Override
    public void toBuild(BlockPlan plan) {
        planToCheck = plan;
        checkBuild = true;
    }

    /**
     * Informs inventory handler about block change in the world.
     * Used to confim whether the block was build by the bot.
     * @param id Id of new block.
     * @param x X coordinate of the block.
     * @param y Y coordinate of the block.
     * @param z Z coordinate of the block.
     */
    @Override
    public void blockChanged(int id, int x, int y, int z) {
        if(!checkBuild)
        {
            return;
        }
        if(planToCheck!=null
                &&planToCheck.id.getValue()==id
                &&planToCheck.x==x
                &&planToCheck.y==y
                &&planToCheck.z==z)
        {
            Block b = slotInfo[36];
            if(b!=null&&b.getId().getValue()==id)
            {
                for (byte[] info : blockInfo.get(b)) {
                    if(info[0]==36)
                    {
                        if(info[1]==1)
                            setSlot((byte)0,(byte)36,-1,(byte)0,(byte)0);
                        else
                            setSlot((byte)0, (byte)36, id, b.getMeta(), (byte)(info[1]-1));
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Provides base class for actions supported by inventory.
     */
    abstract class InventoryAction
    {
        public final ActionType type;
        
        public InventoryAction(ActionType type)
        {
            this.type = type;
        }
        
        /**
         * Executes this action.
         */
        abstract public void execute();
    }
    
    /**
     * Represents action of dropping items from inventory.
     */
    class DroppingAction extends InventoryAction
    {

        public final Id id;
        public DroppingAction(Id id) {
            super(ActionType.Dropping);
            this.id = id;
        }

        @Override
        public void execute() {
            dropReally(((DroppingAction)currentAction).id);
        }


        
    }
    
    /**
     * Represents action swaping items to get desired item in the hand slot.
     */
    class SwappingAction extends InventoryAction
    {

        public final Id id;
        public SwappingAction(Id id) {
            super(ActionType.Swapping);
            this.id = id;
        }

        @Override
        public void execute() {
            throw new UnsupportedOperationException("Not supported yet."); 
        }
        
    }
    
    /**
     * Represents action of crafting item planks on pick up.
     */
    class AutocraftingAction extends InventoryAction
    {

        public final byte slot;
        public AutocraftingAction(byte slot) {
            super(ActionType.Autocrafting);
            this.slot = slot;
        }

        @Override
        public void execute() {
                   autocraft(((AutocraftingAction)currentAction).slot);
        }
        
    }
    
    /**
     * Type of inventory action.
     */
    enum ActionType
    {
        Autocrafting, Dropping, Swapping, Recovery;
    }
}
