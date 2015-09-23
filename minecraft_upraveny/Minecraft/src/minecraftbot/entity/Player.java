
package minecraftbot.entity;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;


public class Player extends Entity {

    private final String name;
    private int ping;
    
    public Player(String name, int ID,  Location location)
    {
        super(ID, 1, location);
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public int getPing() {
        return ping;
    }
    
    
}