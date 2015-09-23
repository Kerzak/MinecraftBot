/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot;

import minecraftbot.network.MinecraftOutputStream;
import java.io.IOException;
import minecraftbot.packet.out.Out01ChatMessage;

/**
 * Handles outcoming chat messages
 * @author eZ
 */
public class ChatHandler {
    private final Out01ChatMessage sender;
    
    public ChatHandler(Out01ChatMessage sender)
    {
        this.sender = sender;
    }
    
    /**
     * Sends chat message
     * @param text text of message
     */
    public void sendMessage(String text)
    {
        sender.sendMessage(text);
    }
    
    public void sendMessage(Object o)
    {
        sender.sendMessage(o.toString());
    }
    
}
