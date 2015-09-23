/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Base class for input packet handlers.
 * refer to http://wiki.vg/Protocol
 */
public interface IInPacketHandler {
    public void process(MinecraftDataInputStream in) throws IOException;
}
