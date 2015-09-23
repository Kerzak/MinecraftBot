/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * refer to http://wiki.vg/Protocol
 * @author eZ
 */
public interface IInPacket {
    public void process(MinecraftDataInputStream in) throws IOException;
}
