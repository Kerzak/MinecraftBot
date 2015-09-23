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
public class Out08PlayerBlockPlacement extends OutPacket{

    public Out08PlayerBlockPlacement(MinecraftOutputStream out) {
        super(out);
    }
    
    public void sendMessage(int x, byte y, int z, byte direction, byte cursorX, byte cursorY, byte cursorZ)
    {
        try {
            out.write(8);
            out.writeInt(x);
            out.write(y);
            out.writeInt(z);

            out.write(direction);
            
            out.write(255);
            out.write(255);
            
            
            out.write(cursorX);
            out.write(cursorX);
            out.write(cursorZ);
            
            out.closePacket();
        } catch (IOException ex) {
            System.err.println("IOError while building.");
            System.exit(0);        }
    }    
}
