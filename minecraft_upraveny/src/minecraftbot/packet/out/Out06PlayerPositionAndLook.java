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
            //System.out.println("TRYING TO SEND PACKET: " + " x=" + x+ " ; y=" + y+ " ; y+1.62=" + y+1.62+ " ; z=" + z+ " ; r0=" + r0+ ";r1=" + r1 + " ; onGround=" + onGround);
            out.write(6);
            out.writeDouble(x);
            out.writeDouble(y);
            out.writeDouble(y+1.62);
            out.writeDouble(z);
            out.writeFloat(r0);
            out.writeFloat(r1);
            out.writeBool(onGround);
            out.closePacket();
            //System.out.println("PACKET WITH ID 6 SENT ");
        } catch (IOException ex) {
            
            System.out.println("IO Error while moving." + ex.getMessage());
            System.exit(0);
        }    
    }
}
