
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.messaging.MessageQueue;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In02ChatMessage implements IInPacketHandler{

    private final MessageQueue chat;
    
    public In02ChatMessage(MessageQueue chat)
    {
        this.chat = chat;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        chat.addMessage(in.readString());
    }
    
}
