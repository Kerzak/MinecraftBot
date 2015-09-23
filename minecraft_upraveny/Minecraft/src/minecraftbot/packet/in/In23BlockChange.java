
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.IInventoryStorage;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.network.MinecraftDataInputStream;
import minecraftbot.world.WorldHandler;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In23BlockChange implements IInPacketHandler{
    private final WorldHandler world;
    private final IInventoryStorage inventory;
    
    public In23BlockChange(WorldHandler out,IInventoryStorage inventory)
    {
        this.world = out;
        this.inventory = inventory;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int x = in.readInt(),
            y = in.readUnsignedByte(),
            z = in.readInt(),
            id = in.readVarInt(),
            meta = in.readUnsignedByte();
        world.setBlock(x, y, z,id,(byte)meta);
        inventory.blockChanged(id, x, y, z);
        Util.logger.log(LogElement.World, LogLevel.Debug, "BLOCK CHANGE "+x+"-"+y+"-"+z+" "+id+":"+meta);
            
       
        
    }

    
}
