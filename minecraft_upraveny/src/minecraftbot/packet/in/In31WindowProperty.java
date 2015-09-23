/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.network.MinecraftDataInputStream;

/**
 *
 * @author Kerzak
 */
public class In31WindowProperty implements IInPacket {

    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        System.out.println("PACKET WITH ID 31 RECIEVED");
    }
    
}
