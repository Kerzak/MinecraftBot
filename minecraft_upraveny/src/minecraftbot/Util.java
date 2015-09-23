/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.HashMap;
import minecraftbot.logger.Logger;

/**
 *
 * @author eZ
 */
public class Util {
    
    public static Logger logger;
    
    static public Location blockLocation(Location location)
    {
        return new Location((int)location.x+(location.x<0?1:0),
                (int)location.y,
                (int)location.z+(location.z<0?1:0));
    }
    
    public static HashMap<Integer, Id> idMap;
            
    public static void Load()
    {
        logger = new Logger();
        
        idMap = new HashMap<>();
        for(Id id : Id.values())
        {
            idMap.put(id.getValue(), id);
        }
    }
}
