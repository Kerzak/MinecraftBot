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
public class Out00Login extends OutPacket{

    public Out00Login(MinecraftOutputStream out) {
        super(out);
    }
    
    public void sendMessage(String name)
    {
        try {
            out.write(0);
            out.writeString(name);
            out.closePacket();
            out.flush();
        } catch (IOException ex) {
            System.err.println("Error while logging in.");
            System.exit(0);        }
    }    
}
