
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.IInventoryStorage;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In2FSetSlot implements IInPacketHandler{

    private final IInventoryStorage inventory;
    
    public In2FSetSlot(IInventoryStorage inventory)
    {
        this.inventory = inventory;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Set slot OUT");
        int id;
        byte slot, meta, count, emptyCheck, type;
        type = in.readByte();
        slot = (byte)in.readShort();
        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "To set slot: "+slot);
        emptyCheck = in.readByte();
        if(emptyCheck<0)
        {
            inventory.setSlot(type, slot, (byte)-1, (byte)0, (byte)0);     
            return;
        }
        id=in.readByte()+emptyCheck*256;
        count=in.readByte();
        in.readByte();
        meta=in.readByte();
      // System.out.println("To set: "+slot+"  "+id+" "+meta);
        inventory.setSlot(type, slot, id, meta, count);
    }
    
}
