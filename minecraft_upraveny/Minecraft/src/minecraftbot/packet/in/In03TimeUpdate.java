
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.GameInfo;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In03TimeUpdate implements IInPacketHandler{

    private final GameInfo info;
    
    public In03TimeUpdate(GameInfo info)
    {
        this.info = info;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
       info.setTime(in.readLong(), in.readLong());
        
    }
    
}
