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
public class Out16ClientStatus extends OutPacket{

    public Out16ClientStatus(MinecraftOutputStream out) {
        super(out);
    }
    
    public void respawn()
    {
        
        try {
            out.write(0x16);
            out.write(0);
            out.closePacket();
            out.flush();
        } catch (IOException ex) {
            System.err.println("Error while respawning.");
            System.exit(0);
        }
    }    
}
