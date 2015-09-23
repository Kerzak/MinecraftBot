
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.entity.EntityHandler;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In15EntityRelativeMove implements IInPacketHandler{
    private final EntityHandler handler;
    
    public In15EntityRelativeMove(EntityHandler handler)
    {
        this.handler = handler;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int id = in.readInt();
        byte x = in.readByte(),
            y = in.readByte(),
            z = in.readByte();
        //System.out.println("ID - "+id);
        handler.setRelativeLocation(id, x, y, z);
    }

    
}
