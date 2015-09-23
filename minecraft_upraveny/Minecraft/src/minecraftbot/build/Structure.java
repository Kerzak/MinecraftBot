
package minecraftbot.build;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import minecraftbot.IWorldView;
import minecraftbot.Id;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.world.Block;

/**
 * Represents complex structure that bot can build.
 */
public class Structure extends Buildable{

    private int xWidth, zWidth, height;
    private int minX, maxX, minZ,maxZ;
    private byte minY, maxY;
    private final ArrayList<BlockPlan> blockList;

    public Structure() {
        blockList = new ArrayList<>();
        xWidth = zWidth = height = 0;
    }

    /**
     * Loads structure from file.
     * @param path Source file.
     * @return Loaded structure.
     */
    public static Structure fromFile(String path) throws IOException, Exception {
        Structure result = new Structure();
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            int lineNum=1;
            String line = in.readLine();
            String[] lines;
            HashMap<Character,Id> charMap = new HashMap<>();
            charMap.put(' ', Id.None);
            Id id = null;
            while(!line.equals("-"))
            {
                lines=line.split("=");
                if(lines.length!=2)
                {
                    error(lineNum, path, "Bad format \""+line+"\", should be Identifier=ID");
                }
                if(lines[0].length()!=1)
                {
                    error(lineNum,path,"Identifier too long - should be only one character");
                }
                try
                {
                    id = Id.valueOf(lines[1]);
                }
                catch(IllegalArgumentException ex)
                {
                    error(lineNum,path,"Cannot find ID "+lines[1]);
                }
                charMap.put(lines[0].charAt(0), id);
                line=in.readLine();
                lineNum++;
            }
            for (Map.Entry<Character, Id> entry : charMap.entrySet()) {
                Character character = entry.getKey();
                Id id1 = entry.getValue();
            }
            int x=0,y=0,z=0;
            Id bid;
            while((line=in.readLine())!=null)
            {
                while(!line.equals("-"))
                {
                    for(x = 0;x<line.length();x++)
                    {
                        bid = charMap.get(line.charAt(x));
                        if(bid==null)
                            error(lineNum, path, "Row " + (x+1) + " : Unknown identifier - "+line.charAt(x));
                        else if(!bid.equals(Id.None))
                        {
                            result.block(bid, x, y, z);
                        }
                    }
                    z++;
                    line=in.readLine();
                    if(line==null)
                        break;
                }
                z=0;
                y++;
            }
        }
        print(result);
        return result;
    }
    
    private static void error(int line, String file, String msg) throws Exception
    {
        throw new Exception(file+" Line - "+line+" : "+msg);
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
        
        List<BlockPlan> result = new ArrayList<>();
        for (int i = 0; i < height; i+=4) {
            result.addAll(getBlockPlan(comparator, world, x, y, z, i, i+4));
        }
        return result;
    }
    
    List<BlockPlan> getBlockPlan(Comparator<BlockPlan> comparator, Block[][][] world, int x, int y, int z,int min, int max)
    {
        ArrayList<BlockPlan> result = new ArrayList<>();
        List<BlockPlan> queue = new ArrayList<>();


        
        queue = findStartingBlocks(world,min,max);
        
        Collections.sort(queue, comparator);

        Util.logger.log(LogElement.Building, LogLevel.Debug, "QUEUE:");        
        for (BlockPlan bp : queue) {
            Util.logger.log(LogElement.Building, LogLevel.Debug, bp);
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
                queue = findStartingBlocks(world,min,max);

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
                    break;
                }
            }
            current = queue.get(0);
            open.remove(current);
            closed.add(current);
            result.add(new BlockPlan(current, x, y, z));
            queue.clear();
            
            world[current.x+1][current.y+1][current.z+1] = new Block(Id.End, (byte)0);
            
            queue = neighbours(current,min,max);
            
            toRemove.clear();
            for (BlockPlan nb : queue) {
                if(closed.contains(nb))
                    toRemove.add(nb);
            }
            
            for (BlockPlan r : toRemove) {
                queue.remove(r);
            }
            
            
            Collections.sort(queue,comparator);
        }

        Util.logger.log(LogElement.Building, LogLevel.Debug, "RESULT:");
        for (BlockPlan bp : result) {
            Util.logger.log(LogElement.Building, LogLevel.Debug, bp);
        }
        
        return result;
    }

    private List<BlockPlan> neighbours(BlockPlan b, int min, int max)
    {
        return neighbours(b.x, b.y, b.z,min,max);
    }
    
    private List<BlockPlan> neighbours(int x, int y, int z, int min, int max)
    {
        ArrayList<BlockPlan> result = new ArrayList<>();
        
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    if((i==0&&j==0&&k!=0)||(i==0&&j!=0&&k==0)||(i!=0&&j==0&&k==0))
                    {
                        if(y+j<0||y+j<min||y+j>=max)
                            continue;
                        int index = blockList.indexOf(new BlockPlan(Id.End, x+i, y+j, z+k));
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

    private List<BlockPlan> findStartingBlocks(Block[][][] world, int min, int max) {
        List<BlockPlan> result = new ArrayList<>();
        for (int i = 0; i < xWidth+2; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < zWidth+2; k++) {
                    if(!world[i][j][k].isEmpty())
                    {
                        for(BlockPlan nbr : neighbours(i-1, j-1, k-1,min,max))
                        {
                            if(!result.contains(nbr)&&(nbr.y>=min)&&nbr.y<max)
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
    
    private static void print(Structure s)
    {
        for (BlockPlan plan : s.getBlockList()) {
            System.out.println(plan);
        }
    }
    
}
