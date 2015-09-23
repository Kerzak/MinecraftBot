
package minecraftbot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.HashMap;
import minecraftbot.logger.Logger;
import minecraftbot.world.BlockFactory;

/**
 * Provides general tools for easier development of bots.
 */
public class Util {
    
    /**
     * Logs bots activity
     */
    public static Logger logger;
    
    
    /**
     * Implementation of flyweight factory for creating blocks.
     */
    public static BlockFactory blockFactory;
    
    /**
     * Converts normal location to block location.
     * @param location Original location.
     * @return Block location.
     */
    static public Location blockLocation(Location location)
    {
        return new Location((int)location.x-((location.x<0&&location.x-(int)location.x!=0)?1:0),
                (int)location.y,
                (int)location.z-((location.z<0&&location.z-(int)location.z!=0)?1:0));
    }
    
    /**
     * @param located Normal location of object.
     * @return Block location of th object.
     */
    static public Location blockLocation(ILocated located)
    {
        return blockLocation(located.getLocation());
    }

    /**
     * @param location1 Normal location 1.
     * @param location2 Normal location 2.
     * @return Whether two normal locations represents same block.
     */
    static public boolean equalBlockLocation(Location location1, Location location2)
    {
        return blockLocation(location1).equals(blockLocation(location2));
    }

    /**
     * @param located1 Object 1 location.
     * @param located2 Object 2 location.
     * @return Whether two objects are on the same block.
     */
    static public boolean equalBlockLocation(ILocated located1, ILocated located2)
    {
        return blockLocation(located1).equals(blockLocation(located2));
    }
    
    /**
     * Maps integers to correspondent block Ids.
     */
    public static HashMap<Integer, Id> idMap;
    
    
    /**
     * Creates int-id map and initializes logger.
     */
    public static void Load()
    {
        logger = new Logger();
        
        blockFactory = new BlockFactory();
        
        idMap = new HashMap<>();
        for(Id id : Id.values())
        {
            idMap.put(id.getValue(), id);
        }
    }
}
