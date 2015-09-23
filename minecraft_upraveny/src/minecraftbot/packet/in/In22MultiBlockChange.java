/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.in;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.IOException;
import minecraftbot.Util;
import minecraftbot.network.MinecraftDataInputStream;
import minecraftbot.world.WorldHandler;

/**
 *
 * @author eZ
 */
public class In22MultiBlockChange implements IInPacket{
    private final WorldHandler world;
    
    public In22MultiBlockChange(WorldHandler out)
    {
        this.world = out;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int x = in.readInt(),
            z = in.readInt(),
            count = in.readShort(),
            dataSize = in.readInt();
        System.out.println("MULTI BLOCK CHANGE : " + count + " * 4 = "+ dataSize);
        for (int i = 0; i < count; i++) {
            System.out.println("===================");
            byte b = in.readByte();
            System.out.println(b>>4);
            System.out.println(b&0x0f);
            System.out.println("+++++");
            System.out.println(in.readByte());
            System.out.println(in.readShort());
        }
//            meta = in.readUnsignedByte();
//        world.getBlock(x, y, z).set(Util.idMap.get(id), (byte)meta);
        /*System.out.println("BLOCK CHANGE "+x+"-"+y+"-"+z+" "+id+":"+meta);
        if(id!=17)
        {
            world.treeDown(new Location(x, y, z));
        }
        else
        {
            world.addTree(x,y,z);
        }*/
            
       
        
    }

    
}
