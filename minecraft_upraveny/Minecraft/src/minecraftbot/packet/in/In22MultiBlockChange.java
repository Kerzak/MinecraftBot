
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.network.MinecraftDataInputStream;
import minecraftbot.world.WorldHandler;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In22MultiBlockChange implements IInPacketHandler{
    private final WorldHandler world;
    
    public In22MultiBlockChange(WorldHandler out)
    {
        this.world = out;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int cx = in.readInt(),
            cz = in.readInt(),
            count = in.readShort(),
            dataSize = in.readInt();
        int x,z,y,id,meta,temp;
        for (int i = 0; i < count; i++) {
            temp = in.readByte();
            if(temp<0)
                temp+=256;
            x= temp>>4;
            if(x<0)
                x*=-1;
            z = temp&0x0f;
            y = in.readByte();
            id=in.readByte();
            meta=in.readByte();
            id*=16;
            id+=Math.abs(meta>>4);
            meta = meta&0x0f;;
            world.setBlock(16*cx+x, y, 16*cz+z, id, (byte)meta);
        }
            
       
        
    }

    
}
