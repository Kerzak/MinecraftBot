/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.packet.out;

import java.io.IOException;
import minecraftbot.network.MinecraftOutputStream;

/**
 *
 * @author eZ
 */
public class Out07PlayerDigging extends OutPacket{

    public Out07PlayerDigging(MinecraftOutputStream out) {
        super(out);
    }
    
    public void sendMessage(byte status, int x, byte y, int z, byte face)
    {
        try {
            out.write(7);
            out.write(status);
            out.writeInt(x);
            out.write(y);
            out.writeInt(z);
            out.write(face);
            out.closePacket();
        } catch (IOException ex) {
            System.err.println("Error while logging in.");
            System.exit(0);        }
    }    
}
