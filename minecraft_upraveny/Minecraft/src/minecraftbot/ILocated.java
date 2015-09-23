
package minecraftbot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/**
 * Interface for objects, that are located in the game world.
 */
public interface ILocated {
    /**
     * @return Location of the object.
     */
    public Location getLocation();
}
