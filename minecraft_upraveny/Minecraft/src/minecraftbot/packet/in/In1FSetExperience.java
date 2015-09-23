
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.GameInfo;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In1FSetExperience implements IInPacketHandler{

    private final GameInfo info;
    
    public In1FSetExperience(GameInfo info)
    {
        this.info = info;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        info.setExperience(in.readFloat(), in.readShort(), in.readShort());
        
    }
    
}
