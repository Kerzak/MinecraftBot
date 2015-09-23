/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.in;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.IOException;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.network.MinecraftDataInputStream;
import minecraftbot.world.WorldHandler;

/**
 *
 * @author eZ
 */
public class In23BlockChange implements IInPacket{
    private final WorldHandler world;
    
    public In23BlockChange(WorldHandler out)
    {
        this.world = out;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int x = in.readInt(),
            y = in.readUnsignedByte(),
            z = in.readInt(),
            id = in.readVarInt(),
            meta = in.readUnsignedByte();
        world.setBlock(x, y, z,id,(byte)meta);
        Util.logger.log(LogElement.World, LogLevel.Debug, "BLOCK CHANGE "+x+"-"+y+"-"+z+" "+id+":"+meta);
            
       
        
    }

    
}
