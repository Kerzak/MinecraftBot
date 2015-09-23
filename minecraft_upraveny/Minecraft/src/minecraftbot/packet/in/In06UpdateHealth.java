
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.GameInfo;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In06UpdateHealth implements IInPacketHandler{

    private final GameInfo info;
    
    public In06UpdateHealth(GameInfo info)
    {
        this.info = info;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        info.setHealth(in.readFloat(), in.readShort(), in.readFloat());
        
    }
    
}
