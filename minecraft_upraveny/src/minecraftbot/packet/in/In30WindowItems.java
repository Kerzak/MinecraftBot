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
 */
public class In30WindowItems implements IInPacket{

    private final InventoryManager inventoryManager;
    
    public In30WindowItems(InventoryManager inventoryManager)
    {
        this.inventoryManager = inventoryManager;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        Util.logger.log(LogElement.Inventory, LogLevel.Error, "RECOVERY IMMINENT!!");
        int id;
        byte slot, meta, count, emptyCheck, type;
        type = in.readByte();
        short slotCount = in.readShort();
        for (int i = 0; i < slotCount; i++) {
            slot = (byte)in.readShort();
            Util.logger.log(LogElement.Inventory, LogLevel.Trace, "To set slot: "+slot);
            emptyCheck = in.readByte();
            if(emptyCheck<0)
            {
                System.out.println("TYPE: " + type + " SLOT: " + slot);
                inventoryManager.getCurrentInventory().setSlot(type, slot, (byte)-1, (byte)0, (byte)0);     
                return;
            }
            id=in.readByte()+emptyCheck*256;
            count=in.readByte();
            in.readByte();
            meta=in.readByte();
            System.out.println("TYPE: " + type + " SLOT: " + slot + " ID: " + id + " META: " + meta + " COUNT: " + count);
            inventoryManager.getCurrentInventory().setSlot(type, slot, id, meta, count);
        }
    }
    
}
