
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.messaging.MessageQueue;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In40Disconnect implements IInPacketHandler{

    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        String reason = in.readString();
        switch(reason)
        {
            case "disconnect.spam":
                reason = "Spam";
                break;
        }
        System.out.println("Disconnected from the server. Reason: "+reason);
    }
    
}
