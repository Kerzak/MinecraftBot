/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.GameInfo;
import minecraftbot.network.MinecraftDataInputStream;

/**
 *
 * @author eZ
 */
public class In01JoinGame implements IInPacket{

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
