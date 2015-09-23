
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.entity.EntityHandler;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In1CEntityMetadata implements IInPacketHandler{
    private EntityHandler handler;
    
    public In1CEntityMetadata(EntityHandler handler)
    {
        this.handler = handler;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int id = in.readVarInt();
        //todo process metadata
    }

    
}
