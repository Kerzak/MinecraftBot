
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.entity.EntityHandler;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In17EntityRelativeMoveAndLook implements IInPacketHandler{
    private final EntityHandler handler;
    
    public In17EntityRelativeMoveAndLook(EntityHandler handler)
    {
        this.handler = handler;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int id = in.readInt();
        byte x = in.readByte(),
            y = in.readByte(),
            z = in.readByte();
        handler.setRelativeLocation(id, x, y, z);
        //todo rotation
    }

    
}
