/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.entity.EntityHandler;
import minecraftbot.network.MinecraftDataInputStream;

/**
 *
 * @author eZ
 */
public class In1CEntityMetadata implements IInPacket{
    private EntityHandler handler;
    
    public In1CEntityMetadata(EntityHandler handler)
    {
        this.handler = handler;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int id = in.readVarInt();
        //todo process metadata
    }

    
}
