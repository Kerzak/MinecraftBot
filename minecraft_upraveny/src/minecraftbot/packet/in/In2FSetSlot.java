/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.in;

import java.io.IOException;
import minecraft.inventory.InventoryManager;
import minecraftbot.IInventoryStorage;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.network.MinecraftDataInputStream;

/**
 *
 * @author eZ
 * 
 * //KERZAK: Two implementations for players inventory run concurrently.
 * InventoryManager will replace IInventoryStorage
 */
public class In2FSetSlot implements IInPacket{

    private final InventoryManager inventoryManager;
    private final IInventoryStorage inventory;
    
    public In2FSetSlot(InventoryManager inventoryManager, IInventoryStorage inventory)
    {
        this.inventoryManager = inventoryManager;
        this.inventory = inventory;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int id;
        byte slot, meta, count, emptyCheck, type;
        type = in.readByte();
        slot = (byte)in.readShort();
        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "To set slot: "+slot);
        emptyCheck = in.readByte();
        if(emptyCheck<0)
        {
            inventory.setSlot(type, slot, (byte)-1, (byte)0, (byte)0);    
            inventoryManager.getCurrentInventory().setSlot(type, slot, (byte)-1, (byte)0, (byte)0);     
            return;
        }
        id=in.readByte()+emptyCheck*256;
        count=in.readByte();
        in.readByte();
        meta=in.readByte();
        inventoryManager.getCurrentInventory().setSlot(type, slot, id, meta, count);
        inventory.setSlot(type, slot, id, meta, count);
    }
    
}
