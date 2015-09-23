
package minecraftbot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import minecraftbot.world.Block;

/**
 * Interface providing information about world.
 */
public interface IWorldView {

    /**
     * @param location Location of block.
     * @return Information about block that is placed on requested location.
     */
    public Block getBlock(Location location);
    
    /**
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @return Information about block that is placed on requested location.
     */
    public Block getBlock(double x, double y, double z);

    /**
     * @param location Location to check.
     * @return Block that is placed 1 block under requested location.
     */
    public Block getBlockUnder(Location location);
    
    public Block getBlock(int x, int y, int z);
    
    /**
     * @param x X coordinate of the location.
     * @param y Y coordinate of the location.
     * @param z Z coordinate of the location.
     * @return Whether bot can stand at the location.
     */    
    public boolean canStandAt(int x, int y, int z);
    
    /**
     * @param location Location for bot to stand on.
     * @return Whether bot can stand at the location.
     */
    public boolean canStandAt(Location location);
    
    /**
     * @param resource Id of resource to check.
     * @param location Source location.
     * @return Location of closest resource of particular type to the specified location.
     */
    public Location getClosestResource(Id resource, Location location);

    /**
     * Deletes information about location of a resource.
     * @param location Location of the resource.
     */
    public void ignoreResourceLocation(Location location);
    
}
