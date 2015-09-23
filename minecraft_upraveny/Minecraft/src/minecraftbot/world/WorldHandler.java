
package minecraftbot.world;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import minecraftbot.IWorldView;
import minecraftbot.Id;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;

/**
 * Stores information about enviroment.
 * @author eZ
 */
public class WorldHandler implements IWorldView{
    private final Chunk[][] chunks;
    private final Set<Location> woods;
    private final HashMap<Id,Set<Location>> resourcesLocations;
    private final Set<Id> resources;
    private final int negativeOffset = 300, chunkSize = 600;
    
    public WorldHandler()
    {
        chunks = new Chunk[chunkSize][chunkSize];
        woods = new HashSet<>();
        resources = new HashSet<>();
        resources.add(Id.Log);
        resources.add(Id.GoldOre);
        resources.add(Id.IronOre);
        resources.add(Id.CoalOre);
        resources.add(Id.DiamondOre);
        resources.add(Id.RedstoneOre);
        resourcesLocations = new HashMap<>();
        for (Id id: resources) {
            resourcesLocations.put(id, new HashSet<Location>());
        }
    }
    
    /**
     * @param location Location to check.
     * @return Block that is placed 1 block under requested location.
     */
    @Override
    public Block getBlockUnder(Location location)
    {
        return getBlock(location.x, location.y-1, location.z);
    }
    
    /**
     * @param location Location of block.
     * @return Information about block that is placed on requested location.
     */
    @Override
    public Block getBlock(Location location)
    {
        location = Util.blockLocation(location);
        return getBlock((int)location.x, (int)location.y, (int)location.z);
    }
    
    /**
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @return Information about block that is placed on requested location.
     */
    @Override
    public Block getBlock(double x, double y, double z)
    {
        int ix = (int)x-(x<0?1:0),
            iz = (int)z-(z<0?1:0);
       return getBlock(ix, (int)y, iz);
    }
    
    /**
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @return Information about block that is placed on requested location.
     */    
    @Override
    public Block getBlock(int x, int y, int z)
    {
        int cx = x/16, cz = z/16;
        if(x<0&&x%16!=0)
        {
            cx--;
        }
        if(z<0&&z%16!=0)
        {
            cz--;
        }
        cx+=negativeOffset;
        cz+=negativeOffset;
        if(chunks[cx][cz]==null)
        {
            return new Block(Id.Air,(byte)0);
        }
        return chunks[cx][cz].getBlock(x%16, y, z%16);
    }
    
    /**
     * Sets information about block in the world.
     * @param x X coordinate of the block.
     * @param y Y coordinate of the block.
     * @param z Z coordinate of the block.
     * @param id Block id.
     * @param meta Block meta.
     */
    public void setBlock(int x, int y, int z, int id, byte meta)
    {
        Location location = new Location(x, y, z);
        Id oldId = getBlock(x,y,z).getId(),
           newId = Util.idMap.get(id);
        if(resources.contains(oldId))
        {
            removeResourceLocation(location);
        }
        
        if(resources.contains(newId))
        {
            resourcesLocations.get(newId).add(location);
        }

        int cx = x/16, cz = z/16;
        if(x<0&&x%16!=0)
        {
            cx--;
            x=x%16;
            x+=16;
        }
        if(z<0&&z%16!=0)
        {
            cz--;
            z=z%16;
            z+=16;
        }
        cx+=negativeOffset;
        cz+=negativeOffset;
        if(chunks[cx][cz]==null)
        {
            Util.logger.log(LogElement.World, LogLevel.Error, "New block out of range");
            return;
        }
        chunks[cx][cz].setBlock(x%16,y,z%16,id,meta);
        
    }
    
     /**
     * @param location Location for bot to stand on.
     * @return Whether bot can stand at the location.
     */  
    @Override
    public boolean canStandAt(Location location)
    {
        location = Util.blockLocation(location);
        return canStandAt((int)location.x, (int)location.y, (int)location.z);
    }
    
    /**
     * @param x X coordinate of the location.
     * @param y Y coordinate of the location.
     * @param z Z coordinate of the location.
     * @return Whether bot can stand at the location.
     */
    @Override
    public boolean canStandAt(int x, int y, int z)
    {
        return (!getBlock(x,y-1,z).isPassable())&&((getBlock(x,y,z).isPassable())&&((getBlock(x,y+1,z).isPassable())));
    }
    
    /**
     * Adds information about new chunk.
     * @param chunk 
     */
    public void addChunk(Chunk chunk)
    {
        chunks[chunk.getX()+negativeOffset][chunk.getZ()+negativeOffset] = chunk;
        for (Id id: resources) {
            resourcesLocations.get(id).addAll(chunk.getResourceLocations(id));
        }
    }
    
    
    /**
     * @param location Bot location.
     * @return Returns location of closest wood to bot.
     */
    public Location getClosestWood(Location location)
    {
        Location closestWood = null;
        double currentDist, bestDist = 999999999;
        for (Location wood : woods) {
            currentDist = Location.getDistance(location, wood);
            if(currentDist<bestDist)
            {
                bestDist = currentDist;
                closestWood = wood;
            }
        }
        return  closestWood;
    }

    /**
     * Deletes information about location of a resource.
     * @param location Location of the resource.
     */
    private void removeResourceLocation(Location location)
    {
        Id oldId = getBlock(location).getId();
        Set<Location> locations = resourcesLocations.get(oldId);
        locations.remove(location);
        for (Location l : locations) {
            if(l.equals(location, 0.1))
            {
                resourcesLocations.get(oldId).remove(l);
                break;
            }
        }   
    }

    /**
     * @param resource Id of resource to check.
     * @param location Source location.
     * @return Location of closest resource of particular type to the specified location.
     */
    @Override
    public Location getClosestResource(Id resource, Location location) {
        Util.logger.log(LogElement.Inventory, LogLevel.Info, resourcesLocations.get(resource).size());
        if(!resources.contains(resource))
        {
            Util.logger.log(LogElement.Inventory, LogLevel.Warn, resource + " is not considered resource.");
            return null;
        }
        Location closestRes = null;
        double currentDist, bestDist = 999999999;
        for (Location loc : resourcesLocations.get(resource)) {
            currentDist = Location.getDistance(location, loc);
            if(currentDist<bestDist)
            {
                bestDist = currentDist;
                closestRes = loc;
            }
        }
        return  closestRes;
    }

    /**
     * Deletes information about location of a resource.
     * @param location Location of the resource.
     */
    @Override
    public void ignoreResourceLocation(Location location) {
        removeResourceLocation(location);
    }
    
    
    
}
