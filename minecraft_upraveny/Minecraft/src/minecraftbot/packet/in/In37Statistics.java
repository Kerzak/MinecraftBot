
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.GameInfo;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In37Statistics implements IInPacketHandler{

    private final GameInfo info;
    
    public In37Statistics(GameInfo info)
    {
        this.info = info;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        /*int count = in.readVarInt();
        System.out.println("STATISTICS "+count );
        for (int i = 0; i < count; i++) {
            System.out.println(in.readString() + " - "+ in.readVarInt());
        }*/
        
    }
    
}
