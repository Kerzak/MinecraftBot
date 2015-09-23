/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot;


import minecraftbot.network.MinecraftOutputStream;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import java.io.IOException;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.packet.out.Out06PlayerPositionAndLook;

/**
 * Allows basic movement
 * @author eZ
 */
public class Locomotion {

    private final double JUMPHEIGHT = 2, FALLINGVELOCITY=0.18;
    private Location location;
    private Velocity velocity;
    private float[] rotation;
    private double angle, feetLoc;
    private boolean onGround=false, overSolidBlock, initialized = false, reset = false, floating = false;
    public boolean stuck;
    private final IWorldView world;
    private final Out06PlayerPositionAndLook sender;
    
    public Locomotion(IWorldView world, Out06PlayerPositionAndLook sender) {
        location = Location.NONE;
        rotation = new float[]{0,0};
        velocity = Velocity.ZERO;
        this.sender = sender;
        this.world = world;
    }
    
    public void moveNorth()
    {
        velocity = velocity.setZ(velocity.z-1);
    }
    
    public void moveSouth()
    {
        velocity = velocity.setZ(velocity.z+1);
    }
    
    public void moveWest()
    {
        velocity = velocity.setX(velocity.x-1);

    }
    
    public void moveEast()
    {
        velocity = velocity.setX(velocity.x+1);
    }

    public void moveForward()
    {
        double ax = Math.sin(angle);
        double az = Math.cos(angle);
        velocity = velocity.add(new Velocity(-ax, 0, az));
    }

    public void moveBack()
    {
        double ax = Math.sin(angle+Math.PI);
        double az = Math.cos(angle+Math.PI);
        velocity = velocity.add(new Velocity(-ax, 0, az));
    }
    
    public void moveRight()
    {
        double ax = Math.sin(angle+Math.PI/2);
        double az = Math.cos(angle+Math.PI/2);
        velocity = velocity.add(new Velocity(-ax, 0, az));
    }
    
    public void moveLeft()
    {
        double ax = Math.sin(angle-Math.PI/2);
        double az = Math.cos(angle-Math.PI/2);
        velocity = velocity.add(new Velocity(-ax, 0, az));
    }
    
    public void jump()
    {
        //if(onGround)
        {
            velocity = velocity.setY(JUMPHEIGHT);
        }
    }
    
    public void turnHorizontal(int x)
    {
        rotation[0] += x;
        rotation[0] %= 360;
        setAngle();
    }
    
    public void turnHorizontalTo(int x)
    {
        rotation[0] = x;
        setAngle();
    }
    
    public void lookAt(Location b)
    {
        double x = b.x-location.x,
               z = b.z-location.z;
        rotation[0] = (float)(180.0/Math.PI*Math.acos((0*x+z) / (Math.sqrt(x*x+z*z))));
        if(x>0)
        {
            rotation[0] = -rotation[0];
        }
        double y = b.y-location.y,
               xz = Math.sqrt(x*x+z*z);
        rotation[1] = (float)(180.0/Math.PI*Math.acos((0*y+xz) / (Math.sqrt(y*y+xz*xz))));
        if(y>0)
            rotation[1]*=-1;
        setAngle();
    }
    
    public Location getLocation()
    {
        return location;
    }
    
    public float getAngle()
    {
        return rotation[0];
    }
    
    public void setLocation(double x, double y, double z)
    {
        //System.out.println("Location: " + x+ ", "+y+", "+z);
        location = new Location(x, y, z);
        reset = true;
    }
    
    public void setLocation(double[] targetLocation)
    {
        initialized = true;
        location = new Location(targetLocation[0],targetLocation[1],targetLocation[2]);
        //location = new MinecraftLocation(36, 44, 32.59);
        Util.logger.log(LogElement.Movement, LogLevel.Warn, "Location Reset");
        reset = true;
    }
        
    public void setLocation(Location targetLocation)
    {
        this.location = targetLocation;
        reset = true;
    }
        
    public void setRotation(float[] targetRotation)
    {
        rotation = targetRotation;
        setAngle();
    }

    public void setFloating(boolean floating) {
        this.floating = floating;
    }

    public boolean isFloating() {
        return floating;
    }
    
    public void toggleFloating()
    {
        floating = !floating;
    }
    
    private void setAngle()
    {
        angle = rotation[0]*Math.PI/180.0;
    }
    
    private void fallDown()
    {
        int oldY, newY;
        oldY = (int)(location.y);
        
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
     * @return is bot standing on something?
     */
    private boolean checkBlocksUnderneath()
    {
        if(location.y<1)
            return true;
        for (double x = -0.3; x < 0.5; x+=0.6) {
            for (double z = -0.3; z < 0.5; z+=0.6) {
                if(!world.getBlockUnder(location.addX(x).addZ(z)).isEmpty())
                    return true;
            }
        }
        return false;
    }
    
    public void loopStep(MinecraftOutputStream out) 
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
