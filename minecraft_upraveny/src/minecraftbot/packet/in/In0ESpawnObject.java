/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.in;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.IOException;
import minecraftbot.network.MinecraftDataInputStream;
import minecraftbot.entity.EntityHandler;

/**
 *
 * @author eZ
 */
public class In0ESpawnObject implements IInPacket{
    private final EntityHandler handler;
    
    public In0ESpawnObject(EntityHandler handler)
    {
        this.handler = handler;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        int id = in.readVarInt();
        byte type = in.readByte();
        int x, y, z;
        x = in.readInt();
        y = in.readInt();
        z = in.readInt();
        in.readByte();
        in.readByte();
        //todo read object data
        /*
        while (in.available()>0) {            
            System.out.println(in.readByte());
        }*/
        switch(type)
        {
            case 2:
                //STACK
                in.readShort();
                short vx, vy, vz;
                vx = in.readShort();
                vy = in.readShort();
                vz = in.readShort();
                handler.addStack(null, (byte)0, id, new Location(x/32.0, y/32.0, z/32.0));
                handler.setVelocity(id, vx, vy, vz);
                break;
        }
    }

    
}