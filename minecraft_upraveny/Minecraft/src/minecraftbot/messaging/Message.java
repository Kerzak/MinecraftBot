/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.messaging;

import java.security.InvalidParameterException;
import java.util.Map;

/**
 * Minecraft chat message
 * @author eZ
 */
 public class Message {
        private final Map<String, String> map;
        private final MessageEvent event;

        public Message(MessageEvent event, Map<String,String> map)
        {
            this.event = event;
            this.map = map;
        }

    public MessageEvent getEvent() {
        return event;
    }
        
    public String getValue(String property)
    {
        if(!map.containsKey(property))
            throw new InvalidParameterException("Message doesn't contain key "+ property);
        return map.get(property);
    }
 }
