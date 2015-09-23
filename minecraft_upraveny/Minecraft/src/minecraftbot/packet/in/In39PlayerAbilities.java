
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.GameInfo;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In39PlayerAbilities implements IInPacketHandler{

    private final GameInfo info;
    
    public In39PlayerAbilities(GameInfo info)
    {
        this.info = info;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
       /* System.out.println("PLAYER ABILITIES");
        System.out.println(in.readByte());
        System.out.println(in.readFloat());
        System.out.println(in.readFloat());*/
        
    }
    
}
