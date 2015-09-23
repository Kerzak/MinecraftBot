

package minecraftbot;


import minecraftbot.network.MinecraftOutputStream;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.packet.out.Out06PlayerPositionAndLook;
import minecraftbot.packet.out.Out08PlayerBlockPlacement;
import minecraftbot.world.Block;

/**
 * Allows basic movement.
 * Simulates gravity.
 * @author eZ
 */
public class Locomotion {

    private final double JUMPHEIGHT = 1.2, FALLINGVELOCITY=0.18;
    private Location location;
    private Velocity velocity;
    private float[] rotation;
    private double angle, feetLoc;
    private boolean onGround=false, overSolidBlock, initialized = false, reset = false, floating = false;
    public boolean stuck;
    private final IWorldView world;
    private final Out06PlayerPositionAndLook sender;
    private final Out08PlayerBlockPlacement blockPlacementPacket;
    
    public Locomotion(IWorldView world, Out06PlayerPositionAndLook senderPacket, Out08PlayerBlockPlacement blockPlacementPacket)
    {
        location = Location.NONE;
        rotation = new float[]{0,0};
        velocity = Velocity.ZERO;
        this.sender = senderPacket;
        this.blockPlacementPacket = blockPlacementPacket;
        this.world = world;
    }
    
    /**
     * Moves north by 1 block.
     */
    public void moveNorth()
    {
        velocity = velocity.setZ(velocity.z-1);
    }
    
    /**
     * Moves south by 1 block.
     */
    public void moveSouth()
    {
        velocity = velocity.setZ(velocity.z+1);
    }
    
    /**
     * Moves west by 1 block.
     */
    public void moveWest()
    {
        velocity = velocity.setX(velocity.x-1);

    }
    
    /**
     * Moves east by 1 block.
     */
    public void moveEast()
    {
        velocity = velocity.setX(velocity.x+1);
    }

    /**
     * Moves forward by 1 block.
     */
    public void moveForward()
    {
        double ax = Math.sin(angle);
        double az = Math.cos(angle);
        velocity = velocity.add(new Velocity(-ax, 0, az));
    }

    /**
     * Moves back by 1 block.
     */
    public void moveBack()
    {
        double ax = Math.sin(angle+Math.PI);
        double az = Math.cos(angle+Math.PI);
        velocity = velocity.add(new Velocity(-ax, 0, az));
    }
    
    /**
     * Moves right by 1 block.
     */
    public void moveRight()
    {
        double ax = Math.sin(angle+Math.PI/2);
        double az = Math.cos(angle+Math.PI/2);
        velocity = velocity.add(new Velocity(-ax, 0, az));
    }
    
    /**
     * Moves left by 1 block.
     */
    public void moveLeft()
    {
        double ax = Math.sin(angle-Math.PI/2);
        double az = Math.cos(angle-Math.PI/2);
        velocity = velocity.add(new Velocity(-ax, 0, az));
    }
    
