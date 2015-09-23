

package minecraftbot.build;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import minecraftbot.Id;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;


public abstract class Buildable {
    
    /**
     * Builds block.
     * @param id Id of block.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate. 
     */
    public void block(Id id, int x, int y, int z)
    {   
        
        addBlock(id,x,y,z);
    }
    
    /**
     * Builds block on specified location.
     * @param id Id of the block. 
     * @param location Location of the block.
     */
    public void block(Id id, Location location)
    {   
        location = Util.blockLocation(location);
        addBlock(id,(int)location.x,(int)location.y,(int)location.z);
    }
    
    /**
     * Builds block according to plan.
     */
    void block(BlockPlan plan)
    {   
        addBlock(plan.id,plan.x,plan.y,plan.z);
    }  
    
    /**
     * Builds block according to plan with an offset.
     */
    void block(BlockPlan plan, int ox, int oy, int oz)
    {   
        addBlock(plan.id,plan.x+ox,plan.y+oy,plan.z+oz);
    }
    
    /**
     * Builds straight column.
     * @param id Id of block used.
     * @param x X coordinate.
     * @param ay Lowest Y coordinate. 
     * @param by Highest Y coordinate.
     * @param z Z coordinate. 
     */
    public void column(Id id, int x, int ay, int by, int z)
    {
        if(by<ay)
            column(id, x, by, ay, z);
        else
        {
            int count = by-ay+1;
            for(byte i = 0;i<count;i++)
            {   
                block(id, x, (byte) (ay+i), z);
            }
        }
    }
    
    /**
     * Builds straight column.
     * @param id Id of block used. 
     * @param location Location of the column.
     * @param height Height of the column (in blocks).
     */
    public void collumn(Id id, Location location, int height)
    {
        location = Util.blockLocation(location);
        {
            for(byte i = 0;i<height;i++)
            {   
                block(id, (int)location.x, (byte) ((int)location.y+i), (int)location.z);
            }
        }
    }

    /**
     * Builds wall between two blocks.
     * @param id Id of block used.
     * @param ax X coordinate of first block.
     * @param ay Y coordinate of first block.
     * @param az Z coordinate of first block.
     * @param bx X coordinate of second block.
     * @param by Y coordinate of second block.
     * @param bz Z coordinate of second block. 
     */
    public void wall(Id id, int ax, int ay, int az, int bx, int by, int bz)
    {
        if(bx<ax)
        {
            wall(id, bx, ay, az, ax, by, bz);
        }
        if(bz<az)
        {
            wall(id, ax, ay, bz, bx, by, az);
        }
        float dx = bx-ax+1, dz = bz-az+1;
        int total = dx>dz?bx-ax+1:bz-az+1;
        dx/=total;
        dz/=total;
        for(int i=0;i<total;i++)
        {
            column(id, (int)(ax+i*dx), ay, by, (int)(az+i*dz));
        }  
            
        
    }

    /**
     * Builds wall between two locations.
     * @param id Id of block used.
     * @param location1 Location of the first block.
     * @param location2 Location of the second block.
     */
    public void wall(Id id, Location location1, Location location2)
    {
        int ax = (int)location1.x;
        int bx = (int)location2.x;
        int ay = (int)location1.y;
        int by = (int)location2.y;
        int az = (int)location1.z;
        int bz = (int)location2.z;
        
        if(bx<ax)
        {
            wall(id, bx, ay, az, ax, by, bz);
        }
        if(bz<az)
        {
            wall(id, ax, ay, bz, bx, by, az);
        }
        float dx = bx-ax+1, dz = bz-az+1;
        int total = dx>dz?bx-ax+1:bz-az+1;
        dx/=total;
        dz/=total;
        for(int i=0;i<total;i++)
        {
            column(id, (int)(ax+i*dx), ay, by, (int)(az+i*dz));
        }  
            
    }
    
    /**
     * Builds square platform (horizontal wall).
     * @param id Id of block used.
     * @param ax X coordinate of first corner.
     * @param bx X coordinate of second corner.
     * @param y Y coordinate.
     * @param az Z coordinate of first corner.
     * @param bz Z coordinate of second corner.
     */
    public void platform(Id id, int ax, int bx, int y, int az, int bz)
    {
        int dx = (int)Math.signum(bx-ax);
        int dz = (int)Math.signum(bz-az);
        int cx = (bx-ax)*dx+1, cz =(bz-az)*dz+1;
        for (int i = 0; i < cx; i++) {
            for (int j = 0; j < cz; j++) {
                Util.logger.log(LogElement.Building, LogLevel.Trace, "BUILT AT "+(int)(ax+i*dx)+" x "+(byte) (y) + " x "+(int)(az+j*dz));                
                block(id, ax+i*dx, y, az+j*dz);
            }
        }
    }
    
    /**
     * Builds square platform between two locations (corners)..
     * If Y coordinate of locations differs, first one is used.
     * @param id Id of block used.
     * @param corner1 Location of the first corner.
     * @param corner2 Location of the second corner.
     */
    public void platform(Id id, Location corner1, Location corner2)
    {
        int ax = (int)corner1.x;
        int bx = (int)corner2.x;
        int y = (int)corner1.y;
        int az = (int)corner1.z;
        int bz = (int)corner2.z;
        int dx = (int)Math.signum(bx-ax);
        int dz = (int)Math.signum(bz-az);
        int cx = (bx-ax)*dx+1, cz =(bz-az)*dz+1;
        for (int i = 0; i < cx; i++) {
            for (int j = 0; j < cz; j++) {
                Util.logger.log(LogElement.Building, LogLevel.Trace, "BUILT AT "+(int)(ax+i*dx)+" x "+(byte) (y) + " x "+(int)(az+j*dz));                
                block(id, ax+i*dx, y, az+j*dz);
            }
        }
    }

    /**
     * Adds block to the list of blocks to be build.
     * @param id Id of block.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate. 
     */
    abstract protected void addBlock(Id id, int x, int y, int z);
}
