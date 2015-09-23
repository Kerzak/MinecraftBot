
package minecraftbot.entity;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import minecraftbot.world.Block;

/**
 * Item stack
 * @author eZ
 */
public class Stack extends Entity {

    private final Block type;
    private final byte count;
    
    public Stack(Block type, byte count, int ID,  Location location)
    {
        super(ID, 2, location);
        this.type = type;
        this.count = count;
    }

    public Block getBlockType() {
        return type;
    }

    public byte getCount() {
        return count;
    }
    
    

    
    
}