
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.GameInfo;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In01JoinGame implements IInPacketHandler{

    private final GameInfo info;
    
    public In01JoinGame(GameInfo info)
    {
        this.info = info;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        
        info.loadInfo(in.readInt(),
                in.readByte(), 
                in.readByte(), 
                in.readByte(), 
                in.readByte(), 
                in.readString());
    }
    
}
