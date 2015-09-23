
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.network.MinecraftDataInputStream;
import minecraftbot.entity.EntityHandler;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In12EntityVelocity implements IInPacketHandler{
    private final EntityHandler handler;
    
    public In12EntityVelocity(EntityHandler handler)
    {
        this.handler = handler;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int id = in.readInt();
        short x, y, z;
        x = in.readShort();
        y = in.readShort();
        z = in.readShort();
        handler.setVelocity(id, x, y, z);
    }

    
}