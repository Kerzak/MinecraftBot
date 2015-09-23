/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import java.util.HashMap;
import java.util.Map;
import minecraftbot.entity.Stack;

/**
 * Handles information about collectible stacks.
 */
public class Collecting {

    private final HashMap<Integer, Stack> list;
    private final HashMap<Integer, Velocity> velocities;
    private final Locomotion locomotion;
    
    Collecting(HashMap<Integer, Stack> stackList, HashMap<Integer, Velocity> velocities, Locomotion locomotion) {
        list = stackList;
        this.velocities = velocities;
        this.locomotion = locomotion;
    }
    
    /**
     * @return Whether there is pickable stack in 4 block range.
     */
    public boolean isPickableInRange()
    {
        return isPickableNearby(4);
    }
    
    /**
     * @param distance Distance between bot and stack.
     * @return Wheter there is a pickable stack in specified distance from the bot.
     */
    public boolean isPickableNearby(double distance)
    {
        for(Stack stack : list.values())
        {
            if(!velocities.containsKey(stack.getID())&&Location.getDistance(stack.getLocation(), locomotion.getLocation())<=distance)
                return true;
        }
        return false;
    }
    
    /**
     * @return Nearect pickable stack.
     */
    public Stack getNearestStack()
    {
        double min = Double.MAX_VALUE, newDist;
        Stack best = null;
        
        for(Stack stack : list.values())
        {
            newDist = Location.getDistance(stack.getLocation(), locomotion.getLocation());
            if(newDist<=min)
            {
                min = newDist;
                best = stack;
            }
        }
                
        return best;
    }
    
    /**
     * Allows bot to ignore existence of a stack.
     * @param stack Stack to ignore.
     */
    public void ignoreStack(Stack stack)
    {
        boolean found = false;
        int key = 0;
        for (Map.Entry<Integer, Stack> entry : list.entrySet()) {
            key = entry.getKey();
            Stack current = entry.getValue();
            if(stack==current)
            {
                break;
            }
            
        }
        if(found)
            list.remove(key);
    }
    
}
