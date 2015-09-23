/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.build;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.HashSet;
import java.util.List;
import minecraftbot.Id;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;

/**
 *
 * @author eZ
 */
public abstract class Buildable {

    
    public Buildable()
    {
 //       blockList = new
    }
    
    public void block(Id id, Location loc)
    {
        loc = Util.blockLocation(loc);
        block(id, (int)loc.x, (byte)loc.y, (int)loc.z);
    }
    
    /**
     * Builds block.
     * @param id Id of block.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @return 
     */
    public void block(Id id, int x, int y, int z)
    {        
       /* if(!world.getBlock(x, y, z).isEmpty())
        {
            return false;
        }        
        if(!inventory.contains(id))
        {
            return false;
        }*/
        //System.out.println("PLAN @ "+x+":"+y+":"+z);
/*        if(!world.getBlock(x, y, z).getId().equals(Id.Air))
        {
            blockingBlocks.add(new Location(x, y, z));
        }
        buildQueue.add(new BlockPlan(id, x, y, z));*/
        //return true;
        
        addBlock(id,x,y,z);
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
     * Builds straight collumn.
     * @param id Id of block used.
     * @param x X coordinate.
     * @param ay Lowest Y coordinate. 
     * @param by Highest Y coordinate.
     * @param z Z coordinate.
     * @return 
     */
    public void collumn(Id id, int x, int ay, int by, int z)
    {
        if(by<ay)
            collumn(id, x, by, ay, z);
        else
        {
            int count = by-ay+1;
            //System.out.println("COUNT IS "+count);
            /*if(inventory.amountOf(id)<count)
            {
                System.out.println(id);
                System.out.println(inventory.amountOf(id));
                return false;
            }*/
            //System.out.println("Is it false?");
            for(byte i = 0;i<count;i++)
            {   
                //System.out.println("BUILT AT "+(int)(x)+" x "+(byte) (ay+i) + " x "+(int)(z));
                block(id, x, (byte) (ay+i), z);
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
     * @return 
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
            collumn(id, (int)(ax+i*dx), ay, by, (int)(az+i*dz));
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
        /*(inventory.amountOf(id)<(cx*cz))
        {
            return false;
        }*/
        for (int i = 0; i < cx; i++) {
            for (int j = 0; j < cz; j++) {
                Util.logger.log(LogElement.Building, LogLevel.Trace, "BUILT AT "+(int)(ax+i*dx)+" x "+(byte) (y) + " x "+(int)(az+j*dz));                
                block(id, ax+i*dx, y, az+j*dz);
            }
        }
    }

    abstract protected void addBlock(Id id, int x, int y, int z);
}
