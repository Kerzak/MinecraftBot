
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
public class In32ConfirmTransaction implements IInPacketHandler{

    private final IInventoryStorage inventory;
    
    public In32ConfirmTransaction(IInventoryStorage inventory)
    {
        this.inventory = inventory;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        byte magic;
        boolean trueMagic;
        magic = in.readByte();
        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Magic number: "+magic);
        if(magic!=0)
            return;
        short actionNumber;
        actionNumber = (byte)in.readShort();
        trueMagic = in.readBoolean();
        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Action number: "+actionNumber);
        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Error: "+!trueMagic);
        if(trueMagic)    
        {
            inventory.confirmTransaction(actionNumber);
        }
        else
        {
            Util.logger.log(LogElement.Inventory, LogLevel.Error, "Inventory inconsistent");
        }
    }
    
}
