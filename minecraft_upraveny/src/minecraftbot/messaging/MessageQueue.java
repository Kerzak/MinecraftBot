/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.messaging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Queue of incoming chat messages
 * @author eZ
 */
public class MessageQueue {
    
    List<Message> messages;
    JSONParser parser;
    
    public MessageQueue()
    {
        messages = new LinkedList<>();
        parser = new JSONParser();
    }
    
    /**
     * @return are there any unread messages?
     */
    public boolean isEmpty()
    {
        return messages.isEmpty();
    }
    
    /**
     * parse and add new message to queue
     * @param text message in JSON format
     */
    public void addMessage(String text)
    {
        //System.out.println("SPRAVA:\n\t"+text);
        try {
            JSONObject json = (JSONObject)parser.parse(text);
            switch(json.get("translate").toString())
            {
                case "chat.type.text":
                    parseChatMessage(json);
                    break;
                case "multiplayer.player.joined":
                    parsePlayerJoined(json);
                    break;
                case "multiplayer.player.left":
                    parsePlayerLeft(json);
                    break;
                default:
                    Util.logger.log(LogElement.Chat, LogLevel.Warn, "Unsupported type of incoming message: "+json.get("translate"));
                    break;
                        
            }
        } catch (ParseException ex) {
            Util.logger.log(LogElement.Chat, LogLevel.Error, "Incoming chat message couldn't be parsed.");
            System.exit(0);
        } catch (NullPointerException ex) {
            
        }
        
        /*
        try{
            String name;
            text = text.substring(94);
            int x = text.indexOf("\"") - 1;
            name = text.substring(0, x);
            text = text.substring(x+16+name.length());
            text = text.substring(0,text.indexOf("\""));
            messages.add(new Message(name, text));
        }
        catch (Exception e){
            System.out.println(text);
        }*/
    }
    
    private void parseChatMessage(JSONObject json)
    {
            JSONArray array = (JSONArray) json.get("with");
            
            
            Map<String, String> map = new HashMap();
            map.put("player", (String)((Map)(array.get(0))).get("text"));
            map.put("text", (String)array.get(1));
            messages.add(new Message(MessageEvent.ChatMessage, map));
            /*
            Iterator iter = json.entrySet().iterator();
            while(iter.hasNext()){
              Map.Entry entry = (Map.Entry)iter.next();
              System.out.println(entry.getKey() + "=>" + entry.getValue());
            }*/
    }
    
    private void parsePlayerJoined(JSONObject json)
    {
            JSONArray array = (JSONArray) json.get("with");
            Map<String, String> map = new HashMap();
            map.put("player", (String)((Map)(array.get(0))).get("text"));
            messages.add(new Message(MessageEvent.PlayerJoined, map));
    }
    
    private void parsePlayerLeft(JSONObject json)
    {
            JSONArray array = (JSONArray) json.get("with");
            Map<String, String> map = new HashMap();
            map.put("player", (String)((Map)(array.get(0))).get("text"));
            messages.add(new Message(MessageEvent.PlayerLeft, map));
    }
    
    /**
     * @return oldest unread message
     */
    public Message getMessage()
    {
        if(messages.isEmpty())
            throw new NullPointerException("Message queue is empty!");
        Message msg = messages.get(0);
        messages.remove(0);
        return msg;
    }

}
