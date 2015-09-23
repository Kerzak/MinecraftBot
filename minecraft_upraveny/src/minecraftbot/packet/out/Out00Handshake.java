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
public class Out00Handshake extends OutPacket{

    public Out00Handshake(MinecraftOutputStream out) {
        super(out);
    }
    
    public void sendMessage(byte version, String address, int port)
    {
        try {
            out.write(0);
            out.write(version);
            out.writeString(address);
            out.writeShort(port);
            out.write(2);
            out.closePacket();
        } catch (IOException ex) {
            System.err.println("Error while sending handshake.");
            System.exit(0);        }
    }    
}
