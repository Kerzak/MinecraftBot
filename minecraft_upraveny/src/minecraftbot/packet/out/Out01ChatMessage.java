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
public class Out01ChatMessage extends OutPacket{

    public Out01ChatMessage(MinecraftOutputStream out) {
        super(out);
    }
    
    
    public void sendMessage(String text)
    {
        try {
            out.write(1);
            out.writeString(text);
            out.closePacket();
        } catch (IOException ex) {
            System.err.println("Error while sending chat message");
            System.exit(0);
        }        
    }
}
