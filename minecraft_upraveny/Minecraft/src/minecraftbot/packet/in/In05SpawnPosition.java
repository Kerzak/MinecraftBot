
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.GameInfo;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In05SpawnPosition implements IInPacketHandler{

    private final GameInfo info;
    
    public In05SpawnPosition(GameInfo info)
    {
        this.info = info;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        info.setSpawnLocation(in.readInt(),in.readInt(),in.readInt());        
    }
    
}