    /**
     * Tries to step on a nearby block.
     * @return Whether steping aside can be performed.
     */
    public boolean stepAside()
    {
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                if(z!=0||x!=0)
                {
                    for (int y = -1; y < 2; y++) {
                        if(world.canStandAt(location.addXYZ(x, y, z)))
                        {
                            location = location.addXYZ(x, y, z);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Opens door.
     * @param location Location of the door.
     */
    public void openDoor(Location location)
    {
        location = Util.blockLocation(location);
        openDoor((int)location.x, (int)location.y, (int)location.z);
    }
    
    /**
     * Opens door.
     * @param x X coordinate of the door.
     * @param y Y coordinate of the door.
     * @param z Z coordinate of the door.
     */
    public void openDoor(int x, int y, int z)
    {
        Block b = world.getBlock(x,y,z);
        if(b.getId().getType().equals(ItemType.Door)&&!b.isPassable())
        {
            if(y<0||y>255)
                throw new IllegalArgumentException("Y coordinate must be in range 0-255, is "+y);
            blockPlacementPacket.sendMessage(x, (byte)y, z, (byte)1, (byte)1, (byte)1, (byte)1);
        }
    }
    
    /**
     * Closes door.
     * @param location Location of the door.
     */
    public void closeDoor(Location location)
    {
        location = Util.blockLocation(location);
        openDoor((int)location.x, (int)location.y, (int)location.z);
    }
    
    /**
     * Closes door.
     * @param x X coordinate of the door.
     * @param y Y coordinate of the door.
     * @param z Z coordinate of the door.
     */
    public void closeDoor(int x, int y, int z)
    {
        Block b = world.getBlock(x,y,z);
        if(b.getId().getType().equals(ItemType.Door)&&b.isPassable())
        {
            if(y<0||y>255)
                throw new IllegalArgumentException("Y coordinate must be in range 0-255, is "+y);
            blockPlacementPacket.sendMessage(x, (byte)y, z, (byte)1, (byte)1, (byte)1, (byte)1);
        }
    }
    
    /**
     * Jumps up.
     */
    public void jump()
    {
        velocity = velocity.setY(JUMPHEIGHT);
    }
    
    /**
     * Turns horizontally by some angle.
     * @param x Size of the angle in degrees.
     */
    public void turnHorizontal(int x)
    {
        rotation[0] += x;
        rotation[0] %= 360;
        setAngle();
    }
    
    /**
     * Turns horizonally to an angle.
     * @param x Angle in degrees.
     */
    public void turnHorizontalTo(int x)
    {
        rotation[0] = x;
        setAngle();
    }
    
    /**
     * Looks at location.
     * @param location Location to look at.
     */
    public void lookAt(Location location)
    {
        double x = location.x-this.location.x,
               z = location.z-this.location.z;
        rotation[0] = (float)(180.0/Math.PI*Math.acos((0*x+z) / (Math.sqrt(x*x+z*z))));
        if(x>0)
        {
            rotation[0] = -rotation[0];
        }
        double y = location.y-this.location.y,
               xz = Math.sqrt(x*x+z*z);
        rotation[1] = (float)(180.0/Math.PI*Math.acos((0*y+xz) / (Math.sqrt(y*y+xz*xz))));
        if(y>0)
            rotation[1]*=-1;
        setAngle();
    }
    
    /**
     * Looks at target.
     * @param target Target to look at.
     */
    public void lookAt(ILocated target)
    {
        lookAt(target.getLocation());
    }
    
    /**
     * @return Current bot location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * @return Current horizontal angle of bot in degrees.
     */
    public float getAngle()
    {
        return rotation[0];
    }
    
    /**
     * Sets bot location, triggers location reset event.
     * @param x X coordinate of new location.
     * @param y Y coordinate of new location.
     * @param z Z coordinate of new location. 
     */
    public void setLocation(double x, double y, double z)
    {
        location = new Location(x, y, z);
        reset = true;
    }
    
    /**
     * Sets bot location, triggers location reset event.
     * @param targetLocation Array containing information about new location. 
     */
    public void setLocation(double[] targetLocation)
    {
        if(targetLocation.length!=3)
            throw new IllegalArgumentException("Bad format of array, legnth must be 3.");
        Util.logger.log(LogElement.Movement, LogLevel.Warn, "Location Reset "+location);
        initialized = true;
        location = new Location(targetLocation[0],targetLocation[1],targetLocation[2]);
        reset = true;
    }
        
    /**
     * Sets bot location, triggers location reset event.
     * @param targetLocation New location.
     */
    public void setLocation(Location targetLocation)
    {
        this.location = targetLocation;
        reset = true;
    }
        
    /**
     * Sets bot rotation.
     * @param targetRotation  Array containing 2 values - pitch and yaw.
     */
    public void setRotation(float[] targetRotation)
    {
        if(targetRotation.length!=2)
            throw new IllegalArgumentException("Bad format of array, legnth must be 3.");
        rotation = targetRotation;
        setAngle();
    }

    /**
     * Calculates new angle value.
     */
    private void setAngle()
    {
        angle = rotation[0]*Math.PI/180.0;
    }
    
    /**
     * Simulates falling down.
     */
    private void fallDown()
    {
        boolean problematic = (int)(location.y)!=(int)(location.y-FALLINGVELOCITY);
        if(overSolidBlock&&problematic)
        {
            location = location.setY((int)location.y);
        }
        else
        {
        velocity = velocity.setY(-FALLINGVELOCITY);
        }

    }
    
    /**
     * @return Whether bot is standing on something.
     */
    private boolean checkBlocksUnderneath()
    {
        if(location.y<1)
            return true;
        for (double x = -0.3; x < 0.5; x+=0.6) {
            for (double z = -0.3; z < 0.5; z+=0.6) {
                if(!world.getBlockUnder(location.addX(x).addZ(z)).isPassable())
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void update(MinecraftOutputStream out) 
    {
        overSolidBlock = checkBlocksUnderneath();
        onGround = overSolidBlock
                && location.y-(int)(location.y)<0.005;
        //onGround = true;
        if(initialized&&!onGround&&!reset&&!floating)
            fallDown();
        location = location.add(velocity);
        Util.logger.log(LogElement.Movement, LogLevel.Trace, "Is over solid block - "+overSolidBlock);
        Util.logger.log(LogElement.Movement, LogLevel.Trace, "Is on ground - "+onGround);
        Util.logger.log(LogElement.Movement, LogLevel.Trace, "Location - "+location);
        velocity = Velocity.ZERO;
        reset = false;
        sender.send(location.x, location.y, location.z, rotation[0], rotation[1], onGround);
               
    }
}
