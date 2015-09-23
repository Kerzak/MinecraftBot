/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.packet.out;

import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import java.io.IOException;
import minecraftbot.network.MinecraftOutputStream;

/**
 *
 * @author eZ
 */
public class Out06PlayerPositionAndLook extends OutPacket{

    public Out06PlayerPositionAndLook(MinecraftOutputStream out) {
        super(out);
    }
    
    public void send(double x, double y, double z, float r0, float r1, boolean onGround)
    {
        try {
            out.write(6);
            out.writeDouble(x);
            out.writeDouble(y);
            out.writeDouble(y+1.62);
            out.writeDouble(z);
            out.writeFloat(r0);
            out.writeFloat(r1);
            out.writeBool(onGround);
            out.closePacket();
            
        } catch (IOException ex) {
            System.out.println("IO Error while moving.");
            System.exit(0);
        }    
    }
}
