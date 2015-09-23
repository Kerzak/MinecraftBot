
package minecraftbot.packet.in;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.IOException;
import minecraftbot.entity.EntityHandler;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In18EntityTeleport implements IInPacketHandler{
    private final EntityHandler handler;
    
    public In18EntityTeleport(EntityHandler handler)
    {
        this.handler = handler;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int id = in.readVarInt();
        int x, y, z;
        x = in.readInt();
        y = in.readInt();
        z = in.readInt();
        in.readByte();
        in.readByte();
        handler.setLocation(id, new Location(x/32.0, y/32.0, z/32.0));
        // todo rotation
    }

    
}