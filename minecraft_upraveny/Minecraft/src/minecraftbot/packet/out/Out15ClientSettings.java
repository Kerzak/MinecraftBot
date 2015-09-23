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
public class Out15ClientSettings extends OutPacket{

    public Out15ClientSettings(MinecraftOutputStream out) {
        super(out);
    }
    
    public void sendMessage()
    {
        try {
            out.write(0x15);
            out.writeString("en_US");
            out.writeByte((byte)2);
            out.writeByte((byte)0);
            out.writeBool(true);
            out.writeByte((byte)0);
            out.writeBool(true);
            out.closePacket();
        } catch (IOException ex) {
            System.err.println("Error sending client settings.");
            System.exit(0);        }
    }    
}
