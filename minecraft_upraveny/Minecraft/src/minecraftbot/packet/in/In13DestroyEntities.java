
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.network.MinecraftDataInputStream;
import minecraftbot.entity.EntityHandler;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In13DestroyEntities implements IInPacketHandler{
    private final EntityHandler handler;
    
    public In13DestroyEntities(EntityHandler handler)
    {
        this.handler = handler;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        byte count = in.readByte();
        int[] ids = new int[count];
        
        for(int i = 0; i<count; i++)
        {
            ids[i] = in.readInt();
        }
        
        handler.destroyEntities(ids);
    }

    
}