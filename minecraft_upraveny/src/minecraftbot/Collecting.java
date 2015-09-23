/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import java.util.HashMap;
import minecraftbot.entity.Stack;

/**
 * Handles information about collectible items - doesn't work properly yet.
 * @author eZ
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
    
    public boolean isPickableInRange()
    {
        return isPickableNearby(4);
    }
    
    public boolean isPickableNearby(double distance)
    {
        for(Stack stack : list.values())
        {
            if(!velocities.containsKey(stack.getID())&&Location.getDistance(stack.getLocation(), locomotion.getLocation())<=distance)
                return true;
        }
        return false;
    }
    
    public Stack getNearestStack()
    {
        double min = Double.MAX_VALUE, newDist;
        Stack best = null;
        
        for(Stack stack : list.values())
        {
            newDist = Location.getDistance(stack.getLocation(), locomotion.getLocation());
            if(/*!velocities.containsKey(stack.getID())&&*/newDist<=min)
            {
                min = newDist;
                best = stack;
            }
        }
                
        return best;
    }
    
}
