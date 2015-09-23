/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.build;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import minecraftbot.IWorldView;
import minecraftbot.Id;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.world.Block;

/**
 *
 * @author student
 */
public class Structure extends Buildable{
    private int xWidth, zWidth, height;
    private int minX, maxX, minZ,maxZ;
    private byte minY, maxY;
    private ArrayList<BlockPlan> blockList;

    public Structure() {
        blockList = new ArrayList<>();
        xWidth = zWidth = height = 0;
    }
    
    @Override
    protected void addBlock(Id id, int x, int y, int z)
    {
        
        if(x<0||y<0||z<0)
        {
            String reason = "";
            if(x<0)
                reason += " x="+x;
            if(y<0)
                reason += " y="+y;
            if(z<0)
                reason += " z="+z;
            throw new IllegalArgumentException("Indices in custom structure cant be lesser than zero:" + reason);
        }
        
        if(y>255)
            throw new IllegalArgumentException("Y cannot be greater than 255: y=" + y);
        
        if(x>xWidth)
            xWidth = x;
        if(y>height)
            height = y;
        if(z>zWidth)
            zWidth = z;
                
        blockList.add(new BlockPlan(id, x, (byte)y, z));
    }


    public Structure getFlippedX() {
        Structure result = new Structure();
        for (Iterator<BlockPlan> it = blockList.iterator(); it.hasNext();) {
            BlockPlan bPlan = it.next();
            result.addBlock(bPlan.id, xWidth-bPlan.x, bPlan.y, bPlan.z);
        }
        
        return result;
    }
    
    public Structure getFlippedZ() {
        Structure result = new Structure();
        for (Iterator<BlockPlan> it = blockList.iterator(); it.hasNext();) {
            BlockPlan bPlan = it.next();
            result.addBlock(bPlan.id, bPlan.x, bPlan.y, zWidth-bPlan.z);
        }
        
        return result;
    }
    
    public Structure getFlippedXZ() {
        Structure result = new Structure();
        for (Iterator<BlockPlan> it = blockList.iterator(); it.hasNext();) {
            BlockPlan bPlan = it.next();
            result.addBlock(bPlan.id, xWidth-bPlan.x, bPlan.y, zWidth-bPlan.z);
        }
        
        return result;
    }

    public Structure getRotatedClockwise() {
        Structure result = new Structure();
        for (Iterator<BlockPlan> it = blockList.iterator(); it.hasNext();) {
            BlockPlan bPlan = it.next();
            result.addBlock(bPlan.id, zWidth-bPlan.z, bPlan.y, bPlan.x);
        }
        
        return result;
    }
    
    public Structure getRotatedCounterClockwise() {
        Structure result = new Structure();
        for (Iterator<BlockPlan> it = blockList.iterator(); it.hasNext();) {
            BlockPlan bPlan = it.next();
            result.addBlock(bPlan.id, bPlan.z, bPlan.y, xWidth-bPlan.x);
        }
        
        return result;
    }

    public List<BlockPlan> getBlockList() {
        return blockList;
    }    
 
    List<BlockPlan> getBlockPlan(Comparator<BlockPlan> comparator, IWorldView worldView, int x, int y, int z)
    {
        ArrayList<BlockPlan> result = new ArrayList<>();
        List<BlockPlan> queue = new ArrayList<>();
        Block[][][] world = new Block[xWidth+2][height+2][zWidth+2];

        //create smaller world abstraction
        for (int i = 0; i < xWidth+2; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < zWidth+2; k++) {
                    try {
                        world[i][j][k] = worldView.getBlock(i + x - 1, j + y - 1, k + z - 1);
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println(e);
                        System.exit(0);
                    }
                }
            }
        }

        
        queue = findStartingBlocks(world);
        
        Collections.sort(queue, comparator);

        Util.logger.log(LogElement.Building, LogLevel.Info, "QUEUE:");        
        for (BlockPlan bp : queue) {
            Util.logger.log(LogElement.Building, LogLevel.Info, bp);
        }

        Set<BlockPlan> open = new HashSet<>();
        Set<BlockPlan> closed = new HashSet<>();
        List<BlockPlan> toRemove = new ArrayList<>();
        BlockPlan current = null;
        
        for (BlockPlan bPlan : blockList) {
            open.add(bPlan);
        }
        
        while(!open.isEmpty())
        {
            if(queue.isEmpty())
            {
                queue = findStartingBlocks(world);

                toRemove.clear();
                for (BlockPlan nb : queue) {
                    if(closed.contains(nb))
                        toRemove.add(nb);
                }

                for (BlockPlan r : toRemove) {
                    queue.remove(r);
                }
                if(queue.isEmpty())
                    {
                    System.err.println("CHYBA JE V "+current);
                    break;
                }
            }
            current = queue.get(0);
            open.remove(current);
            closed.add(current);
            result.add(current);
            queue.clear();
            
            world[current.x+1][current.y+1][current.z+1] = new Block(Id.END, (byte)0);
            
            queue = neighbours(current);
            
            toRemove.clear();
            for (BlockPlan nb : queue) {
                if(closed.contains(nb))
                    toRemove.add(nb);
            }
            
            for (BlockPlan r : toRemove) {
                queue.remove(r);
            }
            
            
            Collections.sort(queue,comparator);
            System.out.println(open.size()+" TO GO!");
        }
/*
        BlockPlan current;
        List<BlockPlan> neighbours;
        Set<BlockPlan> closed = new HashSet<>();
        while(!queue.isEmpty())
        {
            current = queue.remove(0);
            closed.add(current);
            result.add(current);
            neighbours = neighbours(current);
            for (BlockPlan nb : neighbours) {
                if(!queue.contains(nb)&&!closed.contains(nb))
                {
                    for (int i = 0; i < queue.size(); i++) {
                        if(comparator.compare(nb, queue.get(i))<0)
                        {
                            queue.add(i, nb);
                            break;
                        }
                        if(i-1==queue.size())
                            queue.add(nb);
                    }
                }
            }

            //Collections.sort(queue, comparator);
            
            
        }
*/
        Util.logger.log(LogElement.Building, LogLevel.Info, "RESULT:");
        for (BlockPlan bp : result) {
            Util.logger.log(LogElement.Building, LogLevel.Info, bp);
        }
        
        return result;
    }

    private List<BlockPlan> neighbours(BlockPlan b)
    {
        return neighbours(b.x, b.y, b.z);
    }
    
    private List<BlockPlan> neighbours(int x, int y, int z)
    {
        ArrayList<BlockPlan> result = new ArrayList<>();
        
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    if((i==0&&j==0&&k!=0)||(i==0&&j!=0&&k==0)||(i!=0&&j==0&&k==0))
                    {
                        if(y+j<0)
                            continue;
                        int index = blockList.indexOf(new BlockPlan(Id.END, x+i, y+j, z+k));
                        if(index>=0)
                        {
                            result.add(blockList.get(index));
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<BlockPlan> findStartingBlocks(Block[][][] world) {
        List<BlockPlan> result = new ArrayList<>();
        for (int i = 0; i < xWidth+2; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < zWidth+2; k++) {
                    if(!world[i][j][k].isEmpty())
                    {
                        //System.out.println("SOLID: "+ i + " / "  + j + " / " + k);
                        for(BlockPlan nbr : neighbours(i-1, j-1, k-1))
                        {
                            if(!result.contains(nbr))
                            {
                                result.add(nbr);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

}
