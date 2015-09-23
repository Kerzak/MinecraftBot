
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.IInventoryStorage;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In30WindowItems implements IInPacketHandler{

    private final IInventoryStorage inventory;
    
    public In30WindowItems(IInventoryStorage inventory)
    {
        this.inventory = inventory;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        inventory.startRecovery();
        int id;
        byte slot, meta, count, emptyCheck, type;
        type = in.readByte();
        short slotCount = in.readShort();
       
        for (byte i = 0; i < slotCount; i++) {
            emptyCheck = in.readByte();
            if(emptyCheck<0)
            {
                in.readByte();
                inventory.setSlot(type, i, (byte)-1, (byte)0, (byte)0);     
                continue;
            }
            id=in.readByte();
            id+=256*emptyCheck;
            count=in.readByte();
            meta=in.readByte();
            inventory.setSlot(type, i, id, meta, count);
            in.readByte();
            in.readByte();
            in.readByte();
        }
        inventory.endRecovery();
    }
    
}
