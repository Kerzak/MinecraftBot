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
public class In17EntityRelativeMoveAndLook implements IInPacket{
    private final EntityHandler handler;
    
    public In17EntityRelativeMoveAndLook(EntityHandler handler)
    {
        this.handler = handler;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int id = in.readInt();
        byte x = in.readByte(),
            y = in.readByte(),
            z = in.readByte();
        handler.setRelativeLocation(id, x, y, z);
        //todo rotation
    }

    
}
