
package minecraftbot.entity;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import minecraftbot.IWorldView;
import minecraftbot.world.Block;

/**
 * Stores and handles information about entities in game.
 */
public class EntityHandler {
    
    private final HashMap<Integer, Player> players;
    private final HashMap<Integer, Stack> stacks;
    private final HashMap<Integer, Entity> entities;
    private final HashMap<Integer, Velocity> velocities;
    private final IWorldView world;
    private long lastTime;
    
    public  EntityHandler(IWorldView world)
    {
        players = new HashMap<>();
        entities = new HashMap<>();
        stacks = new HashMap<>();
        velocities = new HashMap<>();
        
        this.world = world;
    }
    
    /**
     * Adds new player entity.
     * @param name Name of player
     * @param id Players entity id.
     * @param location Players location.
     */
    public void addPlayer(String name, int id, Location location)
    {

            players.put(id, new Player(name, id, location));
    }

    /**
     * Removes a player from the list of entities.
     * @param name
     * @param connected
     * @param ping 
     */
    public void removePlayer(String name, boolean connected, short ping)
    {
        if(!connected)
        {
            int id = -1;
            for(Player p : players.values())
            {
                if(p.getName().equals(name))
                {
                    id = p.getID();
                }
            }
            players.remove(id);
        }
    }
    
    public void addStack(Block type, byte count, int id,  Location location)
    {
        stacks.put(id, new Stack(type, count, id, location));
    }
    
    /**
     * Removes entities from the list of entities.
     * @param ids Array of entity ids to be removed.
     */
    public void destroyEntities(int[] ids)
    {
        for(Integer id: ids)
        {
            stacks.remove(id);
        }
    }
    
    /**
     * @return map of all players known to bot
     */
    public HashMap<Integer, Player> getPlayers()
    {
        return players;
    }

    public HashMap<Integer, Stack> getStackList() {
        return stacks;
    }

    public HashMap<Integer, Velocity> getVelocities() {
        return velocities;
    }
    
    public void setVelocity(int id, short x, short y, short z)
    {
        {
            velocities.put(id, new Velocity(x/8000.0, y/8000.0, z/8000.0));
        }
        {
            velocities.remove(id);
        }
    }
    
    /**
     * Changes location of an entity.
     * @param id Entity id.
     * @param x Addition to x.
     * @param y Addition to y.
     * @param z Addition to z.
     */
    public void setRelativeLocation(int id, byte x, byte y, byte z)
    {
        if(players.containsKey(id))
        {
            Player p = players.get(id);
            p.setLocation(p.getLocation().addXYZ(x/32.0, y/32.0, z/32.0));
        }
        if(stacks.containsKey(id))
        {
            Stack s = stacks.get(id);
            s.setLocation(s.getLocation().addXYZ(x/32.0, y/32.0, z/32.0));
        }
    }
    
    /**
     * sets location of entity
     * @param id entity id
     * @param loc new location of entity
     */
    public void setLocation(int id, Location loc)
    {
        if(players.containsKey(id))
        {
            Player p = players.get(id);
            p.setLocation(loc);
        }
    }
    
    /**
     * Updates locations of entities.
     */
    public void update()
    {
        long currentTime = System.nanoTime(), elapsedTime = currentTime-lastTime;
        double ratio = (currentTime-lastTime)/50000000.0;
        for(Stack s : stacks.values())
        {
        }
        for (Map.Entry<Integer, Velocity> entry : velocities.entrySet()) {
            if(stacks.containsKey(entry.getKey()))
            stacks.get(entry.getKey()).applyVelocity(entry.getValue().scale(ratio));
        }
        lastTime = currentTime;
    }
}
