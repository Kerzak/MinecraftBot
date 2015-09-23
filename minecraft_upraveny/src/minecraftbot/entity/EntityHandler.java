/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.entity;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import java.util.HashMap;
import java.util.Map;
import minecraftbot.IWorldView;
import minecraftbot.world.Block;

/**
 * Class for handling entities
 * @author eZ
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
     * add new player entity
     * @param name name of player
     * @param id entity id
     * @param location players location
     */
    public void addPlayer(String name, int id, Location location)
    {
        players.put(id, new Player(name, id, location));
    }

    public void addStack(Block type, byte count, int id,  Location location)
    {
        stacks.put(id, new Stack(type, count, id, location));
    }
    
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
        //System.out.println("VELOCITY "+x/8000.0+" "+y/8000.0+" "+ z/8000.0);
        //if(x>1||x<-1||y>1||y<-1||z>1||z<-1)
        {
            //System.out.println("SET VELOCITY FOR "+id);
            velocities.put(id, new Velocity(x/8000.0, y/8000.0, z/8000.0));
            /*System.out.println("VELOCITY IS "+velocities.get(id));
            System.out.println(x);
            System.out.println(y);
            System.out.println(z);*/
        }
        //else
        {
            /*System.out.println("VELOCITY FOR "+id+" REMOVED");
            System.out.println(x);
            System.out.println(y);
            System.out.println(z);
            System.out.println(stacks.get(id).getLocation().toString());*/
            velocities.remove(id);
        }
    }
    
    /**
     * changes location of entity
     * @param id entity id
     * @param x addition to x
     * @param y addition to y
     * @param z addition to z
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
            //System.out.println("ADD POS "+x/32.0+" "+y/32.0+" "+z/32.0);
            Stack s = stacks.get(id);
            s.setLocation(s.getLocation().addXYZ(x/32.0, y/32.0, z/32.0));
            /*if(!world.getBlockUnder(s.getLocation()).isEmpty())
            {
                s.setLocation(s.getLocation().addY(-y/32.0));
            }*/
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
    
    public void loopStep()
    {
        long currentTime = System.nanoTime(), elapsedTime = currentTime-lastTime;
        double ratio = (currentTime-lastTime)/50000000.0;
        //System.out.println("RATIO: "+ratio);
        for(Stack s : stacks.values())
        {
           // System.out.println(s.getLocation());
        }
        for (Map.Entry<Integer, Velocity> entry : velocities.entrySet()) {
  //          System.out.println("VELOCITY "+entry.getValue().scale(ratio));
           /* for (Stack s : stacks.values()) {
                System.out.println(s.getID());
                System.out.println(velocities.containsKey(s.getID()));
            }*/
            if(stacks.containsKey(entry.getKey()))
            stacks.get(entry.getKey()).applyVelocity(entry.getValue().scale(ratio));
        }
        lastTime = currentTime;
    }
}
