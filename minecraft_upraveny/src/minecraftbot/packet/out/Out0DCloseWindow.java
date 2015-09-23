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
public class Out0DCloseWindow extends OutPacket{

    public Out0DCloseWindow(MinecraftOutputStream out) {
        super(out);
    }
    
    
    public void sendMessage(byte windowID)
    {
        try {
            out.writeByte((byte)0x0D);
            out.writeByte(windowID);
            out.closePacket();
        } catch (IOException ex) {
            System.err.println("Error while closing inventory window.");
            System.exit(0);
        }        
    }
}
