
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.GameInfo;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In07Respawn implements IInPacketHandler{

    private final GameInfo info;
    
    public In07Respawn(GameInfo info)
    {
        this.info = info;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        info.respawn(in.readInt(), in.readByte(), in.readByte(), in.readString());
    }
    
}
