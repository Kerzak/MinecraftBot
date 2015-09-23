
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.GameInfo;
import minecraftbot.entity.EntityHandler;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In38PlayerListItem implements IInPacketHandler{

    private final GameInfo info;
    private final EntityHandler handler;
    
    public In38PlayerListItem(GameInfo info, EntityHandler handler)
    {
        this.handler = handler;
        this.info = info;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        handler.removePlayer(in.readString(), in.readBoolean(), in.readShort());
    }
    
}
