/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.in;

import java.io.IOException;
import minecraftbot.messaging.MessageQueue;
import minecraftbot.network.MinecraftDataInputStream;

/**
 *
 * @author eZ
 */
public class In02ChatMessage implements IInPacket{

    private final MessageQueue chat;
    
    public In02ChatMessage(MessageQueue chat)
    {
        this.chat = chat;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        //temp
        String chatString =in.readString();
                System.out.println(chatString);
        //temp end
        chat.addMessage(chatString);
    }
    
}
