/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot;

import java.util.HashMap;
import java.util.Map;
import minecraftbot.entity.Player;

/**
 *
 * @author eZ
 */
public class PlayerList {

    private HashMap<Integer, Player> players;
    
    public PlayerList(HashMap<Integer, Player> set)
    {
        this.players = set;
    }
    
    /**
     * @return List of players known to bot.
     */
    public HashMap<Integer, Player> getPlayers()
    {
        return players;
    }
    
    /**
     * @param name Name of the player.
     * @return Reference to named player.
     */
    public Player getPlayerNamed(String name)
    {
        for (Map.Entry<Integer, Player> entry : players.entrySet()) {
            if(entry.getValue().getName().equals(name))
                return entry.getValue();
        }
        return null;
    }
}
