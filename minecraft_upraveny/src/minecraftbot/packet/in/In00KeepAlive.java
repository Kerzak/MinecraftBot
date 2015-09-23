/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.network.MinecraftDataInputStream;
import minecraftbot.network.MinecraftOutputStream;

/**
 *
 * @author eZ
 */
public class In00KeepAlive implements IInPacket{
    private final MinecraftOutputStream out;
    
    public In00KeepAlive(MinecraftOutputStream out)
    {
        this.out = out;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) {
        byte[] buffer= new byte[4];
        try {
            in.readFully(buffer);
            out.write(0);
            out.write(buffer);
            out.closePacket();
        } catch (IOException ex) {
            System.err.println("Error while keeping alive");
            System.exit(0);
        }
    }

    
}
