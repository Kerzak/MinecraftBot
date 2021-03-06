
package minecraftbot.packet.in;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.IOException;
import minecraftbot.network.MinecraftDataInputStream;
import minecraftbot.network.MinecraftOutputStream;
import minecraftbot.entity.EntityHandler;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In0CSpawnPlayer implements IInPacketHandler{
    private EntityHandler handler;
    
    public In0CSpawnPlayer(EntityHandler handler)
    {
        this.handler = handler;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int id = in.readVarInt();
        in.readString();
        String name = in.readString();
        int x, y, z;
        x = in.readInt();
        y = in.readInt();
        z = in.readInt();
        in.readByte();
        in.readByte();
        handler.addPlayer(name, id, new Location(x/32.0, y/32.0, z/32.0));
        // todo rotation
    }

    
}