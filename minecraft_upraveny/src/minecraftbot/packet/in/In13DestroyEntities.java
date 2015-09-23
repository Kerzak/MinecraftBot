/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.network.MinecraftDataInputStream;
import minecraftbot.entity.EntityHandler;

/**
 *
 * @author eZ
 */
public class In13DestroyEntities implements IInPacket{
    private final EntityHandler handler;
    
    public In13DestroyEntities(EntityHandler handler)
    {
        this.handler = handler;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        byte count = in.readByte();
        int[] ids = new int[count];
        
        for(int i = 0; i<count; i++)
        {
            ids[i] = in.readInt();
        }
        
        handler.destroyEntities(ids);
    }

    
}