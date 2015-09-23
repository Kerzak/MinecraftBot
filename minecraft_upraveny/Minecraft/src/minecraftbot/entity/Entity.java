
package minecraftbot.entity;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import minecraftbot.ILocated;


public class Entity implements ILocated{
    private Location location;
    private final int ID, type;
    
    public Entity(int ID, int type, Location location)
    {
        this.location = location;
        this.ID = ID;
        this.type = type;
    }
    
    public final int getID()
    {
        return ID;
    }
    
    public final int getType()
    {
        return type;
    }
    
    @Override
    public final Location getLocation()
    {
        return location;
    }

    final void setLocation(Location location)
    {
        this.location = location;
    }
    
    final void applyVelocity(Velocity velocity)
    {
        location = location.add(velocity);
    }
}
